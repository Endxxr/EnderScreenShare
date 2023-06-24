package dev.endxxr.enderss.velocity.commands.enderss;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import dev.endxxr.enderss.common.storage.GlobalConfig;
import dev.endxxr.enderss.velocity.utils.VelocityChat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class EnderSSCommand implements SimpleCommand {

    private final List<VelocitySubCommand> subCommands;
    public EnderSSCommand() {
        subCommands=new ArrayList<>();
        subCommands.add(new AlertsCommand());
        subCommands.add(new ReloadCommand());
        subCommands.add(new HelpCommand());
        subCommands.add(new VersionCommand());
    }

    @Override
    public void execute(Invocation invocation) {

        String[] args = invocation.arguments();
        CommandSource sender = invocation.source();

        if (args.length==0) {
            sender.sendMessage(VelocityChat.formatAdventureComponent("&cPlease use /enderss help"));
            return;
        }

        for (VelocitySubCommand subCommand : subCommands) {
            if (subCommand.getName().equalsIgnoreCase(args[0])) {
                if (!sender.hasPermission(subCommand.getPermission()) && !sender.hasPermission("enderss.settings")) {
                    sender.sendMessage(VelocityChat.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_NO_PERMISSION.getMessage()));
                    return;
                }
                subCommand.execute(sender, args);
                return;
            }
        }

        sender.sendMessage(VelocityChat.formatAdventureComponent(GlobalConfig.MESSAGES_ERROR_NO_COMMAND.getMessage()));

    }

    @Override
    public List<String> suggest(Invocation invocation) {
        String[] args = invocation.arguments();
        String prefix = args.length == 0 ? "" : args[0];
        return subCommands.stream().map(VelocitySubCommand::getName).filter(s -> s.startsWith(prefix)).collect(Collectors.toList());
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        return CompletableFuture.supplyAsync(() -> suggest(invocation));
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        CommandSource sender = invocation.source();
        return sender.hasPermission("enderss.settings") || sender.hasPermission("enderss.admin");

    }
}
