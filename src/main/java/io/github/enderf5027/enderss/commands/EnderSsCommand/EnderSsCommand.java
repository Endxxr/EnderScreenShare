package io.github.enderf5027.enderss.commands.EnderSsCommand;

import io.github.enderf5027.enderss.utils.SubCommand;
import io.github.enderf5027.enderss.utils.config;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;

import static io.github.enderf5027.enderss.utils.ChatUtils.addPrefix;
import static io.github.enderf5027.enderss.utils.ChatUtils.format;

public class EnderSsCommand extends Command{

    private ArrayList<SubCommand> subcommands = new ArrayList<>();

    public EnderSsCommand() {
        super("enderss", "enderss.admin", "ssconfig");
        subcommands.add(new VersionCommand());
        subcommands.add(new InfoCommand());
        subcommands.add(new HelpCommand());
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
        if (cmdNames.contains(command)) {
            return true;
        } else {
            return false;
        }
    }

}
