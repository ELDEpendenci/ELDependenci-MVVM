package org.eldependenci.mvvm.button;

import org.bukkit.inventory.ItemStack;
import org.eldependenci.mvvm.view.UIButton;

public record UIButtonItem(ItemStack item) implements UIButton {
}
