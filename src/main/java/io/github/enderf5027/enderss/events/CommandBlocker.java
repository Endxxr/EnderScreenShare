package io.github.enderf5027.enderss.events;

import io.github.enderf5027.enderss.session.PlayerSession;
import io.github.enderf5027.enderss.session.SessionManager;
import io.github.enderf5027.enderss.utils.config;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import static io.github.enderf5027.enderss.utils.ChatUtils.format;

public class CommandBlocker implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(ChatEvent event) {
        final ProxiedPlayer sender = (ProxiedPlayer)event.getSender();
        final PlayerSession senderSession = SessionManager.getSession(sender);

        if (!event.isCommand() || !senderSession.isFrozen() || senderSession.isStaff() ) {
            return;
        }

        String message = event.getMessage();
        if (config.blockCommands) {
            if (!config.whitelistedCommands.contains(message)) {
                event.setCancelled(true);
                sender.sendMessage(format(config.cantexecute));
                senderSession.getStaffer().sendMessage(format(config.commandBlocked.replace("%COMMAND%", message)));
            }
        }
    }

}
