package org.eldependenci.mvvm.viewmodel;

import org.bukkit.entity.Player;

public interface ViewModel {

    void initState();

    default void mounted(Player player){}

    default void beforeUnMount(Player player){}

}
