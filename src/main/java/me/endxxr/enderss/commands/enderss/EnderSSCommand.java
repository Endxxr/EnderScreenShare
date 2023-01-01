package me.endxxr.enderss.commands.enderss;

import me.endxxr.enderss.EnderSS;
import me.endxxr.enderss.enums.Config;
import me.endxxr.enderss.utils.ChatUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EnderSSCommand extends Command implements TabExecutor {

    private final List<SubCommand> subCommands;
    public EnderSSCommand() {
        super("enderss", "enderss.admin");
        subCommands=new ArrayList<>();
        subCommands.add(new HelpCommand());
        subCommands.add(new ReloadCommand());
        subCommands.add(new VersionCommand());
        subCommands.add(new AlertsCommand());
    }


    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length==0) {
            sender.sendMessage(ChatUtils.format("&cPlease use /enderss help"));
            return;
        }

        for (SubCommand subCommand : subCommands) {
            if (subCommand.getName().equalsIgnoreCase(args[0])) {
                if (!sender.hasPermission(subCommand.getPermission())) {
                    sender.sendMessage(ChatUtils.format(Config.MESSAGES_ERROR_NO_PERMISSION.getMessage()));
                    return;
                }
                subCommand.execute(sender, args);
                return;
            }
        }

        sender.sendMessage(ChatUtils.format("&cInvalid subcommand, use /enderss help for help"));

    }


    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        return subCommands.stream().map(SubCommand::getName).filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
    }
}
