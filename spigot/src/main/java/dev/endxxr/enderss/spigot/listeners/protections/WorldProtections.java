package dev.endxxr.enderss.spigot.listeners.protections;

import dev.endxxr.enderss.api.EnderSS;
import dev.endxxr.enderss.api.EnderSSProvider;
import dev.endxxr.enderss.common.storage.SpigotConfig;
import dev.endxxr.enderss.spigot.utils.WorldUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import java.util.ArrayList;
import java.util.List;

public class WorldProtections implements Listener {

    private final EnderSS api = EnderSSProvider.getApi();


    public WorldProtections() {

        List<String> worldsNames = SpigotConfig.PROTECTIONS_WORLD_WHITE_LISTED_WORLDS.getStringList();
        List<World> worlds = new ArrayList<>();
        for (String name : worldsNames) {
            World world = Bukkit.getWorld(name);
            if (world != null) {
                worlds.add(world);
            }
        }
        
        
        if (SpigotConfig.PROTECTIONS_WORLD_DAYLIGHT_CYCLE.getBoolean()) {
            for (World world : worlds) {
                world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            }
        }

        if (SpigotConfig.PROTECTIONS_WORLD_WEATHER_CYCLE.getBoolean()) {
            for (World world : worlds) {
                world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            }
        }
        
        if (SpigotConfig.PROTECTIONS_WORLD_FIRE_SPREAD.getBoolean()) {
            for (World world : worlds) {
                world.setGameRule(GameRule.DO_FIRE_TICK, false);
            }
        }
        

    }


    @EventHandler
    public void onMobSpawn(EntitySpawnEvent event) {

        if (event.isCancelled() || !SpigotConfig.PROTECTIONS_WORLD_MOB_SPAWN.getBoolean()) return;

        Entity entity = event.getEntity();
        if (!(entity instanceof Creature)) return;
        if (entity instanceof NPC && !(entity instanceof Villager)) return;

        World world = event.getLocation().getWorld();
        if (!SpigotConfig.PROTECTIONS_WORLD_WHITE_LISTED_WORLDS.getStringList().contains(world.getName())) return;

        event.setCancelled(true);

    }

    @EventHandler
    public void onFoodLevel(FoodLevelChangeEvent event) {

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        if (!isProtectionEnabled(player, SpigotConfig.PROTECTIONS_WORLD_HUNGER)) return;

        event.setCancelled(true);

        if (player.getSaturation() < 20) {
            player.setSaturation(20);
        }
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
        if (isProtectionEnabled(player, SpigotConfig.PROTECTIONS_WORLD_PVP)) return;
        event.setCancelled(true);

    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        if (!isProtectionEnabled(player, SpigotConfig.PROTECTIONS_WORLD_INVULNERABILITY)) return;
        event.setCancelled(true);

    }

    @EventHandler
    public void onVoidDamage(EntityDamageEvent event) {

         if (!(event.getEntity() instanceof Player)) {
            return;
        }
        
        if (event.getCause() != EntityDamageEvent.DamageCause.VOID) return;

        Player player = (Player) event.getEntity();
        if (!isProtectionEnabled(player, SpigotConfig.PROTECTIONS_WORLD_VOID)) return;
        player.teleport(WorldUtils.getSpawnLocation(api.getPlayersManager().getPlayer(player.getUniqueId())));
        

    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {

        if (!SpigotConfig.PROTECTIONS_WORLD_BUILD_BREAK.getBoolean()) return;
        if (!isProtectionEnabled(event.getPlayer(), SpigotConfig.PROTECTIONS_WORLD_BUILD_BREAK)) return;

        event.setCancelled(true);

    }

    private boolean isProtectionEnabled(Player player, SpigotConfig config) {

        String basePermission = SpigotConfig.PROTECTIONS_BYPASS_PERMISSION.getString();
        String specificPermission = basePermission+"."+config.name().toLowerCase().substring(12); // 12 = "protections.".length()
        String worldPermission = basePermission+"."+player.getWorld().getName();

        if (!config.getBoolean()) return false;
        if (!player.hasPermission(specificPermission)) return true;
        if (!player.hasPermission(worldPermission)) return true;
        if (!player.hasPermission(basePermission)) return true;
        if (player.hasPermission("enderss.staff") && SpigotConfig.PROTECTIONS_STAFF_BYPASS.getBoolean()) return false;
        if (!SpigotConfig.PROTECTIONS_WORLD_WHITE_LISTED_WORLDS.getStringList().contains(player.getWorld().getName())) return false;

        return true;

    }


}
