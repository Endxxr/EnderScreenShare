package dev.endxxr.enderss.spigot.commands.enderss;


import org.bukkit.command.CommandSender;

public interface SpigotSubCommand {
    String getName();
    String getPermission();
    void execute(CommandSender sender, String[] args);
}

