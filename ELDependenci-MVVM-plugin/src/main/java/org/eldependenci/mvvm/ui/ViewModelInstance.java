package org.eldependenci.mvvm.ui;

import com.ericlam.mc.eld.services.ItemStackService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.eldependenci.mvvm.ELDMVVMPlugin;
import org.eldependenci.mvvm.InventoryTemplate;
import org.eldependenci.mvvm.ViewRedirection;
import org.eldependenci.mvvm.button.UIButtonFactoryImpl;
import org.eldependenci.mvvm.button.UIButtonItem;
import org.eldependenci.mvvm.model.StateValue;
import org.eldependenci.mvvm.view.UIButton;
import org.eldependenci.mvvm.view.UIButtonFactory;
import org.eldependenci.mvvm.view.UIContext;
import org.eldependenci.mvvm.view.View;
import org.eldependenci.mvvm.viewmodel.RequestMapping;
import org.eldependenci.mvvm.viewmodel.UISession;
import org.eldependenci.mvvm.viewmodel.ViewModel;
import org.eldependenci.mvvm.viewmodel.ViewModelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.function.Consumer;

public class ViewModelInstance {

    private static final Logger LOGGER = LoggerFactory.getLogger(ViewModelInstance.class);

    private final ViewModel viewModelInstance;
    private final View viewInstance;
    private final Inventory nativeInventory;
    private final ItemStackService itemStackService;
    private final Map<Character, List<Integer>> patternMasks;
    private final Map<String, List<Character>> propertyUpdateMap;
    private final Map<Character, Method> updateMethodMap;
    private final Map<RequestMapping, Method> eventHandlerMap;
    private final Map<Character, Boolean> cancelledMap;
    private final Player owner;
    private final StateInvocationHandler stateHandler;
    private final ViewModelContext viewModelContext = new ELDVMContext();
    private final GlobalUIContext globalUIContext = new GlobalUIContext();
    private final ELDMVVMPlugin plugin = ELDMVVMPlugin.getPlugin(ELDMVVMPlugin.class);
    private final UISession session;
    private final ViewRedirection redirection;
    private final Consumer<Player> onDestroy;
    private boolean disableUpdate;
    private boolean doNotDestroyView = false;
    private BukkitTask waitingTask = null;

    public ViewModelInstance(
            UISession session,
            ViewRedirection redirection,
            Consumer<Player> onDestroy,
            ViewModel viewModelInstance,
            Class<? extends View> viewType,
            ItemStackService itemStackService,
            Player player,
            InventoryTemplate template,
            Map<String, List<Character>> propertyUpdateMap,
            Map<Character, Method> updateMethodMap,
            Map<RequestMapping, Method> eventHandlerMap,
            Map<Character, List<Integer>> patternMasks,
            Map<Character, Boolean> cancelledMap,
            @Nullable Field stateField,
            @Nullable Field contextField,
            boolean manualStateUpdate
    ) {
        this.viewModelInstance = viewModelInstance;
        this.itemStackService = itemStackService;
        this.owner = player;
        this.propertyUpdateMap = propertyUpdateMap;
        this.updateMethodMap = updateMethodMap;
        this.eventHandlerMap = eventHandlerMap;
        this.patternMasks = patternMasks;
        this.cancelledMap = cancelledMap;
        this.session = session;
        this.redirection = redirection;
        this.onDestroy = onDestroy;
        this.disableUpdate = true;

        this.stateHandler = new StateInvocationHandler(this::onPropertyUpdate, this::onManualUpdate, manualStateUpdate);

        try {
            this.viewInstance = viewType.getConstructor().newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("error while creating view. (view must be no-arg constructor)", e);
        }

        this.nativeInventory = Bukkit.createInventory(null, template.patterns.size() * 9, ChatColor.translateAlternateColorCodes('&', template.title));
        this.renderFromTemplate(template, itemStackService);
        this.initViewModel(stateField, contextField, player);
        this.renderViews();
        this.owner.openInventory(this.nativeInventory);
        this.disableUpdate = false;
    }

