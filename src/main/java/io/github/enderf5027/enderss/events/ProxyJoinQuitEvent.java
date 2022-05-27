package io.github.enderf5027.enderss.events;

import io.github.enderf5027.enderss.session.PlayerSession;
import io.github.enderf5027.enderss.utils.config;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import static io.github.enderf5027.enderss.Enderss.obsoleteVersion;
import static io.github.enderf5027.enderss.session.SessionManager.*;
import static io.github.enderf5027.enderss.utils.ChatUtils.format;

public class ProxyJoinQuitEvent implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProxyJoin(PostLoginEvent e){
        ProxiedPlayer p = e.getPlayer();
        addSession(p);
        PlayerSession session = getSession(p);
        if (p.hasPermission("enderss.staff")) {
            session.setStaff(true);
        }
        if (p.hasPermission("enderss.admin") && obsoleteVersion) {
            p.sendMessage(format("&8[&d&lEnder&5&lSS&8]&f You need to update the &cplugin!"));
        }
        ServerInfo LastServer = ProxyServer.getInstance().getServerInfo(config.FallbackServer);
        session.setLastServer(LastServer);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProxyQuit(PlayerDisconnectEvent e) {
        ProxiedPlayer p = e.getPlayer();
        PlayerSession session = getSession(p);
        ProxiedPlayer staff = session.getStaffer();
        removeSession(p);
        if (session.isFrozen()) {
            if (config.banonquit) ProxyServer.getInstance().getPluginManager().dispatchCommand( staff, config.quit.replace("%SUSPECT%", p.getName()));
            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                PlayerSession playerSession = getSession(player);
                if (playerSession.isStaff()) {
                    if (playerSession.getAlertsEnabled()) { player.sendMessage(format(config.playerquit, staff, p)); }
                    if (config.LastConnectedServer){
                        ServerInfo lastServer = getSession(staff).getLastServer();
                        if (config.FallBackStaff && !(lastServer==null)) staff.connect(lastServer);
                    } else {
                        if (config.FallBackStaff) player.connect(ProxyServer.getInstance().getServerInfo(config.FallbackServer));
                    }
                }
            }
        }

        if (!(session.getScreenSharing()==null)) {
            ProxiedPlayer sus = session.getScreenSharing();
            PlayerSession susSession = getSession(sus);
            CommandSender console = ProxyServer.getInstance().getConsole();
            ProxyServer.getInstance().getPluginManager().dispatchCommand(console, "/clean "+sus.getName());
            susSession.setFrozen(false);
            susSession.setStaffer(null);
            sus.sendMessage(format(config.cleanplayer, p, sus));
            if (config.LastConnectedServer){
                ServerInfo lastServer = susSession.getLastServer();
                sus.connect(lastServer);
            } else {
                sus.connect(ProxyServer.getInstance().getServerInfo(config.FallbackServer));
            }
        }

    }
}
