package org.eldependenci.mvvm;

import org.bukkit.entity.Player;
import org.eldependenci.mvvm.viewmodel.ViewModel;

import java.util.Map;

/**
 * 界面服務器
 */
public interface InventoryService {
    
    /**
     * 開啓界面
     * @param player 開啓玩家
     * @param view 界面
     * @param props 初始屬性
     */
    void openUI(Player player, Class<? extends ViewModel> view, Map<String, Object> props);

    /**
     * 打開界面
     * @param player 開啓玩家
     * @param view 界面
     */
    void openUI(Player player, Class<? extends ViewModel> view);

    /**
     * 透過 id 開啓界面
     * @param player 開啓玩家
     * @param vmId viewmodel 的 id
     */
    void openUI(Player player, String vmId);

    /**
     * 透過 id 開啓界面
     * @param player 開啓玩家
     * @param vmId view model id
     * @param props 初始屬性
     */
    void openUI(Player player, String vmId, Map<String, Object> props);

}
