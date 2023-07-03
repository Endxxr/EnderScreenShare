package dev.endxxr.enderss.velocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.endxxr.enderss.api.EnderSSProvider;
import dev.endxxr.enderss.velocity.utils.VelocityChat;
import dev.endxxr.enderss.common.storage.GlobalConfig;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

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
            source.sendMessage(VelocityChat.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_CONSOLE.getMessage()));
            return;

        }

        final Player staff = (Player) source;

        if (args.length < 1 || args[0] == null) { //No player specified
            staff.sendMessage(VelocityChat.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_NO_PLAYER.getMessage()));
            return;
        }

        Optional<Player> optionalSuspect = server.getPlayer(args[0]);
        if (!optionalSuspect.isPresent()) { //If the player is offline, sus will be null
            source.sendMessage(VelocityChat.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_PLAYER_OFFLINE.getMessage(), "%SUSPECT%", args[0]));
            return;
        }

        Player suspect = optionalSuspect.get();
        EnderSSProvider.getApi().getScreenShareManager().startScreenShare(staff.getUniqueId(), suspect.getUniqueId());

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
            return EnderSSProvider.getApi().getPlayersManager().getControllablePlayers("");
        }
        return EnderSSProvider.getApi().getPlayersManager().getControllablePlayers(args[0]);
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        return CompletableFuture.supplyAsync(() -> suggest(invocation));
    }
}
