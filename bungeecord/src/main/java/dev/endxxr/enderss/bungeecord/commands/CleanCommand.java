package dev.endxxr.enderss.bungeecord.commands;

import dev.endxxr.enderss.api.EnderSS;
import dev.endxxr.enderss.api.EnderSSProvider;
import dev.endxxr.enderss.api.objects.player.SsPlayer;
import dev.endxxr.enderss.bungeecord.utils.BungeeChat;
import dev.endxxr.enderss.common.storage.GlobalConfig;
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
            sender.sendMessage(BungeeChat.formatComponent(GlobalConfig.MESSAGES_ERROR_CONSOLE.getMessage()));
            return;
        }

        ProxiedPlayer staff = (ProxiedPlayer) sender;
        SsPlayer staffSS = api.getPlayersManager().getPlayer(staff.getUniqueId());


        if (staffSS == null) {
            staff.sendMessage(BungeeChat.formatComponent(GlobalConfig.MESSAGES_ERROR_GENERIC.getMessage()));
            api.getPlugin().getLog().severe("Wasn't able to get the profile of the player, is it online?");
            return;
        }


        if (!staff.hasPermission("enderss.staff") && !staff.hasPermission("enderss.clean")) {
            staff.sendMessage(BungeeChat.formatComponent(GlobalConfig.MESSAGES_ERROR_NO_PERMISSION.getMessage()));
            return;
        }


        if (args.length == 0) {
            staff.sendMessage(BungeeChat.formatComponent(GlobalConfig.MESSAGES_ERROR_NO_PLAYER.getMessage()));
            return;
        }

        final ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);

        if (target == null) {
            staff.sendMessage(BungeeChat.formatComponent(GlobalConfig.MESSAGES_ERROR_PLAYER_OFFLINE.getMessage(), "%SUSPECT%", args[0]));
            return;
        }

        final SsPlayer targetSS = api.getPlayersManager().getPlayer(target.getUniqueId());
        if (targetSS==null) return;
        if (staffSS.getControlled() != targetSS) {
            if (sender.hasPermission("enderss.admin")) {
                ProxiedPlayer realStaffer = ProxyServer.getInstance().getPlayer(targetSS.getStaffer().getUUID());
                if (realStaffer == null) {
                    staff.sendMessage(BungeeChat.formatComponent(GlobalConfig.MESSAGES_ERROR_NOT_CONTROLLING.getMessage(), "%SUSPECT%", target.getName()));
                    return;
                }
                staff = realStaffer;
            } else {
                staff.sendMessage(BungeeChat.formatComponent(GlobalConfig.MESSAGES_ERROR_NOT_CONTROLLING.getMessage(), "%SUSPECT%", target.getName()));
                return;
            }
        }

        api.getScreenShareManager().clearPlayer(staff.getUniqueId(), target.getUniqueId());

    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> players = new ArrayList<>();
        String prefix = args.length == 0 ? "" : args[0];
        for (SsPlayer player : api.getPlayersManager().getRegisteredPlayers()) {
            if (player.isFrozen()) {
                String name = ProxyServer.getInstance().getPlayer(player.getUUID()).getName();
                if (name.toLowerCase().startsWith(prefix)) {
                    players.add(name);
                }
            }
        }
        return players;
    }

}
