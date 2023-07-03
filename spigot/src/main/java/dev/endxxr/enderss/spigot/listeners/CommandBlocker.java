package dev.endxxr.enderss.spigot.listeners;

import dev.endxxr.enderss.api.EnderSS;
import dev.endxxr.enderss.api.EnderSSProvider;
import dev.endxxr.enderss.api.objects.player.SsPlayer;
import dev.endxxr.enderss.common.storage.GlobalConfig;
import dev.endxxr.enderss.common.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.List;

public class CommandBlocker implements Listener {
    private final EnderSS api;

    public CommandBlocker() {
        this.api = EnderSSProvider.getApi();
    }

    @EventHandler
    public void onCommand(AsyncPlayerChatEvent event) {

        if (event.isCancelled()) return;

        Player sender = event.getPlayer();
        SsPlayer senderSession = api.getPlayersManager().getPlayer(sender.getUniqueId());


        if (senderSession == null || !senderSession.isFrozen() || senderSession.isStaff() || !event.getMessage().startsWith("/")) {
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
                sender.sendMessage(ChatUtils.format(GlobalConfig.MESSAGES_ERROR_CANT_EXECUTE.getMessage()));
                String replacedMessage = GlobalConfig.MESSAGES_INFO_COMMAND_BLOCKED.getMessage().replace("%COMMAND%", message);

                Player receiver = Bukkit.getPlayer(senderSession.getStaffer().getUUID());
                receiver.sendMessage(ChatUtils.format(replacedMessage.replace("%SUSPECT%", sender.getName())));

            }
        }
    }

}
