package com.artillexstudios.axvanish.api.group.capabilities;

import com.artillexstudios.axvanish.api.event.UserVanishStateChangeEvent;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;

public final class FlightCapability extends VanishCapability implements Listener {

    public FlightCapability(Map<String, Object> config) {
        super(config);
    }

    @EventHandler
    public void onUserVanishStateChangeEvent(UserVanishStateChangeEvent event) {
        Player player = event.user().onlinePlayer();
        if (player == null) {
            return;
        }

        if (!event.user().hasCapability(VanishCapabilities.FLIGHT)) {
            return;
        }

        if (event.newState()) {
            player.setAllowFlight(true);

            if (!player.isFlying()) {
                player.setFlying(true);
            }
        } else {
            if (player.getGameMode() == GameMode.SPECTATOR || player.getGameMode() == GameMode.CREATIVE) {
                return;
            }

            player.setFlying(false);
            player.setAllowFlight(false);
        }
    }
}
