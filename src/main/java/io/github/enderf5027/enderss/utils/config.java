package io.github.enderf5027.enderss.utils;

import io.github.enderf5027.enderss.Enderss;
import net.md_5.bungee.config.Configuration;

import java.util.List;

public class config  {
    public static Configuration cfg = Enderss.config;
    public static String ScreenShareServer;
    public static String FallbackServer;
    public static boolean LastConnectedServer;
    public static boolean FallBackStaff;
    public static String prefix;
    public static String noperm;
    public static String nocommand;
    public static String console;
    public static String exempt;
    public static String noplayer;
    public static String noreason;
    public static String anydesk;
    public static String discord;
    public static String teamspeak;
    public static String cantexecute;
    public static String cantfallbacksus;
    public static String cantfallbackstaff;
    public static String commandBlocked;
    public static String alertsEnabled;
    public static String alertsDisabled;
    public static String playeroffline;
    public static String alreadyinss;
    public static String staffinss;
    public static String alreadyssplayer;
    public static String suspectnotinss;
    public static String playerquit;
    public static String cleanplayer;
    public static String playercleaned;
    public static String cantcontrolyourself;
    public static String cantcontrolstaff;
    public static String nostaff;
    public static boolean nostaffenabled;
    public static boolean reportsenabled;
    public static List<String> reportsent;
    public static List<String> reportreceived;
    public static List<String> staffmessage;
    public static List<String> ssmessage;
    public static String title;
    public static String subtitle;
    public static int fadein;
    public static int stay;
    public static int fadeout;
    public static boolean sendAnydesk;
    public static boolean sendDiscord;
    public static boolean sendTeamspeak;
    public static boolean blockCommands;
    public static List<String> whitelistedCommands;
    public static boolean clearchat;
    public static boolean sendTitle;
    public static String hours;
    public static String minutes;
    public static String seconds;
    public static String bhack;
    public static String badmission;
    public static String brefuse;
    public static String bquit;
    public static String bclean;
    public static String ssbutton;
    public static String servertpbutton;
    public static String hackbutton;
    public static String bconfirm;
    public static boolean confirmbutton;
    public static String hack;
    public static String admission;
    public static String refuse;
    public static String quit;
    public static String blatant;
    public static boolean banonquit;

    static  {
        assert cfg != null;

        //Config
        ScreenShareServer = cfg.getString("config.ScreenShareServer");
        FallbackServer = cfg.getString("config.FallbackServer");
        LastConnectedServer = cfg.getBoolean("config.LastConnectedServer");

        FallBackStaff = cfg.getBoolean("config.FallBackStaff");
        clearchat = cfg.getBoolean("config.clearChat");
        banonquit = cfg.getBoolean("config.banonquit");

        //Messages on Join
        sendAnydesk = cfg.getBoolean("onjoin.anydesk.send");
        anydesk = cfg.getString("onjoin.anydesk.message");
        sendTeamspeak = cfg.getBoolean("onjoin.teamspeak.send");
        teamspeak = cfg.getString("onjoin.teamspeak.message");
        sendDiscord = cfg.getBoolean("onjoin.discord.send");
        discord = cfg.getString("onjoin.discord.message");
        ssmessage = cfg.getStringList("onjoin.ssmessage");
        staffmessage = cfg.getStringList("onjoin.staffmessage");

        //Title
        title = cfg.getString("title.title");
        sendTitle = cfg.getBoolean("title.send");
        subtitle = cfg.getString("title.subtitle");
        fadein = cfg.getInt("title.fadein");
        fadeout = cfg.getInt("title.fadeout");
        stay = cfg.getInt("title.stay");

        //Command Blocker
        blockCommands = cfg.getBoolean("commandblocker.enabled");
        whitelistedCommands = cfg.getStringList("commandblocker.whitelistedCommands");

        //Prefix
        prefix = cfg.getString("prefix");

        //Messages - Info
        playerquit = cfg.getString("messages.info.playerquit");
        cleanplayer = cfg.getString("messages.info.cleanplayer");
        playercleaned = cfg.getString("messages.info.playercleaned");
        commandBlocked = cfg.getString("messages.info.commandblocked");
        alertsEnabled = cfg.getString("messages.info.alertsEnabled");
        alertsDisabled = cfg.getString("messages.info.alertsDisabled");


        //Messages - Errors
        noplayer = cfg.getString("messages.error.noplayer");
        noperm = cfg.getString("messages.error.noperm");
        nocommand = cfg.getString("messages.error.nocommand");
        noperm = cfg.getString("messages.error.noperm");
        noreason = cfg.getString("messages.error.noreason");
        cantexecute = cfg.getString("messages.error.cantexecute");
        cantfallbacksus = cfg.getString("messages.error.cantfallbacksuspect");
        cantfallbackstaff = cfg.getString("messages.error.cantfallbackstaff");
        playeroffline = cfg.getString("messages.error.playeroffline");
        alreadyinss = cfg.getString("messages.error.alreadyinss");
        staffinss = cfg.getString("messages.error.staffinss");
        alreadyssplayer = cfg.getString("messages.error.alreadyssplayer");
        suspectnotinss =  cfg.getString("messages.error.suspectnotinss");
        cantcontrolyourself = cfg.getString("messages.error.cantcontrolyourself");
        cantcontrolstaff = cfg.getString("messages.error.cantcontrolstaff");
        console = cfg.getString("messages.error.console");
        exempt = cfg.getString("messages.error.exempt");

        //Reports
        reportsenabled = cfg.getBoolean("reports.enabled");
        reportsent = cfg.getStringList("reports.messages.reportsent");
        reportreceived = cfg.getStringList("reports.messages.reportreceived");
        nostaff = cfg.getString("reports.messages.nostaff");
        nostaffenabled = cfg.getBoolean("reports.messages.nostaffenabled");




        //Buttons
        bhack = cfg.getString("buttons.hack");
        badmission = cfg.getString("buttons.admission");
        brefuse = cfg.getString("buttons.refuse");
        bclean = cfg.getString("buttons.clean");
        bquit = cfg.getString("buttons.quit");
        bconfirm = cfg.getString("buttons.confirm");
        ssbutton = cfg.getString("buttons.ss");
        servertpbutton = cfg.getString("buttons.servertp");
        hackbutton = cfg.getString("buttons.ban");
        confirmbutton = cfg.getBoolean("buttons.confirmButton");



        //Commands
        hack = cfg.getString("bancommand.hack");
        admission = cfg.getString("bancommand.admission");
        refuse = cfg.getString("bancommand.refuse");
        quit = cfg.getString("bancommand.quit");
        blatant = cfg.getString("bancommand.blatant");

        hours = cfg.getString("hours");
        minutes = cfg.getString("minutes");
        seconds = cfg.getString("seconds");

    }
}
