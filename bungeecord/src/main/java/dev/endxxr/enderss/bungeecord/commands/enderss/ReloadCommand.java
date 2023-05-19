package dev.endxxr.enderss.bungeecord.commands.enderss;

import dev.endxxr.enderss.bungeecord.utils.BungeeChat;
import dev.endxxr.enderss.api.EnderSSAPI;
import net.md_5.bungee.api.CommandSender;

public class ReloadCommand implements SubCommand {


    public ReloadCommand() {
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getPermission() {
        return "enderss.admin";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        long start = System.currentTimeMillis();
        EnderSSAPI.Provider.getApi().getPlugin().reload();
        EnderSSAPI.Provider.getApi().getPlugin().getLog()
                .info("Config reloaded in " + (System.currentTimeMillis() - start) + "ms");
        sender.sendMessage(BungeeChat.format("Config reloaded in " + (System.currentTimeMillis() - start) + "ms"));

    }
}
