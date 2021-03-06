package org.eldependenci.mvvm;

import com.google.inject.Inject;
import com.google.inject.Injector;
import org.bukkit.entity.Player;
import org.eldependenci.mvvm.view.ViewDescriptor;
import org.eldependenci.mvvm.viewmodel.ViewModel;
import org.eldependenci.mvvm.viewmodel.ViewModelBinding;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InventoryManager implements InventoryService{

    @Inject
    private Injector injector;

    @Inject
    private ELDMVVMPlugin plugin;

    private final Map<String, Class<? extends ViewModel>> viewModelMap;

    @Inject
    public InventoryManager(MVVMInstaller installer){
        this.viewModelMap = installer.getViewBindingMap();
    }


    @Override
    public void openUI(Player player, Class<? extends ViewModel> view) {
        var bindingView = view.getAnnotation(ViewModelBinding.class);
        if (bindingView == null) throw new IllegalStateException("ViewModel must annotated with @ViewModelBinding.");
        var viewType = bindingView.value();
        if (!viewType.isAnnotationPresent(ViewDescriptor.class)) throw new IllegalStateException("View must annotated with @ViewDescriptor");


        var vm = injector.getInstance(view);

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
