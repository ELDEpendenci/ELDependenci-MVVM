package org.eldependenci.mvvm;

import org.eldependenci.mvvm.viewmodel.ViewModel;

public interface MVVMInstallation {

    void bindId(String id, Class<? extends ViewModel> view);

}
