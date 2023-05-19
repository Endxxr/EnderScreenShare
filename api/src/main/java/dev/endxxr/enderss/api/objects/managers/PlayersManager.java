package dev.endxxr.enderss.api.objects.managers;

import dev.endxxr.enderss.api.objects.SSPlayer;
import lombok.Getter;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public abstract class PlayersManager {

    @Getter
    private final HashMap<UUID, SSPlayer> registeredPlayers;

    public PlayersManager() {
        registeredPlayers = new HashMap<>();
    }


    /**
     *
     * Adds a player to the player list and creates a new SsPlayer object
     *
     * @param uuid the player to add
     * @return the SsPlayer object
     */
    public SSPlayer addPlayer(UUID uuid) {
        SSPlayer ssPlayer = new SSPlayer(uuid);
        registeredPlayers.put(uuid, ssPlayer);
        return ssPlayer;
    }


    /**
     *
     * Removes a player from the player list
     *
     * @param player the player to remove
     */

    public void terminatePlayer(SSPlayer player) {
        registeredPlayers.remove(player.getUUID());
    }

    /**
     *
     * Returns the SsPlayer object of the specified player
     *
     * @param uuid the player to get the SsPlayer object
     * @return the SsPlayer object
     */
    public SSPlayer getPlayer(UUID uuid) {
        return registeredPlayers.get(uuid);
    }

    public SSPlayer getPlayer(SSPlayer ssPlayer) {
        return getPlayer(ssPlayer.getUUID());
    }


    /**
     *
     * Returns all online staffers
     *
     * @return all online staffers
     */

    public HashMap<UUID, SSPlayer> getStaffers() {
        HashMap<UUID, SSPlayer> staffers = new HashMap<>();
        for (UUID uuid : registeredPlayers.keySet()) {
            SSPlayer ssPlayer = registeredPlayers.get(uuid);
            if (ssPlayer.isStaff()) {
                staffers.put(uuid, ssPlayer);
            }
        }
        return staffers;
    }


    /**
     *
     * Check if someone in the staff is online
     *
     * @return true if someone in the staff is online, false otherwise
     */

    public boolean isStaffOnline() {
        return !getStaffers().isEmpty();
    }

    public abstract Collection<UUID> getOnlinePlayers();
    public abstract boolean hasPermission(String permission, UUID uuid);


}
