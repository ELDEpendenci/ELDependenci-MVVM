package org.eldependenci.mvvm.ui;

import com.ericlam.mc.eld.services.ConfigPoolService;
import com.ericlam.mc.eld.services.ItemStackService;
import com.ericlam.mc.eld.services.ReflectionService;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.bukkit.entity.Player;
import org.eldependenci.mvvm.ELDMVVMPlugin;
import org.eldependenci.mvvm.InventoryService;
import org.eldependenci.mvvm.MVVMInstaller;
import org.eldependenci.mvvm.config.MVVMLang;
import org.eldependenci.mvvm.viewmodel.ViewModel;
import org.eldependenci.mvvm.viewmodel.ViewModelBinding;

import java.util.HashMap;
import java.util.Map;

public final class InventoryManager implements InventoryService {

    @Inject
    private Injector injector;
    @Inject
    private ELDMVVMPlugin plugin;
    @Inject
    private ConfigPoolService configPoolService;
    @Inject
    private ReflectionService reflectionService;
    @Inject
    private ItemStackService itemStackService;
    @Inject
    private MVVMLang lang;

    private final Map<String, Class<? extends ViewModel>> viewModelMap;

    private final Map<Class<? extends ViewModel>, ViewModelDispatcher> dispatcherMap = new HashMap<>();

    @Inject
    public InventoryManager(MVVMInstaller installer) {
        this.viewModelMap = installer.getViewBindingMap();
    }

    @Override
    public void openUI(Player player, Class<? extends ViewModel> view, Map<String, Object> context) {
        var bindingView = view.getAnnotation(ViewModelBinding.class);
        if (bindingView == null) throw new IllegalStateException("ViewModel must annotated with @ViewModelBinding.");
        var viewType = bindingView.value();
        var dispatcher = dispatcherMap.computeIfAbsent(view, key -> {
            var di = new ViewModelDispatcher(view,
                    viewType,
                    configPoolService,
                    itemStackService,
                    reflectionService,
                    injector,
                    (viewModel, player1, session) -> openUI(player1, viewModel));
            plugin.getServer().getPluginManager().registerEvents(di, plugin);
            return di;
        });
        dispatcher.openFor(player, s -> context.forEach(s::setAttribute));
    }

    @Override
    public void openUI(Player player, Class<? extends ViewModel> view) {
        this.openUI(player, view, Map.of());
    }

    @Override
    public void openUI(Player player, String vmId) {
        this.openUI(player, vmId, Map.of());
    }

    @Override
    public void openUI(Player player, String vmId, Map<String, Object> context) {
        var viewModelType = viewModelMap.get(vmId);
        if (viewModelType == null) {
            player.sendMessage(lang.getLang().getF("ui-not-found", vmId));
            plugin.getLogger().warning(String.format("unknown GUI id %s, ignored", vmId));
            return;
        }
        this.openUI(player, viewModelType, context);
    }


    public synchronized void onClose() {
        dispatcherMap.values().forEach(ViewModelDispatcher::onClose);
    }
}
