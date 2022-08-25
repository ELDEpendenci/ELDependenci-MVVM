package org.eldependenci.mvvm.config;

import com.ericlam.mc.eld.annotations.Prefix;
import com.ericlam.mc.eld.annotations.Resource;
import com.ericlam.mc.eld.components.LangConfiguration;

@Prefix(path = "prefix")
@Resource(locate = "lang.yml")
public class MVVMLang extends LangConfiguration {
}
