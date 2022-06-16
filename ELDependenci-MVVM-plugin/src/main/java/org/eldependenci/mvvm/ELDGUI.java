package org.eldependenci.mvvm;

import com.ericlam.mc.eld.services.ConfigPoolService;
import com.ericlam.mc.eld.services.ItemStackService;
import com.ericlam.mc.eld.services.ReflectionService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.eldependenci.mvvm.button.UIButtonFactoryImpl;
import org.eldependenci.mvvm.button.UIButtonItem;
import org.eldependenci.mvvm.model.State;
import org.eldependenci.mvvm.model.StateValue;
import org.eldependenci.mvvm.view.*;
import org.eldependenci.mvvm.viewmodel.Context;
import org.eldependenci.mvvm.viewmodel.UISession;
import org.eldependenci.mvvm.viewmodel.ViewModel;
import org.eldependenci.mvvm.viewmodel.ViewModelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ELDGUI<T extends ViewModel, V extends View> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ELDGUI.class);

    private final T viewModelInstance;
    private final V viewInstance;
    private final Inventory nativeInventory;

    private final ItemStackService itemStackService;
    private final ReflectionService reflectionService;
    private final Map<Character, List<Integer>> patternMasks = new HashMap<>();
    private final Map<String, List<Character>> propertyUpdateMap = new HashMap<>();
    private final Map<Character, Method> updateMethodMap = new HashMap<>();

    private final Player owner;

    private final StateInvocationHandler stateHandler = new StateInvocationHandler(this::onPropertyUpdate);

    private final ViewModelContext viewModelContext = new ELDVMContext();




    public ELDGUI(
            T viewModelInstance,
            Class<V> viewType,
            ConfigPoolService configPoolService,
            ItemStackService itemStackService,
            ReflectionService reflectionService,
            Player player
    ) {
        this.viewModelInstance = viewModelInstance;
        this.itemStackService = itemStackService;
        this.reflectionService = reflectionService;
        this.owner = player;

        InventoryTemplate template;
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

        try {
            this.viewInstance = viewType.getConstructor().newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("error while creating view. (view must be no-arg constructor)", e);
        }

        this.nativeInventory = Bukkit.createInventory(null, template.patterns.size() * 9, ChatColor.translateAlternateColorCodes('&', template.title));
        this.renderFromTemplate(template, itemStackService);
        this.viewInstance.init(new GlobalUIContext());
        this.patternRender(viewType);


        this.initViewModel(reflectionService, player);

    }

    public void onInventoryClick(InventoryClickEvent e) {

    }


    public void onInventoryDrag(InventoryDragEvent e) {

    }

    public void onInventoryClose(InventoryCloseEvent e) {

    }

    private void patternRender(Class<V> type){
        var renderMethods = Arrays.stream(type.getMethods()).filter(f -> f.isAnnotationPresent(RenderView.class)).toList();
        for (Method method : renderMethods) {
            var rV = method.getAnnotation(RenderView.class);
            var annos = reflectionService.getParameterAnnotations(method);
            for (Annotation[] anno : annos) {
                var s = Arrays.stream(anno).filter(a -> a.annotationType() == StateValue.class).findFirst();
                if (s.isEmpty()) continue;
                var stateValue = (StateValue)s.get();
                propertyUpdateMap.putIfAbsent(stateValue.value(), new ArrayList<>());
                propertyUpdateMap.get(stateValue.value()).add(rV.value());
            }
        }
    }



    private void initViewModel(ReflectionService reflectionService, Player player) {

        var vmFields = reflectionService.getDeclaredFieldsUpTo(viewModelInstance.getClass(), null);

        var stateOpt = vmFields.stream().filter(f -> f.isAnnotationPresent(State.class)).findAny();

        if (stateOpt.isPresent()) {

            var stateHolder = stateOpt.get();

            if (!stateHolder.getType().isInterface()) {
                throw new IllegalStateException("A StateHolder must be an interface.");
            }

            var stateHolderIns = Proxy.newProxyInstance(
                    getClass().getClassLoader(),
                    new Class[]{stateHolder.getType()},
                    stateHandler
            );

            try {
                stateHolder.setAccessible(true);
                stateHolder.set(viewModelInstance, stateHolderIns);
            }catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }


        }

        var contextOpt = vmFields.stream().filter(f -> f.isAnnotationPresent(Context.class)).findAny();

        if (contextOpt.isPresent()){
            var context = contextOpt.get();
            if (context.getType() != ViewModelContext.class){
                throw new IllegalStateException("the field annotated with @Context must be ViewModelContext.class");
            }
            try {
                context.setAccessible(true);
                context.set(viewModelInstance, viewModelContext);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }

        viewModelInstance.mounted(player);
        viewModelInstance.initState();

    }


    private void onPropertyUpdate(String property){

    }

    private void renderFromTemplate(InventoryTemplate demoInventories, ItemStackService itemStackService) {
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
        for (String pattern : demoInventories.items.keySet()) {
            if (!this.patternMasks.containsKey(pattern.charAt(0))) continue;
            var slots = this.patternMasks.get(pattern.charAt(0));
            var itemDescriptor = demoInventories.items.get(pattern);
            var itemBuilder = itemStackService
                    .build(itemDescriptor.material)
                    .amount(itemDescriptor.amount);
            if (!itemDescriptor.name.isBlank()) itemBuilder.display(itemDescriptor.name);
            if (!itemDescriptor.lore.isEmpty()) itemBuilder.lore(itemDescriptor.lore);
            if (itemDescriptor.data > 0) itemBuilder.modelData(itemDescriptor.data);
            var item = itemBuilder.getItem();
            for (Integer slot : slots) {
                this.nativeInventory.setItem(slot, item);
            }
        }
    }

    private class GlobalUIContext implements UIContext {

        @Override
        public void add(UIButton... items) {
            for (UIButton item : items) {
                var any = nativeInventory.addItem(((UIButtonItem) item).item());
                if (!any.isEmpty()) {
                    LOGGER.warn("無法在界面 {} 的 中新增物品, 位置已滿。", viewInstance.getClass().getSimpleName());
                    break;
                }
            }
        }

        @Override
        public void set(int slot, UIButton item) {
            nativeInventory.setItem(slot, ((UIButtonItem) item).item());
        }

        @Override
        public void fill(UIButton item) {
            var items = new ItemStack[nativeInventory.getSize()];
            Arrays.fill(items, ((UIButtonItem) item).item());
            nativeInventory.setContents(items);
        }

        @Override
        public UIButtonFactory createButton() {
            return new UIButtonFactoryImpl(itemStackService);
        }

    }

    private class PatternUIContext implements UIContext {

        private final List<Integer> masks;
        private final char pattern;

        private PatternUIContext(char pattern) {
            this.pattern = pattern;
            this.masks = patternMasks.get(pattern);
            if (this.masks == null)
                throw new IllegalStateException("unknown pattern in view " + viewInstance.getClass().getSimpleName() + ": " + pattern);
        }

        @Override
        public void add(UIButton... items) {
            for (UIButton item : items) {
                if (!add(item)) {
                    LOGGER.warn("無法在界面 {} 的 Pattern {} 中新增物品, 位置已滿。", viewInstance.getClass().getSimpleName(), pattern);
                    return;
                }
            }
        }

        private boolean add(UIButton button) {
            for (Integer slot : masks) {
                var exist = nativeInventory.getItem(slot);
                if (exist != null && exist.getType() == Material.AIR) continue;
                nativeInventory.setItem(slot, ((UIButtonItem) button).item());
                return true;
            }
            return false;
        }

        @Override
        public void set(int slot, UIButton item) {
            int order = 0;
            for (Integer realSlot : masks) {
                if (slot == order) {
                    nativeInventory.setItem(realSlot, ((UIButtonItem) item).item());
                    return;
                }
                order++;
            }
        }

        @Override
        public void fill(UIButton item) {
            for (Integer slot : masks) {
                nativeInventory.setItem(slot, ((UIButtonItem) item).item());
            }
        }

        @Override
        public UIButtonFactory createButton() {
            return new UIButtonFactoryImpl(itemStackService);
        }
    }

    private class ELDVMContext implements ViewModelContext {

        @Override
        public <E extends PlayerEvent> void observeEvent(Class<E> event, long timeout, Consumer<E> callback) {

        }

        @Override
        public <VM extends ViewModel> void navigateTo(Class<VM> view) {

        }

        @Override
        public UISession getSession() {
            return null;
        }
    }

}
