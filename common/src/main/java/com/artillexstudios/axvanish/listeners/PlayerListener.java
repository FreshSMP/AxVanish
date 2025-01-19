package com.artillexstudios.axvanish.listeners;

import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axvanish.AxVanishPlugin;
import com.artillexstudios.axvanish.api.AxVanishAPI;
import com.artillexstudios.axvanish.api.context.VanishContext;
import com.artillexstudios.axvanish.api.context.source.DisconnectVanishSource;
import com.artillexstudios.axvanish.api.context.source.ForceVanishSource;
import com.artillexstudios.axvanish.api.context.source.JoinVanishSource;
import com.artillexstudios.axvanish.api.users.User;
import com.artillexstudios.axvanish.config.Language;
import com.artillexstudios.axvanish.exception.UserAlreadyLoadedException;
import com.artillexstudios.axvanish.users.Users;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class PlayerListener implements Listener {
    private final AxVanishPlugin plugin;

    public PlayerListener(AxVanishPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAsyncPlayerPreLoginEvent(AsyncPlayerPreLoginEvent event) {
        try {
            User user = Users.loadUser(event.getUniqueId()).join();
        } catch (UserAlreadyLoadedException exception) {
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(StringUtils.formatToString(Language.prefix + Language.error.failedToLoadUserData));
        }
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        User user = AxVanishAPI.instance().userOrThrow(player);
        if (user.vanished() && !player.hasPermission("axvanish.vanish")) {
            user.update(false, new VanishContext.Builder()
                    .withSource(JoinVanishSource.INSTANCE)
                    .withSource(ForceVanishSource.INSTANCE)
                    .build()
            );

            AxVanishAPI.instance().broadcast(user, Language.prefix + Language.unVanish.hadNoVanishPermission);
            return;
        }

        user.update(user.vanished(), new VanishContext.Builder()
                .withSource(JoinVanishSource.INSTANCE)
                .build()
        );

        event.setJoinMessage(null);
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        User user = AxVanishAPI.instance().userOrThrow(player);

        user.update(user.vanished(), new VanishContext.Builder()
                .withSource(DisconnectVanishSource.INSTANCE)
                .build()
        );
        if (user.vanished()) {
            player.removeMetadata("vanished", this.plugin);
            event.setQuitMessage(null);
        }
    }
}
