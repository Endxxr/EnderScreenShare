package io.github.enderf5027.enderss.session;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager {

    private static final Map<UUID, PlayerSession> sessions = new HashMap<>();

    public static Map<UUID, PlayerSession> getSessions() {
        return sessions;
    }

    public static PlayerSession getSession(ProxiedPlayer player){
        if (!has(player.getUniqueId())) {
            sessions.put(player.getUniqueId(), SessionLoader.loadSession(player));
        }
        return sessions.get(player.getUniqueId());
    }

    public static boolean has(UUID uuid) {
        return sessions.containsKey(uuid);
    }

    public static void addSession(ProxiedPlayer player) {
        sessions.put(player.getUniqueId(), SessionLoader.loadSession(player));
    }
    public static void removeSession(ProxiedPlayer player) {
        sessions.remove(player.getUniqueId());
    }

}
