package com.artillexstudios.axvanish.api.event;

import com.artillexstudios.axvanish.api.context.VanishContext;
import com.artillexstudios.axvanish.api.users.User;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class UserPreVanishStateChangeEvent extends VanishEvent implements Cancellable {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final VanishContext context;
    private final boolean currentState;
    private final boolean newState;
    private boolean cancelled = false;

    public UserPreVanishStateChangeEvent(User user, boolean currentState, boolean newState, VanishContext context) {
        super(user);
        this.currentState = currentState;
        this.newState = newState;
        this.context = context;
    }

    @Nullable
    public VanishContext context() {
        return this.context;
    }

    public boolean currentState() {
        return this.currentState;
    }

    public boolean newState() {
        return this.newState;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
