package org.eldependenci.mvvm;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class MVVMModule extends AbstractModule  {

    private final MVVMInstaller installer;

    public MVVMModule(MVVMInstaller installer) {
        this.installer = installer;
    }

    @Override
    protected void configure() {
        bind(MVVMInstaller.class).toInstance(installer);
        bind(InventoryService.class).to(InventoryManager.class).in(Scopes.SINGLETON);
    }
}
