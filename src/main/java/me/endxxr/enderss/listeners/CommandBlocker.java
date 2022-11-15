package me.endxxr.enderss.listeners;

import me.endxxr.enderss.EnderSS;
import me.endxxr.enderss.enums.Config;
import me.endxxr.enderss.models.SsPlayer;
import me.endxxr.enderss.utils.ChatUtils;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class CommandBlocker implements Listener {

    /**
     * Blocks commands from players when being controlled
     *
     * @param event
     */

    @EventHandler(priority = EventPriority.NORMAL)
    public void onCommand(ChatEvent event) {
        ProxiedPlayer sender = (ProxiedPlayer) event.getSender();
        SsPlayer senderSession = EnderSS.getInstance().getPlayersManager().getPlayer(sender);

        if (!event.isCommand() || !senderSession.isFrozen() || senderSession.isStaff()) {
            return;
        }

        String message = event.getMessage();
        if (Config.COMMAND_BLOCKER_ENABLED.getBoolean()) {
            if (!Config.COMMAND_BLOCKER_WHITELISTED_COMMANDS.getStringList().contains(message)) {
                event.setCancelled(true);
                sender.sendMessage(ChatUtils.format(Config.MESSAGES_ERROR_CANT_EXECUTE.getMessage()));
                String replacedMessage = Config.MESSAGES_INFO_COMMAND_BLOCKED.getMessage().replace("%COMMAND%", message);
                senderSession.getStaffer().sendMessage(ChatUtils.format(replacedMessage.replace("%SUSPECT%", sender.getDisplayName())));
            }
        }
    }
}
