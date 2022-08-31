package org.eldependenci.mvvm.viewmodel;

import java.util.Map;

import org.bukkit.entity.Player;

/**
 * 界面模型類
 */
public interface ViewModel {

    /**
     * 初始化周期挂鈎
     * @param player 打開界面的玩家
     * @param props 初始屬性
     */
    void init(Player player, Map<String, Object> props);

    /**
     * 關閉界面時的挂鈎
     * @param player 關閉界面的玩家
     */
    default void beforeUnMount(Player player){}

}
