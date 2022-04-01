package io.github.enderf5027.enderss.commands.clean;

import io.github.enderf5027.enderss.session.PlayerSession;
import io.github.enderf5027.enderss.session.SessionManager;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerSwitchEvent implements Listener{

    @EventHandler
    public void onPlayerSwitchEvent(ServerSwitchEvent e){
        PlayerSession session = SessionManager.getSession(e.getPlayer());
        ServerInfo lastServer = e.getFrom();
        session.setLastServer(lastServer);
    }
}
