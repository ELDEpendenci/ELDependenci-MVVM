package org.eldependenci.mvvm.viewmodel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 點擊事件挂鈎
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ClickMapping {

    /**
     * 
     * @return 指定 pattern
     */
    char value();

}
