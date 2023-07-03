package dev.endxxr.enderss.bungeecord.commands.enderss;

import dev.endxxr.enderss.api.EnderSSProvider;
import dev.endxxr.enderss.bungeecord.utils.BungeeChat;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

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
        sender.sendMessage(new TextComponent());
        sender.sendMessage(BungeeChat.formatComponent("&5&lEnderSS &dv"+ EnderSSProvider.getApi().VERSION+ " &7by Endxxr"));
        sender.sendMessage(BungeeChat.formatComponent("&8Running on BungeeCord"));
        sender.sendMessage(new TextComponent());
        sender.sendMessage(BungeeChat.formatComponent("&dhttps://www.spigotmc.org/resources/enderss.93261/"));
        sender.sendMessage(BungeeChat.formatComponent("&dhttps://github.com/Endxxr/EnderSS"));
        sender.sendMessage(new TextComponent());
    }
}
