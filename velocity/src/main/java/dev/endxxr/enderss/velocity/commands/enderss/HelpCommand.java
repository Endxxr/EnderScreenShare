package dev.endxxr.enderss.velocity.commands.enderss;

import com.velocitypowered.api.command.CommandSource;
import dev.endxxr.enderss.api.utils.ChatUtils;
import net.kyori.adventure.text.Component;

public class HelpCommand implements VelocitySubCommand {
    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getPermission() {
        return "enderss.settings.help";
    }

    @Override
    public void execute(CommandSource sender, String[] args) {
        sender.sendMessage(ChatUtils.formatAdventureComponent("&d&lHELP"));
        sender.sendMessage(Component.empty());
        sender.sendMessage(ChatUtils.formatAdventureComponent("&5/ss <player> &7- &fStart a ScreenShare"));
        sender.sendMessage(ChatUtils.formatAdventureComponent("&5/clean <player> &7- &fTerminate a ScreenShare"));
        sender.sendMessage(ChatUtils.formatAdventureComponent("&5/blatant <player> &7- &fBan a player for blatant cheating"));
        sender.sendMessage(Component.empty());
        sender.sendMessage(ChatUtils.formatAdventureComponent("&5/enderss help &7- &fShow this help message"));
        sender.sendMessage(ChatUtils.formatAdventureComponent("&5/enderss reload &7- &fReload the config"));
        sender.sendMessage(ChatUtils.formatAdventureComponent("&5/enderss version &7- &fShow the version of the plugin"));
        sender.sendMessage(Component.empty());
    }
}
