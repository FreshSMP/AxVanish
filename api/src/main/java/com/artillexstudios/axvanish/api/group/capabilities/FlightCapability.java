package com.artillexstudios.axvanish.api.group.capabilities;

import com.artillexstudios.axvanish.api.event.UserVanishStateChangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;

public final class FlightCapability extends VanishCapability implements Listener {

    public FlightCapability(Map<String, Object> config) {
        super(config);
    }

    @EventHandler
    public void onUserVanishStateChangeEvent(UserVanishStateChangeEvent event) {
        if (!event.user().hasCapability(VanishCapabilities.FLIGHT)) {
            return;
        }

        if (event.newState()) {
            event.user().onlinePlayer().setAllowFlight(true);

            if (!event.user().onlinePlayer().isFlying()) {
                event.user().onlinePlayer().setFlying(true);
            }
        } else {
            event.user().onlinePlayer().setAllowFlight(false);
        }
    }
}
