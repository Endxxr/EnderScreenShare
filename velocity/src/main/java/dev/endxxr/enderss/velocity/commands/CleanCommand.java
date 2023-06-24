package dev.endxxr.enderss.velocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.Player;
import dev.endxxr.enderss.api.EnderSS;
import dev.endxxr.enderss.api.EnderSSProvider;
import dev.endxxr.enderss.api.objects.player.SsPlayer;
import dev.endxxr.enderss.velocity.utils.VelocityChat;
import dev.endxxr.enderss.common.storage.GlobalConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
            sender.sendMessage(VelocityChat.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_CONSOLE.getMessage()));
            return;
        }

        Player staff = (Player) sender;
        SsPlayer staffSS = api.getPlayersManager().getPlayer(staff.getUniqueId());

        if (staffSS == null) {
            staff.sendMessage(VelocityChat.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_GENERIC.getMessage()));
            api.getPlugin().getLog().severe("Wasn't able to get the profile of the player, is it online?");
            return;
        }

        if (!staff.hasPermission("enderss.staff") && !staff.hasPermission("enderss.clean")) {
            staff.sendMessage(VelocityChat.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_NO_PERMISSION.getMessage()));
            return;
        }


        if (args.length == 0) {
            staff.sendMessage(VelocityChat.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_NO_PLAYER.getMessage()));
            return;
        }

        Optional<Player> optionalTarget = server.getPlayer(args[0]);
        if (!optionalTarget.isPresent()) {
            staff.sendMessage(VelocityChat.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_PLAYER_OFFLINE.getMessage(), "%SUSPECT%", args[0]));
            return;
        }
        Player target = optionalTarget.get();

        api.getScreenShareManager().clearPlayer(staff.getUniqueId(), target.getUniqueId());
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("enderss.staff") || invocation.source().hasPermission("enderss.clean");
    }

    @Override
    public List<String> suggest(Invocation invocation){

        String[] args = invocation.arguments();
        List<String> players = new ArrayList<>();

        String prefix = args.length == 0 ? "" : args[0];

        for (SsPlayer player : api.getPlayersManager().getRegisteredPlayers()) {
            if (player.isFrozen()) {

                Optional<Player> optionalPlayer = server.getPlayer(player.getUUID());
                if (!optionalPlayer.isPresent()) continue;

                String name = optionalPlayer.get().getUsername();
                if (name.toLowerCase().startsWith(prefix)) {
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
