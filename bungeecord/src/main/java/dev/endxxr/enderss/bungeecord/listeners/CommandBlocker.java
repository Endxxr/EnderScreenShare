package dev.endxxr.enderss.bungeecord.listeners;

import dev.endxxr.enderss.api.EnderSS;
import dev.endxxr.enderss.api.EnderSSProvider;
import dev.endxxr.enderss.api.objects.player.SsPlayer;
import dev.endxxr.enderss.common.storage.GlobalConfig;
import dev.endxxr.enderss.api.utils.ChatUtils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.ArrayList;
import java.util.List;

public class CommandBlocker implements Listener {

    private final EnderSS api;

    public CommandBlocker() {
        this.api = EnderSSProvider.getApi();
    }


    /**
     * Blocks commands from players when being controlled
     *
     * @param event the event
     */

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(ChatEvent event) {
        ProxiedPlayer sender = (ProxiedPlayer) event.getSender();
        SsPlayer senderSession = api.getPlayersManager().getPlayer(sender.getUniqueId());

        if (!event.isCommand() || !senderSession.isFrozen() || senderSession.isStaff()) {
            return;
        }

        String message = event.getMessage();
        if (GlobalConfig.COMMAND_BLOCKER_ENABLED.getBoolean()) {
            List<String> whitelist = GlobalConfig.COMMAND_BLOCKER_WHITELISTED_COMMANDS.getStringList();
            List<String> slashCommands = new ArrayList<>();
            whitelist.forEach(command -> {
                if (command.startsWith("/")) {
                    slashCommands.add(command);
                }
            });
            if (!slashCommands.contains(message)) {
                event.setCancelled(true);
                sender.sendMessage(ChatUtils.formatComponent(GlobalConfig.MESSAGES_ERROR_CANT_EXECUTE.getMessage()));
                String replacedMessage = GlobalConfig.MESSAGES_INFO_COMMAND_BLOCKED.getMessage().replace("%COMMAND%", message);
                ProxiedPlayer receiver = ProxyServer.getInstance().getPlayer(senderSession.getStaffer().getUUID());
                receiver.sendMessage(ChatUtils.formatComponent(replacedMessage.replace("%SUSPECT%", sender.getDisplayName())));
            }
        }
    }
}
