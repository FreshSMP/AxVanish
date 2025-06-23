package com.artillexstudios.axvanish.placeholders;

import com.artillexstudios.axapi.placeholders.PlaceholderHandler;
import com.artillexstudios.axvanish.api.AxVanishAPI;
import com.artillexstudios.axvanish.api.users.User;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public enum PlaceholderRegistry {
    INSTANCE;

    public void register() {
        PlaceholderHandler.registerTransformer(OfflinePlayer.class, User.class, player -> AxVanishAPI.instance().getUserIfLoadedImmediately(player));

        PlaceholderHandler.register("online", ctx -> Integer.toString(Bukkit.getOnlinePlayers().size() - AxVanishAPI.instance().vanished().size()));

        PlaceholderHandler.register("vanished", ctx -> Integer.toString(AxVanishAPI.instance().vanished().size()));

        PlaceholderHandler.register("vanished_players", ctx -> String.join(", ", AxVanishAPI.instance().vanished()
                .stream()
                .map(user -> user.onlinePlayer().getName())
                .toList()
        ));

        PlaceholderHandler.register("state", ctx -> {
            User user = ctx.resolve(User.class);
            return Boolean.toString(user.vanished());
        });
    }
}
