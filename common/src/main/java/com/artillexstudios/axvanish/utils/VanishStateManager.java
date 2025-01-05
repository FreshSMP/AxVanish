package com.artillexstudios.axvanish.utils;

import com.artillexstudios.axvanish.AxVanishPlugin;
import com.artillexstudios.axvanish.api.AxVanishAPI;
import com.artillexstudios.axvanish.api.users.User;
import org.bukkit.entity.Player;

import java.util.List;

public final class VanishStateManager {
    private final AxVanishPlugin plugin;

    public VanishStateManager(AxVanishPlugin plugin) {
        this.plugin = plugin;
    }

    public void updateViewers(User user, boolean current) {
        Player player = user.onlinePlayer();
        if (player == null) {
            return;
        }

        player.setVisibleByDefault(!current);
        List<User> onlineUsers = AxVanishAPI.instance().online();
        for (User online : onlineUsers) {
            Player onlinePlayer = online.onlinePlayer();
            if (onlinePlayer == null) {
                continue;
            }

            // We want to be sure that only people who can see the player
            // can see the player. This is not a mistake
            if (online.canSee(user)) {
                onlinePlayer.showPlayer(this.plugin, player);
            } else {
                onlinePlayer.hidePlayer(this.plugin, player);
            }

            if (user.canSee(online)) {
                player.showPlayer(this.plugin, onlinePlayer);
            } else {
                player.hidePlayer(this.plugin, onlinePlayer);
            }
        }
    }
}
