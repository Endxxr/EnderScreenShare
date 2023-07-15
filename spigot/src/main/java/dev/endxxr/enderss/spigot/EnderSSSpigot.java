package dev.endxxr.enderss.spigot;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import dev.endxxr.enderss.api.EnderPlugin;
import dev.endxxr.enderss.api.enums.Platform;
import dev.endxxr.enderss.api.enums.PluginMessageType;
import dev.endxxr.enderss.api.objects.player.SsPlayer;
import dev.endxxr.enderss.common.EnderSS;
import dev.endxxr.enderss.common.storage.GlobalConfig;
import dev.endxxr.enderss.common.storage.SpigotConfig;
import dev.endxxr.enderss.common.utils.LogUtils;
import dev.endxxr.enderss.spigot.commands.BlatantCommand;
import dev.endxxr.enderss.spigot.commands.CleanCommand;
import dev.endxxr.enderss.spigot.commands.ReportCommand;
import dev.endxxr.enderss.spigot.commands.ScreenShareCommand;
import dev.endxxr.enderss.spigot.commands.enderss.EnderSSCommand;
import dev.endxxr.enderss.spigot.hooks.PapiExpansion;
import dev.endxxr.enderss.spigot.listeners.ControlsMessageListener;
import dev.endxxr.enderss.spigot.listeners.ConnectionListener;
import dev.endxxr.enderss.spigot.listeners.protections.PlayerProtections;
import dev.endxxr.enderss.spigot.listeners.protections.WorldProtections;
import dev.endxxr.enderss.spigot.managers.PlayerManager;
import dev.endxxr.enderss.spigot.managers.ScreenShareManager;
import lombok.SneakyThrows;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Logger;

public final class EnderSSSpigot extends JavaPlugin implements EnderPlugin {

    private EnderSS enderSS;
    private YamlFile generalConfig;
    private YamlFile platformConfig;
    private LuckPerms luckPerms;
    private boolean liteBansPresent = false;
    private boolean luckPermsPresent = false;

    @Override
    public void onEnable() {
        // Plugin startup logic

        try {
            Class.forName("net.md_5.bungee.api.ChatColor");
        } catch (ClassNotFoundException e) {
            getLogger().severe("");
            getLogger().severe("You're not using a Spigot Fork. This plugin requires BungeeCord Chat API");
            getLogger().severe("which is only available on Spigot Forks.");
            getLogger().severe("Disabling plugin...");
            getLogger().severe("");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        checkSoftDependencies();

        saveDefaultConfig();
        saveResource("spigot.yml", false);
        generalConfig = new YamlFile(new File(getDataFolder(), "config.yml"));
        platformConfig = new YamlFile(new File(getDataFolder(), "spigot.yml"));
        loadConfigs();



        enderSS = new EnderSS(this,
                PlayerManager.class,
                ScreenShareManager.class
                );

        boolean proxyMode = SpigotConfig.PROXY_MODE.getBoolean();

        if (proxyMode) {
            getLogger().warning("Proxy mode is enabled. Some features won't work.");
            Bukkit.getMessenger().registerIncomingPluginChannel(this, CHANNEL_NAME, new ControlsMessageListener());
        }

        registerCommands();
        registerListeners();

        Metrics metrics = new Metrics(this, 15533);
        metrics.addCustomChart(new SimplePie("platform", Platform.SPIGOT::name));
    }

    private void loadConfigs() {
        try {
            generalConfig.createOrLoad();
            platformConfig.createOrLoad();
        } catch (IOException e) {
            LogUtils.prettyPrintException(new RuntimeException(e), "Failed to load config files.");
        }
    }

    @Override
    public void onDisable() {
        if (SpigotConfig.PROXY_MODE.getBoolean()) {
            Bukkit.getMessenger().unregisterIncomingPluginChannel(this, CHANNEL_NAME, new ControlsMessageListener());
        }
        enderSS.shutdown();
    }

    private void checkSoftDependencies() {

        PluginManager pluginManager = Bukkit.getPluginManager();

        liteBansPresent = pluginManager.getPlugin("LiteBans") != null;

        if (pluginManager.getPlugin("LuckPerms") != null) {
            luckPermsPresent = true;
            luckPerms = LuckPermsProvider.get();
        }

        if (pluginManager.getPlugin("PlaceholderAPI") != null) {
            new PapiExpansion().register();
        }

    }

    private void registerCommands() {

        if (!SpigotConfig.PROXY_MODE.getBoolean()) {

            ScreenShareCommand screenShareCommand = new ScreenShareCommand();
            CleanCommand cleanCommand = new CleanCommand();

            getCommand("ss").setExecutor(screenShareCommand);
            getCommand("ss").setTabCompleter(screenShareCommand);
            getCommand("clean").setExecutor(cleanCommand);
            getCommand("clean").setTabCompleter(cleanCommand);
        }

        if (GlobalConfig.REPORTS_ENABLED.getBoolean()) {

            ReportCommand reportCommand = new ReportCommand();

            getCommand("report").setExecutor(reportCommand);
            getCommand("report").setTabCompleter(reportCommand);
        }

        EnderSSCommand enderSSCommand = new EnderSSCommand();
        BlatantCommand blatantCommand = new BlatantCommand();

        getCommand("senderss").setExecutor(enderSSCommand);
        getCommand("senderss").setTabCompleter(enderSSCommand);
        getCommand("blatant").setExecutor(blatantCommand);
        getCommand("blatant").setTabCompleter(blatantCommand);

    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new WorldProtections(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerProtections(), this);
        Bukkit.getPluginManager().registerEvents(new ConnectionListener(), this);
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
    public void dispatchCommand(SsPlayer sender, String command) {
        CommandSender finalSender = sender == null ? Bukkit.getConsoleSender() : Bukkit.getPlayer(sender.getUUID());
        Bukkit.dispatchCommand(finalSender, command);
    }


    @SneakyThrows
    @Override
    public void reload() {
        reloadConfig();
        generalConfig.createOrLoad();
        platformConfig.createOrLoad();
        getLogger().info("Reloaded!");
    }

    @Override
    public void sendPluginMessage(SsPlayer staffer, SsPlayer suspect, PluginMessageType type) {

        Player player = Bukkit.getPlayer(staffer.getUUID());
        if (player == null) return;

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(type.name());
        out.writeUTF(staffer.getUUID().toString());
        out.writeUTF(suspect.getUUID().toString());

        player.sendPluginMessage(this, "enderss:controls", out.toByteArray());


    }

    @Override
    public void sendPluginMessage(SsPlayer staffer, PluginMessageType type) {

        Player player = Bukkit.getPlayer(staffer.getUUID());
        if (player == null) return;

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(type.name());
        out.writeUTF(staffer.getUUID().toString());

        player.sendPluginMessage(this, "enderss:controls", out.toByteArray());
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
}
