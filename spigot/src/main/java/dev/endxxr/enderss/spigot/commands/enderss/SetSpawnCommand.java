package dev.endxxr.enderss.spigot.commands.enderss;

import dev.endxxr.enderss.api.EnderSSProvider;
import dev.endxxr.enderss.common.storage.SpigotConfig;
import dev.endxxr.enderss.common.utils.ChatUtils;
import dev.endxxr.enderss.common.utils.LogUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

enum SpawnType {
    STAFF,
    SUSPECT,
    START,
    FALLBACK
}

public class SetSpawnCommand implements SpigotSubCommand {
    @Override
    public String getName() {
        return "setspawn";
    }

    @Override
    public String getPermission() {
        return "enderss.settings.setspawn";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {


        if (args.length < 3) {
            sender.sendMessage(ChatUtils.format(SpigotConfig.COMMANDS_SET_SPAWN_USAGE.getString()));
            return;
        }

        SpawnType spawnType;
        SpawnType phaseType;
        try {
            spawnType = SpawnType.valueOf(args[1].toUpperCase());
            phaseType = SpawnType.valueOf(args[2].toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ChatUtils.format(SpigotConfig.COMMANDS_SET_SPAWN_USAGE.getString()));
            return;
        }


        if (args.length == 3) {
            if (!(sender instanceof Player)) { //Only players can use the shorthand command
                sender.sendMessage(ChatUtils.format(SpigotConfig.COMMANDS_SET_SPAWN_USAGE.getString()));
            } else {
                Player player = (Player) sender;
                Location location = player.getLocation();
                setSpawn(player, spawnType, phaseType, location);
            }
            return;
        }

        if (args.length != 9) {
            sender.sendMessage(ChatUtils.format(SpigotConfig.COMMANDS_SET_SPAWN_USAGE.getString()));
            return;
        }

        World world = Bukkit.getWorld(args[3]);
        if (world == null) {
            sender.sendMessage(ChatUtils.format(SpigotConfig.COMMANDS_SET_SPAWN_INVALID_WORLD.getString()));
            return;
        }

        double x, y, z;
        float yaw, pitch;
        try {
            x = Double.parseDouble(args[4]);
            y = Double.parseDouble(args[5]);
            z = Double.parseDouble(args[6]);
            yaw = Float.parseFloat(args[7]);
            pitch = Float.parseFloat(args[8]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatUtils.format(SpigotConfig.COMMANDS_SET_SPAWN_INVALID_COORDINATES.getString()));
            return;
        }

        Location location = new Location(world, x, y, z, yaw, pitch);
        setSpawn(sender, spawnType, phaseType, location);

    }


    private void setSpawn(CommandSender sender, SpawnType spawnType, SpawnType phaseType, Location location) {

        YamlFile config = EnderSSProvider.getApi().getPlugin().getPlatformConfig();
        String stringLocation = location.getWorld().getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ() + "," + location.getYaw() + "," + location.getPitch();
        config.set(phaseType.name().toLowerCase()+"."+ spawnType.name().toLowerCase(), stringLocation);
        try {
            config.save();
        } catch (IOException e) {
            sender.sendMessage(ChatUtils.format(SpigotConfig.COMMANDS_SET_SPAWN_ERROR.getString()));
            LogUtils.prettyPrintException(new RuntimeException(e), "Failed to save config");
        }

        sender.sendMessage(ChatUtils.format(SpigotConfig.COMMANDS_SET_SPAWN_SET.getString(),
                "%PHASE%", phaseType.name().toUpperCase(), "%TYPE%", spawnType.name().toUpperCase()));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        // /-1          0       1      2     3    4   5   6    7      8
        // /enderss setspawn STAFF [phase] [world] [x] [y] [z] [yaw] [pitch]

        // Gets the world that start with the given string (args[3]) or gets the world of the player
        String world = "";
        String x = "x";
        String y = "y";
        String z = "z";
        String yaw = "yaw";
        String pitch = "pitch";
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Location location = player.getLocation();
            world = location.getWorld().getName();
            x = String.valueOf(location.getX());
            y = String.valueOf(location.getY());
            z = String.valueOf(location.getZ());
            yaw = String.valueOf(location.getYaw());
            pitch = String.valueOf(location.getPitch());
        }


        if (args.length == 2) {
            return Arrays.asList("Choose a type", "STAFF", "SUSPECT");
        }

        if (args.length == 3) {
            return Arrays.asList("Choose a phase", "START", "FALLBACK");
        }

        if (args.length == 4) {
            return Arrays.asList("Choose a world or Run the command", world);
        }

        if (args.length == 5) {
            return Arrays.asList("Choose a x", x);
        }

        if (args.length == 6) {
            return Arrays.asList("Choose a y", y);
        }

        if (args.length == 7) {
            return Arrays.asList("Choose a z", z);
        }

        if (args.length == 8) {
            return Arrays.asList("Choose a yaw", yaw);
        }

        if (args.length == 9) {
            return Arrays.asList("Choose a pitch", pitch);
        }

        if (args.length > 9) {
            return Collections.singletonList("You can run the command");
        }

        return Collections.emptyList();
    }


}
