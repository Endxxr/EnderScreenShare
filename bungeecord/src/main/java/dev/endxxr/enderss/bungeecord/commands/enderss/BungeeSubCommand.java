package dev.endxxr.enderss.bungeecord.commands.enderss;

import net.md_5.bungee.api.CommandSender;

public interface BungeeSubCommand {
    String getName();
    String getPermission();
    void execute(CommandSender sender, String[] args);
}

