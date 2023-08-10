package dev.endxxr.enderss.api;

import dev.endxxr.enderss.api.objects.managers.PlayersManager;
import dev.endxxr.enderss.api.objects.managers.ScreenShareManager;


public interface EnderSS {

    String VERSION = "1.1.1";
    double VERSION_NUMBER = 1.1;

    PlayersManager getPlayersManager();
    ScreenShareManager getScreenShareManager();
    EnderPlugin getPlugin();
    boolean isUpdateAvailable();
    boolean isConfigObsolete();
    void shutdown();


}
