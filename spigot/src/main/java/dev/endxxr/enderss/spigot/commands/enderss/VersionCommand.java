package dev.endxxr.enderss.spigot.commands.enderss;


import dev.endxxr.enderss.api.EnderSSProvider;
import dev.endxxr.enderss.api.utils.ChatUtils;
import org.bukkit.command.CommandSender;

public class VersionCommand implements SpigotSubCommand {
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
        sender.sendMessage(ChatUtils.format("&5&lEnderSS &dv"+EnderSSProvider.getApi().VERSION));
        sender.sendMessage(ChatUtils.format("&8Running on Spigot"));
        sender.sendMessage("");
        sender.sendMessage(ChatUtils.format("&fEnderSS &7by Endxxr"));
        sender.sendMessage(ChatUtils.format("&fhttps://github.com/Endxxr/EnderSS"));
        sender.sendMessage("");
    }
}
