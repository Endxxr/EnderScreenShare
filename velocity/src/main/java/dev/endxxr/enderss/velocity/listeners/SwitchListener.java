package dev.endxxr.enderss.velocity.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.connection.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import dev.endxxr.enderss.api.EnderSS;
import dev.endxxr.enderss.api.EnderSSProvider;
import dev.endxxr.enderss.api.enums.SSEndCause;
import dev.endxxr.enderss.api.events.velocity.SsEndEvent;
import dev.endxxr.enderss.api.objects.player.ProxyPlayer;
import dev.endxxr.enderss.api.utils.ChatUtils;
import dev.endxxr.enderss.common.storage.ProxyConfig;

public class SwitchListener {
    
    private final EnderSS api;
    private final ProxyServer server;

    public SwitchListener(ProxyServer server) {
        this.api = EnderSSProvider.getApi();
        this.server = server;
    }


    @Subscribe
    public void onSwitch(ServerConnectedEvent event) {
        ProxyPlayer proxyPlayer = (ProxyPlayer) api.getPlayersManager().getPlayer(event.player().id());
        RegisteredServer lastServer = event.previousServer();
        Player player = event.player();
        
        if (lastServer != null) {
            proxyPlayer.setLastServer(lastServer.serverInfo().name());
        }

        if (player.hasPermission("enderss.admin") && api.isUpdateAvailable()) {
            player.sendMessage(ChatUtils.formatAdventureComponent("&8[&d&lEnder&5&lSS&8]&f New version available!"));
        }

        if (player.hasPermission("enderss.admin") && api.isConfigObsolete()) {
            player.sendMessage(ChatUtils.formatAdventureComponent("&8[&d&lEnder&5&lSS&8]&f Your config is obsolete!"));
        }


        if (lastServer == server.server(ProxyConfig.SS_SERVER.getString()) && proxyPlayer.getControlled() != null) { // If the player was controlling anyone
            Player suspect = server.player(proxyPlayer.getControlled().getUUID());
            api.getScreenShareManager().clearPlayer(proxyPlayer.getUUID(), suspect.id());
            //plugin.getScoreboardManager().endScoreboard(player, ssPlayer.getControlled());
            server.eventManager().fireAndForget(new SsEndEvent(
                    suspect,
                    player,
                    SSEndCause.STAFF_SWITCH));
        }

    }

}
