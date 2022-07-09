package org.eldependenci.mvvm.demo.time;

import org.bukkit.Material;
import org.eldependenci.mvvm.demo.DemoTemplate;
import org.eldependenci.mvvm.model.StateValue;
import org.eldependenci.mvvm.view.*;

@UseTemplate(template = "time", groupResource = DemoTemplate.class)
public class TimeView implements View {

    @RenderView('B')
    public void renderCurrentTime(UIContext ctx, @StateValue("time") String time) {
        UIButtonFactory btn = ctx.createButton();
        ctx.add(
                btn.decorate(f -> f.material(Material.CLOCK)
                        .display("&e目前時間")
                        .lore("&7" + time))
                        .create()
        );
    }

    @RenderView('C')
    public void renderDuration(UIContext ctx, @StateValue("duration") long duration){
        UIButtonFactory btn = ctx.createButton();
        ctx.add(
                btn.decorate(f -> f.material(Material.REDSTONE)
                        .display("&e你在此界面的逗留時間: ")
                        .lore("&7" + duration + "秒"))
                        .create()
        );
    }
}
