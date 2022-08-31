package org.eldependenci.mvvm.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 初始屬性標注
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface PropValue {
 
    /**
     * 
     * @return 初始屬性
     */
    String value();

    /**
     * 是否可選，默認為 false
     * @return 是否為可選
     */
    boolean optional() default false;

}
