package org.eldependenci.mvvm.view;


import com.ericlam.mc.eld.services.ItemStackService;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

/**
 * 按鈕組件工廠
 */
public interface UIButtonFactory {

    /**
     * 裝飾按鈕組件
     * @param factoryConsumer 裝飾
     * @return this
     */
    UIButtonFactory decorate(Consumer<ItemStackService.ItemFactory> factoryConsumer);

    /**
     * 映射物品, 映射後會以該物品為主體,先前的裝飾會被抛棄
     * @param item 物品
     * @return this
     */
    UIButtonFactory mirror(ItemStack item);

    /**
     * 創建按鈕組件
     * @return 按鈕組件
     */
    UIButton create();

}
