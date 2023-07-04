package dev.endxxr.enderss.common.storage;

import dev.endxxr.enderss.api.EnderSSProvider;
import org.simpleyaml.configuration.file.YamlFile;

public enum ProxyConfig {

    SS_SERVER("screenshare-server"),
    FALLBACK_SERVER("fallback-server"),
    CONFIG_LAST_CONNECTED_SERVER("last-connected-server"),
    CONFIG_FALLBACK_STAFF("fallback-staff"),
    ;
    public static final YamlFile config = EnderSSProvider.getApi().getPlugin().getPlatformConfig();
    private final String path;
    ProxyConfig(String s) {
        path = s;
    }

    public boolean getBoolean() {
        return config.getBoolean(path);
    }

    public String getString() {
        return config.getString(path);
    }



}
