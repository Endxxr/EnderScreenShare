package dev.endxxr.enderss.spigot.commands.enderss;

import dev.endxxr.enderss.api.EnderSSProvider;
import dev.endxxr.enderss.common.utils.ChatUtils;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements SpigotSubCommand {


    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getPermission() {
        return "enderss.settings.reload";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        long start = System.currentTimeMillis();
        EnderSSProvider.getApi().getPlugin().reload();
        EnderSSProvider.getApi().getPlugin().getLog()
                .info("Config reloaded in " + (System.currentTimeMillis() - start) + "ms");
        sender.sendMessage(ChatUtils.format("Config reloaded in " + (System.currentTimeMillis() - start) + "ms"));

    }
}
