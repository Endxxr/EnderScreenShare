package io.github.enderf5027.enderss.commands.EnderSsCommand;

import io.github.enderf5027.enderss.utils.SubCommand;
import io.github.enderf5027.enderss.utils.config;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import static io.github.enderf5027.enderss.utils.ChatUtils.format;

public class HelpCommand extends SubCommand {
    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Show the list of available commands";
    }

    @Override
    public String getSyntax() {
        return "/enderss help";
    }

    @Override
    public void execute(ProxiedPlayer player, String[] args) {
        if (!player.hasPermission("enderss.staff")){
            player.sendMessage(format(config.noperm));
            return;
        }
        player.sendMessage(new TextComponent(""));
        player.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&d&lEnder&5&lSS")));
        player.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&f")));
        player.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&f/ss &7<player>&f: &7Screenshare a player")));
        player.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&f/clean &7<player>&f: &7End the screensharing a player")));
        player.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&f/blatant &7<player>&f: &7Ban a player for blatant cheating")));
        player.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&f/enderss &7[help|version|info]&f: &7Some useful commands")));
        player.sendMessage(new TextComponent(""));
    }
}
