package dev.endxxr.enderss.bungeecord.commands;

import dev.endxxr.enderss.api.EnderSSAPI;
import dev.endxxr.enderss.common.storage.GlobalConfig;
import dev.endxxr.enderss.api.objects.SSPlayer;
import dev.endxxr.enderss.bungeecord.utils.BungeeChat;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.List;

public class CleanCommand extends Command implements TabExecutor {

    private final EnderSSAPI api;

    public CleanCommand() {
        super("clean", "enderss.staff", "nohack","pulito","legit");
        api = EnderSSAPI.Provider.getApi();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(BungeeChat.format(GlobalConfig.MESSAGES_ERROR_CONSOLE.getMessage()));
            return;
        }

        final ProxiedPlayer staff = (ProxiedPlayer) sender;

        if (!api.getPlayersManager().getPlayer(staff.getUniqueId()).isStaff()) {
            staff.sendMessage(BungeeChat.format(GlobalConfig.MESSAGES_ERROR_NO_PERMISSION.getMessage()));
            return;
        }

        if (args.length == 0) {
            staff.sendMessage(BungeeChat.format(GlobalConfig.MESSAGES_ERROR_NO_PLAYER.getMessage()));
            return;
        }

        final ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);

        if (target == null) {
            staff.sendMessage(BungeeChat.format(GlobalConfig.MESSAGES_ERROR_PLAYER_OFFLINE.getMessage(), "%SUSPECT%", args[0]));
            return;
        }

        api.getScreenShareManager().clearPlayer(staff.getUniqueId(), target.getUniqueId());

    }




    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> players = new ArrayList<>();
        for (SSPlayer player : api.getPlayersManager().getRegisteredPlayers().values()) {
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
