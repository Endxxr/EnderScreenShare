package dev.endxxr.enderss.api.events.velocity;

import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.Nullable;

public class SsStartEvent {

    @Nullable
    private final Player staffer;
    @Nullable
    private final Player suspect;

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

}
