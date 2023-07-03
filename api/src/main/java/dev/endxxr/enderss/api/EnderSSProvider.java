package dev.endxxr.enderss.api;

import org.jetbrains.annotations.ApiStatus;

public class EnderSSProvider {


    private static EnderSS api = null;

    public static EnderSS getApi() {
        if (api==null) {
            throw new IllegalStateException("EnderSS API has been not initialized");
        }
        return api;
    }

    @ApiStatus.Internal
    public static void setApi(EnderSS api) {
        EnderSSProvider.api = api;
    }

    @ApiStatus.Internal
    public static void removeApi() {
        EnderSSProvider.api = null;
    }


}
