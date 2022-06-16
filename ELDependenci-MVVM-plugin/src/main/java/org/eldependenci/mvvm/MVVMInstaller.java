package org.eldependenci.mvvm;

import org.eldependenci.mvvm.viewmodel.ViewModel;

import java.util.HashMap;
import java.util.Map;

public class MVVMInstaller implements MVVMInstallation{

    private final Map<String, Class<? extends ViewModel>> viewBindingMap = new HashMap<>();

    @Override
    public void bindId(String id, Class<? extends ViewModel> view) {
        this.viewBindingMap.put(id, view);
    }

    public Map<String, Class<? extends ViewModel>> getViewBindingMap() {
        return viewBindingMap;
    }

}
