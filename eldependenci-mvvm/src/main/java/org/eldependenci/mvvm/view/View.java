package org.eldependenci.mvvm.view;

/**
 * 界面類
 */
public interface View {

    /**
     * 全局初始渲染
     * @param context 界面控制器
     */
    default void init(UIContext context){}

}
