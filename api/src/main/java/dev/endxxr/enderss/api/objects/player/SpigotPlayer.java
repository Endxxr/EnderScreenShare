package dev.endxxr.enderss.api.objects.player;

import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * This class stores the data about the player, used in the context of the spigot
 *
 * @since 1.1
 * @see dev.endxxr.enderss.api.objects.managers.PlayersManager
 *
 */

public class SpigotPlayer extends SsPlayer{

    private byte lastGameMode;
    private Collection<PotionEffect> potionEffects;

    public SpigotPlayer(java.util.UUID UUID) {
        super(UUID);
        potionEffects = new ArrayList<>();
    }


    /**
     *
     * Set the last player's gamemode before the screen share
     *
     */
    public void setLastGameMode(byte gameMode) {
        this.lastGameMode = gameMode;
    }

    /**
     *
     * Get the last player's gamemode before the screen share
     *
     */
    public byte getLastGameMode() {
        return lastGameMode;
    }


    /**
     *
     * Get the player's potion effects before the screen share
     *
     */

    public Collection<PotionEffect> getLastPotionEffects() {
        return potionEffects;
    }

    /**
     *
     * Set the player's potion effects before the screen share
     *
     */
    public void setLastPotionEffects(Collection<PotionEffect> potionEffects) {
        this.potionEffects = potionEffects;
    }

}
