package org.eldependenci.mvvm;


import java.util.Map;

import org.bukkit.entity.Player;
import org.eldependenci.mvvm.viewmodel.ViewModel;

@FunctionalInterface
public interface ViewRedirection {

    void redirect(Player player, Class<? extends ViewModel> viewModel, Map<String, Object> props);

}
