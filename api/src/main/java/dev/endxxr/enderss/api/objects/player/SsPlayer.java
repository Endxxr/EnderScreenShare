package dev.endxxr.enderss.api.objects.player;

import dev.endxxr.enderss.api.EnderSSProvider;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.UUID;

/**
 *
 * This class stores the data about the player, used in many contexts
 *
 * @since 0.1
 * @see dev.endxxr.enderss.api.objects.managers.PlayersManager
 *
 */



public abstract class SsPlayer {

    @Getter
    private final java.util.UUID UUID;
    @Setter
    @Getter
    private boolean frozen = false;
    @Accessors(fluent=true) @Getter @Setter
    private boolean hasAlerts = true;
    private UUID staffer;
    private UUID controlled;

    public SsPlayer(UUID UUID) {
        this.UUID = UUID;
    }

    /**
     *
     * Get the staffer who is "screensharing" this player
     *
     */

    public SsPlayer getStaffer() {
        if (staffer == null) return null;
        return EnderSSProvider.getApi().getPlayersManager().getPlayer(staffer);
    }


    /**
     *
     * Set the staffer who is "screensharing" this player
     *
     */

    public void setStaffer(SsPlayer player) {
        if (player == null) {
            staffer = null;
            return;
        }
        this.staffer = player.getUUID();
    }



    /**
     *
     * Return the player controlled by this staffer
     *
     * @return the player controlled by this staffer
     */

    public SsPlayer getControlled() {
        if (controlled == null) return null;
        return EnderSSProvider.getApi().getPlayersManager().getPlayer(controlled);
    }


    /**
     *
     * Set the player controlled by this staffer
     *
     * @param uuid The uuid of the controlled player
     */

    public void setControlled(UUID uuid) {
        this.controlled = uuid;
    }


    /**
     *
     * Returns true if the player is a staffer
     *
     * @return true if the player is a staffer
     */


    public boolean isStaff() {
        return EnderSSProvider.getApi().getPlayersManager().hasPermission(UUID, "enderss.staff");
    }


}
