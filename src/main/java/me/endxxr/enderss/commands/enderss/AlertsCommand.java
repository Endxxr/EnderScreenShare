package me.endxxr.enderss.commands.enderss;

import me.endxxr.enderss.EnderSS;
import me.endxxr.enderss.enums.Config;
import me.endxxr.enderss.utils.ChatUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class AlertsCommand implements SubCommand {
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
            sender.sendMessage(ChatUtils.format("You must be a player to execute this command!"));
            return;
        }

        final ProxiedPlayer player = (ProxiedPlayer) sender;
        EnderSS.getInstance().getPlayersManager().getPlayer(player).setAlerts(!EnderSS.getInstance().getPlayersManager().getPlayer(player).isAlerts());
        final String messageToSend = EnderSS.getInstance().getPlayersManager().getPlayer(player).isAlerts() ? Config.MESSAGES_INFO_ALERTS_ENABLED.getMessage() : Config.MESSAGES_INFO_ALERTS_DISABLED.getMessage();
        player.sendMessage(ChatUtils.format(messageToSend));

    }
}
