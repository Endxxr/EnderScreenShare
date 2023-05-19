package dev.endxxr.enderss.spigot.listeners.protections.player;

import dev.endxxr.enderss.api.EnderSSAPI;
import dev.endxxr.enderss.api.objects.SSPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class DisableMovement implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

        SSPlayer ssPlayer = EnderSSAPI.Provider.getApi().getPlayersManager().getPlayer(event.getPlayer().getUniqueId());
        if (!ssPlayer.isFrozen()) return;
        if (event.getTo().getY() > event.getFrom().getY()) {
            event.getPlayer().teleport(event.getFrom());
        }

    }
}
