package org.eldependenci.mvvm.view;

/**
 * 界面控制器
 */
public interface UIContext {

    /**
     * 新增UI按鈕組件
     * @param items 按鈕
     */
    void add(UIButton... items);

    /**
     * 設置按鈕組件
     * @param slot 插槽
     * @param item 按鈕
     */
    void set(int slot, UIButton item);

    /**
     * 填滿按鈕組件
     * @param item 按鈕組件
     */
    void fill(UIButton item);

    /**
     * 創建按鈕組件工廠
     * @return 按鈕組件工廠
     */
    UIButtonFactory createButton();

}
