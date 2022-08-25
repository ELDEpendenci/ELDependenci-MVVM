package org.eldependenci.mvvm.demo.profile;

import org.bukkit.Material;
import org.eldependenci.mvvm.demo.DemoTemplate;
import org.eldependenci.mvvm.model.StateValue;
import org.eldependenci.mvvm.view.RenderView;
import org.eldependenci.mvvm.view.UIContext;
import org.eldependenci.mvvm.view.UseTemplate;
import org.eldependenci.mvvm.view.View;


@UseTemplate(template = "profile", groupResource = DemoTemplate.class)
public class ProfileView implements View {

    @RenderView('B')
    public void renderName(UIContext context, @StateValue("name") String name) {
        var btn = context.createButton();
        context.add(
                btn.decorate(f -> f.material(Material.PAPER)
                        .display("&e你的名稱")
                        .lore(
                                "&7" + name,
                                "&b點擊修改"
                        )).create()
        );
    }

    @RenderView('C')
    public void renderAge(UIContext context, @StateValue("age") int age) {
        var btn = context.createButton();
        context.add(
                btn.decorate(f -> f.material(Material.TORCH)
                        .display("&e你的年齡")
                        .lore(
                                "&7" + age,
                                "&b左鍵新增",
                                "&b右鍵減少"
                        )).create()
        );
    }

}
