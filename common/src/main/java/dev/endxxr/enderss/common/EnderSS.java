package dev.endxxr.enderss.common;


import dev.endxxr.enderss.api.EnderPlugin;
import dev.endxxr.enderss.api.EnderSSAPI;
import dev.endxxr.enderss.api.enums.Platform;
import dev.endxxr.enderss.api.objects.managers.PlayersManager;
import dev.endxxr.enderss.api.objects.managers.ScoreboardManager;
import dev.endxxr.enderss.api.objects.managers.ScreenShareManager;
import lombok.SneakyThrows;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.Logger;

public class EnderSS implements EnderSSAPI {

    private final EnderPlugin plugin;

    private boolean updateAvailable;

    private final PlayersManager playersManager;
    private final ScreenShareManager screenShareManager;
    private ScoreboardManager scoreboardManager;

    @SneakyThrows
    public EnderSS(EnderPlugin plugin, String version, Class<? extends PlayersManager> playersManager, Class<? extends ScoreboardManager> scoreboardManager, Class<? extends ScreenShareManager> screenShareManager) {

        EnderSSAPI.Provider.setApi(this);

        //Ugly way to initialize the classes, to fix in the future
        this.plugin = plugin;
        this.playersManager = playersManager.getDeclaredConstructor().newInstance();
        this.screenShareManager = screenShareManager.getDeclaredConstructor().newInstance();
        if (scoreboardManager != null) this.scoreboardManager = scoreboardManager.getDeclaredConstructor().newInstance();
        checkUpdate(version);
    }

    public void start() {

        Logger log = plugin.getLog();

        log.info("§8§l§m------------------");
        log.info("");
        log.info("§5§lEnderSS §8§l» §d§l1.1.0 §5(by Endxxr)");
        log.info("§d§l! Ender#0069 for support");
        log.info("§8Running on "+plugin.getPlatform());
        log.info("");
        log.info("§8§l§m------------------");


    }


    private void checkUpdate(String version) {
        plugin.runTaskAsync(() -> {
            String spigotVersion = version; //Se la connessione non va a buon fine
            try {
                InputStream is = new URL("https://api.spigotmc.org/legacy/update.php?resource=101769").openStream();
                Scanner scanner = new Scanner(is);
                if (scanner.hasNext()) {
                    spigotVersion = scanner.next();
                }
            } catch (IOException e) {
                // plugin.getPluginLogger().prettyPrintException(e, "There was an error while checking for updates.");
            }

            if (!version.equalsIgnoreCase(spigotVersion)) {
                plugin.getLog().warning("There is a new version available: " + spigotVersion);
                updateAvailable = true;
            }
        });
    }



    @Override
    public PlayersManager getPlayersManager() {
        return playersManager;
    }

    @Override
    public ScoreboardManager getScoreboardManager() {
        if (!(this.plugin.getPlatform() == Platform.SPIGOT)) {
            getPlugin().getLog().warning("Inappropriate usage of the API! The ScoreboardManager isn't available on the proxy!");
            return null;
        }

        return scoreboardManager;
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
}
