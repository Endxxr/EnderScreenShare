package dev.endxxr.enderss.api.objects.player;

/**
 *
 * This class stores the data about the player, used in the context of the proxy
 *
 * @since 1.1
 * @see dev.endxxr.enderss.api.objects.managers.PlayersManager
 *
 */



public class ProxyPlayer extends SsPlayer {

    private String lastServer;

    public ProxyPlayer(java.util.UUID UUID) {
        super(UUID);
    }


    /**
     *
     * Set the last server the player was on
     *
     */
    public void setLastServer(String server) {
        this.lastServer = server;
    }


    /**
     *
     * Get the last server the player was on
     *
     */
    public String getLastServer() {
        return lastServer;
    }

}
