package me.endxxr.enderss.models;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

/**
 *
 * This class stores the data about the player, used in many contexts
 *
 *
 * @since 0.1
 * @see PlayersManager
 *
 */


public class SsPlayer {

    @Getter
    private final UUID uuid;
    @Setter
    @Getter
    private boolean frozen = false;
    @Getter
    @Setter
    private boolean alerts = true;
    private UUID staffer;
    private UUID controlled;
    @Getter
    @Setter
    private String lastServer;

    public SsPlayer(ProxiedPlayer player) {
        this.uuid = player.getUniqueId();
    }


    /**
     *
     * Returns the staffer who is "screensharing" this player
     *
     * @return the staffer who is screensharing this player
     */

    public ProxiedPlayer getStaffer() {
        return ProxyServer.getInstance().getPlayer(staffer);
    }

    /**
     *
     * Set the staffer who is "screensharing" this player
     *
     *
     * @param player
     */

    public void setStaffer(ProxiedPlayer player) {
        if (player == null) {
            staffer = null;
            return;
        }
        this.staffer = player.getUniqueId();
    }


    /**
     *
     * Return the player controlled by this staffer
     *
     * @return the player controlled by this staffer
     */

    public ProxiedPlayer getControlled() {
        return ProxyServer.getInstance().getPlayer(controlled);
    }


    /**
     *
     * Set the player controlled by this staffer
     *
     * @param controlled
     */

    public void setControlled(ProxiedPlayer controlled) {
        if (controlled == null) {
            this.controlled = null;
            return;
        }
        this.controlled = controlled.getUniqueId();
    }


    /**
     *
     * Returns true if the player is a staffer
     *
     * @return true if the player is a staffer
     */


    public boolean isStaff() {
        return ProxyServer.getInstance().getPlayer(uuid).hasPermission("enderss.staff");
    }


}
