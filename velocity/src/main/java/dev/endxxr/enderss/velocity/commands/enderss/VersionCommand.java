package dev.endxxr.enderss.velocity.commands.enderss;

import com.velocitypowered.api.command.CommandSource;
import dev.endxxr.enderss.api.EnderSSProvider;
import dev.endxxr.enderss.api.utils.ChatUtils;
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
        sender.sendMessage(ChatUtils.formatAdventureComponent("&5&lEnderSS &dv"+ EnderSSProvider.getApi().VERSION));
        sender.sendMessage(ChatUtils.formatAdventureComponent("&8Running on BunngeeCord"));
        sender.sendMessage(Component.empty());
        sender.sendMessage(ChatUtils.formatAdventureComponent("&fEnderSS &7by Endxxr"));
        sender.sendMessage(ChatUtils.formatAdventureComponent("&fhttps://github.com/Endxxr/EnderSS"));
        sender.sendMessage(Component.empty());
    }
}
