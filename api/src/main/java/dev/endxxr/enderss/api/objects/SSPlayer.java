package dev.endxxr.enderss.api.objects;

import dev.endxxr.enderss.api.EnderSSAPI;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

;

/**
 *
 * This class stores the data about the player, used in many contexts
 *
 * @since 0.1
 * @see dev.endxxr.enderss.api.objects.managers.PlayersManager
 *
 */


public class SSPlayer {

    @Getter
    private final UUID UUID;
    @Setter
    @Getter
    private boolean frozen = false;
    @Getter
    @Setter
    private boolean alerts = true;
    @Getter
    @Setter
    private String lastServer;
    @Getter
    @Setter
    private byte lastMode = 0;

    private UUID staffer;
    private UUID controlled;

    public SSPlayer(UUID UUID) {
        this.UUID = UUID;
    }

    /**
     *
     * Get the staffer who is "screensharing" this player
     *
     */

    public SSPlayer getStaffer() {
        return EnderSSAPI.Provider.getApi().getPlayersManager().getPlayer(staffer);
    }


    /**
     *
     * Set the staffer who is "screensharing" this player
     *
     */

    public void setStaffer(SSPlayer player) {
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

    public SSPlayer getControlled() {
        return EnderSSAPI.Provider.getApi().getPlayersManager().getPlayer(controlled);
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
        return EnderSSAPI.Provider.getApi().getPlayersManager().hasPermission("enderss.staff", UUID);
    }


}
