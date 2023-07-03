package dev.endxxr.enderss.spigot.commands.enderss;

import dev.endxxr.enderss.common.utils.ChatUtils;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class HelpCommand implements SpigotSubCommand {
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
        sender.sendMessage(ChatUtils.format("&d&lHELP"));
        sender.sendMessage(ChatUtils.format(""));
        sender.sendMessage(ChatUtils.format("&5/ss <player> &7- &fStart a ScreenShare"));
        sender.sendMessage(ChatUtils.format("&5/clean <player> &7- &fTerminate a ScreenShare"));
        sender.sendMessage(ChatUtils.format("&5/blatant <player> &7- &fBan a player for blatant cheating"));
        sender.sendMessage(ChatUtils.format(""));
        sender.sendMessage(ChatUtils.format("&5/senderss alerts &7- &fToggle alerts for this plugin"));
        sender.sendMessage(ChatUtils.format("&5/senderss help &7- &fShow this help message"));
        sender.sendMessage(ChatUtils.format("&5/senderss reload &7- &fReload the config"));
        sender.sendMessage(ChatUtils.format("&5/senderss setspawn &7- &fSet the spawn location for the ScreenShare"));
        sender.sendMessage(ChatUtils.format("&5/senderss version &7- &fShow the version of the plugin"));
        sender.sendMessage(ChatUtils.format(""));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
