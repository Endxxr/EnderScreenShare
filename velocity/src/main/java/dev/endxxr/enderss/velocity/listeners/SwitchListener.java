package dev.endxxr.enderss.velocity.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import dev.endxxr.enderss.api.EnderSS;
import dev.endxxr.enderss.api.EnderSSProvider;
import dev.endxxr.enderss.api.enums.SSEndCause;
import dev.endxxr.enderss.api.events.velocity.SsEndEvent;
import dev.endxxr.enderss.api.objects.player.ProxyPlayer;
import dev.endxxr.enderss.velocity.utils.VelocityChat;
import dev.endxxr.enderss.common.storage.GlobalConfig;
import dev.endxxr.enderss.common.storage.ProxyConfig;

import java.util.Optional;

public class SwitchListener {
    
    private final EnderSS api;
    private final ProxyServer server;

    public SwitchListener(ProxyServer server) {
        this.api = EnderSSProvider.getApi();
        this.server = server;
    }


    @Subscribe
    public void onSwitch(ServerConnectedEvent event) {
        Player player = event.getPlayer();
        ProxyPlayer proxyPlayer = (ProxyPlayer) api.getPlayersManager().getPlayer(player.getUniqueId());
        RegisteredServer lastServer = event.getPreviousServer().orElse(null);

        if (proxyPlayer == null) {
            player.sendMessage(VelocityChat.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_GENERIC.getMessage()));
            api.getPlugin().getLog().severe("Wasn't able to get the profile of the player, is it online?");
            return;
        }


        if (lastServer != null) {
            proxyPlayer.setLastServer(lastServer.getServerInfo().getName());
        }

        if (player.hasPermission("enderss.admin") && api.isUpdateAvailable()) {
            player.sendMessage(VelocityChat.formatAdventureComponent("&8[&d&lEnder&5&lSS&8]&f New version available!"));
        }

        if (player.hasPermission("enderss.admin") && api.isConfigObsolete()) {
            player.sendMessage(VelocityChat.formatAdventureComponent("&8[&d&lEnder&5&lSS&8]&f Your config is obsolete!"));
        }


        if (lastServer == server.getServer(ProxyConfig.SS_SERVER.getString()).orElse(null) && proxyPlayer.getControlled() != null) { // If the player was controlling anyone
            Optional<Player> suspectOptional = server.getPlayer(proxyPlayer.getControlled().getUUID());
            if (!suspectOptional.isPresent()) return;

            Player suspect = suspectOptional.get();

            api.getScreenShareManager().clearPlayer(proxyPlayer.getUUID(), suspect.getUniqueId());
            server.getEventManager().fireAndForget(new SsEndEvent(
                    suspect,
                    player,
                    SSEndCause.STAFF_SWITCH));
        }

    }

}
