package com.artillexstudios.axvanish.api.users;

import com.artillexstudios.axvanish.api.context.VanishContext;
import com.artillexstudios.axvanish.api.context.VanishSource;
import com.artillexstudios.axvanish.api.group.Group;
import com.artillexstudios.axvanish.api.group.capabilities.VanishCapabilities;
import com.artillexstudios.axvanish.api.group.capabilities.VanishCapability;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public interface User extends VanishSource {

    Player onlinePlayer();

    OfflinePlayer player();

    Group group();

    default <T extends VanishCapability> T capability(Class<T> capability) {
        return this.vanished() ? this.group().capability(capability) : null;
    }

    default <T extends VanishCapability> T capability(String capability) {
        return this.vanished() ? this.group().capability(VanishCapabilities.parse(capability)) : null;
    }

    default boolean hasCapability(String capability) {
        return this.capability(capability) != null;
    }

    default <T extends VanishCapability> boolean hasCapability(Class<T> capability) {
        return this.capability(capability) != null;
    }

    default boolean update(boolean vanished) {
        return this.update(vanished, null);
    }

    boolean update(boolean vanished, VanishContext context);

    boolean vanished();

    boolean canSee(User user);

    void message(Component message);

    void cancelMessage();
}
