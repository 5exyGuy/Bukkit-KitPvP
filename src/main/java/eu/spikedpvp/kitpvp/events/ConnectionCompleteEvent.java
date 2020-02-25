package eu.spikedpvp.kitpvp.events;

import eu.spikedpvp.kitpvp.extenders.PlayerExtended;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public final class ConnectionCompleteEvent extends Event {

    private final PlayerExtended playerExtended;

    public ConnectionCompleteEvent(final PlayerExtended playerExtended) {
        this.playerExtended = playerExtended;
    }

    private static final HandlerList HANDLERS = new HandlerList();

    @NotNull
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public PlayerExtended getPlayerExtended() {
        return this.playerExtended;
    }
}
