package dev.endxxr.enderss.velocity.commands.enderss;

import com.velocitypowered.api.command.CommandSource;
import dev.endxxr.enderss.api.EnderSSProvider;
import dev.endxxr.enderss.velocity.utils.VelocityChat;
import net.kyori.adventure.text.Component;

public class VersionCommand implements VelocitySubCommand {
    @Override
    public String getName() {
        return "version";
    }

    @Override
    public String getPermission() {
        return "enderss.settings.version";
    }

    @Override
    public void execute(CommandSource sender, String[] args) {
        sender.sendMessage(Component.empty());
        sender.sendMessage(VelocityChat.formatAdventureComponent("&5&lEnderSS &dv"+ EnderSSProvider.getApi().VERSION+ " &7by Endxxr"));
        sender.sendMessage(VelocityChat.formatAdventureComponent("&8Running on Velocity"));
        sender.sendMessage(Component.empty());
        sender.sendMessage(VelocityChat.formatAdventureComponent("&dhttps://www.spigotmc.org/resources/enderss.93261/"));
        sender.sendMessage(VelocityChat.formatAdventureComponent("&dhttps://github.com/Endxxr/EnderSS"));
        sender.sendMessage(Component.empty());
    }
}
