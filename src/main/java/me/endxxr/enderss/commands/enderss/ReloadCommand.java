package me.endxxr.enderss.commands.enderss;

import me.endxxr.enderss.EnderSS;
import me.endxxr.enderss.utils.ChatUtils;
import net.md_5.bungee.api.CommandSender;

public class ReloadCommand implements SubCommand {
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
        final long start = System.currentTimeMillis();
        EnderSS.getInstance().reloadConfig();
        EnderSS.getInstance().getLogger().info("Config reloaded in " + (System.currentTimeMillis() - start) + "ms");
        sender.sendMessage(ChatUtils.format("Config reloaded in " + (System.currentTimeMillis() - start) + "ms"));

    }
}
