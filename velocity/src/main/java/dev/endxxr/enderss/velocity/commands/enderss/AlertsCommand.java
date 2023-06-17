package dev.endxxr.enderss.velocity.commands.enderss;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.connection.Player;
import dev.endxxr.enderss.api.EnderSSProvider;
import dev.endxxr.enderss.api.objects.player.SsPlayer;
import dev.endxxr.enderss.api.utils.ChatUtils;
import dev.endxxr.enderss.common.storage.GlobalConfig;

public class AlertsCommand implements VelocitySubCommand {

    @Override
    public String getName() {
        return "alerts";
    }

    @Override
    public String getPermission() {
        return "enderss.settings.alerts";
    }

    @Override
    public void execute(CommandSource sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatUtils.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_CONSOLE.getMessage()));
            return;
        }

        Player player = (Player) sender;
        SsPlayer proxyPlayer = EnderSSProvider.getApi().getPlayersManager().getPlayer(player.id());
        proxyPlayer.hasAlerts(!proxyPlayer.hasAlerts());
        String messageToSend = proxyPlayer.hasAlerts() ? GlobalConfig.MESSAGES_INFO_ALERTS_ENABLED.getMessage() : GlobalConfig.MESSAGES_INFO_ALERTS_DISABLED.getMessage();
        player.sendMessage(ChatUtils.formatAdventureComponent(messageToSend));

    }
}
