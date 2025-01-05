package com.artillexstudios.axvanish.listeners;

import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axvanish.api.AxVanishAPI;
import com.artillexstudios.axvanish.api.context.VanishContext;
import com.artillexstudios.axvanish.api.context.source.ForceVanishSource;
import com.artillexstudios.axvanish.api.context.source.JoinVanishSource;
import com.artillexstudios.axvanish.api.users.User;
import com.artillexstudios.axvanish.config.Language;
import com.artillexstudios.axvanish.exception.UserAlreadyLoadedException;
import com.artillexstudios.axvanish.users.Users;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class PlayerListener implements Listener {

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
            Component message = StringUtils.format(Language.prefix + Language.unVanish.hadNoVanishPermission);
            for (User vanished : AxVanishAPI.instance().vanished()) {
                vanished.message(message);
            }
            return;
        }

        user.update(user.vanished(), new VanishContext.Builder()
                .withSource(JoinVanishSource.INSTANCE)
                .build());

        if (user.vanished()) {
            event.setJoinMessage(null);
        }
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        User user = AxVanishAPI.instance().userOrThrow(player);

        if (user.vanished()) {
            event.setQuitMessage(null);
        }
    }
}
