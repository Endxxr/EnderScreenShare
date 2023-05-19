package dev.endxxr.enderss.bungeecord.commands.enderss;

import dev.endxxr.enderss.bungeecord.utils.BungeeChat;
import net.md_5.bungee.api.CommandSender;

public class VersionCommand implements SubCommand {
    @Override
    public String getName() {
        return "version";
    }

    @Override
    public String getPermission() {
        return "enderss.admin";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(BungeeChat.format("&5&lEnderSS &dv1.0"));
        sender.sendMessage("");
        sender.sendMessage(BungeeChat.format("&fEnderSS &7by Endxxr"));
        sender.sendMessage(BungeeChat.format("&fhttps://github.com/Endxxr/EnderSS"));
        sender.sendMessage("");
    }
}
