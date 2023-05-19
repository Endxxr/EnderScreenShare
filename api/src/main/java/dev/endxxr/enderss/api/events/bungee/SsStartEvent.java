package dev.endxxr.enderss.api.events.bungee;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;
import org.jetbrains.annotations.Nullable;

public class SsStartEvent extends Event {

    @Nullable
    private final ProxiedPlayer staffer;
    @Nullable private final ProxiedPlayer suspect;

    public SsStartEvent(@Nullable ProxiedPlayer staffer, @Nullable ProxiedPlayer suspect) {

        this.staffer = staffer;
        this.suspect = suspect;
    }

    public @Nullable ProxiedPlayer getSuspect() {
        return suspect;
    }

    public @Nullable ProxiedPlayer getStaffer() {
        return staffer;
    }


}
