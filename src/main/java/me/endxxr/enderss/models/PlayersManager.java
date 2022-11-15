package me.endxxr.enderss.models;

import lombok.Getter;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;
import java.util.UUID;

public class PlayersManager {



    @Getter
    private final HashMap<UUID, SsPlayer> players;

    public PlayersManager() {
        players = new HashMap<>();
    }

    public SsPlayer createNewPlayer(ProxiedPlayer player) {
        SsPlayer ssPlayer = new SsPlayer(player);
        players.put(player.getUniqueId(), ssPlayer);
        return ssPlayer;
    }

    public void terminatePlayer(ProxiedPlayer player) {
        players.remove(player.getUniqueId());
    }

    public SsPlayer getPlayer(ProxiedPlayer player) {
        return players.get(player.getUniqueId());
    }

    public HashMap<UUID, SsPlayer> getStaffers() {
        HashMap<UUID, SsPlayer> staffers = new HashMap<>();
        for (UUID uuid : players.keySet()) {
            SsPlayer ssPlayer = players.get(uuid);
            if (ssPlayer.isStaff()) {
                staffers.put(uuid, ssPlayer);
            }
        }
        return staffers;
    }

    public boolean isStaffOnline() {
        return !getStaffers().isEmpty();
    }

    public boolean hasPlayer(ProxiedPlayer player) {
        return players.containsKey(player.getUniqueId());
    }




}

