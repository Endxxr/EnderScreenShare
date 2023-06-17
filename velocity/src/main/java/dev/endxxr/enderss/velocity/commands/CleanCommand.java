package dev.endxxr.enderss.velocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.connection.Player;
import dev.endxxr.enderss.api.EnderSS;
import dev.endxxr.enderss.api.EnderSSProvider;
import dev.endxxr.enderss.api.objects.player.SsPlayer;
import dev.endxxr.enderss.api.utils.ChatUtils;
import dev.endxxr.enderss.common.storage.GlobalConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CleanCommand implements SimpleCommand {

    private final EnderSS api;
    private final ProxyServer server;

    public CleanCommand(ProxyServer server) {
        this.server = server;
        this.api = EnderSSProvider.getApi();
    }

    @Override
    public void execute(Invocation invocation) {

        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatUtils.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_CONSOLE.getMessage()));
            return;
        }

        Player staff = (Player) sender;

        if (!staff.hasPermission("enderss.staff") && !staff.hasPermission("enderss.clean")) {
            staff.sendMessage(ChatUtils.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_NO_PERMISSION.getMessage()));
            return;
        }


        if (!api.getPlayersManager().getPlayer(staff.id()).isStaff()) {
            staff.sendMessage(ChatUtils.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_NO_PERMISSION.getMessage()));
            return;
        }

        if (args.length == 0) {
            staff.sendMessage(ChatUtils.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_NO_PLAYER.getMessage()));
            return;
        }

        final Player target = server.player(args[0]);

        if (target == null) {
            staff.sendMessage(ChatUtils.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_PLAYER_OFFLINE.getMessage(), "%SUSPECT%", args[0]));
            return;
        }

        api.getScreenShareManager().clearPlayer(staff.id(), target.id());
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("enderss.staff") || invocation.source().hasPermission("enderss.clean");
    }

    @Override
    public List<String> suggest(Invocation invocation){
        String[] args = invocation.arguments();
        List<String> players = new ArrayList<>();

        for (SsPlayer player : api.getPlayersManager().getRegisteredPlayers()) {
            if (player.isFrozen()) {
                String name = server.player(player.getUUID()).username();
                if (name.toLowerCase().startsWith(args[0].toLowerCase())) {
                    players.add(name);
                }
            }
        }
        return players;
    }
    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        return CompletableFuture.supplyAsync(() -> suggest(invocation));
    }
}
