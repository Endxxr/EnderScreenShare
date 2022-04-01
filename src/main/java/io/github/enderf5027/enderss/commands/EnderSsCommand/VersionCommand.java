package io.github.enderf5027.enderss.commands.EnderSsCommand;

import io.github.enderf5027.enderss.utils.SubCommand;
import io.github.enderf5027.enderss.utils.config;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import static io.github.enderf5027.enderss.utils.ChatUtils.format;

public class VersionCommand extends SubCommand {
    @Override
    public String getName() {
        return "version";
    }

    @Override
    public String getDescription() {
        return "Show some info about the plugin";
    }

    @Override
    public String getSyntax() {
        return "/enderss version";
    }

    @Override
    public void execute(ProxiedPlayer player, String[] args) {
        if (!player.hasPermission("enderss.staff")){
            player.sendMessage(format(config.noperm));
            return;
        }

        player.sendMessage(new TextComponent(""));
        player.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&d&lEnder&5&lSS")));
        player.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "Version &d0.1 &f- Beta")));
        player.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&fby &dEndxxr")));
        player.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&fRunning under &d"+ ProxyServer.getInstance().getVersion())));
        player.sendMessage(new TextComponent(""));

    }
}
