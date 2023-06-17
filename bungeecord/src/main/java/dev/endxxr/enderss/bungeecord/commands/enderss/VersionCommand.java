package dev.endxxr.enderss.bungeecord.commands.enderss;

import dev.endxxr.enderss.api.EnderSSProvider;
import dev.endxxr.enderss.api.utils.ChatUtils;
import net.md_5.bungee.api.CommandSender;

public class VersionCommand implements BungeeSubCommand {
    @Override
    public String getName() {
        return "version";
    }

    @Override
    public String getPermission() {
        return "enderss.settings.version";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(ChatUtils.formatComponent("&5&lEnderSS &dv"+ EnderSSProvider.getApi().VERSION));
        sender.sendMessage(ChatUtils.formatComponent("&8Running on BunngeeCord"));
        sender.sendMessage("");
        sender.sendMessage(ChatUtils.formatComponent("&fEnderSS &7by Endxxr"));
        sender.sendMessage(ChatUtils.formatComponent("&fhttps://github.com/Endxxr/EnderSS"));
        sender.sendMessage("");
    }
}
