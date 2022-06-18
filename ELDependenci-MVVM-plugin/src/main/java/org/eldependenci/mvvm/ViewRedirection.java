package org.eldependenci.mvvm;


import org.bukkit.entity.Player;
import org.eldependenci.mvvm.viewmodel.UISession;
import org.eldependenci.mvvm.viewmodel.ViewModel;

@FunctionalInterface
public interface ViewRedirection {

    void redirect(Class<? extends ViewModel> viewModel, Player player, UISession session);

}
