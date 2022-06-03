package tutorial.showcase;

import com.ericlam.mc.eld.bukkit.CommandNode;
import com.ericlam.mc.eld.bukkit.ComponentsRegistry;
import com.ericlam.mc.eld.registration.CommandRegistry;
import com.ericlam.mc.eld.registration.ListenerRegistry;
import org.bukkit.event.Listener;

public class TutorialRegistry implements ComponentsRegistry {


    @Override
    public void registerCommand(CommandRegistry<CommandNode> commandRegistry) {
        // no commands
    }

    @Override
    public void registerListeners(ListenerRegistry<Listener> listenerRegistry) {
        // no listeners
    }

}
