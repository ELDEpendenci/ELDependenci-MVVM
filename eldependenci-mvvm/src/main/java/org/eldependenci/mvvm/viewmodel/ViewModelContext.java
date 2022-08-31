package org.eldependenci.mvvm.viewmodel;

import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * View Model 的控制器
 */
public interface ViewModelContext {

    /**
     * 獲取該 pattern 内的所有物品 (順序)
     * @param pattern 指定 pattern
     * @return 物品列表
     */
    List<ItemStack> getItems(char pattern);

    /**
     * 獲取指定 pattern 的物品連帶 slot
     * @param pattern 指定 pattern
     * @return 連帶 slot 的物品列表
     */
    Map<Integer, ItemStack> getItemMap(char pattern);

    /**
     * 在特定時間内事件並返回處理
     * @param <E> 事件類別
     * @param event 事件
     * @param timeout 過期時間(ticks)
     * @param callback 返回處理
     */
    <E extends PlayerEvent> void observeEvent(Class<E> event, long timeout, Consumer<E> callback);

    /**
     * 跳轉到另一個界面
     * @param <V> 界面模型類
     * @param view 要跳轉的界面
     */
    <V extends ViewModel> void navigateTo(Class<V> view);

    /**
     * 跳轉到另一個節目, 連帶初始屬性
     * @param <V> 界面模型類
     * @param view 要跳轉的界面
     * @param props 初始屬性
     */
    <V extends ViewModel> void navigateTo(Class<V> view, Map<String, Object> props);

}
