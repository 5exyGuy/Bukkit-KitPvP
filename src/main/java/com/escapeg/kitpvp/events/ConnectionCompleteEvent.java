package com.escapeg.kitpvp.events;

import com.escapeg.kitpvp.extenders.PlayerExtended;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public final class ConnectionCompleteEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();
    private final PlayerExtended playerExtended;

    public ConnectionCompleteEvent(final PlayerExtended playerExtended) {
        this.playerExtended = playerExtended;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public PlayerExtended getPlayerExtended() {
        return this.playerExtended;
    }

}
