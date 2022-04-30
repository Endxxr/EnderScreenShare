package io.github.enderf5027.enderss.commands.clean;

import io.github.enderf5027.enderss.Enderss;
import io.github.enderf5027.enderss.session.PlayerSession;
import io.github.enderf5027.enderss.session.SessionManager;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import static io.github.enderf5027.enderss.utils.ChatUtils.format;

public class PlayerSwitchEvent implements Listener{

    @EventHandler
    public void onPlayerSwitchEvent(ServerSwitchEvent e){
        ProxiedPlayer p = e.getPlayer();
        PlayerSession session = SessionManager.getSession(p);
        ServerInfo lastServer = e.getFrom();
        session.setLastServer(lastServer);
        if (p.hasPermission("enderss.admin") && Enderss.obsolete) {
            p.sendMessage(format("&8[&d&lEnder&5&lSS&8]&f You need to &cupdate the config!"));
        }
    }
}
