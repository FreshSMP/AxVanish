package com.artillexstudios.axvanish.api.users;

import com.artillexstudios.axvanish.api.context.VanishContext;
import com.artillexstudios.axvanish.api.context.VanishSource;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public interface User extends VanishSource {

    Player onlinePlayer();

    OfflinePlayer player();

    int priority();

    default boolean update(boolean vanished) {
        return this.update(vanished, null);
    }

    boolean update(boolean vanished, VanishContext context);

    boolean vanished();

    boolean canSee(User user);

    void message(Component message);
}
