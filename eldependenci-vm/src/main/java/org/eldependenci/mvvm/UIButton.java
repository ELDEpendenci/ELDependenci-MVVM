package org.eldependenci.mvvm;


import com.ericlam.mc.eld.services.ItemStackService;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public interface UIButton {

    UIButton decorate(Consumer<ItemStackService.ItemFactory> factoryConsumer);

    UIButton mirror(ItemStack item);

    UIButton bind(String name, Object property);

}
