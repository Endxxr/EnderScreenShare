package dev.endxxr.enderss.spigot.commands;

import dev.endxxr.enderss.api.EnderSSProvider;
import dev.endxxr.enderss.common.storage.GlobalConfig;
import dev.endxxr.enderss.common.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;

public class BlatantCommand implements CommandExecutor, TabExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {


        if (!sender.hasPermission("enderss.blatant") || !sender.hasPermission("enderss.staff")) {
            sender.sendMessage(ChatUtils.format(GlobalConfig.MESSAGES_ERROR_NO_PERMISSION.getMessage()));
            return true;
        }



        if (args.length == 0) {
            sender.sendMessage(ChatUtils.format(GlobalConfig.MESSAGES_ERROR_NO_PLAYER.getMessage()));
            return true;
        }

        final Player target = Bukkit.getPlayer(args[0]);
        if (target==null){
            sender.sendMessage(ChatUtils.format(GlobalConfig.MESSAGES_ERROR_PLAYER_OFFLINE.getMessage()));
            return true;
        }

        String commandString = GlobalConfig.BAN_COMMAND_BLATANT.getString()
                .replaceAll("%SUSPECT%", target.getName());
        if (commandString.startsWith("/")) {
            commandString = commandString.replace("/", "");
        }
        Bukkit.dispatchCommand(sender, commandString);
        
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        String prefix = args.length == 0 ? "" : args[0];
        return EnderSSProvider.getApi().getPlayersManager().getControllablePlayers(prefix);
    }

}
