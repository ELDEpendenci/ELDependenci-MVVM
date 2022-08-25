package org.eldependenci.mvvm.viewmodel;

import org.bukkit.entity.Player;

public interface ViewModel {

    void init(Player player);

    default void beforeUnMount(Player player){}

}
