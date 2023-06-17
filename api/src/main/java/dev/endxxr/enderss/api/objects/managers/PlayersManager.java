package dev.endxxr.enderss.api.objects.managers;

import dev.endxxr.enderss.api.objects.player.SsPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class PlayersManager {


    protected final HashMap<UUID, SsPlayer> registeredPlayers = new HashMap<>();

    /**
     *
     * Adds a player to the player list and creates a new SsPlayer object
     *
     * @param uuid the player to add
     * @return the SsPlayer object
     */
    public abstract SsPlayer registerPlayer(UUID uuid);


    /**
     *
     * Removes a player from the player list
     *
     * @param player the player to remove
     */

    public void unregisterPlayer(@Nullable SsPlayer player) {
        registeredPlayers.remove(player.getUUID());
    }

    /**
     *
     * Returns the SsPlayer object of the specified player
     *
     * @param uuid the player to get the SsPlayer object
     * @return the SsPlayer object
     */
    public SsPlayer getPlayer(UUID uuid) {
        return registeredPlayers.get(uuid);
    }

    public SsPlayer getPlayer(SsPlayer SsPlayer) {
        return getPlayer(SsPlayer.getUUID());
    }


    /**
     *
     * Returns all online staffers
     *
     * @return all online staffers
     */

    public HashMap<UUID, SsPlayer> getStaffers() {
        HashMap<UUID, SsPlayer> staffers = new HashMap<>();
        for (UUID uuid : registeredPlayers.keySet()) {
            SsPlayer SsPlayer = registeredPlayers.get(uuid);
            if (SsPlayer.isStaff()) {
                staffers.put(uuid, SsPlayer);
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
    public abstract boolean hasPermission(UUID uuid, String permission);
    public abstract List<String> getControllablePlayers(String initialChars);

    public Set<SsPlayer> getRegisteredPlayers() {
        return new HashSet<>(registeredPlayers.values());
    }

}
