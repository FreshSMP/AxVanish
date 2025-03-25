package com.artillexstudios.axvanish.placeholders;

import com.artillexstudios.axapi.placeholders.ParseContext;
import com.artillexstudios.axapi.placeholders.Placeholders;
import com.artillexstudios.axvanish.api.AxVanishAPI;
import com.artillexstudios.axvanish.api.users.User;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public enum PlaceholderRegistry {
    INSTANCE;

    public void register() {
        Placeholders.registerTransformer(OfflinePlayer.class, User.class, player -> {
            return AxVanishAPI.instance().getUserIfLoadedImmediately(player);
        });

        Placeholders.register("online", ctx -> {
            return Integer.toString(Bukkit.getOnlinePlayers().size() - AxVanishAPI.instance().vanished().size());
        }, ParseContext.PLACEHOLDER_API);

        Placeholders.register("vanished", ctx -> {
            return Integer.toString(AxVanishAPI.instance().vanished().size());
        }, ParseContext.PLACEHOLDER_API);

        Placeholders.register("vanished_players", ctx -> {
            return String.join(", ", AxVanishAPI.instance().vanished()
                    .stream()
                    .map(user -> {
                        return user.onlinePlayer().getName();
                    })
                    .toList()
            );
        }, ParseContext.PLACEHOLDER_API);

        Placeholders.register("state", ctx -> {
            User user = ctx.resolve(User.class);
            return Boolean.toString(user.vanished());
        }, ParseContext.PLACEHOLDER_API);
    }
}
