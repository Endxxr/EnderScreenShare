package dev.endxxr.enderss.api;

import dev.endxxr.enderss.api.objects.managers.PlayersManager;
import dev.endxxr.enderss.api.objects.managers.ScreenShareManager;


public interface EnderSS {


    String VERSION = "1.1.0";
    double VERSION_NUMBER = 1.1;

    /**
     *
     * Returns the Player Manager of the plugin
     *
     */
    PlayersManager getPlayersManager();
    ScreenShareManager getScreenShareManager();
    EnderPlugin getPlugin();
    void start();
    void shutdown();
    boolean isUpdateAvailable();
    boolean isConfigObsolete();



}
