package org.eldependenci.mvvm;

import org.eldependenci.mvvm.viewmodel.ViewModel;

/**
 *  MVVM 安裝器
 */
public interface MVVMInstallation {

    /**
     * 綁定 ID
     * @param id 自定義 ID
     * @param view View Model 類
     */
    void bindId(String id, Class<? extends ViewModel> view);

}
