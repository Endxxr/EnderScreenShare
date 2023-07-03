package dev.endxxr.enderss.bungeecord.commands.enderss;

import dev.endxxr.enderss.api.EnderSSProvider;
import dev.endxxr.enderss.bungeecord.utils.BungeeChat;
import net.md_5.bungee.api.CommandSender;

public class ReloadCommand implements BungeeSubCommand {

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
        sender.sendMessage(BungeeChat.formatComponent("Config reloaded in " + (System.currentTimeMillis() - start) + "ms"));

    }
}
