package org.eldependenci.mvvm.demo.profile;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ProfileService {

    public record Profile(String name, int age){
    }

    private final Map<UUID, Profile> profiles = new ConcurrentHashMap<>();


    public Profile getProfile(UUID uuid) {
        return profiles.get(uuid);
    }

    public void setProfile(UUID uuid, Profile profile) {
        profiles.put(uuid, profile);
    }
}
