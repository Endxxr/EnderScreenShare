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

}
