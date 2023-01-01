package me.endxxr.enderss.commands.enderss;

import me.endxxr.enderss.EnderSS;
import me.endxxr.enderss.enums.Config;
import me.endxxr.enderss.models.SsPlayer;
import me.endxxr.enderss.utils.ChatUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class AlertsCommand implements SubCommand {

    private final EnderSS plugin = EnderSS.getInstance();

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
            sender.sendMessage(ChatUtils.format(Config.MESSAGES_ERROR_CONSOLE.getMessage()));
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;
        SsPlayer ssPlayer = plugin.getPlayersManager().getPlayer(player);
        ssPlayer.setAlerts(!ssPlayer.isAlerts());
        String messageToSend = ssPlayer.isAlerts() ? Config.MESSAGES_INFO_ALERTS_ENABLED.getMessage() : Config.MESSAGES_INFO_ALERTS_DISABLED.getMessage();
        player.sendMessage(ChatUtils.format(messageToSend));

    }
}
