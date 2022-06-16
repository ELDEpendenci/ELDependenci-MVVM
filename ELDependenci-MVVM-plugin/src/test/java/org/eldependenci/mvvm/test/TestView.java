package org.eldependenci.mvvm.test;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.eldependenci.mvvm.model.State;
import org.eldependenci.mvvm.model.StateHolder;
import org.eldependenci.mvvm.model.StateValue;
import org.eldependenci.mvvm.view.RenderView;
import org.eldependenci.mvvm.view.UIContext;
import org.eldependenci.mvvm.view.View;
import org.eldependenci.mvvm.view.ViewDescriptor;
import org.eldependenci.mvvm.viewmodel.*;

public class TestView {


    interface MyStateHolder{

        void setName(String name);

        String getName();

        void setAge(int age);

        int getAge();

    }

    @ViewModelBinding(MyView.class)
    static class MyViewModel implements ViewModel {

        private static final String[] SELECTIONS = {"John", "Jason", "Anson", "Apple"};

        private int selector = 0;

        @Context
        private ViewModelContext viewModelContext;

        @State
        private MyStateHolder stateHolder;

        @ClickMapping('A')
        public void onNameClick(InventoryClickEvent event){
            if (event.getClick() != ClickType.MIDDLE){
                stateHolder.setName(SELECTIONS[selector++]);
                if(selector >= SELECTIONS.length){
                    selector = 0;
                }
            }else{
                event.getWhoClicked().sendMessage("please enter your name.");
                viewModelContext.observeEvent(AsyncChatEvent.class, 200L, e -> {
                    var input = ((TextComponent)e.message()).content();
                    stateHolder.setName(input);
                });
            }
        }

        @ClickMapping('B')
        public void onClickAge(InventoryClickEvent e) {
            if (e.isLeftClick()) {
                stateHolder.setAge(stateHolder.getAge() + 1);
            } else if (e.isRightClick()) {
                stateHolder.setAge(stateHolder.getAge() - 1);
            }
        }

        @ClickMapping('C')
        public void onSubmit(InventoryClickEvent e) {
            e.getWhoClicked().sendMessage("Name: " + stateHolder.getName());
            e.getWhoClicked().sendMessage("Age: " + stateHolder.getAge());
        }

        @Override
        public void initState() {
            stateHolder.setName("John");
            stateHolder.setAge(30);
        }

    }


    @ViewDescriptor(
            title = "MyView",
            patterns = {
                    "XXAXXXBXX",
                    "XXXXCXXXX"
            }
    )
    static class MyView implements View {

        @RenderView(value = 'A')
        public void renderName(UIContext ctx, @StateValue("name") String name) {
            var btn = ctx.createButton();
            ctx.add(
                    btn.decorate(f -> f.material(Material.REDSTONE).display("&aName: &f" + name)).create()
            );
        }

        @RenderView(value = 'B')
        public void renderAge(UIContext ctx, @StateValue("age") int age) {
            var btn = ctx.createButton();
            ctx.add(
                    btn.decorate(f -> f.material(Material.REDSTONE).display("&eAge: &f" + age)).create()
            );
        }

        @RenderView('C')
        public void renderButton(UIContext ctx){
            var btn = ctx.createButton();
            ctx.add(
                    btn.decorate(f -> f.material(Material.REDSTONE).display("&bSUBMIT")).create()
            );
        }


    }
}
