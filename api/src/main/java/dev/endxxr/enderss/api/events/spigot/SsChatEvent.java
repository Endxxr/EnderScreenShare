package dev.endxxr.enderss.api.events.spigot;

import net.md_5.bungee.api.plugin.Cancellable;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SsChatEvent extends Event implements Cancellable {
    private final String initialMessage;
    private final String message;
    private final Player sender;
    private boolean cancelled = false;
    private static final HandlerList handlers = new HandlerList();

    public SsChatEvent(String initialMessage, String message, Player sender) {
        this.initialMessage = initialMessage;
        this.message = message;
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }


    public Player getSuspect() {
        return sender;
    }

    public String getInitialMessage() {
        return initialMessage;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
