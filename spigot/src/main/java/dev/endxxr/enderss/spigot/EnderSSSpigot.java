package dev.endxxr.enderss.spigot;

import dev.endxxr.enderss.api.EnderPlugin;
import dev.endxxr.enderss.api.enums.Platform;
import dev.endxxr.enderss.api.enums.PluginMessageType;
import dev.endxxr.enderss.api.objects.SSPlayer;
import dev.endxxr.enderss.api.utils.LogUtils;
import dev.endxxr.enderss.common.EnderSS;
import dev.endxxr.enderss.common.storage.GlobalConfig;
import dev.endxxr.enderss.spigot.listeners.ControlsMessageListener;
import dev.endxxr.enderss.spigot.managers.SpigotPlayerManager;
import dev.endxxr.enderss.spigot.managers.SpigotScoreboardManager;
import dev.endxxr.enderss.spigot.managers.SpigotScreenShareManager;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.logging.Logger;

public final class EnderSSSpigot extends JavaPlugin implements EnderPlugin {

    private EnderSSSpigot instance;
    private EnderSS enderSS;
    private YamlFile generalConfig;
    private YamlFile platformConfig;
    private LuckPerms luckPerms;
    private boolean obsoleteConfig;
    private boolean liteBansPresent = false;
    private boolean luckPermsPresent = false;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        updateConfig();
        checkSoftDependencies();

        saveDefaultConfig();
        saveResource("config.yml", false);
        generalConfig = new YamlFile(new File(getDataFolder(), "config.yml"));
        platformConfig = new YamlFile(new File(getDataFolder(), "spigot.yml"));

        boolean proxyMode = GlobalConfig.PROXY_MODE.getBoolean();

        if (proxyMode) {
            getLogger().warning("Proxy mode is enabled. Some features won't work.");
            Bukkit.getMessenger().registerIncomingPluginChannel(this, "enderss:controls", new ControlsMessageListener());
        }

        enderSS = new EnderSS(this,
                generalConfig.getString("version"),
                SpigotPlayerManager.class,
                SpigotScoreboardManager.class,
                SpigotScreenShareManager.class
                );

        new Metrics(this, 15533);



    }

    @Override
    public void onDisable() {
        Bukkit.getMessenger().unregisterIncomingPluginChannel(this, "enderss:controls");
    }

    private void checkSoftDependencies() {
        liteBansPresent = Bukkit.getPluginManager().getPlugin("LiteBans") != null;
        if (Bukkit.getPluginManager().getPlugin("LuckPerms") != null) {
            luckPermsPresent = true;
            luckPerms = LuckPermsProvider.get();
        }
    }

    private void updateConfig() {

        FileConfiguration internalConfig = getConfig();
        FileConfiguration externalConfig = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml"));

        if (internalConfig.getLong("version") > externalConfig.getLong("version")) {
            getLogger().warning("Your plugin configuration is obsolete");
            obsoleteConfig = true;
        }

        if (externalConfig.getLong("version") < 1.0) {
            updateFromLegacyConfig();
        }

    }

    private void updateFromLegacyConfig() {
        File file = new File(getDataFolder(), "config.yml");
        try {
            Files.move(file.toPath(), new File(getDataFolder(), "config.yml.old").toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            LogUtils.prettyPrintException(e, "There was an error while updating the configuration from the legacy version.");
        }

        saveDefaultConfig();
        getLogger().warning("The plugin has updated the configuration file to the new format.");




    }

    @Override
    public void runTaskAsync(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(this, runnable);
    }

    @Override
    public void runTaskTimer(Runnable runnable, int delay, int frequency) {
        Bukkit.getScheduler().runTaskTimer(this, runnable, delay, frequency);
    }

    @Override
    public void runTaskLater(Runnable runnable, int delay) {
        Bukkit.getScheduler().runTaskLater(this, runnable, delay);
    }

    @Override
    public void dispatchCommand(SSPlayer sender, String command) {
        Bukkit.dispatchCommand(Objects.requireNonNull(Bukkit.getPlayer(sender.getUUID())), command);
    }

    @Override
    public void reload() {
        reloadConfig();
        generalConfig = new YamlFile(new File(getDataFolder(), "config.yml"));
        platformConfig = new YamlFile(new File(getDataFolder(), "spigot.yml"));
        getLogger().info("Reloaded!");
    }

    @Override
    public void sendPluginMessage(SSPlayer staffer, SSPlayer suspect, PluginMessageType type) {
        return;
    }

    @Override
    public void sendPluginMessage(SSPlayer staffer, PluginMessageType type) {
        return;
    }

    @Override
    public Platform getPlatform() {
        return Platform.SPIGOT;
    }

    @Override
    public YamlFile getGeneralConfig() {
        return generalConfig;
    }

    @Override
    public YamlFile getPlatformConfig() {
        return platformConfig;
    }

    @Override
    public Logger getLog() {
        return getLogger();
    }

    @Override
    public boolean isLuckPermsPresent() {
        return luckPermsPresent;
    }

    @Override
    public LuckPerms getLuckPermsAPI() {
        return luckPerms;
    }

    @Override
    public boolean isLiteBansPresent() {
        return liteBansPresent;
    }

    @Override
    public boolean isConfigObsolete() {
        return obsoleteConfig;
    }

    public EnderSSSpigot getInstance() {
        return instance;
    }

    public EnderSS getEnderSS() {
        return enderSS;
    }
}
