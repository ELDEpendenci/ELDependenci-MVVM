package org.eldependenci.mvvm;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import org.eldependenci.mvvm.demo.profile.ProfileService;

public class MVVMModule extends AbstractModule  {

    private final MVVMInstaller installer;

    public MVVMModule(MVVMInstaller installer) {
        this.installer = installer;
    }

    @Override
    protected void configure() {
        bind(ProfileService.class).in(Scopes.SINGLETON); // test only
        bind(MVVMInstaller.class).toInstance(installer);
    }
}
