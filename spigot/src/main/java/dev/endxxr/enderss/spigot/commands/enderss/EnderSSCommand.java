package dev.endxxr.enderss.spigot.commands.enderss;

import dev.endxxr.enderss.common.storage.GlobalConfig;
import dev.endxxr.enderss.api.utils.ChatUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EnderSSCommand implements CommandExecutor, TabExecutor {

    private final List<SpigotSubCommand> subCommands;
    public EnderSSCommand() {
        subCommands=new ArrayList<>();
        subCommands.add(new AlertsCommand());
        subCommands.add(new ReloadCommand());
        subCommands.add(new SetSpawnCommand());
        subCommands.add(new HelpCommand());
        subCommands.add(new VersionCommand());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("enderss.settings")) {
            sender.sendMessage(ChatUtils.format(GlobalConfig.MESSAGES_ERROR_NO_PERMISSION.getMessage()));
            return true;
        }

        if (args.length==0) {
            sender.sendMessage(ChatUtils.format("&cPlease use /enderss help"));
            return true;
        }


        for (SpigotSubCommand subCommand : subCommands) {
            if (subCommand.getName().equalsIgnoreCase(args[0])) {
                if (!sender.hasPermission(subCommand.getPermission()) && !sender.hasPermission("enderss.settings")) {
                    sender.sendMessage(ChatUtils.format(GlobalConfig.MESSAGES_ERROR_NO_PERMISSION.getMessage()));
                    return true;
                }
                subCommand.execute(sender, args);
                return true;
            }
        }

        sender.sendMessage(ChatUtils.format(GlobalConfig.MESSAGES_ERROR_NO_COMMAND.getMessage()));

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return subCommands.stream().map(SpigotSubCommand::getName).filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
    }
}
