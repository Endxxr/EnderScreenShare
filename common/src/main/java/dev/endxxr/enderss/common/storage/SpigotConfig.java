package dev.endxxr.enderss.common.storage;

import dev.endxxr.enderss.api.EnderSSProvider;
import org.simpleyaml.configuration.file.YamlFile;

import java.util.List;

public enum SpigotConfig {

    PROXY_MODE("proxy-mode"),
    DISABLED("disabled"),
    TELEPORT_ON_START("teleport-on-start"),
    SPAWN_ENABLED("spawn.enabled"),
    FALLBACK_ENABLED("fallback.enabled"),
    COMMANDS_SET_SPAWN_USAGE("commands.set-spawn.usage"),
    COMMANDS_SET_SPAWN_SET("commands.set-spawn.set"),
    COMMANDS_SET_SPAWN_INVALID_WORLD("commands.set-spawn.invalid-world"),
    COMMANDS_SET_SPAWN_INVALID_COORDINATES("commands.set-spawn.invalid-coordinates"),
    COMMANDS_SET_SPAWN_ERROR("commands.set-spawn.error"),
    PROTECTIONS_BYPASS_PERMISSION("protections.bypass-permission"),
    PROTECTIONS_STAFF_BYPASS("protections.staff-bypass"),
    PROTECTIONS_WORLD_WHITE_LISTED_WORLDS("protections.world.white-listed-worlds"),
    PROTECTIONS_WORLD_BUILD_BREAK("protections.world.build-break"),
    PROTECTIONS_WORLD_PVP("protections.world.pvp"),
    PROTECTIONS_WORLD_INVULNERABILITY("protections.world.invulnerability"),
    PROTECTIONS_WORLD_HUNGER("protections.world.hunger"),
    PROTECTIONS_WORLD_DAYLIGHT_CYCLE("protections.world.daylight-cycle"),
    PROTECTIONS_WORLD_WEATHER_CYCLE("protections.world.weather-cycle"),
    PROTECTIONS_WORLD_MOB_SPAWN("protections.world.mob-spawn"),
    PROTECTIONS_WORLD_FIRE_SPREAD("protections.world.fire-spread"),
    PROTECTIONS_WORLD_VOID("protections.world.void"),
    PROTECTIONS_PLAYER_PVP("protections.player.pvp"),
    PROTECTIONS_PLAYER_DAMAGE("protections.player.damage"),
    PROTECTIONS_PLAYER_BUILD_BREAK("protections.player.build-break"),
    PROTECTIONS_PLAYER_PICK_DROP_ITEMS("protections.player.pick-drop-items"),
    PROTECTIONS_PLAYER_HUNGER("protections.player.hunger"),
    PROTECTIONS_PLAYER_REMOVE_EFFECTS("protections.player.remove-effects"),
    PROTECTIONS_PLAYER_ADVENTURE_MODE("protections.player.adventure-mode"),
    PLACEHOLDER_YES("placeholder.positive"),
    PLACEHOLDER_NO("placeholder.negative"),
    PLACEHOLDER_NONE("placeholder.none"),
    ;

    public static final YamlFile config = EnderSSProvider.getApi().getPlugin().getPlatformConfig();
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

    public List<String> getStringList() {
        return config.getStringList(path);
    }

}
