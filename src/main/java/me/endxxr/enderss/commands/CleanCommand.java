package me.endxxr.enderss.commands;

import me.endxxr.enderss.EnderSS;
import me.endxxr.enderss.enums.Config;
import me.endxxr.enderss.models.SsPlayer;
import me.endxxr.enderss.utils.ChatUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class CleanCommand extends Command implements TabExecutor {

    private final EnderSS plugin;

    public CleanCommand() {
        super("clean", "enderss.staff", "nohack","pulito","legit");
        plugin = EnderSS.getInstance();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(ChatUtils.format(Config.MESSAGES_ERROR_CONSOLE.getMessage()));
            return;
        }

        final ProxiedPlayer staff = (ProxiedPlayer) sender;

        if (!plugin.getPlayersManager().getPlayer(staff).isStaff()) {
            staff.sendMessage(ChatUtils.format(Config.MESSAGES_ERROR_NO_PERMISSION.getMessage()));
            return;
        }


        if (args.length == 0) {
            staff.sendMessage(ChatUtils.format(Config.MESSAGES_ERROR_NO_PLAYER.getMessage()));
            return;
        }

        final ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
        this.cleanPlayer(staff, target);

    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }


    public void cleanPlayer(ProxiedPlayer target) {
        final SsPlayer ssSuspect = plugin.getPlayersManager().getPlayer(target);
        ssSuspect.setStaffer(null);
        ssSuspect.setFrozen(false);

        if (Config.CONFIG_LAST_CONNECTED_SERVER.getBoolean()) {
            target.connect(ProxyServer.getInstance().getServerInfo(ssSuspect.getLastServer()));
        } else {
            target.connect(ProxyServer.getInstance().getServerInfo(Config.CONFIG_FALLBACK.getString()));
        }

        for (SsPlayer online : plugin.getPlayersManager().getPlayers().values()) {
            if (online.isStaff() && online.isAlerts()) {
                ProxyServer.getInstance().getPlayer(online.getUuid()).sendMessage(ChatUtils.format(Config.MESSAGES_INFO_PLAYER_CLEANED.getMessage(),
                        "%STAFF%", "Console",
                        "%SUSPECT%", target.getName()));
            }
        }
    }


    public void cleanPlayer(ProxiedPlayer staff, ProxiedPlayer suspect) {
        if (suspect==null){
            staff.sendMessage(ChatUtils.format(Config.MESSAGES_ERROR_PLAYER_OFFLINE.getMessage()));
            return;
        }
        final SsPlayer ssSuspect = plugin.getPlayersManager().getPlayer(suspect);
        final SsPlayer ssStaff = plugin.getPlayersManager().getPlayer(staff);
        if (ssSuspect.isFrozen()) {
            staff.sendMessage(ChatUtils.format(Config.MESSAGES_ERROR_SUSPECT_NOT_IN_SS.getMessage(), "%SUSPECT%", suspect.getName()));
            return;
        }
        ssStaff.setControlled(null);
        ssSuspect.setStaffer(null);
        ssSuspect.setFrozen(false);

        if (Config.CONFIG_LAST_CONNECTED_SERVER.getBoolean()) {
            suspect.connect(ProxyServer.getInstance().getServerInfo(ssSuspect.getLastServer()));
            if (Config.CONFIG_FALLBACK_STAFF.getBoolean()) staff.connect(ProxyServer.getInstance().getServerInfo(ssStaff.getLastServer()));
        } else {
            suspect.connect(ProxyServer.getInstance().getServerInfo(Config.CONFIG_FALLBACK.getString()));
        }

        for (SsPlayer online : plugin.getPlayersManager().getPlayers().values()) {
            if (online.isStaff() && online.isAlerts()) {
                ProxyServer.getInstance().getPlayer(online.getUuid()).sendMessage(ChatUtils.format(Config.MESSAGES_INFO_PLAYER_CLEANED.getMessage(),
                        "%STAFF%", staff.getName(),
                        "%SUSPECT%", suspect.getName()));
            }
        }
    }


}
