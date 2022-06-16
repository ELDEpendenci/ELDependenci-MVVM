package org.eldependenci.mvvm.viewmodel;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Function;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ClickMapping {

    char value();

    LOGIC logic() default LOGIC.AND;

    ClickType[] clickTypes() default {};

    InventoryAction[] inventoryActions() default {};

    Class<? extends Function<InventoryClickEvent, Boolean>>[] filters() default {};

    enum LOGIC {
        AND, OR
    }

}
