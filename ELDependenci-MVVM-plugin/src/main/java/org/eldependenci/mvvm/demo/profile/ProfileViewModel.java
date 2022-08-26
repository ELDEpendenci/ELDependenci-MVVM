package org.eldependenci.mvvm.demo.profile;

import com.google.inject.Inject;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.TextComponent;

import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.eldependenci.mvvm.model.State;
import org.eldependenci.mvvm.viewmodel.*;


@ViewModelBinding(ProfileView.class)
public class ProfileViewModel implements ViewModel {

    @Inject
    private ProfileService profileService;

    @Context
    private ViewModelContext context;
    @State
    private ProfileStateHolder profileState;

    @Override
    public void init(Player player, Map<String, Object> props) {
        var profile = profileService.getProfile(player.getUniqueId());
        if (profile == null){
            profileState.setName(player.getName());
            profileState.setAge(player.getLevel());
        }else{
            profileState.setName(profile.name());
            profileState.setAge(profile.age());
        }
    }

    @ClickMapping('B')
    public void onClickName(InventoryClickEvent e){
        var player = (Player)e.getWhoClicked();
        player.sendMessage("Please input the name.");
        context.observeEvent(AsyncChatEvent.class, 200L, ee -> {
            var name = ((TextComponent)ee.message()).content();
            profileState.setName(name);
        });
    }


    @ClickMapping('C')
    public void onClickAge(InventoryClickEvent e){
        if (e.isLeftClick()){
            profileState.setAge(profileState.getAge() + 1);
        }else if (e.isRightClick()){
            profileState.setAge(profileState.getAge() - 1);
        }

    }

    @ClickMapping('W')
    public void onSubmit(InventoryClickEvent e){
        var player = (Player)e.getWhoClicked();
        var profile = new ProfileService.Profile(profileState.getName(), profileState.getAge());
        profileService.setProfile(player.getUniqueId(), profile);
        player.sendMessage("Profile updated.");
        player.sendMessage("Name: " + profileState.getName());
        player.sendMessage("Age: " + profileState.getAge());
        player.closeInventory();
    }



}
