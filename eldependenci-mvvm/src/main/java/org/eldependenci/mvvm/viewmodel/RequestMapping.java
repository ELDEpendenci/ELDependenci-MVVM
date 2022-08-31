package org.eldependenci.mvvm.viewmodel;

import org.bukkit.event.inventory.InventoryInteractEvent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 界面事件挂鈎
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {

    /**
     * 指定 pattern
     * @return 指定 pattern
     */
    char pattern();

    /**
     * 指定事件
     * @return 事件
     */
    Class<? extends InventoryInteractEvent> event();

}
