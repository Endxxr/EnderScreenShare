package dev.endxxr.enderss.bungeecord.commands.enderss;

import dev.endxxr.enderss.bungeecord.utils.BungeeChat;
import net.md_5.bungee.api.CommandSender;

public class HelpCommand implements BungeeSubCommand {
    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getPermission() {
        return "enderss.settings.help";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(BungeeChat.formatComponent("&d&lHELP"));
        sender.sendMessage(BungeeChat.formatComponent(""));
        sender.sendMessage(BungeeChat.formatComponent("&5/ss <player> &7- &fStart a ScreenShare"));
        sender.sendMessage(BungeeChat.formatComponent("&5/clean <player> &7- &fTerminate a ScreenShare"));
        sender.sendMessage(BungeeChat.formatComponent("&5/blatant <player> &7- &fBan a player for blatant cheating"));
        sender.sendMessage(BungeeChat.formatComponent(""));
        sender.sendMessage(BungeeChat.formatComponent("&5/enderss help &7- &fShow this help message"));
        sender.sendMessage(BungeeChat.formatComponent("&5/enderss reload &7- &fReload the config"));
        sender.sendMessage(BungeeChat.formatComponent("&5/enderss version &7- &fShow the version of the plugin"));
        sender.sendMessage(BungeeChat.formatComponent(""));
    }
}
