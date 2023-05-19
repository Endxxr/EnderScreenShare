package dev.endxxr.enderss.bungeecord.listeners;

import dev.endxxr.enderss.api.EnderSSAPI;
import dev.endxxr.enderss.api.enums.PluginMessageType;
import dev.endxxr.enderss.api.enums.SSEndCause;
import dev.endxxr.enderss.api.events.bungee.SsEndEvent;
import dev.endxxr.enderss.api.objects.SSPlayer;
import dev.endxxr.enderss.bungeecord.utils.BungeeChat;
import dev.endxxr.enderss.common.storage.GlobalConfig;
import dev.endxxr.enderss.common.storage.ProxyConfig;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class SwitchListener implements Listener {

    private final EnderSSAPI api;

    public SwitchListener() {
        this.api = EnderSSAPI.Provider.getApi();
    }

    @EventHandler
    public void onSwitch(ServerSwitchEvent event) {
        SSPlayer ssPlayer = api.getPlayersManager().getPlayer(event.getPlayer().getUniqueId());
        ServerInfo lastServer = event.getFrom();
        
        if (lastServer != null) {
            ssPlayer.setLastServer(lastServer.getName());

            if (!ssPlayer.isFrozen() && ssPlayer.getControlled()==null //If the player is not frozen and is not controlling anyone
                    && GlobalConfig.SCOREBOARD_ENABLED.getBoolean()
                    && event.getPlayer().getServer().getInfo().getName().equalsIgnoreCase(ProxyConfig.SS_SERVER.getString())) {

                //if (GlobalConfig.SCOREBOARD_ENABLED.getBoolean()) plugin.getScoreboardManager().sendIdlingScoreboard(event.getPlayer());
            }
        }
        
        if (event.getPlayer().hasPermission("enderss.admin") && api.isUpdateAvailable()) {
            event.getPlayer().sendMessage(BungeeChat.format("&8[&d&lEnder&5&lSS&8]&f A new version is available!"));
        }

        if (event.getPlayer().hasPermission("enderss.admin") && api.getPlugin().isConfigObsolete()) {
            event.getPlayer().sendMessage(BungeeChat.format("&8[&d&lEnder&5&lSS&8]&f Your config is obsolete!"));
        }


        if (lastServer == ProxyServer.getInstance().getServerInfo(ProxyConfig.SS_SERVER.getString()) && ssPlayer.getControlled() != null) { // If the player was controlling anyone
            ProxiedPlayer suspect = ProxyServer.getInstance().getPlayer(ssPlayer.getControlled().getUUID());
            api.getScreenShareManager().clearPlayer(ssPlayer.getUUID(), suspect.getUniqueId());
            //plugin.getScoreboardManager().endScoreboard(event.getPlayer(), ssPlayer.getControlled());
            ProxyServer.getInstance().getPluginManager().callEvent(new SsEndEvent(
                    suspect,
                    event.getPlayer(),
                    SSEndCause.STAFF_SWITCH));

            api.getPlugin().sendPluginMessage(
                    ssPlayer,
                    api.getPlayersManager().getPlayer(suspect.getUniqueId()),
                    PluginMessageType.END
            );

        }

    }

}
