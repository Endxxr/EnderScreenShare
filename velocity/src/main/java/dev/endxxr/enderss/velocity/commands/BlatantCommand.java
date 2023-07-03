package dev.endxxr.enderss.velocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.Player;
import dev.endxxr.enderss.velocity.utils.VelocityChat;
import dev.endxxr.enderss.common.storage.GlobalConfig;

import java.util.List;
import java.util.Optional;
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
            sender.sendMessage(VelocityChat.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_NO_PERMISSION.getMessage()));
            return;
        }


        if (args.length == 0) {
            sender.sendMessage(VelocityChat.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_NO_PLAYER.getMessage()));
            return;
        }

        final Optional<Player> targetOptional = server.getPlayer(args[0]);
        if (!targetOptional.isPresent()){
            sender.sendMessage(VelocityChat.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_PLAYER_OFFLINE.getMessage()));
            return;
        }
        Player target = targetOptional.get();

        String command = GlobalConfig.BAN_COMMAND_BLATANT.getString()
                .replaceAll("%SUSPECT%", target.getUsername());
        if (command.startsWith("/")) {
            command = command.replace("/", "");
        }
        server.getCommandManager().executeAsync(sender, command);

    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("enderss.blatant") || invocation.source().hasPermission("enderss.staff");
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        String[] args = invocation.arguments();
        String prefix = args.length == 0 ? "" : args[0];

        List<Player> players = server.getAllPlayers().stream().filter(player -> player.getUsername().startsWith(prefix)).collect(Collectors.toList());
        return players.stream().map(Player::getUsername).collect(Collectors.toList());
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        return CompletableFuture.supplyAsync(() -> suggest(invocation));
    }
}
