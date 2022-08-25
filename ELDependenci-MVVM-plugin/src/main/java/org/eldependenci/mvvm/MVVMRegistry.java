package org.eldependenci.mvvm;

import com.ericlam.mc.eld.bukkit.CommandNode;
import com.ericlam.mc.eld.bukkit.ComponentsRegistry;
import com.ericlam.mc.eld.registration.CommandRegistry;
import com.ericlam.mc.eld.registration.ListenerRegistry;
import org.bukkit.event.Listener;
import org.eldependenci.mvvm.command.MVVMCommand;
import org.eldependenci.mvvm.command.MVVMOpenCommand;

public class MVVMRegistry implements ComponentsRegistry {
    @Override
    public void registerCommand(CommandRegistry<CommandNode> commandRegistry) {
        commandRegistry.command(MVVMCommand.class, cc ->{
            cc.command(MVVMOpenCommand.class);
        });
    }

    @Override
    public void registerListeners(ListenerRegistry<Listener> listenerRegistry) {

    }
}
