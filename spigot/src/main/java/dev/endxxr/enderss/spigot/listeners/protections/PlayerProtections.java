package dev.endxxr.enderss.spigot.listeners.protections;

import dev.endxxr.enderss.api.EnderSS;
import dev.endxxr.enderss.api.EnderSSProvider;
import dev.endxxr.enderss.api.objects.player.SsPlayer;
import dev.endxxr.enderss.common.storage.SpigotConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class PlayerProtections implements Listener {

    private final EnderSS api = EnderSSProvider.getApi();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

        if (event.getTo().getY() > event.getFrom().getY()) { // If the player is falling down, we let him reach the ground
            SsPlayer ssPlayer = api.getPlayersManager().getPlayer(event.getPlayer().getUniqueId());
            if (ssPlayer == null ) return;
            if (!ssPlayer.isFrozen() || SpigotConfig.PROXY_MODE.getBoolean() ) return;
            event.getPlayer().teleport(event.getFrom());
        }
    }

    @EventHandler
    public void onFoodLevel(FoodLevelChangeEvent event) {

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        SsPlayer ssPlayer = api.getPlayersManager().getPlayer(player.getUniqueId());
        if (!isProtectionEnabled(player, SpigotConfig.PROTECTIONS_PLAYER_HUNGER)) return;
        if (ssPlayer==null) return;

        if (!ssPlayer.isFrozen()) return;
        event.setCancelled(true);

        if (player.getSaturation() < 20) {
            player.setSaturation(20);
        }
    }

    @EventHandler
    public void onItemPickUp(PlayerPickupItemEvent event) { //1.8 Support

        if (!isProtectionEnabled(event.getPlayer(), SpigotConfig.PROTECTIONS_PLAYER_PICK_DROP_ITEMS)) return;

        SsPlayer ssPlayer = api.getPlayersManager().getPlayer(event.getPlayer().getUniqueId());
        if (ssPlayer != null && !ssPlayer.isFrozen()) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {

        if (!isProtectionEnabled(event.getPlayer(), SpigotConfig.PROTECTIONS_PLAYER_PICK_DROP_ITEMS)) return;

        SsPlayer ssPlayer = api.getPlayersManager().getPlayer(event.getPlayer().getUniqueId());
        if (ssPlayer == null || !ssPlayer.isFrozen()) return;
        event.setCancelled(true);
    }


    @EventHandler
    public void onPvP(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();

        if (isProtectionEnabled(player, SpigotConfig.PROTECTIONS_PLAYER_PVP)) return;

        SsPlayer ssPlayer = api.getPlayersManager().getPlayer(player.getUniqueId());
        if (ssPlayer == null || !ssPlayer.isFrozen()) return;
        event.setCancelled(true);

    }


    @EventHandler
    public void onDamage(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();

        if (!isProtectionEnabled(player, SpigotConfig.PROTECTIONS_PLAYER_DAMAGE)) return;

        SsPlayer ssPlayer = api.getPlayersManager().getPlayer(player.getUniqueId());
        if (ssPlayer == null || !ssPlayer.isFrozen()) return;
        event.setCancelled(true);

    }

    @EventHandler
    public void onBuild(BlockPlaceEvent event) {

        if (!isProtectionEnabled(event.getPlayer(), SpigotConfig.PROTECTIONS_PLAYER_BUILD_BREAK)) return;

        SsPlayer ssPlayer = api.getPlayersManager().getPlayer(event.getPlayer().getUniqueId());
        if (ssPlayer ==  null || !ssPlayer.isFrozen()) return;
        event.setCancelled(true);

    }


    @EventHandler
    public void onBreak(BlockBreakEvent event) {

        if (!SpigotConfig.PROTECTIONS_PLAYER_BUILD_BREAK.getBoolean()) return;
        if (!isProtectionEnabled(event.getPlayer(), SpigotConfig.PROTECTIONS_PLAYER_BUILD_BREAK)) return;

        SsPlayer ssPlayer = api.getPlayersManager().getPlayer(event.getPlayer().getUniqueId());
        if (ssPlayer == null || !ssPlayer.isFrozen()) return;
        event.setCancelled(true);

    }

    private boolean isProtectionEnabled(Player player, SpigotConfig config) {

        String basePermission = SpigotConfig.PROTECTIONS_BYPASS_PERMISSION.getString();
        String specificPermission = basePermission+"."+config.name().toLowerCase().substring(12); // 12 = "protections.".length()

        if (!config.getBoolean()) return false;
        if (!player.hasPermission(basePermission)) return true;
        if (!player.hasPermission(specificPermission)) return true;
        if (player.hasPermission("enderss.staff") && SpigotConfig.PROTECTIONS_STAFF_BYPASS.getBoolean()) return false;

        return true;

    }



}
