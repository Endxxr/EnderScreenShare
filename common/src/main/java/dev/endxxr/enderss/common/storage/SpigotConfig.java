package dev.endxxr.enderss.common.storage;

import dev.endxxr.enderss.api.EnderSSAPI;
import org.simpleyaml.configuration.file.YamlFile;

public enum SpigotConfig {

    TELEPORT_ON_START("teleport-on-start"),
    SPAWN_ENABLED("spawn.enabled"),
    SPAWN_STAFF("spawn.staff"),
    SPAWN_SUSPECT("spawn.suspect"),
    PROTECTIONS_BYPASS_PERMISSION("protections.bypass-permission"),
    PROTECTIONS_STAFF_BYPASS("protections.staff-bypass"),
    PROTECTIONS_WORLD_DISABLED_WORLDS("protections.world.disabled-worlds"),
    PROTECTIONS_WORLD_BUILD_BREAK("protections.world.build-break"),
    PROTECTIONS_WORLD_PVP("protections.world.pvp"),
    PROTECTIONS_WORLD_HUNGER("protections.world.hunger"),
    PROTECTIONS_WORLD_DAYLIGHT_CYCLE("protections.world.daylight-cycle"),
    PROTECTIONS_WORLD_MOB_SPAWN("protections.world.mob-spawn"),
    PROTECTIONS_WORLD_FIRE_SPREAD("protections.world.fire-spread"),
    PROTECTIONS_WORLD_VOID("protections.world.void"),
    PROTECTIONS_PLAYER_PVP("protections.player.pvp"),
    PROTECTIONS_PLAYER_BUILD_BREAK("protections.player.build-break"),
    PROTECTIONS_PLAYER_PICK_DROP_ITEMS("protections.player.pick-drop-items"),
    PROTECTIONS_PLAYER_HUNGER("protections.player.hunger"),
    PROTECTIONS_PLAYER_REMOVE_EFFECTS("protections.player.remove-effects"),
    PROTECTIONS_PLAYER_ADVENTURE_MODE("protections.player.adventure-mode")
    ;

    private static final YamlFile config = EnderSSAPI.Provider.getApi().getPlugin().getPlatformConfig();
    private final String path;

    SpigotConfig(String s) {
        path = s;
    }

    public boolean getBoolean() {
        return config.getBoolean(path);
    }

    public String getString() {
        return config.getString(path);
    }


}
