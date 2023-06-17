package dev.endxxr.enderss.velocity.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.connection.Player;
import dev.endxxr.enderss.api.EnderSS;
import dev.endxxr.enderss.api.EnderSSProvider;
import dev.endxxr.enderss.api.objects.player.SsPlayer;
import dev.endxxr.enderss.api.utils.ChatUtils;
import dev.endxxr.enderss.common.storage.GlobalConfig;

import java.util.ArrayList;
import java.util.List;

public class CommandBlocker {

    private final EnderSS api;
    private final ProxyServer server;

    public CommandBlocker(ProxyServer server) {
        this.server = server;
        this.api = EnderSSProvider.getApi();
    }

    @Subscribe
    public void onCommand(CommandExecuteEvent event) {
        
        if (!(event.source() instanceof Player)) {
            return;
        }
        
        Player sender = (Player) event.source();
        SsPlayer senderSession = api.getPlayersManager().getPlayer(sender.id());

        if (!senderSession.isFrozen() || senderSession.isStaff()) {
            return;
        }

        String message = event.rawCommand();
        if (GlobalConfig.COMMAND_BLOCKER_ENABLED.getBoolean()) {
            List<String> whitelist = GlobalConfig.COMMAND_BLOCKER_WHITELISTED_COMMANDS.getStringList();
            List<String> slashCommands = new ArrayList<>();
            whitelist.forEach(command -> {
                if (command.startsWith("/")) {
                    slashCommands.add(command);
                }
            });
            if (!slashCommands.contains(message)) {
                event.setResult(CommandExecuteEvent.CommandResult.denied());
                sender.sendMessage(ChatUtils.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_CANT_EXECUTE.getMessage()));
                String replacedMessage = GlobalConfig.MESSAGES_INFO_COMMAND_BLOCKED.getMessage().replace("%COMMAND%", message);
                Player receiver = server.player(senderSession.getStaffer().getUUID());
                receiver.sendMessage(ChatUtils.formatAdventureComponent(replacedMessage.replace("%SUSPECT%", sender.username())));
            }
        }
    }


}
