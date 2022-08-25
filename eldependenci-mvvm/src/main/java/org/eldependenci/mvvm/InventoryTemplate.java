package org.eldependenci.mvvm;

import com.ericlam.mc.eld.components.GroupConfiguration;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryTemplate extends GroupConfiguration {

    public String title;

    public List<String> patterns;

    public Map<String, ItemDescriptor> items = new HashMap<>();

    public static class ItemDescriptor {

        public Material material = Material.AIR;

        public String name = "";

        public int amount = 1;

        public List<String> lore = new ArrayList<>();

        public int data = 0;

        public boolean cancelMove = true;

    }
}
