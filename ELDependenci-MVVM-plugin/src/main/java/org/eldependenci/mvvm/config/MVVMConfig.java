package org.eldependenci.mvvm.config;

import com.ericlam.mc.eld.annotations.Resource;
import com.ericlam.mc.eld.components.Configuration;

@Resource(locate = "config.yml")
public class MVVMConfig extends Configuration {

    public boolean showDemo;

}
