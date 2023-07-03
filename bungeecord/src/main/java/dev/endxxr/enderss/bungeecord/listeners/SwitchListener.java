package dev.endxxr.enderss.bungeecord.listeners;

import dev.endxxr.enderss.api.EnderSS;
import dev.endxxr.enderss.api.EnderSSProvider;
import dev.endxxr.enderss.api.enums.SSEndCause;
import dev.endxxr.enderss.api.events.bungee.SsEndEvent;
import dev.endxxr.enderss.api.objects.player.ProxyPlayer;
import dev.endxxr.enderss.bungeecord.utils.BungeeChat;
import dev.endxxr.enderss.common.storage.ProxyConfig;
import dev.endxxr.enderss.common.utils.LogUtils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class SwitchListener implements Listener {

    private final EnderSS api;

    public SwitchListener() {
        this.api = EnderSSProvider.getApi();
    }

    @EventHandler
    public void onSwitch(ServerSwitchEvent event) {
        ProxyPlayer proxyPlayer = (ProxyPlayer) api.getPlayersManager().getPlayer(event.getPlayer().getUniqueId());
        ServerInfo lastServer = event.getFrom();

        if (proxyPlayer == null) {
            LogUtils.prettyPrintException(new IllegalArgumentException("Wasn't able to get the profile of the player, is it online?"), "Event Error");
            return;
        }

        if (lastServer != null) {
            proxyPlayer.setLastServer(lastServer.getName());
        }
        
        if (event.getPlayer().hasPermission("enderss.admin") && api.isUpdateAvailable()) {
            event.getPlayer().sendMessage(BungeeChat.formatComponent("&8[&d&lEnder&5&lSS&8]&f New version available!"));
        }

        if (event.getPlayer().hasPermission("enderss.admin") && api.isConfigObsolete()) {
            event.getPlayer().sendMessage(BungeeChat.formatComponent("&8[&d&lEnder&5&lSS&8]&f Your config is obsolete!"));
        }


        if (lastServer == ProxyServer.getInstance().getServerInfo(ProxyConfig.SS_SERVER.getString()) && proxyPlayer.getControlled() != null) { // If the player was controlling anyone
            ProxiedPlayer suspect = ProxyServer.getInstance().getPlayer(proxyPlayer.getControlled().getUUID());
            api.getScreenShareManager().clearPlayer(proxyPlayer.getUUID(), suspect.getUniqueId());
            //plugin.getScoreboardManager().endScoreboard(event.getPlayer(), ssPlayer.getControlled());
            ProxyServer.getInstance().getPluginManager().callEvent(new SsEndEvent(
                    suspect,
                    event.getPlayer(),
                    SSEndCause.STAFF_SWITCH));
        }

    }

}
