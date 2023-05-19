package dev.endxxr.enderss.bungeecord;


import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import dev.endxxr.enderss.api.EnderPlugin;
import dev.endxxr.enderss.api.EnderSSAPI;
import dev.endxxr.enderss.api.enums.Platform;
import dev.endxxr.enderss.api.enums.PluginMessageType;
import dev.endxxr.enderss.api.objects.SSPlayer;
import dev.endxxr.enderss.api.utils.LogUtils;
import dev.endxxr.enderss.bungeecord.commands.BlatantCommand;
import dev.endxxr.enderss.bungeecord.commands.CleanCommand;
import dev.endxxr.enderss.bungeecord.commands.ReportCommand;
import dev.endxxr.enderss.bungeecord.commands.ScreenShareCommand;
import dev.endxxr.enderss.bungeecord.commands.enderss.EnderSSCommand;
import dev.endxxr.enderss.bungeecord.listeners.CommandBlocker;
import dev.endxxr.enderss.bungeecord.listeners.JoinLeaveListener;
import dev.endxxr.enderss.bungeecord.listeners.ScreenShareChat;
import dev.endxxr.enderss.bungeecord.listeners.SwitchListener;
import dev.endxxr.enderss.bungeecord.managers.BungeePlayersManager;
import dev.endxxr.enderss.bungeecord.managers.BungeeScreenShareManager;
import dev.endxxr.enderss.common.EnderSS;
import dev.endxxr.enderss.common.storage.GlobalConfig;
import dev.endxxr.enderss.common.storage.ProxyConfig;
import lombok.SneakyThrows;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.bstats.bungeecord.Metrics;
import org.jetbrains.annotations.NotNull;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public final class EnderSSBungeecord extends Plugin implements EnderPlugin {
    private EnderSSBungeecord instance;
    private EnderSS enderSS;
    private YamlFile generalConfig;
    private YamlFile platformConfig;

    private boolean obsoleteConfig;
    private boolean liteBansPresent = false;
    private LuckPerms luckPerms;
    private boolean luckPermsPresent = false;


    @Override
    public void onDisable() {

    }

    @Override
    public void onEnable() {

        instance = this;
        generalConfig = saveConfig("config.yml");
        platformConfig = saveConfig("proxy.yml");


        updateConfig();
        checkSoftDependencies();


        enderSS = new EnderSS(this,
                generalConfig.getString("version"),
                BungeePlayersManager.class,
                null,
                BungeeScreenShareManager.class
        );
        enderSS.start();

        setCommands();
        setListeners();


        getProxy().registerChannel("enderss:controls");


        new Metrics(this, 15533);
    }

    private void setCommands() {
        getProxy().getPluginManager().registerCommand(this, new ScreenShareCommand());
        getProxy().getPluginManager().registerCommand(this, new EnderSSCommand());
        getProxy().getPluginManager().registerCommand(this, new BlatantCommand());
        getProxy().getPluginManager().registerCommand(this, new CleanCommand());

        if (GlobalConfig.REPORTS_ENABLED.getBoolean()) getProxy().getPluginManager().registerCommand(this, new ReportCommand());
    }


    private void setListeners() {
        getProxy().getPluginManager().registerListener(this, new JoinLeaveListener());
        getProxy().getPluginManager().registerListener(this, new SwitchListener());
        getProxy().getPluginManager().registerListener(this, new CommandBlocker());
        if (GlobalConfig.CHAT_ENABLED.getBoolean()) getProxy().getPluginManager().registerListener(this, new ScreenShareChat());
    }

    @SneakyThrows
    private YamlFile saveConfig(String fileName) {
        if (!getDataFolder().exists()) {
            Files.createDirectory(getDataFolder().toPath());
        }
        File file = new File(getDataFolder(), fileName);
        if (!file.exists()) {
            try (InputStream in = getResourceAsStream(fileName)) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                getLogger().severe("Could not save "+fileName+" to "+ file);
                e.printStackTrace();
            }
        }

        YamlFile yamlFile = new YamlFile(file.getAbsolutePath());
        yamlFile.load();
        return yamlFile;
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
            LogUtils.prettyPrintException(e, "There was an error while updating the configuration file.");
        }
    }

    private void updateFromLegacyConfig() {

        File file = new File(getDataFolder(), "config.yml");
        try {
            Files.move(file.toPath(), new File(getDataFolder(), "config.yml.old").toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            LogUtils.prettyPrintException(e, "There was an error while updating the configuration from the legacy version.");
        }

        saveConfig("config.yml");
        getLogger().warning("The plugin has updated the configuration file to the new format.");



    }

    private void checkSoftDependencies() {
        liteBansPresent = getProxy().getPluginManager().getPlugin("LiteBans") != null;
        if (getProxy().getPluginManager().getPlugin("LuckPerms") != null) {
           luckPermsPresent = true;
           luckPerms = LuckPermsProvider.get();
        }
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
    public void reload() {
        generalConfig = new YamlFile(new File(getDataFolder(), "config.yml"));
        platformConfig = new YamlFile(new File(getDataFolder(), "proxy.yml"));
        sendPluginMessage(enderSS.getPlayersManager().getPlayer(ProxyServer.getInstance().getPlayers().iterator().next().getUniqueId()),
                PluginMessageType.RELOAD);
    }

    @Override
    public void sendPluginMessage(SSPlayer staffer, SSPlayer suspect, PluginMessageType type) {

        ProxiedPlayer bungeeStaffer = ProxyServer.getInstance().getPlayer(staffer.getUUID());
        ProxiedPlayer bungeeSuspect = ProxyServer.getInstance().getPlayer(suspect.getUUID());

        ByteArrayDataOutput packet = ByteStreams.newDataOutput();
        packet.writeUTF(type.getString());
        packet.writeUTF(bungeeStaffer.getName());
        packet.writeUTF(bungeeSuspect.getName());


        ProxyServer.getInstance().getServerInfo(ProxyConfig.SS_SERVER.getString()).sendData("enderss:controls", packet.toByteArray());

    }

    @Override
    public void sendPluginMessage(SSPlayer player, PluginMessageType type) {

        ProxiedPlayer bungeePlayer = ProxyServer.getInstance().getPlayer(player.getUUID());

        if (bungeePlayer==null ) {
            EnderSSAPI.Provider.getApi().getPlugin().getLog().warning("The network is empty, can't forward the message to the ss server.");
            return;
        }

        ByteArrayDataOutput packet = ByteStreams.newDataOutput();
        packet.writeUTF(type.getString());
        packet.writeUTF(bungeePlayer.getName());

        ProxyServer.getInstance().getServerInfo(ProxyConfig.SS_SERVER.getString()).sendData("enderss:controls", packet.toByteArray());


    }

    @Override
    public Logger getLog() {
        return getLogger();
    }

    @Override
    public void runTaskAsync(Runnable runnable) {
        ProxyServer.getInstance().getScheduler().runAsync(this, runnable);
    }

    @Override
    public void runTaskTimer(Runnable runnable, int delay, int frequency) {
        ProxyServer.getInstance().getScheduler().schedule(this, runnable, delay, frequency, TimeUnit.MILLISECONDS);
    }

    @Override
    public void runTaskLater(Runnable runnable, int delay) {
        ProxyServer.getInstance().getScheduler().schedule(this, runnable, delay, TimeUnit.MILLISECONDS);
    }

    @Override
    public void dispatchCommand(SSPlayer sender, String command) {
        CommandSender finalSender;

        if (sender==null) {
            finalSender=ProxyServer.getInstance().getConsole();
        } else {
            finalSender= ProxyServer.getInstance().getPlayer(sender.getUUID());
        }

        ProxyServer.getInstance().getPluginManager().dispatchCommand(finalSender, command);
    }

    @Override
    public Platform getPlatform() {
        return Platform.BUNGEECORD;
    }

    @Override
    public boolean isLuckPermsPresent() {
        return luckPermsPresent;
    }

    @Override
    public @NotNull LuckPerms getLuckPermsAPI() {
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


    public EnderSSBungeecord getInstance() {
        return instance;
    }
}
