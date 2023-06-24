package dev.endxxr.enderss.velocity.commands.enderss;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import dev.endxxr.enderss.api.EnderSSProvider;
import dev.endxxr.enderss.api.objects.player.SsPlayer;
import dev.endxxr.enderss.velocity.utils.VelocityChat;
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
            sender.sendMessage(VelocityChat.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_CONSOLE.getMessage()));
            return;
        }

        Player player = (Player) sender;
        SsPlayer proxyPlayer = EnderSSProvider.getApi().getPlayersManager().getPlayer(player.getUniqueId());

        if (proxyPlayer == null) {
            player.sendMessage(VelocityChat.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_GENERIC.getMessage()));
            EnderSSProvider.getApi().getPlugin().getLog().severe("Wasn't able to get the profile of the player, is it online?");
            return;
        }


        proxyPlayer.hasAlerts(!proxyPlayer.hasAlerts());
        String messageToSend = proxyPlayer.hasAlerts() ? GlobalConfig.MESSAGES_INFO_ALERTS_ENABLED.getMessage() : GlobalConfig.MESSAGES_INFO_ALERTS_DISABLED.getMessage();
        player.sendMessage(VelocityChat.formatAdventureComponent(messageToSend));

    }
}
