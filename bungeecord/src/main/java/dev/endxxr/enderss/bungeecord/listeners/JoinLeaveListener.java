package dev.endxxr.enderss.bungeecord.listeners;

import dev.endxxr.enderss.api.EnderSSAPI;
import dev.endxxr.enderss.api.enums.PluginMessageType;
import dev.endxxr.enderss.api.enums.SSEndCause;
import dev.endxxr.enderss.api.events.bungee.SsEndEvent;
import dev.endxxr.enderss.api.objects.SSPlayer;
import dev.endxxr.enderss.bungeecord.utils.BungeeChat;
import dev.endxxr.enderss.common.storage.GlobalConfig;
import dev.endxxr.enderss.common.storage.ProxyConfig;
import litebans.api.Database;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.List;
import java.util.concurrent.CompletableFuture;


public class JoinLeaveListener implements Listener {

    private final EnderSSAPI api;

    public JoinLeaveListener() {
        this.api = EnderSSAPI.Provider.getApi();
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProxyJoin(PostLoginEvent e){
        ProxiedPlayer player = e.getPlayer();
        SSPlayer session = api.getPlayersManager().addPlayer(player.getUniqueId());
        if (player.hasPermission("enderss.admin") && api.isUpdateAvailable()) {
            player.sendMessage(BungeeChat.format("&8[&d&lEnder&5&lSS&8]&f You need to &cupdate &fthe plugin!"));
        }
        session.setLastServer(ProxyConfig.FALLBACK_SERVER.getString());

    }


    //NOTE: this method is fired even if the player is kicked for a ban. If we want to use a ban-on-quit feature, we need to check if the player is banned before executing the commands.
    @EventHandler
    public void onProxyQuit(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        SSPlayer ssPlayer = api.getPlayersManager().getPlayer(player.getUniqueId());
        api.getPlayersManager().terminatePlayer(ssPlayer); //Remove player from the list

       /*
            SUSPECT QUITS
        */

        if (ssPlayer.isFrozen()) { // If the player controlled quits
            SSPlayer ssStaff = ssPlayer.getStaffer();
            ProxiedPlayer staff = ProxyServer.getInstance().getPlayer(ssStaff.getUUID());
            ssStaff.setControlled(null);


            // If the "Fallback Staff" option is active, the staff will be sent to the fallback server or the last server

            if (ProxyConfig.CONFIG_FALLBACK_STAFF.getBoolean()) {
                if (ProxyConfig.CONFIG_LAST_CONNECTED_SERVER.getBoolean()) {
                    staff.connect(ProxyServer.getInstance().getServerInfo(ssPlayer.getLastServer()));
                } else {
                    staff.connect(ProxyServer.getInstance().getServerInfo(ProxyConfig.FALLBACK_SERVER.getString()));
                }
            }

            //Send messages
            for (ProxiedPlayer onlinePlayer : ProxyServer.getInstance().getPlayers()) {
                if (onlinePlayer.hasPermission("enderss.staff")) {
                    onlinePlayer.sendMessage(BungeeChat.format(GlobalConfig.MESSAGES_INFO_PLAYER_QUIT.getMessage(), "%SUSPECT%", player.getName(), "%STAFF%", staff.getName()));
                }
            }

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
                    ssPlayer,
                    PluginMessageType.END
            );

            ProxyServer.getInstance().getPluginManager().callEvent(new SsEndEvent(staff, player, SSEndCause.SUSPECT_QUIT));


            return;
        }

        /*
            STAFFER QUITS
         */

        if (ssPlayer.getControlled()!=null) { //If the player was screensharing someone
            ProxiedPlayer suspect = ProxyServer.getInstance().getPlayer(ssPlayer.getControlled().getUUID());
            SSPlayer susSession = api.getPlayersManager().getPlayer(suspect.getUniqueId());
            api.getScreenShareManager().clearPlayer(suspect.getUniqueId());
            susSession.setFrozen(false);
            susSession.setStaffer(null);
            suspect.sendMessage(BungeeChat.format(GlobalConfig.MESSAGES_ERROR_STAFF_OFFLINE.getMessage()));
            if (ProxyConfig.CONFIG_LAST_CONNECTED_SERVER.getBoolean()){
                suspect.connect(ProxyServer.getInstance().getServerInfo(ssPlayer.getLastServer()));
            } else {
                suspect.connect(ProxyServer.getInstance().getServerInfo(ProxyConfig.FALLBACK_SERVER.getString()));
            }
            ProxyServer.getInstance().getPluginManager().callEvent(new SsEndEvent(player, suspect, SSEndCause.STAFF_QUIT));
        }
    }
}
