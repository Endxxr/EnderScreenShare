package me.endxxr.enderss.listeners;

import me.endxxr.enderss.EnderSS;
import me.endxxr.enderss.enums.Config;
import me.endxxr.enderss.models.SsPlayer;
import me.endxxr.enderss.utils.ChatUtils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class SwitchListener implements Listener {

    private final EnderSS plugin;

    public SwitchListener(EnderSS plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSwitch(ServerSwitchEvent event) {
        SsPlayer ssPlayer = plugin.getPlayersManager().getPlayer(event.getPlayer());
        ServerInfo lastServer = event.getFrom();
        
        if (lastServer != null) {
            ssPlayer.setLastServer(lastServer.getName());

            if (!ssPlayer.isFrozen() && ssPlayer.getControlled()==null //If the player is not frozen and is not controlling anyone
                    && Config.SCOREBOARD_ENABLED.getBoolean()
                    && !event.getFrom().getName().equalsIgnoreCase(Config.CONFIG_SSSERVER.getString())
                    && event.getPlayer().getServer().getInfo().getName().equalsIgnoreCase(Config.CONFIG_SSSERVER.getString())) {

                if (Config.SCOREBOARD_ENABLED.getBoolean()) plugin.getScoreboardManager().sendIdlingScoreboard(event.getPlayer());
            }
        }
        
        if (event.getPlayer().hasPermission("enderss.admin") && plugin.isObsoleteVersion()) {
            event.getPlayer().sendMessage(ChatUtils.format("&8[&d&lEnder&5&lSS&8]&f A new version is available!"));
        }

        if (lastServer == ProxyServer.getInstance().getServerInfo(Config.CONFIG_SSSERVER.getString()) && ssPlayer.getControlled() != null) { // If the player was controlling anyone
            plugin.getCleanCommand().cleanPlayer(event.getPlayer(), ssPlayer.getControlled());
        }

    }

}
