package org.eldependenci.mvvm.command;

import com.ericlam.mc.eld.annotations.Commander;
import com.ericlam.mc.eld.bukkit.CommandNode;
import org.bukkit.command.CommandSender;

@Commander(
        name = "mvvm",
        description = "ELD MVVM指令"
)
public class MVVMCommand implements CommandNode {
    @Override
    public void execute(CommandSender sender) {

    }
}
