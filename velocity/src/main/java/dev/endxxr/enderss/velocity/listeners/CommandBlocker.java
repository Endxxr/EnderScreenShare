package dev.endxxr.enderss.velocity.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.Player;
import dev.endxxr.enderss.api.EnderSS;
import dev.endxxr.enderss.api.EnderSSProvider;
import dev.endxxr.enderss.api.objects.player.SsPlayer;
import dev.endxxr.enderss.velocity.utils.VelocityChat;
import dev.endxxr.enderss.common.storage.GlobalConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CommandBlocker {

    private final EnderSS api;
    private final ProxyServer server;

    public CommandBlocker(ProxyServer server) {
        this.server = server;
        this.api = EnderSSProvider.getApi();
    }

    @Subscribe
    public void onCommand(CommandExecuteEvent event) {
        
        if (!(event.getCommandSource() instanceof Player)) return;
        if (event.getResult() == CommandExecuteEvent.CommandResult.denied()) return;
        
        Player sender = (Player) event.getCommandSource();
        SsPlayer senderSession = api.getPlayersManager().getPlayer(sender.getUniqueId());


        if (senderSession == null || !senderSession.isFrozen() || senderSession.isStaff()) {
            return;
        }

        String message = event.getCommand();
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
                sender.sendMessage(VelocityChat.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_CANT_EXECUTE.getMessage()));
                String replacedMessage = GlobalConfig.MESSAGES_INFO_COMMAND_BLOCKED.getMessage().replace("%COMMAND%", message);

                Optional<Player> optionalReceiver = server.getPlayer(senderSession.getStaffer().getUUID());
                if (!optionalReceiver.isPresent()) return;
                optionalReceiver.get().sendMessage(VelocityChat.formatAdventureComponent(replacedMessage.replace("%SUSPECT%", sender.getUsername())));

            }
        }
    }


}
