package org.eldependenci.mvvm;

import org.bukkit.entity.Player;
import org.eldependenci.mvvm.viewmodel.ViewModel;

public interface InventoryService {

    void openUI(Player player, Class<? extends ViewModel> view);

    void openUI(Player player, String vmId);


}
