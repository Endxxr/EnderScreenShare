package me.endxxr.enderss.enums;

import me.endxxr.enderss.EnderSS;
import net.md_5.bungee.config.Configuration;

import java.util.List;
import java.util.stream.Collectors;

public enum Config {

    CONFIG_SSSERVER("config.screenshare-server"),
    CONFIG_FALLBACK("config.fallback-server"),
    CONFIG_LAST_CONNECTED_SERVER("config.last-connected-server"),
    CONFIG_FALLBACK_STAFF("config.fallback-staff"),
    CONFIG_CLEAR_CHAT("config.clear-chat"),
    CONFIG_STAFF_CONTROLLABLE("config.staff-controllable"),
    CONFIG_TERMINATE_SCREENSHARE_ON_SWITCH("config.terminate_screenshare-on-switch"),
    START_ANYDESK_SEND("start.anydesk.sender"),
    START_ANYDESK_MESSAGE("start.anydesk.message"),
    START_TEAMSPEAK_SEND("start.teamspeak.sender"),
    START_TEAMSPEAK_MESSAGE("start.teamspeak.message"),
    START_DISCORD_SEND("start.discord.sender"),
    START_DISCORD_MESSAGE("start.discord.message"),
    START_SS_MESSAGE("start.ss-message"),
    START_STAFF_MESSAGE("start.staff-message"),
    START_TITLE_SEND("start.title.send"),
    START_TITLE_TITLE("start.title.title"),
    START_TITLE_SUBTITLE("start.title.subtitle"),
    START_TITLE_FADEIN("start.title.fadein"),
    START_TITLE_FADEOUT("start.title.fadeout"),
    START_TITLE_STAY("start.title.stay"),
    START_BUTTONS("start.buttons"),
    START_BUTTONS_ELEMENTS("start.buttons.elements"),
    START_BUTTONS_SEND("start.buttons.send"),
    START_BUTTONS_IN_LINE("start.buttons.in-line"),
    COMMAND_BLOCKER_ENABLED("command-blocker.enabled"),
    COMMAND_BLOCKER_WHITELISTED_COMMANDS("command-blocker.whitelisted-commands"),
    BAN_ON_QUIT_PREVENT_DOUBLE_BAN("ban-on-quit.prevent-double-ban"),
    BAN_ON_QUIT_COMMANDS("ban-on-quit.commands"),
    PREFIX("prefix"),
    CHAT_STAFF("chat.staff"),
    CHAT_ENABLED("chat.enabled"),
    CHAT_SUSPECT("chat.suspect"),
    CHAT_DEBUG("chat.debug"),
    MESSAGES_INFO_PLAYER_QUIT("messages.info.player-quit"),
    MESSAGES_INFO_CLEAN_PLAYER("messages.info.clean-player"),
    MESSAGES_INFO_PLAYER_CLEANED("messages.info.player-cleaned"),
    MESSAGES_INFO_COMMAND_BLOCKED("messages.info.command-blocked"),
    MESSAGES_INFO_ALERTS_ENABLED("messages.info.alerts-enabled"),
    MESSAGES_INFO_ALERTS_DISABLED("messages.info.alerts-disabled"),
    MESSAGES_ERROR_NO_PERMISSION("messages.error.no-permission"),
    MESSAGES_ERROR_NO_COMMAND("messages.error.no-command"),
    MESSAGES_ERROR_NO_PLAYER("messages.error.no-player"),
    MESSAGES_ERROR_NO_REASON("messages.error.no-reason"),
    MESSAGES_ERROR_EXEMPT("messages.error.exempt"),
    MESSAGES_ERROR_CONSOLE("messages.error.console"),
    MESSAGES_ERROR_CANT_EXECUTE("messages.error.cant-execute"),
    MESSAGES_ERROR_CANT_FALLBACK_SUSPECT("messages.error.cant-fallback-suspect"),
    MESSAGES_ERROR_CANT_FALLBACK_STAFF("messages.error.cant-fallback-staff"),
    MESSAGES_ERROR_STAFF_OFFLINE("messages.error.staff-offline"),
    MESSAGES_ERROR_PLAYER_OFFLINE("messages.error.player-offline"),
    MESSAGES_ERROR_ALREADY_IN_SS("messages.error.already-in-ss"),
    MESSAGES_ERROR_STAFF_IN_SS("messages.error.staff-in-ss"),
    MESSAGES_ERROR_ALREADY_SS_PLAYER("messages.error.already-ss-player"),
    MESSAGES_ERROR_SUSPECT_NOT_IN_SS("messages.error.suspect-not-in-ss"),
    MESSAGES_ERROR_CANT_CONNECT_TO_SS("messages.error.cant-connect-to-ss"),
    MESSAGES_ERROR_CANT_CONNECT_SUSPECT("messages.error.cant-connect-suspect"),
    MESSAGES_ERROR_CANNOT_SS_YOURSELF("messages.error.cannot-ss-yourself"),
    SCOREBOARD_ENABLED("scoreboard.enabled"),
    SCOREBOARD_NOT_CONTROLLING_PLACEHOLDER("scoreboard.not-controlling-placeholder"),
    SCOREBOARD_IDLING("scoreboard.idling"),
    SCOREBOARD_IDLING_TITLE("scoreboard.idling.title"),
    SCOREBOARD_IDLING_LINES("scoreboard.idling.lines"),
    SCOREBOARD_STAFF("scoreboard.staff"),
    SCOREBOARD_STAFF_TITLE("scoreboard.staff.title"),
    SCOREBOARD_STAFF_LINES("scoreboard.staff.lines"),
    SCOREBOARD_SUSPECT("scoreboard.suspect"),
    SCOREBOARD_SUSPECT_TITLE("scoreboard.suspect.title"),
    SCOREBOARD_SUSPECT_LINES("scoreboard.suspect.lines"),
    MESSAGES_ERROR_CANNOT_SS_STAFF("messages.error.cannot-ss-staff"),
    MESSAGES_ERROR_NOT_CONTROLLING("messages.error.not-controlling"),
    REPORTS_ENABLED("reports.enabled"),
    REPORTS_COOLDOWN("reports.cooldown"),
    REPORTS_MESSAGES_REPORT_SENT("reports.messages.report-sent"),
    REPORTS_MESSAGES_REPORT_RECEIVED("reports.messages.report-received"),
    REPORTS_MESSAGES_CANNOT_REPORT_YOURSELF("reports.messages.cannot-report-yourself"),
    REPORTS_MESSAGES_CANNOT_REPORT_STAFF("reports.messages.cannot-report-staff"),
    REPORTS_MESSAGES_WAIT("reports.messages.wait"),
    REPORTS_BUTTONS("reports.buttons"),
    REPORTS_BUTTONS_IN_LINE("reports.buttons.in-line"),
    REPORTS_NO_STAFF("reports.no-staff"),
    REPORTS_NO_STAFF_ENABLED("reports.no-staff-enabled"),
    HOURS("hours"),
    MINUTES("minutes"),
    SECONDS("seconds"),
    BUTTONS_HACK("buttons.hack"),
    BUTTONS_ADMISSION("buttons.admission"),
    BUTTONS_REFUSE("buttons.refuse"),
    BUTTONS_QUIT("buttons.quit"),
    BUTTONS_CLEAN("buttons.clean"),
    BUTTONS_CONFIRM("buttons.confirm"),
    BUTTONS_SS("buttons.ss"),
    BUTTONS_SERVER_TP("buttons.server-tp"),
    BUTTONS_BAN("buttons.ban"),
    BUTTONS_CONFIRM_BUTTONS("buttons.confirm-buttons"),
    BAN_COMMAND_REFUSE("ban.command.refuse"),
    BAN_COMMAND_QUIT("ban.command.quit"),
    BAN_COMMAND_HACK("ban.command.hack"),
    BAN_COMMAND_ADMISSION("ban.command.admission"),
    BAN_COMMAND_BLATANT("ban.command.blatant"),
    VERSION("version");
    private final String path;
    private static final EnderSS plugin = EnderSS.getInstance();

    Config(String path) {
        this.path = path;

    }

    public String getPath() {
        return path;
    }

    public String getString() {
        return plugin.getConfiguration().getString(path);
    }

    public List<String> getStringList() {
        return plugin.getConfiguration().getStringList(path);
    }

    public boolean getBoolean() {
        return plugin.getConfiguration().getBoolean(path);
    }

    public int getInt() {
        return plugin.getConfiguration().getInt(path);
    }

    public double getFloat() {
        return plugin.getConfiguration().getDouble(path);
    }

    public long getLong() {
        return plugin.getConfiguration().getLong(path);
    }

    public String getMessage() {
        return String.join("\n", getStringList());
    }

    public Configuration getSection() {
        return plugin.getConfiguration().getSection(path);
    }

    public String getButtonType(String name) {
        return plugin.getConfiguration().getString(path + "." + name + ".type");
    }

    public String getButtonText(String name) {
        return plugin.getConfiguration().getString(path + "." + name + ".text");
    }

    public String getButtonCommand(String name) {
        return plugin.getConfiguration().getString(path + "." + name + ".command");
    }



}
