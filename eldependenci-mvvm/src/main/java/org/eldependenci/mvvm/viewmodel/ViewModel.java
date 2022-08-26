package org.eldependenci.mvvm.viewmodel;

import java.util.Map;

import org.bukkit.entity.Player;

public interface ViewModel {

    void init(Player player, Map<String, Object> props);

    default void beforeUnMount(Player player){}

}
