package com.artillexstudios.axvanish.api;

import com.artillexstudios.axapi.AxPlugin;
import com.artillexstudios.axvanish.api.users.User;
import com.artillexstudios.axvanish.users.Users;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class AxVanishAPIImpl implements AxVanishAPI {

    @Override
    public User getUserIfLoadedImmediately(UUID uuid) {
        return Users.getUserIfLoadedImmediately(uuid);
    }

    @Override
    public CompletableFuture<User> user(UUID uuid) {
        return Users.getUser(uuid, LoadContext.TEMPORARY);
    }

    @Override
    public List<User> online() {
        return Users.online();
    }

    @Override
    public List<User> vanished() {
        return Users.vanished();
    }

    @Override
    public JavaPlugin plugin() {
        return AxPlugin.getPlugin();
    }
}
