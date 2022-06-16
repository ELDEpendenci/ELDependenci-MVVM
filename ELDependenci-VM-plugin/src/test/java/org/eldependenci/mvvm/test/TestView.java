package org.eldependenci.mvvm.test;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.eldependenci.mvvm.*;

public class TestView {


    interface MyStateHolder extends StateHolder {

        void setName(String name);

        String getName();

        void setAge(int age);

        int getAge();

    }

    @ForView(MyView.class)
    static class ViewModel {

        private static final String[] SELECTIONS = {"John", "Jason", "Anson", "Apple"};

        private int selector = 0;

        @State
        private MyStateHolder stateHolder;

        @PostConstruct
        public void init(){
            stateHolder.setName("John");
            stateHolder.setAge(30);
        }


        @ClickMapping('A')
        public void onNameClick(InventoryClickEvent event){
            stateHolder.setName(SELECTIONS[selector++]);
            if(selector >= SELECTIONS.length){
                selector = 0;
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
    }


    @View(
            title = "MyView",
            patterns = {
                    "XXAXXXBXX",
                    "XXXXCXXXX"
            }
    )
    static class MyView implements UIView {


        @Override
        public void init(UIContext context) {
        }

        @RenderView(value = 'A')
        public void renderName(UIContext ctx, @StateValue("name") String name) {
            var btn = ctx.createButton();
            ctx.add(
                    btn.decorate(f -> f.material(Material.REDSTONE).display("&aName: &f" + name))
            );
        }

        @RenderView(value = 'B')
        public void renderAge(UIContext ctx, @StateValue("age") int age) {
            var btn = ctx.createButton();
            ctx.add(
                    btn.decorate(f -> f.material(Material.REDSTONE).display("&eAge: &f" + age))
            );
        }

        @RenderView('C')
        public void renderButton(UIContext ctx){
            var btn = ctx.createButton();
            ctx.add(
                    btn.decorate(f -> f.material(Material.REDSTONE).display("&bSUBMIT"))
            );
        }


    }
}
