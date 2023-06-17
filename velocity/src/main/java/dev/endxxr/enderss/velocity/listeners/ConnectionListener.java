package dev.endxxr.enderss.velocity.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.DisconnectEvent;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.PostLoginEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.connection.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import dev.endxxr.enderss.api.EnderSS;
import dev.endxxr.enderss.api.EnderSSProvider;
import dev.endxxr.enderss.api.enums.PluginMessageType;
import dev.endxxr.enderss.api.enums.SSEndCause;
import dev.endxxr.enderss.api.events.velocity.SsEndEvent;
import dev.endxxr.enderss.api.objects.player.ProxyPlayer;
import dev.endxxr.enderss.api.objects.player.SsPlayer;
import dev.endxxr.enderss.api.utils.ChatUtils;
import dev.endxxr.enderss.common.storage.GlobalConfig;
import dev.endxxr.enderss.common.storage.ProxyConfig;
import litebans.api.Database;

import java.util.List;
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
        Player player = event.player();
        ProxyPlayer session = (ProxyPlayer) api.getPlayersManager().registerPlayer(player.id());
        if (player.hasPermission("enderss.admin") && api.isUpdateAvailable()) {
            player.sendMessage(ChatUtils.formatAdventureComponent("&8[&d&lEnder&5&lSS&8]&f You need to &cupdate &fthe plugin!"));
        }
        session.setLastServer(ProxyConfig.FALLBACK_SERVER.getString());
    }



    @Subscribe
    public void onProxyQuit(DisconnectEvent event) {
        Player player = event.player();
        ProxyPlayer proxyPlayer = (ProxyPlayer) api.getPlayersManager().getPlayer(player.id());
        api.getPlayersManager().unregisterPlayer(proxyPlayer); //Remove player from the list

        if (proxyPlayer==null) return; //If the player is null, we don't need to do anything else.

       /*
            SUSPECT QUITS
        */

        if (proxyPlayer.isFrozen()) { // If the player controlled quits
            ProxyPlayer ssStaff = (ProxyPlayer) proxyPlayer.getStaffer();
            Player staff = server.player(ssStaff.getUUID());
            ssStaff.setControlled(null);


            // If the "Fallback Staff" option is active, the staff will be sent to the fallback server or the last server

            if (ProxyConfig.CONFIG_FALLBACK_STAFF.getBoolean()) {
                if (ProxyConfig.CONFIG_LAST_CONNECTED_SERVER.getBoolean()) {
                    
                    staff.createConnectionRequest(server.server(proxyPlayer.getLastServer())).fireAndForget();
                } else {
                    staff.createConnectionRequest(server.server(ProxyConfig.FALLBACK_SERVER.getString())).fireAndForget();
                }
            }

            //Send messages
            for (Player onlinePlayer : server.connectedPlayers()) {
                if (onlinePlayer.hasPermission("enderss.staff") || api.getPlayersManager().getPlayer(onlinePlayer.id()).hasAlerts()) {
                    onlinePlayer.sendMessage(ChatUtils.formatAdventureComponent(GlobalConfig.MESSAGES_INFO_PLAYER_QUIT.getMessage(), "%SUSPECT%", player.username(), "%STAFF%", staff.username()));
                }
            }

            //Ban On Quit
            List<String> banOnQuitCommands = GlobalConfig.BAN_ON_QUIT_COMMANDS.getStringList();
            if (!banOnQuitCommands.isEmpty()) {

                if (api.getPlugin().isLiteBansPresent() && GlobalConfig.BAN_ON_QUIT_PREVENT_DOUBLE_BAN.getBoolean()) {
                    if (CompletableFuture.supplyAsync(() -> Database.get().isPlayerBanned(player.id(), null)).join()) {
                        return;
                    }
                }

                for (String command : banOnQuitCommands) {
                    command = command.replace("%SUSPECT%", player.username());
                    api.getPlugin().dispatchCommand(null, command);
                }
            }

            api.getPlugin().sendPluginMessage(
                    ssStaff,
                    proxyPlayer,
                    PluginMessageType.END
            );

            server.eventManager().fireAndForget(new SsEndEvent(staff, player, SSEndCause.SUSPECT_QUIT));


            return;
        }

        /*
            STAFFER QUITS
         */

        if (proxyPlayer.getControlled()!=null) { //If the player was screensharing someone
            Player suspect = server.player(proxyPlayer.getControlled().getUUID());
            api.getScreenShareManager().clearPlayer(suspect.id());
            suspect.sendMessage(ChatUtils.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_STAFF_OFFLINE.getMessage()));
            if (ProxyConfig.CONFIG_LAST_CONNECTED_SERVER.getBoolean()){
                suspect.createConnectionRequest(server.server(proxyPlayer.getLastServer())).fireAndForget();
            } else {
                suspect.createConnectionRequest(server.server(ProxyConfig.FALLBACK_SERVER.getString())).fireAndForget();
            }

            api.getPlugin().sendPluginMessage(
                    proxyPlayer,
                    PluginMessageType.END
            );

            server.eventManager().fireAndForget(new SsEndEvent(player, suspect, SSEndCause.STAFF_QUIT));
        }
    }



    @Subscribe
    public void onPlayerKick(KickedFromServerEvent event) {
        
        RegisteredServer serverInfo = event.server();
        SsPlayer ssPlayer = api.getPlayersManager().getPlayer(event.player().id());


        if (server.server(ProxyConfig.FALLBACK_SERVER.getString()).equals(serverInfo)) {
            if (ssPlayer.isFrozen() || ssPlayer.getControlled()!=null) {
                api.getScreenShareManager().clearPlayer(ssPlayer.getUUID());

            }
        }
        
    }
}
