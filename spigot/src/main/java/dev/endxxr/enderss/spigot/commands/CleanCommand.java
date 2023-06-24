package dev.endxxr.enderss.spigot.commands;

import dev.endxxr.enderss.api.EnderSS;
import dev.endxxr.enderss.api.EnderSSProvider;
import dev.endxxr.enderss.api.enums.SSEndCause;
import dev.endxxr.enderss.api.events.spigot.SsEndEvent;
import dev.endxxr.enderss.api.objects.player.SsPlayer;
import dev.endxxr.enderss.common.storage.GlobalConfig;
import dev.endxxr.enderss.common.utils.ChatUtils;
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

        Player staff = (Player) sender;
        SsPlayer staffSS = api.getPlayersManager().getPlayer(staff.getUniqueId());

        if (staffSS == null) {
            staff.sendMessage(ChatUtils.format(GlobalConfig.MESSAGES_ERROR_GENERIC.getMessage()));
            api.getPlugin().getLog().severe("Wasn't able to get the profile of the player, is it online?");
            return true;
        }

        if (!staff.hasPermission("enderss.staff") && !staff.hasPermission("enderss.clean")) {
            staff.sendMessage(ChatUtils.format(GlobalConfig.MESSAGES_ERROR_NO_PERMISSION.getMessage()));
            return true;
        }

        if (staffSS.isStaff()) {
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
        String prefix = args.length == 0 ? "" : args[0];
        for (SsPlayer player : api.getPlayersManager().getRegisteredPlayers()) {
            if (player.isFrozen()) {
                String name = Bukkit.getPlayer(player.getUUID()).getName();
                if (name.toLowerCase().startsWith(prefix)) {
                    players.add(name);
                }
            }
        }
        return players;
    }
}

