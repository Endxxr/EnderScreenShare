package dev.endxxr.enderss.bungeecord.commands;

import dev.endxxr.enderss.api.EnderSS;
import dev.endxxr.enderss.api.EnderSSProvider;
import dev.endxxr.enderss.api.objects.player.SsPlayer;
import dev.endxxr.enderss.common.storage.GlobalConfig;
import dev.endxxr.enderss.api.utils.ChatUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.List;

public class CleanCommand extends Command implements TabExecutor {

    private final EnderSS api;

    public CleanCommand() {
        super("clean", "enderss.clean", "nohack","legit");
        api = EnderSSProvider.getApi();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(ChatUtils.formatComponent(GlobalConfig.MESSAGES_ERROR_CONSOLE.getMessage()));
            return;
        }

        final ProxiedPlayer staff = (ProxiedPlayer) sender;

        if (!staff.hasPermission("enderss.staff") && !staff.hasPermission("enderss.clean")) {
            staff.sendMessage(ChatUtils.formatComponent(GlobalConfig.MESSAGES_ERROR_NO_PERMISSION.getMessage()));
            return;
        }


        if (!api.getPlayersManager().getPlayer(staff.getUniqueId()).isStaff()) {
            staff.sendMessage(ChatUtils.formatComponent(GlobalConfig.MESSAGES_ERROR_NO_PERMISSION.getMessage()));
            return;
        }

        if (args.length == 0) {
            staff.sendMessage(ChatUtils.formatComponent(GlobalConfig.MESSAGES_ERROR_NO_PLAYER.getMessage()));
            return;
        }

        final ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);

        if (target == null) {
            staff.sendMessage(ChatUtils.formatComponent(GlobalConfig.MESSAGES_ERROR_PLAYER_OFFLINE.getMessage(), "%SUSPECT%", args[0]));
            return;
        }

        api.getScreenShareManager().clearPlayer(staff.getUniqueId(), target.getUniqueId());

    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> players = new ArrayList<>();
        for (SsPlayer player : api.getPlayersManager().getRegisteredPlayers()) {
            if (player.isFrozen()) {
                String name = ProxyServer.getInstance().getPlayer(player.getUUID()).getName();
                if (name.toLowerCase().startsWith(args[0].toLowerCase())) {
                    players.add(name);
                }
            }
        }
        return players;
    }

}
