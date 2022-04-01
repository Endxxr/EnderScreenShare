package io.github.enderf5027.enderss.session;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public class SessionLoader {

    public static PlayerSession loadSession(ProxiedPlayer player){
        return new PlayerSession(player.getUniqueId(), player.getName());
    }

}
