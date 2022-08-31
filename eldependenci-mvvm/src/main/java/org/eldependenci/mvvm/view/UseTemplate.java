package org.eldependenci.mvvm.view;

import org.eldependenci.mvvm.InventoryTemplate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 使用界面模板
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface UseTemplate {

    /**
     * 模板 ID
     * @return 模板 ID
     */
    String template();

    /**
     * 模板類別
     * @return 模板類別
     */
    Class<? extends InventoryTemplate> groupResource();

}
