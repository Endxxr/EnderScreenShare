package dev.endxxr.enderss.api;

import dev.endxxr.enderss.api.objects.managers.PlayersManager;
import dev.endxxr.enderss.api.objects.managers.ScoreboardManager;
import dev.endxxr.enderss.api.objects.managers.ScreenShareManager;
import lombok.Getter;


public interface EnderSSAPI {


    /**
     *
     * Returns the Player Manager of the plugin
     *
     */
    PlayersManager getPlayersManager();

    ScoreboardManager getScoreboardManager();

    ScreenShareManager getScreenShareManager();

    EnderPlugin getPlugin();

    boolean isUpdateAvailable();

    class Provider {
        @Getter
        private static EnderSSAPI api;

        public static void setApi(EnderSSAPI api) {
            Provider.api = api;
        }

    }


}
