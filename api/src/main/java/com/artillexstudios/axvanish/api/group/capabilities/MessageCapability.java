package com.artillexstudios.axvanish.api.group.capabilities;

import org.bukkit.event.Listener;

import java.util.Map;

public final class MessageCapability extends VanishCapability implements Listener {

    public MessageCapability(Map<String, Object> config) {
        super(config);
    }
}
