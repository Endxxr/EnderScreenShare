package dev.endxxr.enderss.velocity.utils;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import dev.endxxr.enderss.api.objects.player.ProxyPlayer;
import dev.endxxr.enderss.common.storage.ProxyConfig;
import dev.endxxr.enderss.common.utils.LogUtils;

import java.util.Optional;

public class ConnectionUtils {

    public static void fallback(Player player, ProxyPlayer ssPlayer, ProxyServer server) {
        if (ProxyConfig.CONFIG_LAST_CONNECTED_SERVER.getBoolean()){

            Optional<RegisteredServer> optionalServer = server.getServer(ssPlayer.getLastServer());
            if (!optionalServer.isPresent()) {
                LogUtils.prettyPrintException(new NullPointerException("The server " + ssPlayer.getLastServer() + " is not present in the proxy!"),"The fallback server doesn't exist" );
                return;
            }

            player.createConnectionRequest(optionalServer.get()).fireAndForget();
        } else {
            Optional<RegisteredServer> optionalServer = server.getServer(ProxyConfig.FALLBACK_SERVER.getString());
            if (!optionalServer.isPresent()) {
                LogUtils.prettyPrintUserMistake(new NullPointerException("The server " + ProxyConfig.FALLBACK_SERVER.getString() + " is not present in the proxy!"),"The fallback server doesn't exist" );
                return;
            }

            player.createConnectionRequest(optionalServer.get()).fireAndForget();
        }

    }


}
