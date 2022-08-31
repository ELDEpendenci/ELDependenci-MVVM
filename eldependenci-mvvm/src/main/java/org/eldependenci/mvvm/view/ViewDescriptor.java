package org.eldependenci.mvvm.view;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 界面描述
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ViewDescriptor {

    /**
     * 界面標題
     * @return 界面標題
     */
    String title();

    /**
     * 界面 pattern
     * @return patterns
     */
    String[] patterns();

}
