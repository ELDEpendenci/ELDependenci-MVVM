package org.eldependenci.mvvm.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 狀態屬性標注
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface StateValue {

    /**
     * 狀態屬性的 key
     * @return 狀態屬性 key
     */
    String value();

}
