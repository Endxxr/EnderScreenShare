package dev.endxxr.enderss.bungeecord.commands.enderss;

import dev.endxxr.enderss.api.EnderSSProvider;
import dev.endxxr.enderss.api.objects.player.SsPlayer;
import dev.endxxr.enderss.bungeecord.utils.BungeeChat;
import dev.endxxr.enderss.common.storage.GlobalConfig;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class AlertsCommand implements BungeeSubCommand {

    @Override
    public String getName() {
        return "alerts";
    }

    @Override
    public String getPermission() {
        return "enderss.settings.alerts";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(BungeeChat.formatComponent(GlobalConfig.MESSAGES_ERROR_CONSOLE.getMessage()));
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;
        SsPlayer proxyPlayer = EnderSSProvider.getApi().getPlayersManager().getPlayer(player.getUniqueId());

        if (proxyPlayer == null) {
            player.sendMessage(BungeeChat.formatComponent(GlobalConfig.MESSAGES_ERROR_GENERIC.getMessage()));
            EnderSSProvider.getApi().getPlugin().getLog().severe("Wasn't able to get the profile of the player, is it online?");
            return;
        }

        proxyPlayer.hasAlerts(!proxyPlayer.hasAlerts());
        String messageToSend = proxyPlayer.hasAlerts() ? GlobalConfig.MESSAGES_INFO_ALERTS_ENABLED.getMessage() : GlobalConfig.MESSAGES_INFO_ALERTS_DISABLED.getMessage();
        player.sendMessage(BungeeChat.formatComponent(messageToSend));

    }
}
