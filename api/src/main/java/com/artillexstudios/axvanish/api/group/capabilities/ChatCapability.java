package com.artillexstudios.axvanish.api.group.capabilities;

import com.artillexstudios.axvanish.api.AxVanishAPI;
import com.artillexstudios.axvanish.api.users.User;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Map;

public final class ChatCapability extends VanishCapability implements Listener {
    private final String bypassPrefix;

    public ChatCapability(String bypassPrefix) {
        super(null);
        this.bypassPrefix = bypassPrefix;
    }

    public ChatCapability(Map<String, Object> config) {
        this((String) config.getOrDefault("bypass-prefix", "!"));
    }

    public String bypassPrefix() {
        return this.bypassPrefix;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
        User user = AxVanishAPI.instance().userOrThrow(event.getPlayer());
        ChatCapability capability = user.capability(VanishCapabilities.CHAT);
        if (capability == null) {
            return;
        }

        String bypassPrefix = capability.bypassPrefix();
        if (bypassPrefix == null || bypassPrefix.isBlank()) {
            return;
        }

        if (event.getMessage().startsWith(bypassPrefix)) {
            event.setMessage(event.getMessage().substring(bypassPrefix.length()));
        } else {
            event.setCancelled(true);
        }
    }
}
