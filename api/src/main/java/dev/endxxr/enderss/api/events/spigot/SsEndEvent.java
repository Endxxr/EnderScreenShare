package dev.endxxr.enderss.api.events.spigot;

import dev.endxxr.enderss.api.enums.SSEndCause;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.Nullable;

public class SsEndEvent extends Event {


    @Nullable
    private final Player staffer;
    @Nullable
    private final Player suspect;
    private final SSEndCause staffQuit;

    private final HandlerList HANDLER_LIST = new HandlerList();

    public SsEndEvent(@Nullable Player staffer, @Nullable Player suspect, SSEndCause staffQuit) {

        this.staffer = staffer;
        this.suspect = suspect;
        this.staffQuit = staffQuit;

    }


    public SSEndCause getStaffQuit() {
        return staffQuit;
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
