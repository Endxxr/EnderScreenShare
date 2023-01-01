package me.endxxr.enderss.utils;

import me.endxxr.enderss.enums.Config;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PlayerUtils {

    public static boolean isInSsServer(ProxiedPlayer player) {
        return player.getServer().getInfo().getName().equalsIgnoreCase(Config.CONFIG_SSSERVER.getString());
    }

}
