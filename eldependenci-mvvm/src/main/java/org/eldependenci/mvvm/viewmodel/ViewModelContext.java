package org.eldependenci.mvvm.viewmodel;

import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Consumer;

public interface ViewModelContext {
    List<ItemStack> getItems(char pattern);

    <E extends PlayerEvent> void observeEvent(Class<E> event, long timeout, Consumer<E> callback);

    <V extends ViewModel> void navigateTo(Class<V> view);

    UISession getSession();

}
