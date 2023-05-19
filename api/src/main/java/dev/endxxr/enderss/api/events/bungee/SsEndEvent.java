package dev.endxxr.enderss.api.events.bungee;

import dev.endxxr.enderss.api.enums.SSEndCause;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;
import org.jetbrains.annotations.Nullable;

public class SsEndEvent extends Event {

    @Nullable private final ProxiedPlayer staffer;
    @Nullable private final ProxiedPlayer suspect;
    private final SSEndCause staffQuit;

    public SsEndEvent(@Nullable ProxiedPlayer staffer, @Nullable ProxiedPlayer suspect, SSEndCause staffQuit) {

        this.staffer = staffer;
        this.suspect = suspect;
        this.staffQuit = staffQuit;

    }

    public @Nullable ProxiedPlayer getStaffer() {
        return staffer;
    }

    public @Nullable ProxiedPlayer getSuspect() {
        return suspect;
    }

    public SSEndCause getStaffQuit() {
        return staffQuit;
    }

}
