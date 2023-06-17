package dev.endxxr.enderss.api.events.velocity;


import com.velocitypowered.api.event.Event;
import com.velocitypowered.api.proxy.connection.Player;

public class SsChatEvent implements Event {


    private final String initialMessage;
    private final String message;
    private final Player sender;
    private boolean cancelled = false;

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

    public boolean isCancelled() {
        return cancelled;
    }

    
}
