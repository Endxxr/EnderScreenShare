package dev.endxxr.enderss.bungeecord;


import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import dev.endxxr.enderss.api.EnderPlugin;
import dev.endxxr.enderss.api.enums.Platform;
import dev.endxxr.enderss.api.enums.PluginMessageType;
import dev.endxxr.enderss.api.objects.player.SsPlayer;
import dev.endxxr.enderss.bungeecord.commands.BlatantCommand;
import dev.endxxr.enderss.bungeecord.commands.CleanCommand;
import dev.endxxr.enderss.bungeecord.commands.ReportCommand;
import dev.endxxr.enderss.bungeecord.commands.ScreenShareCommand;
import dev.endxxr.enderss.bungeecord.commands.enderss.EnderSSCommand;
import dev.endxxr.enderss.bungeecord.listeners.CommandBlocker;
import dev.endxxr.enderss.bungeecord.listeners.ConnectionListener;
import dev.endxxr.enderss.bungeecord.listeners.ScreenShareChat;
import dev.endxxr.enderss.bungeecord.listeners.SwitchListener;
import dev.endxxr.enderss.bungeecord.managers.PlayersManager;
import dev.endxxr.enderss.bungeecord.managers.ScreenShareManager;
import dev.endxxr.enderss.common.storage.GlobalConfig;
import dev.endxxr.enderss.common.storage.ProxyConfig;
import dev.endxxr.enderss.common.utils.FileUtils;
import lombok.SneakyThrows;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import org.bstats.bungeecord.Metrics;
import org.bstats.charts.SimplePie;
import org.jetbrains.annotations.NotNull;
import org.simpleyaml.configuration.file.YamlFile;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public final class EnderSSBungeeCord extends Plugin implements EnderPlugin {
    private EnderSSBungeeCord instance;
    private dev.endxxr.enderss.common.EnderSS enderSS;
    private YamlFile generalConfig;
    private YamlFile platformConfig;
    private boolean liteBansPresent = false;
    private boolean luckPermsPresent = false;
    private LuckPerms luckPerms;


    @Override
    public void onDisable() {
        getProxy().unregisterChannel(CHANNEL_NAME);
        enderSS.shutdown();
    }

    @Override
    public void onEnable() {

        instance = this;
        generalConfig = FileUtils.saveConfig("config.yml");
        platformConfig = FileUtils.saveConfig("proxy.yml");

        checkSoftDependencies();

        enderSS = new dev.endxxr.enderss.common.EnderSS(this,
                PlayersManager.class,
                ScreenShareManager.class
        );

        setCommands();
        setListeners();


        getProxy().registerChannel(CHANNEL_NAME);

        Metrics metrics = new Metrics(this, 15533);
        metrics.addCustomChart(new SimplePie("platform", Platform.BUNGEECORD::name));
    }

    private void setCommands() {
        getProxy().getPluginManager().registerCommand(this, new ScreenShareCommand());
        getProxy().getPluginManager().registerCommand(this, new EnderSSCommand());
        getProxy().getPluginManager().registerCommand(this, new BlatantCommand());
        getProxy().getPluginManager().registerCommand(this, new CleanCommand());

        if (GlobalConfig.REPORTS_ENABLED.getBoolean()) getProxy().getPluginManager().registerCommand(this, new ReportCommand());
    }


    private void setListeners() {
        getProxy().getPluginManager().registerListener(this, new ConnectionListener());
        getProxy().getPluginManager().registerListener(this, new SwitchListener());
        getProxy().getPluginManager().registerListener(this, new CommandBlocker());
        if (GlobalConfig.CHAT_ENABLED.getBoolean()) getProxy().getPluginManager().registerListener(this, new ScreenShareChat());
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

    @SneakyThrows
    @Override
    public void reload() {
        generalConfig.createOrLoad();
        platformConfig.createOrLoad();

        sendPluginMessage(enderSS.getPlayersManager().getRegisteredPlayers().iterator().next(),
                PluginMessageType.RELOAD);
    }

    @Override
    public void sendPluginMessage(SsPlayer staffer, SsPlayer suspect, PluginMessageType type) {

        ProxiedPlayer bungeeStaffer = ProxyServer.getInstance().getPlayer(staffer.getUUID());
        ProxiedPlayer bungeeSuspect = ProxyServer.getInstance().getPlayer(suspect.getUUID());

        ByteArrayDataOutput packet = ByteStreams.newDataOutput();
        packet.writeUTF(type.getString());
        packet.writeUTF(bungeeStaffer.getUniqueId().toString());
        packet.writeUTF(bungeeSuspect.getUniqueId().toString());


        ProxyServer.getInstance().getServerInfo(ProxyConfig.SS_SERVER.getString()).sendData("enderss:controls", packet.toByteArray());

    }

    @Override
    public void sendPluginMessage(SsPlayer player, PluginMessageType type) {

        ProxiedPlayer bungeePlayer = ProxyServer.getInstance().getPlayer(player.getUUID());

        if (bungeePlayer==null ) {
            getLogger().warning("The network is empty, can't forward the message to the ss server.");
            return;
        }

        ByteArrayDataOutput packet = ByteStreams.newDataOutput();
        packet.writeUTF(type.getString());
        packet.writeUTF(bungeePlayer.getUniqueId().toString());

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
    public void dispatchCommand(SsPlayer sender, String command) {
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

    public EnderSSBungeeCord getInstance() {
        return instance;
    }
}
