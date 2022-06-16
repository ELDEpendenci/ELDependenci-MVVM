package org.eldependenci.mvvm;

public interface UIContext {

    void add(UIButton... item);

    void set(int slot, UIButton item);

    void fill(UIButton item);

    UIButton createButton();

}
