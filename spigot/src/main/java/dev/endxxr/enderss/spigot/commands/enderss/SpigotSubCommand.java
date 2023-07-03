package dev.endxxr.enderss.spigot.commands.enderss;


import org.bukkit.command.CommandSender;

import java.util.List;

public interface SpigotSubCommand {
    String getName();
    String getPermission();
    void execute(CommandSender sender, String[] args);
    List<String> tabComplete(CommandSender sender, String[] args);
}

