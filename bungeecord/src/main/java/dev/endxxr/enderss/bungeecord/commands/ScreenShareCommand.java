package dev.endxxr.enderss.bungeecord.commands;

import dev.endxxr.enderss.api.EnderSSAPI;
import dev.endxxr.enderss.common.storage.GlobalConfig;
import dev.endxxr.enderss.bungeecord.utils.BungeeChat;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ScreenShareCommand extends Command implements TabExecutor {


    public ScreenShareCommand() {
        super("screenshare", "enderss.staff", "ss", "freeze", "controllo");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        /*
             CHECKS
         */


        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(BungeeChat.format(GlobalConfig.MESSAGES_ERROR_CONSOLE.getMessage()));
            return;

        }

        final ProxiedPlayer staff = (ProxiedPlayer) sender;

        if (args.length < 1 || args[0] == null) { //No player specified
            staff.sendMessage(BungeeChat.format(GlobalConfig.MESSAGES_ERROR_NO_PLAYER.getMessage()));
            return;
        }

        final ProxiedPlayer suspect = ProxyServer.getInstance().getPlayer(args[0]);

        if (suspect == null) { //If the player is offline, sus will be null
            sender.sendMessage(BungeeChat.format(GlobalConfig.MESSAGES_ERROR_PLAYER_OFFLINE.getMessage(), "%SUSPECT%", args[0]));
            return;
        }

        EnderSSAPI.Provider.getApi().getScreenShareManager().startScreenShare(staff.getUniqueId(), suspect.getUniqueId());

    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return Collections.emptyList();
        }
        List<ProxiedPlayer> players = ProxyServer.getInstance().getPlayers().stream().filter(player -> player.getName().startsWith(args[0])).collect(Collectors.toList());
        List<String> results = new ArrayList<>();
        players.forEach(player -> results.add(player.getName()));
        players.clear();
        return results;
    }
}