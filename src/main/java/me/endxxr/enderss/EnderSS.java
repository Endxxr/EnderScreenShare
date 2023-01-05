package me.endxxr.enderss;

import lombok.Getter;
import me.endxxr.enderss.commands.BlatantCommand;
import me.endxxr.enderss.commands.CleanCommand;
import me.endxxr.enderss.commands.ReportCommand;
import me.endxxr.enderss.commands.ScreenShareCommand;
import me.endxxr.enderss.commands.enderss.EnderSSCommand;
import me.endxxr.enderss.enums.Config;
import me.endxxr.enderss.exceptions.ConfigException;
import me.endxxr.enderss.listeners.CommandBlocker;
import me.endxxr.enderss.listeners.JoinLeaveListener;
import me.endxxr.enderss.listeners.ScreenShareChat;
import me.endxxr.enderss.listeners.SwitchListener;
import me.endxxr.enderss.models.PlayersManager;
import me.endxxr.enderss.scoreboard.ScoreboardManager;
import me.endxxr.enderss.utils.ChatUtils;
import me.endxxr.enderss.utils.ConfigValidator;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.md_5.bungee.api.ProxyServer;
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
import java.nio.file.StandardCopyOption;
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
    private LuckPerms luckPerms;
    @Getter
    private boolean luckPermsPresent = false;
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
        validateConfig();
        checkUpdates();
        new Metrics(this, 15533);
        checkSoftDependencies();
        setInstances();
        setCommands();
        setListeners();
    }

    private void setCommands() {
        getProxy().getPluginManager().registerCommand(this, new ScreenShareCommand(this));
        getProxy().getPluginManager().registerCommand(this, new BlatantCommand());
        getProxy().getPluginManager().registerCommand(this, new EnderSSCommand());
        getProxy().getPluginManager().registerCommand(this, this.cleanCommand);

        if (Config.REPORTS_ENABLED.getBoolean()) getProxy().getPluginManager().registerCommand(this, new ReportCommand());

    }

    private void setListeners() {
        getProxy().getPluginManager().registerListener(this, new JoinLeaveListener(this));
        getProxy().getPluginManager().registerListener(this, new SwitchListener(this));
        getProxy().getPluginManager().registerListener(this, new CommandBlocker(this));
        if (Config.CHAT_ENABLED.getBoolean()) getProxy().getPluginManager().registerListener(this, new ScreenShareChat(this));
    }

    private void validateConfig() {
        if (!ConfigValidator.validate()) {
            getLogger().severe("§c§lThe config.yml is not valid! Please fix it!");
        }
    }


    private void setInstances() {
        playersManager = new PlayersManager();
        cleanCommand = new CleanCommand();
        scoreboardManager = new ScoreboardManager(this);
        if (luckPermsPresent) {
            luckPerms = LuckPermsProvider.get();
        }

    }

    private void saveDefaultConfig() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        File file = new File(getDataFolder(), "config.yml");
        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                getLogger().severe("Could not save config.yml to " + file);
                e.printStackTrace();
            }
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)));
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(reader);
        } catch (IOException e) {
            ChatUtils.prettyPrintException(e, "There was an error while loading the configuration file.");
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

            if (externalConfig.getFloat("version") < 1.0) {
                updateFromLegacyConfig();
            }

        } catch (IOException e) {
            ChatUtils.prettyPrintException(e, "There was an error while updating the configuration file.");
        }
    }

    private void checkUpdates() {
        ProxyServer.getInstance().getScheduler().runAsync(this, () -> {
            String internalVersion = getDescription().getVersion();
            String spigotVersion = getDescription().getVersion(); //Se la connessione non va a buon fine
            try {
                InputStream is = new URL("https://api.spigotmc.org/legacy/update.php?resource=101769").openStream();
                Scanner scanner = new Scanner(is);
                if (scanner.hasNext()) {
                    spigotVersion = scanner.next();
                }
            } catch (IOException e) {
                ChatUtils.prettyPrintException(e, "There was an error while checking for updates.");
            }

            if (!internalVersion.equals(spigotVersion)) {
                getLogger().warning("There is a new version available: " + spigotVersion);
                obsoleteVersion = true;
            }

    });
}

    private void updateFromLegacyConfig() {

        File file = new File(getDataFolder(), "config.yml");
        try {
            Files.move(file.toPath(), new File(getDataFolder(), "config.yml.old").toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            ChatUtils.prettyPrintException(e, "There was an error while updating the configuration from the legacy version.");
        }

        saveDefaultConfig();
        getLogger().warning("The plugin has updated the configuration file to the new format.");



    }

    private void checkSoftDependencies() {
        liteBansPresent = getProxy().getPluginManager().getPlugin("LiteBans") != null;
        luckPermsPresent = getProxy().getPluginManager().getPlugin("LuckPerms") != null;
    }

    public void reloadConfig() {
        try {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(getDataFolder(), "config.yml")), StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)));
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(reader);
        } catch (IOException e) {
            ChatUtils.prettyPrintException(e, "There was an error while reloading the configuration file.");
        }
    }
}
