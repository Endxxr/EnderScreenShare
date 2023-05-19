package dev.endxxr.enderss.api.events.velocity;

import com.velocitypowered.api.proxy.connection.Player;
import dev.endxxr.enderss.api.enums.SSEndCause;
import org.jetbrains.annotations.Nullable;

public class SsEndEvent {

    @Nullable private final Player staffer;
    @Nullable private final Player suspect;
    private final SSEndCause staffQuit;

    public SsEndEvent(@Nullable Player staffer, @Nullable Player suspect, SSEndCause staffQuit) {

        this.staffer = staffer;
        this.suspect = suspect;
        this.staffQuit = staffQuit;

    }

    public @Nullable Player getStaffer() {
        return staffer;
    }

    public @Nullable Player getSuspect() {
        return suspect;
    }

    public SSEndCause getStaffQuit() {
        return staffQuit;
    }

}
