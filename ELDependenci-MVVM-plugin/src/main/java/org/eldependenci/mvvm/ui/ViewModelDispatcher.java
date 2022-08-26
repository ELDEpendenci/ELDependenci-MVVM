package org.eldependenci.mvvm.ui;

import com.ericlam.mc.eld.services.ConfigPoolService;
import com.ericlam.mc.eld.services.ItemStackService;
import com.ericlam.mc.eld.services.ReflectionService;
import com.google.inject.Injector;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.eldependenci.mvvm.CodeInventoryTemplate;
import org.eldependenci.mvvm.InventoryTemplate;
import org.eldependenci.mvvm.ViewRedirection;
import org.eldependenci.mvvm.model.State;
import org.eldependenci.mvvm.model.StateHolder;
import org.eldependenci.mvvm.model.StateValue;
import org.eldependenci.mvvm.view.*;
import org.eldependenci.mvvm.viewmodel.*;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class ViewModelDispatcher implements Listener {

    @SuppressWarnings("unchecked")
    private static final Class<? extends Annotation>[] EVENT_TYPES = new Class[]{ClickMapping.class, DragMapping.class, RequestMapping.class};
    private static final Set<Function<Method, RequestMapping>> REQUEST_MAPPERS = Set.of(
            method -> method.getAnnotation(RequestMapping.class),
            method -> Optional.ofNullable(method.getAnnotation(ClickMapping.class))
                    .map(mapping -> new DynamicRequestMapping(mapping.value(), InventoryClickEvent.class))
                    .orElse(null),
            method -> Optional.ofNullable(method.getAnnotation(DragMapping.class))
                    .map(mapping -> new DynamicRequestMapping(mapping.value(), InventoryDragEvent.class))
                    .orElse(null)
    );

    private final Map<Character, List<Integer>> patternMasks = new HashMap<>();
    private final Map<String, List<Character>> propertyUpdateMap = new HashMap<>();
    private final Map<Character, Method> updateMethodMap = new HashMap<>();
    private final Map<RequestMapping, Method> eventHandlerMap = new HashMap<>();
    private final Map<Character, Boolean> cancelledMap = new HashMap<>();
    private final Map<Player, ViewModelInstance> uiSessionMap = new ConcurrentHashMap<>();

    @Nullable
    private Field contextField;
    @Nullable
    private Field stateField;

    private final InventoryTemplate template;
    private final ReflectionService reflectionService;
    private final ItemStackService itemStackService;

    private final Class<? extends ViewModel> viewModelType;
    private final Class<? extends View> viewType;
    private final Injector injector;
    private boolean manualUpdate = false;

    private final ViewRedirection viewRedirection;



    public ViewModelDispatcher(
            Class<? extends ViewModel> viewModelType,
            Class<? extends View> viewType,
            ConfigPoolService configPoolService,
            ItemStackService itemStackService,
            ReflectionService reflectionService,
            Injector injector,
            ViewRedirection viewRedirection
    ) {

        this.itemStackService = itemStackService;
        this.reflectionService = reflectionService;
        this.injector = injector;
        this.viewModelType = viewModelType;
        this.viewType = viewType;
        this.viewRedirection = viewRedirection;

        if (viewType.isAnnotationPresent(UseTemplate.class)) {
            var temp = viewType.getAnnotation(UseTemplate.class);
            var pool = configPoolService.getGroupConfig(temp.groupResource());
            template = pool.findById(temp.template()).orElseThrow(() -> new IllegalStateException("Cannot find template: " + temp.template()));
        } else if (viewType.isAnnotationPresent(ViewDescriptor.class)) {
            var descriptor = viewType.getAnnotation(ViewDescriptor.class);
            template = new CodeInventoryTemplate(descriptor);
        } else {
            throw new IllegalStateException("view is lack of either @UseTemplate or @ViewDescriptor annotation.");
        }
        this.initPatternMasks(template);
        this.initRenderMethod(viewType);
        this.initEventHandlerMethod(viewModelType);
        this.initViewModelFields(viewModelType);
    }

    public void openFor(Player player){
        this.openFor(player, Map.of());
    }


    public void openFor(Player player, Map<String, Object> props){
        var vm = injector.getInstance(viewModelType);
        var vmIns = new ViewModelInstance(
                props,
                viewRedirection,
                uiSessionMap::remove,
                vm,
                viewType,
                itemStackService,
                player,
                template,
                Map.copyOf(propertyUpdateMap),
                Map.copyOf(updateMethodMap),
                Map.copyOf(eventHandlerMap),
                Map.copyOf(patternMasks),
                Map.copyOf(cancelledMap),
                stateField,
                contextField,
                manualUpdate);
        this.uiSessionMap.put(player, vmIns);
    }

    public synchronized void onClose(){
        uiSessionMap.values().forEach(ViewModelInstance::destroyView);
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        if (!(e.getWhoClicked() instanceof Player player)) return;
        Optional.ofNullable(uiSessionMap.get(player)).ifPresent(vmIns -> vmIns.onInventoryClick(e));
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e){
        if (!(e.getWhoClicked() instanceof Player player)) return;
        Optional.ofNullable(uiSessionMap.get(player)).ifPresent(vmIns -> vmIns.onInventoryDrag(e));
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e){
        if (!(e.getPlayer() instanceof Player player)) return;
        Optional.ofNullable(uiSessionMap.get(player)).ifPresent(vmIns -> vmIns.onInventoryClose(e));
    }


    private void initViewModelFields(Class<? extends ViewModel> viewModelType) {
        var fields = reflectionService.getDeclaredFieldsUpTo(viewModelType, null);
        for (var field : fields) {

            if (field.isAnnotationPresent(Context.class)) {
                if (this.contextField != null) {
                    throw new IllegalStateException("Only one context field is allowed.");
                }

                if (field.getType() != ViewModelContext.class){
                    throw new IllegalStateException("Context field must be of type ViewModelContext.");
                }

                this.contextField = field;
                continue;
            }

            if (field.isAnnotationPresent(State.class)) {
                if (this.stateField != null) {
                    throw new IllegalStateException("Only one state field is allowed.");
                }

                if (!StateHolder.class.isAssignableFrom(field.getType())){
                    throw new IllegalStateException("State field must be of type StateHolder.");
                } else if (!field.getType().isInterface()) {
                    throw new IllegalStateException("State field must be an interface.");
                }

                manualUpdate = field.getAnnotation(State.class).manual();

                this.stateField = field;
            }

            if (contextField != null && stateField != null) break;
        }
    }



    private void initRenderMethod(Class<? extends View> type) {
        var renderMethods = Arrays.stream(reflectionService.getMethods(type)).filter(f -> f.isAnnotationPresent(RenderView.class)).toList();
        for (Method method : renderMethods) {
            var rV = method.getAnnotation(RenderView.class);
            var parameters = method.getParameters();
            for (Parameter parameter : parameters) {
                var anno = parameter.getDeclaredAnnotations();
                var s = Arrays.stream(anno).filter(a -> a.annotationType() == StateValue.class).findFirst();
                if (s.isEmpty()) {
                    if (parameter.getType() != UIContext.class) {
                        throw new IllegalStateException("render method parameter must be either UIContext or state value");
                    }

                    continue;
                }
                var stateValue = (StateValue) s.get();
                propertyUpdateMap.putIfAbsent(stateValue.value(), new ArrayList<>());
                propertyUpdateMap.get(stateValue.value()).add(rV.value());
                updateMethodMap.put(rV.value(), method);
                cancelledMap.put(rV.value(), rV.cancelMove());
            }
        }
    }

    private void initPatternMasks(InventoryTemplate demoInventories){
        this.patternMasks.clear();
        int line = 0;
        for (String mask : demoInventories.patterns) {
            var masks = Arrays.copyOf(mask.toCharArray(), 9);
            for (int i = 0; i < masks.length; i++) {
                patternMasks.putIfAbsent(masks[i], new ArrayList<>());
                final int slots = i + 9 * line;
                patternMasks.get(masks[i]).add(slots);
            }
            line++;
        }
        for (String key : demoInventories.items.keySet()) {
            var pattern = key.charAt(0);
            if (!patternMasks.containsKey(pattern)) continue;
            var item = demoInventories.items.get(key);
            cancelledMap.put(pattern, item.cancelMove);
        }
    }


    private void initEventHandlerMethod(Class<? extends ViewModel> viewModelType) {
        var eventHandlerMethods = Arrays.stream(reflectionService.getMethods(viewModelType))
                .filter(m -> Arrays.stream(EVENT_TYPES).anyMatch(m::isAnnotationPresent))
                .toList();
        for (Method method : eventHandlerMethods) {
            RequestMapping mapping = null;
            for (Function<Method, RequestMapping> mapper : REQUEST_MAPPERS) {
                mapping = mapper.apply(method);
                if (mapping != null) {
                    break;
                }
            }

            if (mapping == null) {
                throw new IllegalStateException("cannot find request mapping for method: " + method.getName());
            }

            eventHandlerMap.put(mapping, method);
        }
    }


    private record DynamicRequestMapping(char pattern, Class<? extends InventoryInteractEvent> event) implements RequestMapping {
        @Override
        public Class<? extends Annotation> annotationType() {
            return RequestMapping.class;
        }
    }

}
