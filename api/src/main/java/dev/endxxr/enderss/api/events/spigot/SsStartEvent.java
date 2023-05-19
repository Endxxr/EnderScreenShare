package dev.endxxr.enderss.api.events.spigot;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.Nullable;

public class SsStartEvent extends Event {

    @Nullable
    private final Player staffer;
    @Nullable private final Player suspect;
    private final HandlerList HANDLER_LIST = new HandlerList();

    public SsStartEvent(@Nullable Player staffer, @Nullable Player suspect) {

        this.staffer = staffer;
        this.suspect = suspect;
    }
    
    public @Nullable Player getSuspect() {
        return suspect;
    }

    public @Nullable Player getStaffer() {
        return staffer;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
