package io.github.enderf5027.enderss.session;

import io.github.enderf5027.enderss.utils.ChatUtils;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

public class PlayerSession {

    private boolean isFrozen = false;
    private boolean isStaff = false;
    private boolean alerts = true;
    private ProxiedPlayer Staffer;
    private ProxiedPlayer PlayerScreenShared;
    private ServerInfo LastServer;

    private long secondsElapsed;

    public PlayerSession(UUID uuid, String name) {
    }

    public ServerInfo getLastServer() {
        return LastServer;
    }

    public void setLastServer(ServerInfo lastServer) {
        LastServer = lastServer;
    }

    public boolean isFrozen() {
        return isFrozen;
    }

    public boolean isStaff() {
        return isStaff;
    }

    public ProxiedPlayer getScreenSharing() {
        return PlayerScreenShared;
    }

    public ProxiedPlayer getStaffer() {
        return Staffer;
    }
    public boolean getAlertsEnabled() { return alerts; }

    public String getTime() {
        return ChatUtils.calculateTime(secondsElapsed);
    }

    public void setFrozen(boolean frozen) {
        isFrozen = frozen;
    }

    public void setPlayerScreenShared(ProxiedPlayer screenSharing) {
        PlayerScreenShared = screenSharing;
    }

    public void setStaff(boolean staff) {
        isStaff = staff;
    }

    public void setStaffer(ProxiedPlayer staffer) {
        Staffer = staffer;
    }

    public void setAlerts(boolean Alerts) { alerts = Alerts; }

}