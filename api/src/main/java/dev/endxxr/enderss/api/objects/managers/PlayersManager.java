package dev.endxxr.enderss.api.objects.managers;

import dev.endxxr.enderss.api.objects.player.SsPlayer;
import org.jetbrains.annotations.NotNull;
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
    public abstract @NotNull SsPlayer registerPlayer(@NotNull UUID uuid);


    /**
     *
     * Removes a player from the player list
     *
     * @param player the player to remove
     */

    public void unregisterPlayer(@NotNull SsPlayer player) {
        registeredPlayers.remove(player.getUUID());
    }

    /**
     *
     * Returns the SsPlayer object of the specified player
     *
     * @param uuid the player to get the SsPlayer object
     * @return the SsPlayer object
     */
    public @Nullable SsPlayer getPlayer(UUID uuid) {
        SsPlayer ssPlayer = registeredPlayers.get(uuid);
        if (ssPlayer == null && getPlatformPlayer(uuid) != null) {
            ssPlayer = registerPlayer(uuid);
        }
        return ssPlayer;
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

    public boolean isStaffOffline() {
        return getStaffers().isEmpty();
    }


    /**
     *
     * Broadcast a message to all online staffers with alerts enabled
     *
     * @param formattedMessage the formatted message to broadcast. It has to have the placeholders already replaced
     */

    public abstract void broadcastStaff(String formattedMessage);

    /**
     *
     *  Check if a player has a permission using the API of the running platform
     *
     * @param uuid the player to check
     * @param permission the permission to check
     * @return true if the player has the permission, false otherwise
     */
    public abstract boolean hasPermission(UUID uuid, String permission);
    public abstract @NotNull List<String> getControllablePlayers(String initialChars);
    public Set<SsPlayer> getRegisteredPlayers() {
        return new HashSet<>(registeredPlayers.values());
    }
    public abstract Object getPlatformPlayer(UUID uuid);

}
