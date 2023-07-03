package dev.endxxr.enderss.api.events.bungee;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;

public class SsChatEvent extends Event implements Cancellable {


    private final String initialMessage;
    private final String message;
    private final ProxiedPlayer sender;
    private boolean cancelled = false;

    public SsChatEvent(String initialMessage, String message, ProxiedPlayer sender) {
        this.initialMessage = initialMessage;
        this.message = message;
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }


    public ProxiedPlayer getSuspect() {
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
}
