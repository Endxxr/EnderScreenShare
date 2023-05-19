package dev.endxxr.enderss.bungeecord.commands.enderss;

import dev.endxxr.enderss.api.EnderSSAPI;
import dev.endxxr.enderss.common.storage.GlobalConfig;
import dev.endxxr.enderss.api.objects.SSPlayer;
import dev.endxxr.enderss.bungeecord.utils.BungeeChat;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class AlertsCommand implements SubCommand {

    public AlertsCommand() {
    }

    @Override
    public String getName() {
        return "alerts";
    }

    @Override
    public String getPermission() {
        return "enderss.staff";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(BungeeChat.format(GlobalConfig.MESSAGES_ERROR_CONSOLE.getMessage()));
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;
        SSPlayer ssPlayer = EnderSSAPI.Provider.getApi().getPlayersManager().getPlayer(player.getUniqueId());
        ssPlayer.setAlerts(!ssPlayer.isAlerts());
        String messageToSend = ssPlayer.isAlerts() ? GlobalConfig.MESSAGES_INFO_ALERTS_ENABLED.getMessage() : GlobalConfig.MESSAGES_INFO_ALERTS_DISABLED.getMessage();
        player.sendMessage(BungeeChat.format(messageToSend));

    }
}
