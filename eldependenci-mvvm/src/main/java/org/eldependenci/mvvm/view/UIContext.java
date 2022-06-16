package org.eldependenci.mvvm.view;

public interface UIContext {

    void add(UIButtonFactory... item);

    void set(int slot, UIButtonFactory item);

    void fill(UIButtonFactory item);

    UIButtonFactory createButton();

}
