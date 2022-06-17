package org.eldependenci.mvvm;

import com.ericlam.mc.eld.services.ConfigPoolService;
import com.ericlam.mc.eld.services.ItemStackService;
import com.ericlam.mc.eld.services.ReflectionService;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.bukkit.entity.Player;
import org.eldependenci.mvvm.viewmodel.ViewModel;
import org.eldependenci.mvvm.viewmodel.ViewModelBinding;

import java.util.HashMap;
import java.util.Map;

public class InventoryManager implements InventoryService{

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

    private final Map<String, Class<? extends ViewModel>> viewModelMap;

    private final Map<Class<? extends ViewModel>, ViewModelDispatcher> dispatcherMap = new HashMap<>();

    @Inject
    public InventoryManager(MVVMInstaller installer){
        this.viewModelMap = installer.getViewBindingMap();
    }


    @Override
    public void openUI(Player player, Class<? extends ViewModel> view) {
        var bindingView = view.getAnnotation(ViewModelBinding.class);
        if (bindingView == null) throw new IllegalStateException("ViewModel must annotated with @ViewModelBinding.");
        var viewType = bindingView.value();
        var dispatcher = dispatcherMap.computeIfAbsent(view, key -> new ViewModelDispatcher(view, viewType, configPoolService, itemStackService, reflectionService, injector));
        dispatcher.openFor(player);
    }

    @Override
    public void openUI(Player player, String vmId) {
        var viewModelType = viewModelMap.get(vmId);
        if (viewModelType == null) {
            player.sendMessage("unknown GUI: "+vmId);
            plugin.getLogger().warning(String.format("unknown GUI id %s, ignored", vmId));
            return;
        }
        this.openUI(player, viewModelType);
    }
}
