package dev.endxxr.enderss.velocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.connection.Player;
import dev.endxxr.enderss.api.utils.ChatUtils;
import dev.endxxr.enderss.common.storage.GlobalConfig;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class BlatantCommand implements SimpleCommand {

    private final ProxyServer server;

    public BlatantCommand(ProxyServer server) {
        this.server = server;
    }

    @Override
    public void execute(Invocation invocation) {

        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();


        if (!sender.hasPermission("enderss.staff") && !sender.hasPermission("enderss.blatant")) {
            sender.sendMessage(ChatUtils.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_NO_PERMISSION.getMessage()));
            return;
        }


        if (args.length == 0) {
            sender.sendMessage(ChatUtils.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_NO_PLAYER.getMessage()));
            return;
        }

        final Player target = server.player(args[0]);
        if (target==null){
            sender.sendMessage(ChatUtils.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_PLAYER_OFFLINE.getMessage()));
            return;
        }

        String command = GlobalConfig.BAN_COMMAND_BLATANT.getString()
                .replaceAll("%SUSPECT%", target.username());
        if (command.startsWith("/")) {
            command = command.replace("/", "");
        }
        server.commandManager().execute(sender, command);

    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("enderss.blatant") || invocation.source().hasPermission("enderss.staff");
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        String[] args = invocation.arguments();
        if (args.length==0) {
            return Collections.emptyList();
        }
        List<Player> players = server.connectedPlayers().stream().filter(player -> player.username().startsWith(args[0])).collect(Collectors.toList());
        return players.stream().map(Player::username).collect(Collectors.toList());
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        return CompletableFuture.supplyAsync(() -> suggest(invocation));
    }
}
