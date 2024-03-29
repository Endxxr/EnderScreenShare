package dev.endxxr.enderss.bungeecord.commands.enderss;

import dev.endxxr.enderss.bungeecord.utils.BungeeChat;
import dev.endxxr.enderss.common.storage.GlobalConfig;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EnderSSCommand extends Command implements TabExecutor {

    private final List<BungeeSubCommand> subCommands;
    public EnderSSCommand() {
        super("enderss", "enderss.settings", "ssplugin", "endersettings");
        subCommands=new ArrayList<>();
        subCommands.add(new AlertsCommand());
        subCommands.add(new ReloadCommand());
        subCommands.add(new HelpCommand());
        subCommands.add(new VersionCommand());
    }


    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length==0) {
            sender.sendMessage(BungeeChat.formatComponent("&cPlease use /enderss help"));
            return;
        }

        for (BungeeSubCommand subCommand : subCommands) {
            if (subCommand.getName().equalsIgnoreCase(args[0])) {
                if (!sender.hasPermission(subCommand.getPermission()) && !sender.hasPermission("enderss.settings")) {
                    sender.sendMessage(BungeeChat.formatComponent(GlobalConfig.MESSAGES_ERROR_NO_PERMISSION.getMessage()));
                    return;
                }
                subCommand.execute(sender, args);
                return;
            }
        }

        sender.sendMessage(BungeeChat.formatComponent(GlobalConfig.MESSAGES_ERROR_NO_COMMAND.getMessage()));

    }


    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        String prefix = args.length == 0 ? "" : args[0];
        return subCommands.stream().map(BungeeSubCommand::getName).filter(s -> s.startsWith(prefix)).collect(Collectors.toList());
    }
}
