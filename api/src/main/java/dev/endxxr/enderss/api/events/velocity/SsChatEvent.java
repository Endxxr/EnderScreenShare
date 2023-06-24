package dev.endxxr.enderss.api.events.velocity;

import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.proxy.Player;
import lombok.Getter;
import lombok.Setter;

public class SsChatEvent {


    private final String initialMessage;
    private final String message;
    private final Player sender;
    @Getter
    @Setter
    private ResultedEvent.GenericResult result;

    public SsChatEvent(String initialMessage, String message, Player sender) {
        this.initialMessage = initialMessage;
        this.message = message;
        this.sender = sender;
        result = ResultedEvent.GenericResult.allowed();
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

    
}
