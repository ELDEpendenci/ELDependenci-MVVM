package org.eldependenci.mvvm.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 狀態標注
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface State {

    /**
     * 是否改用手動更新 (不啓用自動更新)
     * @return 是否改用手動更新
     */
    boolean manual() default false;

}
