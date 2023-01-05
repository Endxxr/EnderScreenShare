package me.endxxr.enderss.listeners;

import litebans.api.Database;
import me.endxxr.enderss.EnderSS;
import me.endxxr.enderss.enums.Config;
import me.endxxr.enderss.models.SsPlayer;
import me.endxxr.enderss.utils.ChatUtils;
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

    private final EnderSS plugin;

    public JoinLeaveListener(EnderSS plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProxyJoin(PostLoginEvent e){
        ProxiedPlayer player = e.getPlayer();
        SsPlayer session = plugin.getPlayersManager().createNewPlayer(player);
        if (player.hasPermission("enderss.admin") && plugin.isObsoleteVersion()) {
            player.sendMessage(ChatUtils.format("&8[&d&lEnder&5&lSS&8]&f You need to update the &cplugin!"));
        }
        session.setLastServer(Config.CONFIG_FALLBACK.getString());

    }


    //NOTE: this method is fired even if the player is kicked for a ban. If we want to use a ban-on-quit feature, we need to check if the player is banned before executing the commands.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProxyQuit(PlayerDisconnectEvent event) {
        final ProxiedPlayer player = event.getPlayer();
        final SsPlayer session = plugin.getPlayersManager().getPlayer(player);
        plugin.getPlayersManager().terminatePlayer(player); //Remove player from the list

       /*
            SUSPECT QUITS
        */

        if (session.isFrozen()) { // If the player controlled quits
            final ProxiedPlayer staff = session.getStaffer();
            final SsPlayer staffSession = plugin.getPlayersManager().getPlayer(staff);
            // SsManager.terminateScreenShare(staff);
            staffSession.setControlled(null);


            // If the "Fallback Staff" option is active, the staff will be sent to the fallback server or the last server

            if (Config.CONFIG_FALLBACK_STAFF.getBoolean()) {
                if (Config.CONFIG_LAST_CONNECTED_SERVER.getBoolean()) {
                    staff.connect(ProxyServer.getInstance().getServerInfo(session.getLastServer()));
                } else {
                    staff.connect(ProxyServer.getInstance().getServerInfo(Config.CONFIG_FALLBACK.getString()));
                }
            }

            //Send messages
            for (ProxiedPlayer onlinePlayer : ProxyServer.getInstance().getPlayers()) {
                if (onlinePlayer.hasPermission("enderss.staff")) {
                    onlinePlayer.sendMessage(ChatUtils.format(Config.MESSAGES_INFO_PLAYER_QUIT.getMessage(), "%SUSPECT%", player.getName(), "%STAFF%", staff.getName()));
                }
            }

            //Ban On Quit
            List<String> banOnQuitCommands = Config.BAN_ON_QUIT_COMMANDS.getStringList();
            if (!banOnQuitCommands.isEmpty()) {

                if (plugin.isLiteBansPresent() && Config.BAN_ON_QUIT_PREVENT_DOUBLE_BAN.getBoolean()) {
                    if (CompletableFuture.supplyAsync(() -> Database.get().isPlayerBanned(player.getUniqueId(), null)).join()) {
                        return;
                    }
                }

                for (String command : banOnQuitCommands) {
                    plugin.getProxy().getPluginManager().dispatchCommand(plugin.getProxy().getConsole(), command.replace("%SUSPECT%", player.getName()));
                }
            }

            if (Config.SCOREBOARD_ENABLED.getBoolean()) plugin.getScoreboardManager().endScoreboard(staff, player);
            return;
        }

        /*
            STAFFER QUITS
         */

        if (session.getControlled()!=null) { //If the player was screensharing someone
            final ProxiedPlayer sus = session.getControlled();
            final SsPlayer susSession = plugin.getPlayersManager().getPlayer(sus);
            plugin.getCleanCommand().cleanPlayer(sus);
            susSession.setFrozen(false);
            susSession.setStaffer(null);
            sus.sendMessage(ChatUtils.format(Config.MESSAGES_ERROR_STAFF_OFFLINE.getMessage()));
            if (Config.CONFIG_LAST_CONNECTED_SERVER.getBoolean()){
                sus.connect(ProxyServer.getInstance().getServerInfo(session.getLastServer()));
            } else {
                sus.connect(ProxyServer.getInstance().getServerInfo(Config.CONFIG_FALLBACK.getString()));
            }
            if (Config.SCOREBOARD_ENABLED.getBoolean()) plugin.getScoreboardManager().endScoreboard(player, sus);
            return;
        }
    }
}
