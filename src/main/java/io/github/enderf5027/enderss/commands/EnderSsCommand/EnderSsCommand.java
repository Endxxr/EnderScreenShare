package io.github.enderf5027.enderss.commands.EnderSsCommand;

import io.github.enderf5027.enderss.utils.SubCommand;
import io.github.enderf5027.enderss.utils.config;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static io.github.enderf5027.enderss.utils.ChatUtils.addPrefix;
import static io.github.enderf5027.enderss.utils.ChatUtils.format;

public class EnderSsCommand extends Command implements TabExecutor {

    private final ArrayList<SubCommand> subcommands = new ArrayList<>();

    public EnderSsCommand() {
        super("enderss", "enderss.staff", "ssconfig");
        subcommands.add(new VersionCommand());
        subcommands.add(new InfoCommand());
        subcommands.add(new HelpCommand());
        subcommands.add(new AlertsCommand());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!sender.hasPermission("enderss.admin")) {
            sender.sendMessage(addPrefix(format(config.noperm)));
            return;
        }
        if (sender instanceof ProxiedPlayer){
            ProxiedPlayer p = (ProxiedPlayer) sender;


            if (args.length > 0) {
                boolean exist = commandExist(args[0]);
                if (!exist) {
                    p.sendMessage(addPrefix(format(config.nocommand)));
                }
                for (int i = 0; i < getSubcommands().size(); i++) {
                    if (args[0].equalsIgnoreCase(getSubcommands().get(i).getName())) {
                        getSubcommands().get(i).execute(p, args);
                    }
                }
            } else {
                p.sendMessage("");
                for (int i = 0; i < getSubcommands().size(); i++){
                    p.sendMessage(getSubcommands().get(i).getSyntax() + " - " +ChatColor.GRAY+getSubcommands().get(i).getDescription());
                }
                p.sendMessage("");
            }

        }
    }

    public ArrayList<SubCommand> getSubcommands(){
        return subcommands;
    }
    private boolean commandExist(String command){
        ArrayList<String> cmdNames = new ArrayList<>();
        for (int i = 0; i < getSubcommands().size(); i++) {
            cmdNames.add(getSubcommands().get(i).getName());
        }
        return cmdNames.contains(command);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return Collections.emptyList();
        }

        if (args.length == 2) {
            List<ProxiedPlayer> players = ProxyServer.getInstance().getPlayers().stream().filter(player -> player.getName().startsWith(args[0])).collect(Collectors.toList());
            List<String> results = new ArrayList<>();
            players.forEach(player -> results.add(player.getName()));
            players.clear();
            return results;
        }
        List<String> results = new ArrayList<>();
        results.add(new VersionCommand().getName());
        results.add(new HelpCommand().getName());
        results.add(new InfoCommand().getName());
        results.add(new AlertsCommand().getName());
        return results;
    }
}
