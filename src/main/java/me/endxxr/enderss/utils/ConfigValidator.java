package me.endxxr.enderss.utils;

import me.endxxr.enderss.enums.Config;
import me.endxxr.enderss.exceptions.ConfigException;
import net.md_5.bungee.api.ProxyServer;

public class ConfigValidator {

    public static boolean validate() {

        if (!validateServer(Config.CONFIG_SSSERVER)) return false;
        if (!validateServer(Config.CONFIG_FALLBACK)) return false;

        
        return true;
    }


    private static boolean validateServer(Config server) {
        String serverName = server.getString();
        if (serverName == null || serverName.isEmpty() || ProxyServer.getInstance().getServerInfo(serverName) == null) {
            ChatUtils.prettyPrintException(new ConfigException("The server specified in the config.yml is not valid!"),
                    "The server specified at "+server.getPath()+" doesn't exist or isn't set!");
            return false;
        }
        return true;
    }

}
