package org.eldependenci.mvvm.view;


import com.ericlam.mc.eld.services.ItemStackService;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public interface UIButtonFactory {

    UIButtonFactory decorate(Consumer<ItemStackService.ItemFactory> factoryConsumer);

    UIButtonFactory mirror(ItemStack item);

    UIButtonFactory bind(String name, Object property);

    UIButton create();

}
