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

public final class Enderss extends Plugin {

    public static Configuration config;
    private static Enderss plugin;
    private static final Logger log = ProxyServer.getInstance().getLogger();

    @Override
    public void onEnable() {

        // Plugin startup logic
        plugin=this;
        getLogger().info("§8§l§m------------------");
        getLogger().info("");
        getLogger().info("§5§lEnderSS §8§l» §d§l0.1 §5(Made by Endxxr)");
        getLogger().info("§d§l! Ender#0001 for support");
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
        getLogger().info("§6§lEnderSS §8§l» §e§l0.1 §6(Made by Endxxr)");
        getLogger().info("§e§lSpigotLink");
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
            InputStream internalIS = getResourceAsStream("config.yml");
            Configuration internalConfig = YamlConfiguration.getProvider(YamlConfiguration.class).load(internalIS);
            Configuration externalConfig = YamlConfiguration.getProvider(YamlConfiguration.class).load(new File(plugin.getDataFolder(), "config.yml"));
            if (internalConfig.getFloat("version") > externalConfig.getFloat("version")){
                log.warning("Updating the configuration...");
                for (String string : internalConfig.getKeys()) {
                    if (!externalConfig.contains(string)) {
                        externalConfig.set(string, internalConfig.get(string));
                    }
                }
            }

            YamlConfiguration.getProvider(YamlConfiguration.class).save(externalConfig, new File(getDataFolder(), "config.yml"));
            log.info("Configuration updated! Thank you for using EnderScreenShare");
        } catch (IOException e) {
            log.severe("Couldn't update the config ! Check the error(s) below and try to fix it");
            e.printStackTrace();
        }


    }


}
