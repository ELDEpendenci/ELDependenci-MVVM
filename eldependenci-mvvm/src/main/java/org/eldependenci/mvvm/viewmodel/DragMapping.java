package org.eldependenci.mvvm.viewmodel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 拖拽事件請求
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DragMapping {

    /**
     * 
     * @return 指定 pattern
     */
    char value();

}
