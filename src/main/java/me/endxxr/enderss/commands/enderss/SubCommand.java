package me.endxxr.enderss.commands.enderss;

import net.md_5.bungee.api.CommandSender;

public interface SubCommand {
    String getName();
    String getPermission();
    void execute(CommandSender sender, String[] args);
}

