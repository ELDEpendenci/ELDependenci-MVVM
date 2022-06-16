package org.eldependenci.mvvm;

import com.ericlam.mc.eld.BukkitManagerProvider;
import com.ericlam.mc.eld.ELDBukkit;
import com.ericlam.mc.eld.ELDBukkitPlugin;
import com.ericlam.mc.eld.ServiceCollection;

@ELDBukkit(
        lifeCycle = MVVMLifeCycle.class,
        registry = MVVMRegistry.class
)
public class ELDMVVMPlugin extends ELDBukkitPlugin {

    @Override
    protected void manageProvider(BukkitManagerProvider bukkitManagerProvider) {

    }

    @Override
    public void bindServices(ServiceCollection serviceCollection) {

    }
}
