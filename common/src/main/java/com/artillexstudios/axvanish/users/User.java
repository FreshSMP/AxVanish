package com.artillexstudios.axvanish.users;

import com.artillexstudios.axapi.nms.NMSHandlers;
import com.artillexstudios.axvanish.api.context.VanishContext;
import com.artillexstudios.axvanish.api.context.source.ForceVanishSource;
import com.artillexstudios.axvanish.api.event.UserPreVanishStateChangeEvent;
import com.artillexstudios.axvanish.api.event.UserVanishStateChangeEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class User implements com.artillexstudios.axvanish.api.users.User {
    private boolean vanished;

    @Override
    public Player onlinePlayer() {
        return null;
    }

    @Override
    public OfflinePlayer player() {
        return null;
    }

    @Override
    public int priority() {
        return 0;
    }

    @Override
    public boolean update(boolean vanished, VanishContext context) {
        UserPreVanishStateChangeEvent event = new UserPreVanishStateChangeEvent(this, this.vanished, vanished, context);
        if (event.call() || context.getSource(ForceVanishSource.class) != null) {
            boolean prev = this.vanished;
            this.vanished = vanished;
            new UserVanishStateChangeEvent(this, prev, vanished, context).call();
        }

        return false;
    }

    @Override
    public boolean vanished() {
        return this.vanished;
    }

    @Override
    public boolean canSee(com.artillexstudios.axvanish.api.users.User user) {
        return this.priority() >= user.priority();
    }

    @Override
    public void message(Component message) {
        Player player = this.onlinePlayer();
        if (player == null) {
            return;
        }

        NMSHandlers.getNmsHandler().sendMessage(player, message);
    }
}
