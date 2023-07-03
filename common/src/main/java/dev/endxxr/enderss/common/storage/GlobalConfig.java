package dev.endxxr.enderss.common.storage;

import dev.endxxr.enderss.api.EnderSS;
import dev.endxxr.enderss.api.EnderSSProvider;
import dev.endxxr.enderss.common.utils.ChatUtils;
import org.simpleyaml.configuration.ConfigurationSection;
import org.simpleyaml.configuration.file.YamlFile;

import java.util.HashMap;
import java.util.List;

public enum GlobalConfig {

    PREFIX("prefix"),
    STAFF_CONTROLLABLE("staff-controllable"),
    START_CLEAR_CHAT("start.clear-chat"),
    START_ANYDESK_SEND("start.anydesk.send"),
    START_ANYDESK_MESSAGE("start.anydesk.message"),
    START_TEAMSPEAK_SEND("start.teamspeak.send"),
    START_TEAMSPEAK_MESSAGE("start.teamspeak.message"),
    START_DISCORD_SEND("start.discord.send"),
    START_DISCORD_MESSAGE("start.discord.message"),
    START_SS_MESSAGE("start.ss-message"),
    START_STAFF_MESSAGE("start.staff-message"),
    START_TITLE_SEND("start.title.send"),
    START_TITLE_TITLE("start.title.title"),
    START_TITLE_SUBTITLE("start.title.subtitle"),
    START_TITLE_FADEIN("start.title.fadein"),
    START_TITLE_FADEOUT("start.title.fadeout"),
    START_TITLE_DELAY("start.title.delay"),
    START_TITLE_STAY("start.title.stay"),
    START_BUTTONS("start.buttons"),
    START_BUTTONS_ELEMENTS("start.buttons.elements"),
    START_BUTTONS_IN_LINE("start.buttons.in-line"),
    COMMAND_BLOCKER_ENABLED("command-blocker.enabled"),
    COMMAND_BLOCKER_WHITELISTED_COMMANDS("command-blocker.whitelisted-commands"),
    BAN_ON_QUIT_PREVENT_DOUBLE_BAN("ban-on-quit.prevent-double-ban"),
    BAN_ON_QUIT_COMMANDS("ban-on-quit.commands"),
    CHAT_ENABLED("chat.enabled"),
    CHAT_STAFFER_READS_STAFFERS("chat.staffer-reads-staffers"),
    CHAT_NOT_INVOLVED_EVERYONE("chat.not-involved-everyone"),
    CHAT_FORMAT_STAFF("chat.format.staff"),
    CHAT_FORMAT_SUSPECT("chat.format.suspect"),
    CHAT_FORMAT_NOT_INVOLVED("chat.format.not-involved"),
    CHAT_PREFIX_STAFF("chat.prefix.staff"),
    CHAT_PREFIX_SUSPECT("chat.prefix.suspect"),
    CHAT_PREFIX_NOT_INVOLVED("chat.prefix.not-involved"),
    CHAT_CANCEL_EVENT("chat.cancel-event"),
    MESSAGES_INFO_PLAYER_QUIT("messages.info.player-quit"),
    MESSAGES_INFO_CONTROL_ENDED("messages.info.control-ended"),
    MESSAGES_INFO_PLAYER_CLEANED("messages.info.player-cleaned"),
    MESSAGES_INFO_COMMAND_BLOCKED("messages.info.command-blocked"),
    MESSAGES_INFO_ALERTS_ENABLED("messages.info.alerts-enabled"),
    MESSAGES_INFO_ALERTS_DISABLED("messages.info.alerts-disabled"),
    MESSAGES_ERROR_GENERIC("messages.error.generic"),
    MESSAGES_ERROR_NO_PERMISSION("messages.error.no-permission"),
    MESSAGES_ERROR_NO_COMMAND("messages.error.no-command"),
    MESSAGES_ERROR_NO_PLAYER("messages.error.no-player"),
    MESSAGES_ERROR_NO_REASON("messages.error.no-reason"),
    MESSAGES_ERROR_EXEMPT("messages.error.exempt"),
    MESSAGES_ERROR_CONSOLE("messages.error.console"),
    MESSAGES_ERROR_CANT_EXECUTE("messages.error.cant-execute"),
    MESSAGES_ERROR_STAFF_OFFLINE("messages.error.staff-offline"),
    MESSAGES_ERROR_PLAYER_OFFLINE("messages.error.player-offline"),
    MESSAGES_ERROR_ALREADY_IN_SS("messages.error.already-in-ss"),
    MESSAGES_ERROR_STAFF_IN_SS("messages.error.staff-in-ss"),
    MESSAGES_ERROR_ALREADY_SS_PLAYER("messages.error.already-ss-player"),
    MESSAGES_ERROR_SUSPECT_NOT_IN_SS("messages.error.suspect-not-in-ss"),
    MESSAGES_ERROR_CANT_CONNECT_TO_SS("messages.error.cant-connect-to-ss"),
    MESSAGES_ERROR_CANNOT_SS_YOURSELF("messages.error.cannot-ss-yourself"),
    MESSAGES_ERROR_CANNOT_SS_STAFF("messages.error.cannot-ss-staff"),
    MESSAGES_ERROR_NOT_CONTROLLING("messages.error.not-controlling"),
    REPORTS_ENABLED("reports.enabled"),
    REPORTS_COOLDOWN("reports.cooldown"),
    REPORTS_MESSAGES_REPORT_SENT("reports.messages.report-sent"),
    REPORTS_MESSAGES_REPORT_RECEIVED("reports.messages.report-received"),
    REPORTS_MESSAGES_CANNOT_REPORT_YOURSELF("reports.messages.cannot-report-yourself"),
    REPORTS_MESSAGES_CANNOT_REPORT_STAFF("reports.messages.cannot-report-staff"),
    REPORTS_MESSAGES_WAIT("reports.messages.wait"),
    REPORTS_BUTTONS_ELEMENTS("reports.buttons.elements"),
    REPORTS_BUTTONS_IN_LINE("reports.buttons.in-line"),
    REPORTS_NO_STAFF("reports.no-staff"),
    REPORTS_NO_STAFF_ENABLED("reports.no-staff-enabled"),
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
    BAN_COMMAND_REFUSE("ban-command.refuse"),
    BAN_COMMAND_QUIT("ban-command.quit"),
    BAN_COMMAND_HACK("ban-command.hack"),
    BAN_COMMAND_ADMISSION("ban-command.admission"),
    BAN_COMMAND_BLATANT("ban-command.blatant"),
    ;

