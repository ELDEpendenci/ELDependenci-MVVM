package org.eldependenci.mvvm.view;

public interface UIContext {

    void add(UIButton... items);

    void set(int slot, UIButton item);

    void fill(UIButton item);

    UIButtonFactory createButton();

}
