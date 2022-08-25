package org.eldependenci.mvvm;

import com.ericlam.mc.eld.bukkit.ELDLifeCycle;
import com.google.inject.Inject;
import org.bukkit.plugin.java.JavaPlugin;
import org.eldependenci.mvvm.ui.InventoryManager;

public class MVVMLifeCycle implements ELDLifeCycle {


    @Inject
    private InventoryService inventoryService;


    @Override
    public void onEnable(JavaPlugin plugin) {

    }

    @Override
    public void onDisable(JavaPlugin plugin) {
        ((InventoryManager)inventoryService).onClose();
    }
}
