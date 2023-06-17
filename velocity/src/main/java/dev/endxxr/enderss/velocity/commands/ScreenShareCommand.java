package dev.endxxr.enderss.velocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.connection.Player;
import dev.endxxr.enderss.api.EnderSSProvider;
import dev.endxxr.enderss.api.utils.ChatUtils;
import dev.endxxr.enderss.common.storage.GlobalConfig;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

public class ScreenShareCommand implements SimpleCommand {

    private final ProxyServer server;

    public ScreenShareCommand(ProxyServer server) {
        this.server = server;
    }

    @Override
    public void execute(Invocation invocation) {

        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        if (!(source instanceof Player)) {
            source.sendMessage(ChatUtils.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_CONSOLE.getMessage()));
            return;

        }

        final Player staff = (Player) source;

        if (args.length < 1 || args[0] == null) { //No player specified
            staff.sendMessage(ChatUtils.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_NO_PLAYER.getMessage()));
            return;
        }

        final Player suspect = server.player(args[0]);

        if (suspect == null) { //If the player is offline, sus will be null
            source.sendMessage(ChatUtils.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_PLAYER_OFFLINE.getMessage(), "%SUSPECT%", args[0]));
            return;
        }

        EnderSSProvider.getApi().getScreenShareManager().startScreenShare(staff.id(), suspect.id());

    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        CommandSource source = invocation.source();
        return source.hasPermission("enderss.staff") || source.hasPermission("enderss.ss");
    }

    @Override
    public List<String> suggest(Invocation invocation) {

        String[] args = invocation.arguments();

        if (args.length == 0) {
            return Collections.emptyList();
        }
        return EnderSSProvider.getApi().getPlayersManager().getControllablePlayers(args[0]);
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        return CompletableFuture.supplyAsync(() -> suggest(invocation));
    }
}
