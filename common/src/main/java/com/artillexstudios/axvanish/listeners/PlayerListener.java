package com.artillexstudios.axvanish.listeners;

import com.artillexstudios.axapi.scheduler.Scheduler;
import com.artillexstudios.axapi.utils.FileLogger;
import com.artillexstudios.axvanish.api.AxVanishAPI;
import com.artillexstudios.axvanish.api.context.VanishContext;
import com.artillexstudios.axvanish.api.context.source.DisconnectVanishSource;
import com.artillexstudios.axvanish.api.context.source.ForceVanishSource;
import com.artillexstudios.axvanish.api.context.source.JoinVanishSource;
import com.artillexstudios.axvanish.api.users.User;
import com.artillexstudios.axvanish.config.Language;
import com.artillexstudios.axvanish.exception.UserAlreadyLoadedException;
import com.artillexstudios.axvanish.users.Users;
import com.artillexstudios.axvanish.utils.PermissionUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class PlayerListener implements Listener {
    private final FileLogger logger = new FileLogger("join-logs");
    private final JavaPlugin plugin;

    public PlayerListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAsyncPlayerPreLoginEvent(AsyncPlayerPreLoginEvent event) throws UserAlreadyLoadedException {
        if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            this.logger.log("User %s AsyncPlayerPreLoginEvent was cancelled!".formatted(event.getName()));
            return;
        }

        this.logger.log("User %s AsyncPlayerPreLoginEvent!".formatted(event.getName()));

        Users.loadUser(event.getUniqueId()).thenAcceptAsync(user -> this.logger.log("User %s AsyncPlayerPreLoginEvent finished!".formatted(event.getName()))).exceptionally(ex -> {
            if (ex.getCause() instanceof UserAlreadyLoadedException) {
                this.logger.log("UserAlreadyLoadedException for user: %s. How did this happen?".formatted(event.getName()));
            } else {
                this.logger.log("Failed to load user %s during AsyncPlayerPreLoginEvent.".formatted(event.getName()));
            }
            return null;
        });
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        this.logger.log("User join: %s.".formatted(event.getPlayer().getName()));
        User user = AxVanishAPI.instance().userOrThrow(player);
        ((com.artillexstudios.axvanish.users.User) user).onlinePlayer(player);
        ((com.artillexstudios.axvanish.users.User) user).group(PermissionUtils.INSTANCE.group(player));
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
        User user = Users.disconnect(player.getUniqueId());
        this.logger.log("User disconnect: %s.".formatted(event.getPlayer().getName()));

        if (user == null) {
            return;
        }

        user.update(user.vanished(), new VanishContext.Builder()
                .withSource(DisconnectVanishSource.INSTANCE)
                .build()
        );

        if (user.vanished()) {
            player.removeMetadata("vanished", this.plugin);
            event.setQuitMessage(null);
        }

        ((com.artillexstudios.axvanish.users.User) user).onlinePlayer(null);
    }
}
