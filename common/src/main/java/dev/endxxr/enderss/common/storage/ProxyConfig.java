package dev.endxxr.enderss.common.storage;

import dev.endxxr.enderss.api.EnderSSAPI;
import org.simpleyaml.configuration.file.YamlFile;

public enum ProxyConfig {

    SS_SERVER("screenshare-server"),
    FALLBACK_SERVER("fallback-server"),
    CONFIG_LAST_CONNECTED_SERVER("last-connected-server"),
    CONFIG_FALLBACK_STAFF("fallback-staff"),
    RELOAD_SS("reload-ss");
    private static final YamlFile config = EnderSSAPI.Provider.getApi().getPlugin().getPlatformConfig();
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
