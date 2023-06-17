package dev.endxxr.enderss.spigot.commands;

import dev.endxxr.enderss.api.EnderSSProvider;
import dev.endxxr.enderss.api.objects.player.SsPlayer;
import dev.endxxr.enderss.common.storage.GlobalConfig;
import dev.endxxr.enderss.api.utils.ChatUtils;
import dev.endxxr.enderss.common.storage.SpigotConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ScreenShareCommand implements CommandExecutor, TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (SpigotConfig.PROXY_MODE.getBoolean()) {
            sender.sendMessage(ChatUtils.format(SpigotConfig.PROXY_MODE.getString()));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatUtils.format(GlobalConfig.MESSAGES_ERROR_CONSOLE.getMessage()));
            return true;
        }

        final Player staff = (Player) sender;

        if (args.length < 1 || args[0] == null) { //No player specified
            staff.sendMessage(ChatUtils.format(GlobalConfig.MESSAGES_ERROR_NO_PLAYER.getMessage()));
            return true;
        }

        final Player suspect = Bukkit.getPlayer(args[0]);

        if (suspect == null) { //If the player is offline, sus will be null
            sender.sendMessage(ChatUtils.format(GlobalConfig.MESSAGES_ERROR_PLAYER_OFFLINE.getMessage(), "%SUSPECT%", args[0]));
            return true;
        }

        EnderSSProvider.getApi().getScreenShareManager().startScreenShare(staff.getUniqueId(), suspect.getUniqueId());

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 0) {
            return Collections.emptyList();
        }
        List<Player> players = Bukkit.getOnlinePlayers().stream().filter(player -> player.getName().startsWith(args[0])).collect(Collectors.toList());
        List<String> results = new ArrayList<>();
        players.forEach( proxiedPlayer -> {
            SsPlayer ssPlayer = EnderSSProvider.getApi().getPlayersManager().getPlayer(proxiedPlayer.getUniqueId());
            if (!ssPlayer.isFrozen()) {
                if (ssPlayer.isStaff() && GlobalConfig.STAFF_CONTROLLABLE.getBoolean()) {
                    results.add(proxiedPlayer.getName());
                } else if (!ssPlayer.isStaff()) {
                    results.add(proxiedPlayer.getName());
                }
            }
        });
        players.clear();
        return results;
    }
}