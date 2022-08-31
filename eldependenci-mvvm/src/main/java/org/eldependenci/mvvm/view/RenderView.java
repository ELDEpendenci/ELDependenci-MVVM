package org.eldependenci.mvvm.view;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 局部渲染界面
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RenderView {

    /**
     * 指定 pattern
     * @return 指定 pattern
     */
    char value();

    /**
     * 取消移動, 默認為 true
     * @return 是否取消移動
     */
    boolean cancelMove() default true;

}
