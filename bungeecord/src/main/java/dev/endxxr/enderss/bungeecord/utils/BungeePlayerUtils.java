package dev.endxxr.enderss.bungeecord.utils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import dev.endxxr.enderss.api.EnderSSAPI;
import dev.endxxr.enderss.api.enums.PluginMessageType;
import dev.endxxr.enderss.api.objects.SSPlayer;
import dev.endxxr.enderss.common.storage.ProxyConfig;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

public class BungeePlayerUtils {

    public static boolean isInSsServer(ProxiedPlayer player) {
        return player.getServer().getInfo().getName().equalsIgnoreCase(ProxyConfig.SS_SERVER.getString());
    }

    /**
     *
     * Gets the staffer or the controlled player of the specified player
     *
     * @param ssPlayer
     * @return
     */
    public static SSPlayer getPartner(SSPlayer ssPlayer) {

        if (ssPlayer == null) return null;

        if (ssPlayer.isStaff()) {
            if (ssPlayer.getControlled()!=null) {
                return ssPlayer.getControlled();
            }
            if (ssPlayer.getStaffer()!=null && ssPlayer.isFrozen()) {
                return ssPlayer.getStaffer();
            }
        } else {
            if (ssPlayer.getStaffer()!=null) return ssPlayer.getStaffer();
        }

        return null;
    }




}
