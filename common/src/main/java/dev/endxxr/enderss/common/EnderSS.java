package dev.endxxr.enderss.common;


import dev.endxxr.enderss.api.EnderPlugin;
import dev.endxxr.enderss.api.EnderSSProvider;
import dev.endxxr.enderss.api.objects.managers.PlayersManager;
import dev.endxxr.enderss.api.objects.managers.ScreenShareManager;
import dev.endxxr.enderss.common.utils.FileUtils;
import dev.endxxr.enderss.common.utils.LogUtils;
import lombok.SneakyThrows;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.Logger;

public class EnderSS implements dev.endxxr.enderss.api.EnderSS {

    private final EnderPlugin plugin;
    private boolean updateAvailable;
    private boolean obsoleteConfig;
    private PlayersManager playersManager;
    private ScreenShareManager screenShareManager;

    @SneakyThrows
    public EnderSS(EnderPlugin plugin, Class<? extends PlayersManager> playersManager, Class<? extends ScreenShareManager> screenShareManager) {

        EnderSSProvider.setApi(this);

        //Ugly way to initialize the classes, to fix in the future
        this.plugin = plugin;
        if (playersManager!=null) this.playersManager = playersManager.getDeclaredConstructor().newInstance();
        if (screenShareManager!=null) this.screenShareManager = screenShareManager.getDeclaredConstructor().newInstance();

        start();
    }

    @Override
    public void start() {

        Logger log = plugin.getLog();

        log.info("§8§l§m------------------");
        log.info("");
        log.info("§5§lEnderSS §8§l» §d§l"+VERSION+" §5by Endxxr");
        log.info("§8Running on "+plugin.getPlatform());
        log.info("Enabling...");
        log.info("");
        checkUpdate();
        log.info("Enabled.");
        log.info("§8§l§m------------------");

    }

    @Override
    public void shutdown() {

        Logger log = plugin.getLog();

        log.info("§8§l§m------------------");
        log.info("");
        log.info("§5§lEnderSS §8§l» §d§l"+VERSION+" §5by Endxxr");
        log.info("§d§l! Ender#0069 for support");
        log.info("");
        log.info("Shutdown complete.");
        log.info("");
        log.info("§8§l§m------------------");
    }


    private void checkUpdate() {

        plugin.getLog().info("Checking for updates...");

        plugin.runTaskAsync(() -> {
            String spigotVersion = VERSION; //Se la connessione non va a buon fine
            try {
                InputStream is = new URL("https://api.spigotmc.org/legacy/update.php?resource=101769").openStream();
                Scanner scanner = new Scanner(is);
                if (scanner.hasNext()) {
                    spigotVersion = scanner.next();
                }
            } catch (IOException e) {
                LogUtils.prettyPrintException(e, "There was an error while checking for updates.");
            }

            if (!VERSION.equalsIgnoreCase(spigotVersion)) {
                plugin.getLog().warning("There is a new version available: " + spigotVersion);
                updateAvailable = true;
            }
        });

        checkConfigUpdate();
    }


    @SneakyThrows
    private void checkConfigUpdate() {

        plugin.getLog().info("Checking for config updates...");

        YamlFile externalConfig = plugin.getGeneralConfig();
        if ( VERSION_NUMBER > externalConfig.getDouble("version")) {
            plugin.getLog().warning("Your plugin configuration is obsolete");
            obsoleteConfig = true;
        }

        double version = externalConfig.getDouble("version");
        if (version < 1.0) {
            FileUtils.updateFromLegacyConfig(externalConfig);
        }

    }

    @Override
    public PlayersManager getPlayersManager() {
        return playersManager;
    }

    @Override
    public ScreenShareManager getScreenShareManager() {
        return screenShareManager;
    }

    @Override
    public EnderPlugin getPlugin() {
        return plugin;
    }

    @Override
    public boolean isUpdateAvailable() {
        return updateAvailable;
    }

    @Override
    public boolean isConfigObsolete() {
        return obsoleteConfig;
    }


}
