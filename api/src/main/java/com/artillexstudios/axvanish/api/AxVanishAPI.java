package com.artillexstudios.axvanish.api;

import com.artillexstudios.axapi.AxPlugin;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axvanish.api.users.User;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.util.Services;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface AxVanishAPI {

    default User getUserIfLoadedImmediately(OfflinePlayer offlinePlayer) {
        return this.getUserIfLoadedImmediately(offlinePlayer.getUniqueId());
    }

    default User userOrThrow(UUID uuid) {
        User user = this.getUserIfLoadedImmediately(uuid);
        if (user == null) {
            throw new IllegalStateException();
        }

        return user;
    }

    default User userOrThrow(OfflinePlayer player) {
        return this.userOrThrow(player.getUniqueId());
    }

    User getUserIfLoadedImmediately(UUID uuid);

    /**
     * Get a user by their uuid, even if the player isn't online at the time
     * @param uuid The uuid of the player
     * @return A completablefuture of a user
     */
    CompletableFuture<User> user(UUID uuid);

    /**
     * Get the online users
     * @return A list of online users. This is a copy of the list, modifying this does not change anything.
     */
    List<User> online();

    /**
     * Get the vanished users
     * @return A list of the vanished users. This is a copy of the list, modifying this does not change anything.
     */
    List<User> vanished();

    AxPlugin plugin();

    default void broadcast(User user, String message) {
        this.broadcast(user, "", message);
    }

    default void broadcast(User user, String prefix, String message, TagResolver... resolvers) {
        if (message.isBlank()) {
            return;
        }

        Component formatted = StringUtils.format(prefix + message, resolvers);
        for (User vanished : this.online()) {
            if (vanished.canSee(user)) {
                user.message(formatted);
            }
        }
    }

    static AxVanishAPI instance() {
        return Holder.INSTANCE;
    }

    class Holder {
        private static final AxVanishAPI INSTANCE = Services.service(AxVanishAPI.class).orElseThrow();
    }
}
