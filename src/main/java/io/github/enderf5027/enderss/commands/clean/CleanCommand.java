package io.github.enderf5027.enderss.commands.clean;

import io.github.enderf5027.enderss.session.PlayerSession;
import io.github.enderf5027.enderss.session.SessionManager;
import io.github.enderf5027.enderss.utils.config;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static io.github.enderf5027.enderss.session.SessionManager.getSession;
import static io.github.enderf5027.enderss.utils.ChatUtils.format;

public class CleanCommand extends Command implements TabExecutor {
    public CleanCommand() {
        super("clean", "enderss.staff", "nohack","pulito","legit");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ProxiedPlayer player = (ProxiedPlayer) sender;
        if (!(getSession(player).isStaff())) {
            player.sendMessage(format(config.noperm));
            return;
        }
        if (args.length == 0) {
            player.sendMessage(format(config.noplayer));
            return;
        }

        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
        if (target==null){
            player.sendMessage(format(config.playeroffline));
            return;
        }

        PlayerSession targetSession = getSession(target);
        PlayerSession playerSession = getSession(player);
        if (!targetSession.isFrozen()) {
            player.sendMessage(format(config.suspectnotinss, target));
            return;
        }


        target.sendMessage(format(config.cleanplayer, player, target));
        player.sendMessage(format(config.playercleaned, player, target));
        targetSession.setFrozen(false);
        targetSession.setStaffer(null);
        playerSession.setPlayerScreenShared(null);

        if (config.LastConnectedServer){

            if (targetSession.getLastServer()==null) { player.sendMessage(format(config.cantfallbacksus)); return; }
            if (playerSession.getLastServer()==null) { player.sendMessage(format(config.cantfallbackstaff)); return; }

            target.connect(targetSession.getLastServer());
            if (config.FallBackStaff) player.connect(playerSession.getLastServer());
        } else {
            target.connect(ProxyServer.getInstance().getServerInfo(config.FallbackServer));
            if (config.FallBackStaff) player.connect(ProxyServer.getInstance().getServerInfo(config.FallbackServer));
        }


        for (ProxiedPlayer staff : ProxyServer.getInstance().getPlayers()){
            if (SessionManager.getSession(staff).isStaff()) {
                format(config.playercleaned, target, staff);
            }
        }

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
