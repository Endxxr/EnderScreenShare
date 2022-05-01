package io.github.enderf5027.enderss;

import io.github.enderf5027.enderss.commands.EnderSsCommand.EnderSsCommand;
import io.github.enderf5027.enderss.commands.blatant.BlatantCommand;
import io.github.enderf5027.enderss.commands.clean.CleanCommand;
import io.github.enderf5027.enderss.commands.clean.PlayerSwitchEvent;
import io.github.enderf5027.enderss.commands.ss.SsCommand;
import io.github.enderf5027.enderss.events.CommandBlocker;
import io.github.enderf5027.enderss.events.ProxyJoinQuitEvent;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.logging.Logger;

/*
        TODO:
        interrupt the screenshare if the players can't connect to the ss server
 */

public final class Enderss extends Plugin {

    public static Configuration config;
    private static Enderss plugin;
    private static final Logger log = ProxyServer.getInstance().getLogger();
    public static boolean obsolete;

    @Override
    public void onEnable() {

        // Plugin startup logic
        plugin=this;
        getLogger().info("§8§l§m------------------");
        getLogger().info("");
        getLogger().info("§5§lEnderSS §8§l» §d§l0.2 §5(Made by Endxxr)");
        getLogger().info("§d§l! Ender#0069 for support");
        getLogger().info("");
        getLogger().info("§8§l§m------------------");
        createConfig();
        updateConfig();
        registerConfig();

        //Register Commands
        getProxy().getPluginManager().registerCommand(this, new EnderSsCommand());
        getProxy().getPluginManager().registerCommand(this, new SsCommand());
        getProxy().getPluginManager().registerCommand(this, new CleanCommand());
        getProxy().getPluginManager().registerCommand(this, new BlatantCommand());

        //Register Events
        getProxy().getPluginManager().registerListener(this, new ProxyJoinQuitEvent());
        getProxy().getPluginManager().registerListener(this, new PlayerSwitchEvent());
        getProxy().getPluginManager().registerListener(this, new CommandBlocker());
    }

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

    //No comments needed for this method, the well-known basic code for create a config
    public void createConfig() {
        if (!getDataFolder().exists())
            getDataFolder().mkdir();
        File file = new File(getDataFolder(), "config.yml");
        if (!file.exists())
            try {
                InputStream in = getResourceAsStream("config.yml");
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public static void registerConfig() {
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(plugin.getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void checkUpdate() {} //Coming in 0.3, I need the plugin uploaded on Spigot for this (I will use Spiget API)

    public void updateConfig() {
        try{
            Configuration internalConfig = YamlConfiguration.getProvider(YamlConfiguration.class).load(getResourceAsStream("config.yml"));
            Configuration externalConfig = YamlConfiguration.getProvider(YamlConfiguration.class).load(new File(plugin.getDataFolder(), "config.yml"));
            if (internalConfig.getFloat("version") > externalConfig.getFloat("version")){
                log.warning("[EnderSS] Your plugin configuration is obsolete");
                obsolete = true;
            }
        } catch (IOException e) {
            log.severe("Couldn't check the config ! Check the error(s) below and try to fix it");
            e.printStackTrace();
        }

    }


}
