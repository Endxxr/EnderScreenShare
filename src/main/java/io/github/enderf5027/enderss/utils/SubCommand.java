package io.github.enderf5027.enderss.utils;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public abstract class SubCommand {

    public abstract String getName();

    public abstract String getDescription();

    public abstract String getSyntax();

    public abstract void execute(ProxiedPlayer player, String[] args);


}
