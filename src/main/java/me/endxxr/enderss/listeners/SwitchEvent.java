package me.endxxr.enderss.listeners;

import me.endxxr.enderss.EnderSS;
import me.endxxr.enderss.enums.Config;
import me.endxxr.enderss.models.SsPlayer;
import me.endxxr.enderss.utils.ChatUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class SwitchEvent implements Listener {

    private final EnderSS plugin = EnderSS.getInstance();

    @EventHandler
    public void onSwitch(ServerSwitchEvent e) {
        SsPlayer ssPlayer = EnderSS.getInstance().getPlayersManager().getPlayer(e.getPlayer());
        ServerInfo lastServer = e.getFrom();
        ssPlayer.setLastServer(lastServer.getName());

        if (!ssPlayer.isFrozen() && ssPlayer.getControlled()==null //If the player is not frozen and is not controlling anyone
            && e.getPlayer().getServer().getInfo().getName().equals(Config.CONFIG_SSSERVER.getString())) {
            plugin.getScoreboardManager().sendIdlingScoreboard(e.getPlayer());
        }

        if (e.getPlayer().hasPermission("enderss.admin") && EnderSS.getInstance().isObsoleteVersion()) {
            e.getPlayer().sendMessage(ChatUtils.format("&8[&d&lEnder&5&lSS&8]&f A new version is available!!"));
        }

        if (lastServer == ProxyServer.getInstance().getServerInfo(Config.CONFIG_SSSERVER.getString()) && ssPlayer.getControlled() != null) { // If the player was controlling anyone
            plugin.getCleanCommand().cleanPlayer(e.getPlayer(), ssPlayer.getControlled());
        }

    }

}
