package org.eldependenci.mvvm.command;

import com.ericlam.mc.eld.annotations.CommandArg;
import com.ericlam.mc.eld.annotations.Commander;
import com.ericlam.mc.eld.bukkit.CommandNode;
import com.google.inject.Inject;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.eldependenci.mvvm.InventoryService;
import org.eldependenci.mvvm.MVVMInstaller;

import java.util.ArrayList;
import java.util.List;

@Commander(
        name = "open",
        description = "打開界面指令",
        permission = "mvvm.open",
        playerOnly = true
)
public class MVVMOpenCommand implements CommandNode {

    @Inject
    private InventoryService inventoryService;

    @Inject
    private MVVMInstaller installer;

    @CommandArg(order = 1)
    private String id;

    @Override
    public void execute(CommandSender sender) {
        var player = (Player) sender;
        inventoryService.openUI(player, id);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, List<String> args) {
        return new ArrayList<>(installer.getViewBindingMap().keySet());
    }
}
