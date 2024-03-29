package org.eldependenci.mvvm.demo;

import com.ericlam.mc.eld.annotations.GroupResource;
import org.eldependenci.mvvm.InventoryTemplate;

@GroupResource(
        folder = "Demo",
        preloads = { "profile", "time" }
)
public final class DemoTemplate extends InventoryTemplate {
}
