package org.eldependenci.mvvm;

import org.bukkit.entity.Player;
import org.eldependenci.mvvm.viewmodel.ViewModel;

import java.util.Map;

public interface InventoryService {
    void openUI(Player player, Class<? extends ViewModel> view, Map<String, Object> context);

    void openUI(Player player, Class<? extends ViewModel> view);

    void openUI(Player player, String vmId);

    void openUI(Player player, String vmId, Map<String, Object> context);

}