    public void onInventoryClick(InventoryClickEvent e) {
        var eventHandlerOpt = this.eventHandlerMap.entrySet()
                .stream()
                .filter(en -> {
                    var mapping = en.getKey();
                    return mapping.event() == InventoryClickEvent.class &&
                            e.getClickedInventory() == this.nativeInventory &&
                            e.getWhoClicked() == this.owner &&
                            patternMasks.getOrDefault(mapping.pattern(), Collections.emptyList()).contains(e.getSlot());
                })
                .findAny();

        if (eventHandlerOpt.isEmpty()) return;
        var eventHandler = eventHandlerOpt.get();
        this.handleEvent(eventHandler, e);
    }


    public void onInventoryDrag(InventoryDragEvent e) {
        var eventHandlerOpt = this.eventHandlerMap.entrySet()
                .stream()
                .filter(en -> {
                    var mapping = en.getKey();
                    return mapping.event() == InventoryDragEvent.class &&
                            e.getInventory() == this.nativeInventory &&
                            e.getWhoClicked() == this.owner &&
                            patternMasks.getOrDefault(mapping.pattern(), Collections.emptyList()).stream().anyMatch(s -> e.getInventorySlots().contains(s));
                })
                .findAny();
        if (eventHandlerOpt.isEmpty()) return;
        var eventHandler = eventHandlerOpt.get();
        this.handleEvent(eventHandler, e);
    }

    private void handleEvent(Map.Entry<RequestMapping, Method> eventHandler, InventoryInteractEvent e) {
        var pattern = eventHandler.getKey().pattern();
        var method = eventHandler.getValue();
        if (cancelledMap.get(pattern)){
            e.setCancelled(true);
        }
        try {
            method.invoke(this.viewModelInstance, e);
        } catch (Exception ex) {
            handleException(ex, pattern);
        }
    }

    public void onInventoryClose(InventoryCloseEvent e) {
        if (doNotDestroyView) return;
        this.destroyView();
    }


    private void renderViews() {

        this.viewInstance.init(globalUIContext);

        for (Character pattern : updateMethodMap.keySet()) {
            this.renderView(pattern);
        }
    }

    private void renderView(char pattern) {

        var slots = patternMasks.get(pattern);
        if (slots == null) {
            LOGGER.warn("pattern {} is not defined in template", pattern);
            return;
        }

        if (!updateMethodMap.containsKey(pattern)) {
            LOGGER.warn("no update method found for pattern: {}", pattern);
            return;
        }

        var method = updateMethodMap.get(pattern);

        var parameters = method.getParameters();

        var arguments = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            var parameter = parameters[i];

            if (parameter.getType() == UIContext.class) {
                arguments[i] = new PatternUIContext(pattern);
                continue;
            }

            var stateValue = parameter.getAnnotation(StateValue.class);
            if (stateValue == null) {
                throw new IllegalArgumentException("method parameter must be annotated with @StateValue or with UIContext");
            }

            var property = stateValue.value();
            arguments[i] = stateHandler.getState(property);
        }

