package com.artillexstudios.axvanish.utils;

import com.artillexstudios.axapi.utils.LogUtils;
import com.artillexstudios.axvanish.api.AxVanishAPI;
import com.artillexstudios.axvanish.api.users.User;
import com.artillexstudios.axvanish.config.Config;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class VanishStateManager {
    private final JavaPlugin plugin;

    public VanishStateManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void updateViewers(User user, boolean current) {
        ThreadUtils.ensureMain("updateViewers can only be called from the main thread!");
        Player player = user.onlinePlayer();
        if (player == null) {
            return;
        }

        if (Config.debug) {
            LogUtils.debug("Called updateViewers for user {}, current: {}", player.getName(), current);
        }

        if (current ^ !player.isVisibleByDefault()) {
            if (Config.debug) {
                LogUtils.debug("Vanish state changed!");
            }
            if (current) {
                user.onlinePlayer().setMetadata("vanished", new FixedMetadataValue(this.plugin, true));
            } else {
                user.onlinePlayer().removeMetadata("vanished", this.plugin);
            }
            player.setVisibleByDefault(!current);
        }
        List<User> onlineUsers = AxVanishAPI.instance().online();
        for (User online : onlineUsers) {
            Player onlinePlayer = online.onlinePlayer();
            if (onlinePlayer == null) {
                continue;
            }

            // We want to be sure that only people who can see the player
            // can see the player. This is not a mistake
            if (Config.debug) {
                LogUtils.debug("Can {} see {}: {}", onlinePlayer.getName(), player.getName(), online.canSee(user));
            }
            if (online.canSee(user)) {
                onlinePlayer.showPlayer(this.plugin, player);
            } else {
                onlinePlayer.hidePlayer(this.plugin, player);
            }

            if (Config.debug) {
                LogUtils.debug("Can {} see {}: {}", player.getName(), onlinePlayer.getName(), user.canSee(online));
            }
            if (user.canSee(online)) {
                player.showPlayer(this.plugin, onlinePlayer);
            } else {
                player.hidePlayer(this.plugin, onlinePlayer);
            }
        }
    }
}
