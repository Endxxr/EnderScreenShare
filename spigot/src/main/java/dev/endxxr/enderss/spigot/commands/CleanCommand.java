package dev.endxxr.enderss.spigot.commands;

import dev.endxxr.enderss.api.EnderSS;
import dev.endxxr.enderss.api.EnderSSProvider;
import dev.endxxr.enderss.api.enums.SSEndCause;
import dev.endxxr.enderss.api.events.spigot.SsEndEvent;
import dev.endxxr.enderss.api.objects.player.SsPlayer;
import dev.endxxr.enderss.common.storage.GlobalConfig;
import dev.endxxr.enderss.api.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CleanCommand implements CommandExecutor, TabExecutor {

    private final EnderSS api;

    public CleanCommand() {
        api = EnderSSProvider.getApi();
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatUtils.format(GlobalConfig.MESSAGES_ERROR_CONSOLE.getMessage()));
            return true;
        }

        final Player staff = (Player) sender;

        if (!staff.hasPermission("enderss.staff") && !staff.hasPermission("enderss.clean")) {
            staff.sendMessage(ChatUtils.format(GlobalConfig.MESSAGES_ERROR_NO_PERMISSION.getMessage()));
            return true;
        }

        if (!api.getPlayersManager().getPlayer(staff.getUniqueId()).isStaff()) {
            staff.sendMessage(ChatUtils.format(GlobalConfig.MESSAGES_ERROR_NO_PERMISSION.getMessage()));
            return true;
        }

        if (args.length == 0) {
            staff.sendMessage(ChatUtils.format(GlobalConfig.MESSAGES_ERROR_NO_PLAYER.getMessage()));
            return true;
        }

        final Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            staff.sendMessage(ChatUtils.format(GlobalConfig.MESSAGES_ERROR_PLAYER_OFFLINE.getMessage(), "%SUSPECT%", args[0]));
            return true;
        }

        api.getScreenShareManager().clearPlayer(staff.getUniqueId(), target.getUniqueId());
        Bukkit.getPluginManager().callEvent(new SsEndEvent(staff, target, SSEndCause.SUSPECT_CLEAN));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> players = new ArrayList<>();
        for (SsPlayer player : api.getPlayersManager().getRegisteredPlayers()) {
            if (player.isFrozen()) {
                String name = Bukkit.getPlayer(player.getUUID()).getName();
                if (name.toLowerCase().startsWith(args[0].toLowerCase())) {
                    players.add(name);
                }
            }
        }
        return players;
    }
}

