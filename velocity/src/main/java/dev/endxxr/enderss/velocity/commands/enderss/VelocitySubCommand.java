package dev.endxxr.enderss.velocity.commands.enderss;

import com.velocitypowered.api.command.CommandSource;

public interface VelocitySubCommand {
    String getName();
    String getPermission();
    void execute(CommandSource sender, String[] args);
}

