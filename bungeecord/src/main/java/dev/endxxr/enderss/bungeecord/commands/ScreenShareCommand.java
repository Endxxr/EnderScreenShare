package dev.endxxr.enderss.bungeecord.commands;

import dev.endxxr.enderss.api.EnderSSProvider;
import dev.endxxr.enderss.common.storage.GlobalConfig;
import dev.endxxr.enderss.bungeecord.utils.BungeeChat;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class ScreenShareCommand extends Command implements TabExecutor {


    public ScreenShareCommand() {
        super("screenshare", "enderss.ss", "ss", "freeze", "controllo");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        /*
             CHECKS
         */


        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(BungeeChat.formatComponent(GlobalConfig.MESSAGES_ERROR_CONSOLE.getMessage()));
            return;

        }

        final ProxiedPlayer staff = (ProxiedPlayer) sender;

        if (args.length < 1 || args[0] == null) { //No player specified
            staff.sendMessage(BungeeChat.formatComponent(GlobalConfig.MESSAGES_ERROR_NO_PLAYER.getMessage()));
            return;
        }

        final ProxiedPlayer suspect = ProxyServer.getInstance().getPlayer(args[0]);

        if (suspect == null) { //If the player is offline, sus will be null
            sender.sendMessage(BungeeChat.formatComponent(GlobalConfig.MESSAGES_ERROR_PLAYER_OFFLINE.getMessage(), "%SUSPECT%", args[0]));
            return;
        }

        EnderSSProvider.getApi().getScreenShareManager().startScreenShare(staff.getUniqueId(), suspect.getUniqueId());

    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        String prefix = args.length == 0 ? "" : args[0];
        return EnderSSProvider.getApi().getPlayersManager().getControllablePlayers(prefix);
    }
}