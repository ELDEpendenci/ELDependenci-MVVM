package org.eldependenci.mvvm.viewmodel;

import org.eldependenci.mvvm.view.View;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 綁定界面與界面模型
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ViewModelBinding {

    /**
     * 界面綁定
     * @return 界面類別
     */
    Class<? extends View> value();

}
