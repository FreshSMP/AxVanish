package com.artillexstudios.axvanish.api.event;

import com.artillexstudios.axvanish.api.users.User;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public abstract class VanishEvent extends Event {
    private final User user;

    protected VanishEvent(User user) {
        this.user = user;
    }

    public User user() {
        return this.user;
    }

    public boolean call() {
        Bukkit.getPluginManager().callEvent(this);
        if (this instanceof Cancellable cancellable) {
            return !cancellable.isCancelled();
        }

        return true;
    }
}
