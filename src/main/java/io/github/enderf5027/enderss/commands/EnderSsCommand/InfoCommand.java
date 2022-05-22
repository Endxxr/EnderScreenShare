package io.github.enderf5027.enderss.commands.EnderSsCommand;

import io.github.enderf5027.enderss.session.PlayerSession;
import io.github.enderf5027.enderss.utils.SubCommand;
import io.github.enderf5027.enderss.utils.config;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import static io.github.enderf5027.enderss.session.SessionManager.getSession;
import static io.github.enderf5027.enderss.utils.ChatUtils.format;

public class InfoCommand extends SubCommand {
    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getDescription() {
        return "Shows some info about a specified player";
    }

    @Override
    public String getSyntax() {
        return "/enderss info <player>";
    }

    @Override
    public void execute(ProxiedPlayer player, String[] args) {
        if (!player.hasPermission("enderss.staff")){
            player.sendMessage(format(config.noperm));
            return;
        }

        if (args.length == 1) {
            player.sendMessage(format(config.noplayer));
            return;
        }

        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[1]);
        if (target==null){
            player.sendMessage(format(config.playeroffline));
            return;
        }
        //Yeah,  this code & message sucks, got to update it in the next version
        PlayerSession targetSession = getSession(target);
        player.sendMessage(new TextComponent(""));
        player.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&dInfo about "+target.getName())));
        player.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&fStaff")));
        if (targetSession.isStaff()) {
            player.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&aYes")));
            player.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&fPlayer Screenshared")));
            if (!(targetSession.getScreenSharing()==null)) {
                player.sendMessage(new TextComponent(targetSession.getScreenSharing().getDisplayName()));
            } else {
                player.sendMessage(new TextComponent("None"));
            }
        } else {
            player.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&cNo")));
            player.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&fScreenshared by")));
            if (!(targetSession.getStaffer()==null)) {
                player.sendMessage(new TextComponent(ChatColor.DARK_PURPLE+targetSession.getStaffer().getDisplayName()));
            } else {
                player.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('c', "&cYes")));
            }
        }
        player.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&fFrozen")));
        if (targetSession.isFrozen()) {
            player.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&aYes")));
        } else {
            player.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&cNo")));
        }
    }
}
