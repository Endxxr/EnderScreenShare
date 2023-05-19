package dev.endxxr.enderss.bungeecord.commands;

import dev.endxxr.enderss.common.storage.GlobalConfig;
import dev.endxxr.enderss.bungeecord.utils.BungeeChat;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BlatantCommand extends Command implements TabExecutor {
    public BlatantCommand() {
        super("blatant", "enderss.blatant");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!sender.hasPermission("enderss.blatant")) {
            sender.sendMessage(BungeeChat.format(GlobalConfig.MESSAGES_ERROR_NO_PERMISSION.getMessage()));
            return;
        }

        if (args.length == 0) {
            sender.sendMessage(BungeeChat.format(GlobalConfig.MESSAGES_ERROR_NO_PLAYER.getMessage()));
            return;
        }

        final ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
        if (target==null){
            sender.sendMessage(BungeeChat.format(GlobalConfig.MESSAGES_ERROR_PLAYER_OFFLINE.getMessage()));
            return;
        }

        String command = GlobalConfig.BAN_COMMAND_BLATANT.getString()
                .replaceAll("%SUSPECT%", target.getName());
        if (command.startsWith("/")) {
            command = command.replace("/", "");
        }
        ProxyServer.getInstance().getPluginManager().dispatchCommand(sender, command);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return Collections.emptyList();
        }
        List<ProxiedPlayer> players = ProxyServer.getInstance().getPlayers().stream().filter(player -> player.getName().startsWith(args[0])).collect(Collectors.toList());
        return players.stream().map(ProxiedPlayer::getName).collect(Collectors.toList());
    }
}
