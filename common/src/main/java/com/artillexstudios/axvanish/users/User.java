package com.artillexstudios.axvanish.users;

import com.artillexstudios.axapi.nms.NMSHandlers;
import com.artillexstudios.axvanish.AxVanishPlugin;
import com.artillexstudios.axvanish.api.context.VanishContext;
import com.artillexstudios.axvanish.api.context.source.ForceVanishSource;
import com.artillexstudios.axvanish.api.event.UserPreVanishStateChangeEvent;
import com.artillexstudios.axvanish.api.event.UserVanishStateChangeEvent;
import com.artillexstudios.axvanish.api.group.Group;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public final class User implements com.artillexstudios.axvanish.api.users.User {
    private OfflinePlayer offlinePlayer;
    private Group group;
    private Player onlinePlayer;
    private boolean vanished;

    public User(OfflinePlayer player, Player onlinePlayer, Group group) {
        this.onlinePlayer = onlinePlayer;
        this.offlinePlayer = player;
        this.group = group;
    }

    @Override
    public Player onlinePlayer() {
        return this.onlinePlayer;
    }

    @Override
    public OfflinePlayer player() {
        return this.offlinePlayer;
    }

    @Override
    public Group group() {
        return this.group;
    }

    @Override
    public boolean update(boolean vanished, VanishContext context) {
        boolean prev = this.vanished;
        UserPreVanishStateChangeEvent event = new UserPreVanishStateChangeEvent(this, this.vanished, vanished, context);
        if (event.call() || context.getSource(ForceVanishSource.class) != null) {
            this.vanished = vanished;
            new UserVanishStateChangeEvent(this, prev, vanished, context).call();
            AxVanishPlugin.instance().stateManager().updateViewers(this, this.vanished);
            return true;
        }

        return false;
    }

    @Override
    public boolean vanished() {
        return this.vanished;
    }

    @Override
    public boolean canSee(com.artillexstudios.axvanish.api.users.User user) {
        if (this.group() == null || user.group() == null) {
            return false;
        }

        return this.group().priority() >= user.group().priority();
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
