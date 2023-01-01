package me.endxxr.enderss.commands.enderss;

import me.endxxr.enderss.utils.ChatUtils;
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
        sender.sendMessage(ChatUtils.format("&5&lEnderSS &dv1.0"));
        sender.sendMessage("");
        sender.sendMessage(ChatUtils.format("&fEnderSS &7by Endxxr"));
        sender.sendMessage(ChatUtils.format("&fhttps://github.com/Endxxr/EnderSS"));
        sender.sendMessage("");
    }
}