        try {
            // clear items of that pattern first
            for (Integer slot : slots) {
                nativeInventory.setItem(slot, null);
            }
            // render items
            method.invoke(viewInstance, arguments);
        } catch (Exception e) {
            handleException(e, pattern);
        }
    }


    private void handleException(Exception e, char pattern) {
        LOGGER.error("error while invoking update method for pattern {}: {}", pattern, e.getMessage());
        e.printStackTrace();
        var ctx = new PatternUIContext(pattern);
        var btn = ctx.createButton();
        ctx.fill(btn.decorate(f -> f.material(Material.BARRIER)
                .display("&cError: "+e.getClass().getSimpleName())
                .lore("&c" + e.getMessage()))
                .create());
        cancelledMap.put(pattern, true);
    }


    private void initViewModel(@Nullable Field stateField, @Nullable Field contextField, Player player) {

        if (stateField != null) {
            try {
                stateField.setAccessible(true);
                stateField.set(viewModelInstance, Proxy.newProxyInstance(
                        viewModelInstance.getClass().getClassLoader(),
                        new Class[]{stateField.getType()},
                        stateHandler
                ));
            } catch (Exception e) {
                throw new IllegalStateException("error while setting state field", e);
            }
        }

        if (contextField != null) {
            try {
                contextField.setAccessible(true);
                contextField.set(viewModelInstance, viewModelContext);
            } catch (Exception e) {
                throw new IllegalStateException("error while setting context field", e);
            }
        }

        viewModelInstance.init(player);
    }


    private void onPropertyUpdate(String property) {

        if (disableUpdate) return;

        if (!propertyUpdateMap.containsKey(property)) {
            LOGGER.warn("no update method found for property: {}", property);
            return;
        }

        var characters = propertyUpdateMap.get(property);

        for (char pattern : characters) {
            this.renderView(pattern);
        }
    }


    private void onManualUpdate(Queue<String> properties) {
        Set<Character> toUpdate = new HashSet<>();
        while (!properties.isEmpty()) {
            var property = properties.poll();
            if (!propertyUpdateMap.containsKey(property)) continue;
            var patterns = propertyUpdateMap.get(property);
            toUpdate.addAll(patterns);
        }
        for (Character pattern : toUpdate) {
            this.renderView(pattern);
        }
    }

    private void renderFromTemplate(InventoryTemplate demoInventories, ItemStackService itemStackService) {
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


    public void destroyView() {
        if (waitingTask != null && !waitingTask.isCancelled()) waitingTask.cancel();
        viewModelInstance.beforeUnMount(owner);
        this.nativeInventory.clear();
        this.onDestroy.accept(owner);
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


        public List<ItemStack> getItems(char pattern) {
            if (!patternMasks.containsKey(pattern)) return List.of();
            var slots = patternMasks.get(pattern);
            var items = new ArrayList<ItemStack>();
            for (Integer slot : slots) {
                var item = nativeInventory.getItem(slot);
                if (item != null && item.getType().isAir()) items.add(item);
            }
            return items;
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
                if (exist != null && exist.getType().isAir()) continue;
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
        public List<ItemStack> getItems(char pattern) {
            return globalUIContext.getItems(pattern);
        }

        @Override
        public <E extends PlayerEvent> void observeEvent(Class<E> event, long timeout, Consumer<E> callback) {
            var listener = new Listener() {
            };
            Runnable cancelListener = () -> {
                HandlerList.unregisterAll(listener);
                owner.openInventory(nativeInventory);
                doNotDestroyView = false;
                if (waitingTask != null && !waitingTask.isCancelled()) waitingTask.cancel();
                waitingTask = null;
            };
            doNotDestroyView = true;
            owner.closeInventory();
            plugin.getServer().getPluginManager().registerEvent(event, listener, EventPriority.NORMAL,
                    (listen, e) -> {
                        if (e.getClass() != event) return;
                        E realEvent = event.cast(e);
                        if (realEvent.getPlayer() != owner) return;
                        if (e instanceof Cancellable canceller) {
                            canceller.setCancelled(true);
                        }
                        if (e.isAsynchronous()) {
                            Bukkit.getScheduler().runTask(plugin, () -> {
                                callback.accept(realEvent);
                                cancelListener.run();
                            });
                        } else {
                            callback.accept(realEvent);
                            cancelListener.run();
                        }
                    },
                    plugin);
            waitingTask = Bukkit.getScheduler().runTaskLater(plugin, cancelListener, timeout);
        }

        @Override
        public <VM extends ViewModel> void navigateTo(Class<VM> view) {
            destroyView();
            Bukkit.getScheduler().runTask(plugin, () -> redirection.redirect(view, owner, session));
        }

        @Override
        public UISession getSession() {
            return session;
        }
    }

}
