package com.artillexstudios.axvanish.placeholders;

import com.artillexstudios.axapi.placeholders.ParseContext;
import com.artillexstudios.axapi.placeholders.Placeholders;
import com.artillexstudios.axvanish.api.AxVanishAPI;
import org.bukkit.Bukkit;

public enum PlaceholderRegistry {
    INSTANCE;

    public void register() {
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
    }
}
