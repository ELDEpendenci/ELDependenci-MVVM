package org.eldependenci.mvvm.button;

import com.ericlam.mc.eld.services.ItemStackService;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.eldependenci.mvvm.view.UIButton;
import org.eldependenci.mvvm.view.UIButtonFactory;

import java.util.function.Consumer;

public final class UIButtonFactoryImpl implements UIButtonFactory {

    private final ItemStackService itemStackService;
    private ItemStackService.ItemFactory itemFactory;

    public UIButtonFactoryImpl(ItemStackService itemStackService) {
        this.itemStackService = itemStackService;
        this.reset();
    }

    @Override
    public UIButtonFactory decorate(Consumer<ItemStackService.ItemFactory> factoryConsumer) {
        factoryConsumer.accept(this.itemFactory);
        return this;
    }

    @Override
    public UIButtonFactory mirror(ItemStack item) {
        this.itemFactory = itemStackService.edit(item);
        return this;
    }

    @Override
    public UIButton create() {
        UIButton btn = new UIButtonItem(itemFactory.getItem());
        this.reset();
        return btn;
    }

    private void reset(){
        this.itemFactory = itemStackService.build(Material.STONE);
    }
}
