package dev.endxxr.enderss.velocity.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import dev.endxxr.enderss.api.EnderSS;
import dev.endxxr.enderss.api.EnderSSProvider;
import dev.endxxr.enderss.api.enums.PluginMessageType;
import dev.endxxr.enderss.api.enums.SSEndCause;
import dev.endxxr.enderss.api.events.velocity.SsEndEvent;
import dev.endxxr.enderss.api.objects.player.ProxyPlayer;
import dev.endxxr.enderss.api.objects.player.SsPlayer;
import dev.endxxr.enderss.common.utils.ChatUtils;
import dev.endxxr.enderss.velocity.utils.VelocityChat;
import dev.endxxr.enderss.common.storage.GlobalConfig;
import dev.endxxr.enderss.common.storage.ProxyConfig;
import dev.endxxr.enderss.velocity.utils.ConnectionUtils;
import litebans.api.Database;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ConnectionListener {

    private final EnderSS api;
    private final ProxyServer server;
    
    public ConnectionListener(ProxyServer server) {
        this.server = server;
        this.api = EnderSSProvider.getApi();
    }

    @Subscribe
    public void onProxyJoin(PostLoginEvent event) {
        Player player = event.getPlayer();
        ProxyPlayer session = (ProxyPlayer) api.getPlayersManager().registerPlayer(player.getUniqueId());
        if (player.hasPermission("enderss.admin") && api.isUpdateAvailable()) {
            player.sendMessage(VelocityChat.formatAdventureComponent("&8[&d&lEnder&5&lSS&8]&f New version available!"));
        }
        session.setLastServer(ProxyConfig.FALLBACK_SERVER.getString());
    }



    @Subscribe
    public void onProxyQuit(DisconnectEvent event) {
        Player leftPlayer = event.getPlayer();
        ProxyPlayer leftProxyPlayer = (ProxyPlayer) api.getPlayersManager().getPlayer(leftPlayer.getUniqueId());

        if (leftProxyPlayer==null) return;
        api.getPlayersManager().unregisterPlayer(leftProxyPlayer); //Remove player from the list


       /*
            SUSPECT QUITS
        */

        if (leftProxyPlayer.isFrozen()) { // If the player controlled quits
            ProxyPlayer ssStaff = (ProxyPlayer) leftProxyPlayer.getStaffer();
            
            Optional<Player> optionalStaff = server.getPlayer(ssStaff.getUUID());
            if (!optionalStaff.isPresent()) return;
            Player staff = optionalStaff.get();

            ssStaff.setControlled(null);


            // If the "Fallback Staff" option is active, the staff will be sent to the fallback server or the last server

            if (ProxyConfig.CONFIG_FALLBACK_STAFF.getBoolean()) {
                ConnectionUtils.fallback(leftPlayer, ssStaff, server);
            }

            api.getPlayersManager().broadcastStaff(ChatUtils.format(GlobalConfig.MESSAGES_INFO_PLAYER_QUIT.getMessage(), "%SUSPECT%", leftPlayer.getUsername(), "%STAFF%", staff.getUsername()));


            //Ban On Quit
            List<String> banOnQuitCommands = GlobalConfig.BAN_ON_QUIT_COMMANDS.getStringList();
            if (!banOnQuitCommands.isEmpty()) {

                if (api.getPlugin().isLiteBansPresent() && GlobalConfig.BAN_ON_QUIT_PREVENT_DOUBLE_BAN.getBoolean()) {
                    if (CompletableFuture.supplyAsync(() -> Database.get().isPlayerBanned(leftPlayer.getUniqueId(), null)).join()) {
                        return;
                    }
                }

                for (String command : banOnQuitCommands) {
                    command = command.replace("%SUSPECT%", leftPlayer.getUsername());
                    api.getPlugin().dispatchCommand(null, command);
                }
            }

            api.getPlugin().sendPluginMessage(
                    ssStaff,
                    leftProxyPlayer,
                    PluginMessageType.END
            );

            server.getEventManager().fireAndForget(new SsEndEvent(staff, leftPlayer, SSEndCause.SUSPECT_QUIT));

            return;
        }

        /*
            STAFFER QUITS
         */

        if (leftProxyPlayer.getControlled()!=null) { //If the player was screensharing someone

            Optional<Player> optionalSuspect = server.getPlayer(leftProxyPlayer.getControlled().getUUID());
            if (!optionalSuspect.isPresent()) return;
            Player suspect = optionalSuspect.get();

            api.getScreenShareManager().clearPlayer(suspect.getUniqueId());
            suspect.sendMessage(VelocityChat.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_STAFF_OFFLINE.getMessage()));
            ConnectionUtils.fallback(suspect, (ProxyPlayer) api.getPlayersManager().getPlayer(suspect.getUniqueId()), server);

            api.getPlugin().sendPluginMessage(
                    leftProxyPlayer,
                    PluginMessageType.END
            );

            server.getEventManager().fireAndForget(new SsEndEvent(leftPlayer, suspect, SSEndCause.STAFF_QUIT));
        }
    }



    @Subscribe
    public void onPlayerKick(KickedFromServerEvent event) {
        
        RegisteredServer serverInfo = event.getServer();
        RegisteredServer fallbackServer = server.getServer(ProxyConfig.FALLBACK_SERVER.getString()).orElse(null);
        SsPlayer ssPlayer = api.getPlayersManager().getPlayer(event.getPlayer().getUniqueId());

        if (ssPlayer==null || fallbackServer == null) return;

        if (fallbackServer.equals(serverInfo)) {
            if (ssPlayer.isFrozen() || ssPlayer.getControlled()!=null) {
                api.getScreenShareManager().clearPlayer(ssPlayer.getUUID());

            }
        }
    }

}
