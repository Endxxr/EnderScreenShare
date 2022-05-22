package io.github.enderf5027.enderss.session;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager {

    private static final Map<UUID, PlayerSession> sessions = new HashMap<>();

    public static ArrayList<PlayerSession> getSessions() {
        ArrayList<PlayerSession> sessionsList = new ArrayList<>();
        for (int sessionIndex = 0; sessionIndex < sessions.size(); sessionIndex++) {
            sessionsList.add(sessions.get(sessionIndex));
        }

        return sessionsList;

    }

    public static ArrayList<PlayerSession> getStaff() {
        ArrayList<PlayerSession> sessionsList = new ArrayList<>();
        for (int sessionIndex = 0; sessionIndex < sessions.size(); sessionIndex++) {
            PlayerSession session = sessions.get(0);
            if (session == null) {
                continue;
            }
            if (session.isStaff()) {
                sessionsList.add(session);
            }

        }
        return sessionsList;
    }
    public static boolean isStaffOnline() {
        boolean isEmpty;
        ArrayList<PlayerSession> staffSessions = getStaff();
        isEmpty = staffSessions.isEmpty();

        return isEmpty;
    }

    public static boolean isStaff(ProxiedPlayer staff) {
        return getSession(staff).isStaff();
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
