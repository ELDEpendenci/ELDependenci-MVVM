package org.eldependenci.mvvm.demo.profile;

import org.eldependenci.mvvm.model.StateHolder;

public interface ProfileStateHolder extends StateHolder {

    void setName(String name);

    void setAge(int age);

    int getAge();

    String getName();

}
