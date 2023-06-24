package dev.endxxr.enderss.bungeecord.listeners;

import dev.endxxr.enderss.api.EnderSS;
import dev.endxxr.enderss.api.EnderSSProvider;
import dev.endxxr.enderss.api.enums.PluginMessageType;
import dev.endxxr.enderss.api.enums.SSEndCause;
import dev.endxxr.enderss.api.events.bungee.SsEndEvent;
import dev.endxxr.enderss.api.objects.player.ProxyPlayer;
import dev.endxxr.enderss.api.objects.player.SsPlayer;
import dev.endxxr.enderss.bungeecord.utils.BungeeChat;
import dev.endxxr.enderss.common.storage.GlobalConfig;
import dev.endxxr.enderss.common.storage.ProxyConfig;
import dev.endxxr.enderss.common.utils.ChatUtils;
import dev.endxxr.enderss.common.utils.LogUtils;
import litebans.api.Database;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.List;
import java.util.concurrent.CompletableFuture;


public class ConnectionListener implements Listener {

    private final EnderSS api;

    public ConnectionListener() {
        this.api = EnderSSProvider.getApi();
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProxyJoin(PostLoginEvent e){
        ProxiedPlayer player = e.getPlayer();
        ProxyPlayer session = (ProxyPlayer) api.getPlayersManager().registerPlayer(player.getUniqueId());
        if (player.hasPermission("enderss.admin") && api.isUpdateAvailable()) {
            player.sendMessage(BungeeChat.formatComponent("&8[&d&lEnder&5&lSS&8]&f You need to &cupdate &fthe plugin!"));
        }
        session.setLastServer(ProxyConfig.FALLBACK_SERVER.getString());

    }


    //NOTE: this method is fired even if the player is kicked for a ban. If we want to use a ban-on-quit feature, we need to check if the player is banned before executing the commands.
    @EventHandler
    public void onProxyQuit(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        ProxyPlayer proxyPlayer = (ProxyPlayer) api.getPlayersManager().getPlayer(player.getUniqueId());

        if (proxyPlayer==null) { //If the player is null, we don't need to do anything else.
            LogUtils.prettyPrintException(new NullPointerException("Player is null!"), "Couldn't get the profile of the quitting player!");
            return;
        }

        api.getPlayersManager().unregisterPlayer(proxyPlayer); //Remove player from the list


       /*
            SUSPECT QUITS
        */

        if (proxyPlayer.isFrozen()) { // If the player controlled quits
            ProxyPlayer ssStaff = (ProxyPlayer) proxyPlayer.getStaffer();
            ProxiedPlayer staff = ProxyServer.getInstance().getPlayer(ssStaff.getUUID());
            ssStaff.setControlled(null);


            // If the "Fallback Staff" option is active, the staff will be sent to the fallback server or the last server

            if (ProxyConfig.CONFIG_FALLBACK_STAFF.getBoolean()) {
                if (ProxyConfig.CONFIG_LAST_CONNECTED_SERVER.getBoolean()) {
                    staff.connect(ProxyServer.getInstance().getServerInfo(proxyPlayer.getLastServer()));
                } else {
                    staff.connect(ProxyServer.getInstance().getServerInfo(ProxyConfig.FALLBACK_SERVER.getString()));
                }
            }

            //Send messages
            api.getPlayersManager().broadcastStaff(ChatUtils.format(GlobalConfig.MESSAGES_INFO_PLAYER_QUIT.getMessage(), "%SUSPECT%", player.getName(), "%STAFF%", staff.getName()));

            //Ban On Quit
            List<String> banOnQuitCommands = GlobalConfig.BAN_ON_QUIT_COMMANDS.getStringList();
            if (!banOnQuitCommands.isEmpty()) {

                if (api.getPlugin().isLiteBansPresent() && GlobalConfig.BAN_ON_QUIT_PREVENT_DOUBLE_BAN.getBoolean()) {
                    if (CompletableFuture.supplyAsync(() -> Database.get().isPlayerBanned(player.getUniqueId(), null)).join()) {
                        return;
                    }
                }

                for (String command : banOnQuitCommands) {
                    command = command.replace("%SUSPECT%", player.getName());
                    api.getPlugin().dispatchCommand(null, command);
                }
            }

            api.getPlugin().sendPluginMessage(
                    ssStaff,
                    proxyPlayer,
                    PluginMessageType.END
            );

            ProxyServer.getInstance().getPluginManager().callEvent(new SsEndEvent(staff, player, SSEndCause.SUSPECT_QUIT));


            return;
        }

        /*
            STAFFER QUITS
         */

        if (proxyPlayer.getControlled()!=null) { //If the player was screensharing someone
            ProxiedPlayer suspect = ProxyServer.getInstance().getPlayer(proxyPlayer.getControlled().getUUID());
            api.getScreenShareManager().clearPlayer(suspect.getUniqueId());
            suspect.sendMessage(BungeeChat.formatComponent(GlobalConfig.MESSAGES_ERROR_STAFF_OFFLINE.getMessage()));
            if (ProxyConfig.CONFIG_LAST_CONNECTED_SERVER.getBoolean()){
                suspect.connect(ProxyServer.getInstance().getServerInfo(proxyPlayer.getLastServer()));
            } else {
                suspect.connect(ProxyServer.getInstance().getServerInfo(ProxyConfig.FALLBACK_SERVER.getString()));
            }


            api.getPlugin().sendPluginMessage(
                    proxyPlayer,
                    PluginMessageType.END
            );


            ProxyServer.getInstance().getPluginManager().callEvent(new SsEndEvent(player, suspect, SSEndCause.STAFF_QUIT));


        }
    }



    @EventHandler
    public void onPlayerKick(ServerKickEvent event) {

        if (event.isCancelled()) return;

        ServerInfo serverInfo = event.getKickedFrom();
        SsPlayer ssPlayer = api.getPlayersManager().getPlayer(event.getPlayer().getUniqueId());


        if (ProxyServer.getInstance().getServerInfo(ProxyConfig.FALLBACK_SERVER.getString()).equals(serverInfo)) {
            if (ssPlayer != null && (ssPlayer.isFrozen() || ssPlayer.getControlled()!=null)) {
                api.getScreenShareManager().clearPlayer(ssPlayer.getUUID());
            }
        }
    }

}
