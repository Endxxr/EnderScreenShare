package dev.endxxr.enderss.velocity;


import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.lifecycle.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.connection.Player;
import com.velocitypowered.api.proxy.messages.PairedPluginChannelId;
import com.velocitypowered.api.proxy.messages.PluginChannelId;
import dev.endxxr.enderss.api.EnderPlugin;
import dev.endxxr.enderss.api.enums.Platform;
import dev.endxxr.enderss.api.enums.PluginMessageType;
import dev.endxxr.enderss.api.objects.player.SsPlayer;
import dev.endxxr.enderss.common.EnderSS;
import dev.endxxr.enderss.common.storage.GlobalConfig;
import dev.endxxr.enderss.common.utils.FileUtils;
import dev.endxxr.enderss.velocity.commands.BlatantCommand;
import dev.endxxr.enderss.velocity.commands.CleanCommand;
import dev.endxxr.enderss.velocity.commands.ReportCommand;
import dev.endxxr.enderss.velocity.commands.ScreenShareCommand;
import dev.endxxr.enderss.velocity.commands.enderss.EnderSSCommand;
import dev.endxxr.enderss.velocity.listeners.CommandBlocker;
import dev.endxxr.enderss.velocity.listeners.ConnectionListener;
import dev.endxxr.enderss.velocity.listeners.ScreenShareChat;
import dev.endxxr.enderss.velocity.listeners.SwitchListener;
import dev.endxxr.enderss.velocity.manager.PlayerManager;
import dev.endxxr.enderss.velocity.manager.ScreenShareManager;
import lombok.Getter;
import lombok.SneakyThrows;
import net.kyori.adventure.key.Key;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bstats.charts.SimplePie;
import org.bstats.velocity.Metrics;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Plugin(
        id = "enderss",
        name = "EnderSS",
        version = "1.1.0",
        description = "A simple and customizable ScreenShare plugin for Spigot, BungeeCord and Velocity",
        authors = {"Endxxr"},
        dependencies = {
                @Dependency(id = "luckperms", optional = true),
                @Dependency(id = "litebans", optional = true)
        }

)
public class EnderSSVelocity implements EnderPlugin {

    @Getter
    private static EnderSSVelocity instance;
    @Getter
    private final ProxyServer server;
    private EnderSS enderSS;
    private final Logger logger;
    private final Metrics.Factory metricsFactory;
    private final Path dataDirectory;
    private LuckPerms luckPerms;
    private YamlFile config;
    private YamlFile platformConfig;
    private boolean liteBansPresent;
    private boolean luckPermsPresent;


    @Inject
    public EnderSSVelocity(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory, Metrics.Factory metrics) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.metricsFactory = metrics;
    }

    @Subscribe
    public void onProxyStart(ProxyInitializeEvent event) {


        config = FileUtils.saveConfig("config.yml");
        platformConfig = FileUtils.saveConfig("proxy.yml");

        checkSoftDependencies();

        enderSS = new EnderSS(this,
            PlayerManager.class,
            ScreenShareManager.class
        );

        setCommands();
        setListeners();

        server.channelRegistrar().register(PluginChannelId.withLegacy("enderss:controls", Key.key("enderss", "controls")));

        Metrics metrics = metricsFactory.make(this, 15533);
        metrics.addCustomChart(new SimplePie("platform", Platform.VELOCITY::name));
    }

    private void setCommands() {

        registerCommand(new ScreenShareCommand(server), "ss", "control", "screenshare", "freeze");
        registerCommand(new EnderSSCommand(), "enderss", "ssplugin", "endersettings");
        registerCommand(new CleanCommand(server), "legit");
        registerCommand(new BlatantCommand(server), "blatant");

        if (GlobalConfig.REPORTS_ENABLED.getBoolean()) registerCommand(new ReportCommand(server), "report");


    }

    private void setListeners() {

        EventManager eventManager = server.eventManager();

        eventManager.register(this, new ConnectionListener(server));
        eventManager.register(this, new SwitchListener(server));
        eventManager.register(this, new CommandBlocker(server));

        if (GlobalConfig.CHAT_ENABLED.getBoolean()) eventManager.register(this, new ScreenShareChat(server));
    }

    private void checkSoftDependencies() {
        liteBansPresent = server.pluginManager().getPlugin("LiteBans") != null;
        if (server.pluginManager().getPlugin("LuckPerms") != null) {
            luckPermsPresent = true;
            luckPerms = LuckPermsProvider.get();
        }
    }

    @Override
    public void runTaskAsync(Runnable runnable) {
        server.scheduler().buildTask(this, runnable).schedule();
    }

    @Override
    public void runTaskTimer(Runnable runnable, int delay, int frequency) {
        server.scheduler().buildTask(this, runnable).delay(delay, TimeUnit.MILLISECONDS).repeat(frequency, TimeUnit.MILLISECONDS).schedule();
    }

    @Override
    public void runTaskLater(Runnable runnable, int delay) {
        server.scheduler().buildTask(this, runnable).delay(delay, TimeUnit.MILLISECONDS).schedule();
    }

    @Override
    public void dispatchCommand(SsPlayer sender, String command) {
        server.commandManager().execute(server.player(sender.getUUID()), command);
    }

    @SneakyThrows
    @Override
    public void reload() {
        config = new YamlFile(new File(dataDirectory.toFile(), "config.yml"));
        platformConfig = new YamlFile(new File(dataDirectory.toFile(), "proxy.yml"));

        config.load();
        platformConfig.load();

        sendPluginMessage(enderSS.getPlayersManager().getPlayer(server.connectedPlayers().iterator().next().id()),
                PluginMessageType.RELOAD);
    }

    @Override
    public void sendPluginMessage(SsPlayer staffer, SsPlayer suspect, PluginMessageType type) {


        Player player = server.player(staffer.getUUID());
        if (player == null) return;

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(type.name());
        out.writeUTF(staffer.getUUID().toString());
        out.writeUTF(suspect.getUUID().toString());


        PairedPluginChannelId id = PluginChannelId.withLegacy("enderss:controls", Key.key("enderss", "controls"));
        player.sendPluginMessage(id, out.toByteArray());
    }

    @Override
    public void sendPluginMessage(SsPlayer staffer, PluginMessageType type) {

        Player player = server.player(staffer.getUUID());
        if (player == null) return;

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(type.name());
        out.writeUTF(staffer.getUUID().toString());


        PairedPluginChannelId id = PluginChannelId.withLegacy("enderss:controls", Key.key("enderss", "controls"));
        player.sendPluginMessage(id, out.toByteArray());
    }

    @Override
    public Platform getPlatform() {
        return Platform.VELOCITY;
    }

    @Override
    public YamlFile getGeneralConfig() {
        return config;
    }

    @Override
    public YamlFile getPlatformConfig() {
        return platformConfig;
    }

    @Override
    public Logger getLog() {
        return logger;
    }

    @Override
    public LuckPerms getLuckPermsAPI() {
        return luckPerms;
    }

    @Override
    public boolean isLuckPermsPresent() {
        return luckPermsPresent;
    }

    @Override
    public boolean isLiteBansPresent() {
        return liteBansPresent;
    }

    private void registerCommand(Command command, String name, String... aliases ) {
        CommandMeta meta = server.commandManager()
                .createMetaBuilder(name)
                .aliases(aliases)
                .build();
        server.commandManager().register(meta, command);
    }

}
