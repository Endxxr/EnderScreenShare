package me.endxxr.enderss;

import lombok.Getter;
import me.endxxr.enderss.commands.BlatantCommand;
import me.endxxr.enderss.commands.CleanCommand;
import me.endxxr.enderss.commands.ReportCommand;
import me.endxxr.enderss.commands.ScreenShareCommand;
import me.endxxr.enderss.commands.enderss.EnderSSCommand;
import me.endxxr.enderss.listeners.CommandBlocker;
import me.endxxr.enderss.listeners.JoinLeaveEvent;
import me.endxxr.enderss.listeners.SwitchEvent;
import me.endxxr.enderss.models.PlayersManager;
import me.endxxr.enderss.scoreboard.ScoreboardManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.bstats.bungeecord.Metrics;

import java.io.*;
import java.net.URL;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Scanner;

public final class EnderSS extends Plugin {
    @Getter
    private static EnderSS instance;

    @Getter
    private Configuration configuration;
    @Getter
    private boolean obsoleteConfig;
    @Getter
    private boolean obsoleteVersion = false;
    @Getter
    private boolean liteBansPresent = false;
    @Getter
    private PlayersManager playersManager;
    @Getter
    private ScoreboardManager scoreboardManager;
    @Getter
    private CleanCommand cleanCommand;


    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("§8§l§m------------------");
        getLogger().info("");
        getLogger().info("§5§lEnderSS §8§l» §d§lDisabling...");
        getLogger().info("§d§l! Ender#0069 for support");
        getLogger().info("");
        getLogger().info("§8§l§m------------------");
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        getLogger().info("§8§l§m------------------");
        getLogger().info("");
        getLogger().info("§5§lEnderSS §8§l» §d§l1.0 §5(Made by Endxxr)");
        getLogger().info("§d§l! Ender#0069 for support");
        getLogger().info("");
        getLogger().info("§8§l§m------------------");
        saveDefaultConfig();
        updateConfig();
        checkUpdates();
        new Metrics(this, 15533);
        setInstances();
        setCommands();
        setListeners();
        checkSoftDependencies();
    }

    private void setCommands() {
        getProxy().getPluginManager().registerCommand(this, new ScreenShareCommand(this));
        getProxy().getPluginManager().registerCommand(this, new BlatantCommand());
        getProxy().getPluginManager().registerCommand(this, new ReportCommand());
        getProxy().getPluginManager().registerCommand(this, new EnderSSCommand());
        getProxy().getPluginManager().registerCommand(this, this.cleanCommand);
    }

    private void setListeners() {
        getProxy().getPluginManager().registerListener(this, new JoinLeaveEvent());
        getProxy().getPluginManager().registerListener(this, new SwitchEvent());
        getProxy().getPluginManager().registerListener(this, new CommandBlocker());
    }

    private void setInstances() {
        playersManager = new PlayersManager();
        cleanCommand = new CleanCommand();
        scoreboardManager = new ScoreboardManager(this);
    }

    private void saveDefaultConfig() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        final File file = new File(getDataFolder(), "config.yml");
        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                getLogger().severe("Could not save config.yml to " + file);
                e.printStackTrace();
            }
        }
        try {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)));
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(reader);
        } catch (IOException e) {
            getLogger().severe("Could not load config.yml from " + file);
            e.printStackTrace();
        }

    }

    private void updateConfig() {
        try {
            Configuration internalConfig = ConfigurationProvider.getProvider(YamlConfiguration.class).load(getResourceAsStream("config.yml"));
            Configuration externalConfig = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
            if (internalConfig.getFloat("version") > externalConfig.getFloat("version")) {
                getLogger().warning("Your plugin configuration is obsolete");
                obsoleteConfig = true;
            }
        } catch (IOException e) {
            getLogger().severe("Couldn't check the config ! Check the error(s) below and try to fix it");
            e.printStackTrace();
        }
    }

    public void reloadConfig() {
        try {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(getDataFolder(), "config.yml")), StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)));
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(reader);
        } catch (IOException e) {
            getLogger().severe("Could not load config.yml from " + new File(getDataFolder(), "config.yml"));
            e.printStackTrace();
        }
    }

    private void checkUpdates() {
        ProxyServer.getInstance().getScheduler().runAsync(this, () -> {
            final String internalVersion = getDescription().getVersion();
            String spigotVersion = getDescription().getVersion();
            try {
                final InputStream is = new URL("https://api.spigotmc.org/legacy/update.php?resource=101769").openStream();
                final Scanner scanner = new Scanner(is);
                if (scanner.hasNext()) {
                    spigotVersion = scanner.next();
                }
            } catch (IOException e) {
                getLogger().warning("Could not check for updates: " + e.getMessage());
                return;
            }

            if (!internalVersion.equals(spigotVersion)) {
                getLogger().warning("There is a new version available: " + spigotVersion);
                obsoleteVersion = true;
            }

    });
}

    private void checkSoftDependencies() {
        liteBansPresent = getProxy().getPluginManager().getPlugin("LiteBans") != null;
    }
}