    private static final EnderSS api = EnderSSProvider.getApi();
    public static final YamlFile config = api.getPlugin().getGeneralConfig();


    GlobalConfig(String path) {
        this.path = path;
    }
    private final String path;

    public String getString() {
        String s =config.getString(path);
        return s == null ? "" : s;
    }

    public List<String> getStringList() {
        return config.getStringList(path);
    }

    public boolean getBoolean() {
        return config.getBoolean(path);
    }

    public int getInt() {
        return config.getInt(path);
    }

    public long getLong() {
        return config.getLong(path);
    }

    public String getMessage() {
        return String.join("\n", getStringList());
    }

    public ConfigurationSection getSection() {
        return config.getConfigurationSection(path);
    }

    public String getButtonType(String name) {
        return config.getString(path + "." + name + ".type");
    }

    public String getButtonText(String name) {
        return config.getString(path + "." + name + ".text");
    }

    public String getButtonCommand(String name) {
        return config.getString(path + "." + name + ".command");
    }


    public static HashMap<String, String> getScreenShareButtons(String suspectName) {

        HashMap<String, String> buttons = new HashMap<>();

        for (String button : GlobalConfig.START_BUTTONS_ELEMENTS.getSection().getKeys(false)) {

            String typeName = GlobalConfig.START_BUTTONS_ELEMENTS.getButtonType(button).toUpperCase();
            GlobalConfig type;
            try {
                type = GlobalConfig.valueOf("BUTTONS_" + typeName);
            } catch (IllegalArgumentException e) {
                type = null;
            }

            if (type!=null) { //Checks if the button is a premade button
                String text = ChatUtils.format(type.getString());
                String command;
                if (typeName.equalsIgnoreCase("clean")) {
                    command = "/clean "+suspectName;
                } else {
                    command = "/"+GlobalConfig.valueOf("BAN_COMMAND_" + typeName.toUpperCase()).getString().replace("%SUSPECT%", suspectName);
                }
                buttons.put(text, command);

            } else {
                String text = ChatUtils.format(GlobalConfig.START_BUTTONS_ELEMENTS.getButtonText(ChatUtils.format(button)));
                String command = "/"+GlobalConfig.START_BUTTONS_ELEMENTS.getButtonCommand(button);
                buttons.put(text, command);
            }
        }

        return buttons;
    }

    public static HashMap<String, String> getReportButtons(String reporterName, String reportedName, String reason, String serverName) {
        HashMap<String, String> buttons = new HashMap<>();
        ConfigurationSection section = REPORTS_BUTTONS_ELEMENTS.getSection();

        for (String key : section.getKeys(false)) {
            String text = ChatUtils.format(REPORTS_BUTTONS_ELEMENTS.getButtonText(key)); //Button text
            String command = REPORTS_BUTTONS_ELEMENTS.getButtonCommand(key); //Button command
            if (text == null || command == null) {
                api.getPlugin().getLog().info("The report button: " + key + " is not configured correctly!");
                continue;
            }

            String formattedCommand = "/"+command
                    .replace("%REPORTER%", reporterName)
                    .replace("%REPORTED%", reportedName)
                    .replace("%REASON%", reason)
                    .replace("%SERVER%", serverName);

            buttons.put(text, formattedCommand);
        }
        return buttons;
    }



}
