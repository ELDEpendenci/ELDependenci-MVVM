package org.eldependenci.mvvm;

import com.ericlam.mc.eld.*;
import org.eldependenci.mvvm.config.MVVMConfig;
import org.eldependenci.mvvm.config.MVVMLang;
import org.eldependenci.mvvm.demo.DemoTemplate;
import org.eldependenci.mvvm.demo.profile.ProfileViewModel;
import org.eldependenci.mvvm.ui.InventoryManager;

@ELDBukkit(
        lifeCycle = MVVMLifeCycle.class,
        registry = MVVMRegistry.class
)
public class ELDMVVMPlugin extends ELDBukkitPlugin {

    private final MVVMInstaller installer = new MVVMInstaller();

    @Override
    protected void manageProvider(BukkitManagerProvider bukkitManagerProvider) {
        var config = bukkitManagerProvider.getConfigStorage().getConfigAs(MVVMConfig.class);

        if (config.showDemo){
            installer.bindId("profile", ProfileViewModel.class);
        }
    }

    @Override
    public void bindServices(ServiceCollection serviceCollection) {

        serviceCollection.addConfiguration(MVVMConfig.class);
        serviceCollection.addConfiguration(MVVMLang.class);
        serviceCollection.addGroupConfiguration(DemoTemplate.class);

        serviceCollection.bindService(InventoryService.class, InventoryManager.class);

        AddonInstallation installation = serviceCollection.getInstallation(AddonInstallation.class);
        installation.customInstallation(MVVMInstallation.class, installer);
        installation.installModule(new MVVMModule(installer));
    }
}
