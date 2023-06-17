package dev.endxxr.enderss.spigot.commands.enderss;

import dev.endxxr.enderss.api.EnderSSProvider;
import dev.endxxr.enderss.api.objects.player.SsPlayer;
import dev.endxxr.enderss.common.storage.GlobalConfig;
import dev.endxxr.enderss.common.storage.SpigotConfig;
import dev.endxxr.enderss.api.utils.ChatUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AlertsCommand implements SpigotSubCommand {

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

        if (SpigotConfig.PROXY_MODE.getBoolean()) {
            sender.sendMessage(ChatUtils.format(SpigotConfig.DISABLED.getString()));
            return;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatUtils.format(GlobalConfig.MESSAGES_ERROR_CONSOLE.getMessage()));
            return;
        }

        Player player = (Player) sender;
        SsPlayer proxyPlayer = EnderSSProvider.getApi().getPlayersManager().getPlayer(player.getUniqueId());
        proxyPlayer.hasAlerts(!proxyPlayer.hasAlerts());
        String messageToSend = proxyPlayer.hasAlerts() ? GlobalConfig.MESSAGES_INFO_ALERTS_ENABLED.getMessage() : GlobalConfig.MESSAGES_INFO_ALERTS_DISABLED.getMessage();
        player.sendMessage(ChatUtils.format(messageToSend));

    }
}
