package com.artillexstudios.axvanish.api.group.capabilities;

import com.artillexstudios.axapi.nms.wrapper.WrapperRegistry;
import com.artillexstudios.axapi.reflection.ClassUtils;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axvanish.api.context.source.DisconnectVanishSource;
import com.artillexstudios.axvanish.api.context.source.JoinVanishSource;
import com.artillexstudios.axvanish.api.event.UserVanishStateChangeEvent;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;

public final class MessageCapability extends VanishCapability implements Listener {
    private final String joinMessage;
    private final String leaveMessage;

    public MessageCapability(String joinMessage, String leaveMessage) {
        super(null);
        this.joinMessage = joinMessage;
        this.leaveMessage = leaveMessage;
    }

    public MessageCapability(Map<String, Object> config) {
        this((String) config.get("join"), (String) config.get("leave"));
    }

    @EventHandler
    public void onUserVanishStateChangeEvent(UserVanishStateChangeEvent event) {
        Player onlinePlayer = event.user().onlinePlayer();
        MessageCapability capability = event.user().capability(VanishCapabilities.MESSAGE);
        if (capability == null) {
            return;
        }

        if (event.context() == null) {
            return;
        }

        if (event.context().getSource(JoinVanishSource.class) != null || event.context().getSource(DisconnectVanishSource.class) != null) {
            return;
        }

        if (event.newState() == event.currentState()) {
            return;
        }

        String message = event.newState() ? capability.joinMessage : capability.leaveMessage;
        if (message == null || message.isBlank()) {
            return;
        }

        if (ClassUtils.INSTANCE.classExists("me.clip.placeholderapi.PlaceholderAPI")) {
            message = PlaceholderAPI.setPlaceholders(onlinePlayer == null ? event.user().player() : onlinePlayer, message);
        }

        Component formatted = StringUtils.format(message);
        for (Player player : Bukkit.getOnlinePlayers()) {
            WrapperRegistry.SERVER_PLAYER.map(player).message(formatted);
        }
    }
}
