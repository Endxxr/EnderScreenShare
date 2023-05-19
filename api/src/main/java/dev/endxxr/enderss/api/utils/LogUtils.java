package dev.endxxr.enderss.api.utils;

import dev.endxxr.enderss.api.EnderSSAPI;

import java.util.logging.Logger;

public class LogUtils {

    public static void prettyPrintUserMistake(Exception e, String customMessage) {
        Logger logger = EnderSSAPI.Provider.getApi().getPlugin().getLog(); // Get the logger from the API because it's static
        String message = customMessage == null ? e.getMessage() : customMessage;
        logger.severe("========================");
        logger.severe("");
        logger.severe(message);
        logger.info("");
        logger.severe("========================");

    }

    public static void prettyPrintException(Exception exception, String customMessage) {
        EnderSSAPI api = EnderSSAPI.Provider.getApi();// Get the logger from the API because it's static

        Logger logger;
        if (api == null) {
            logger = Logger.getLogger("EnderSS");
            logger.info("The API is null, can't get the logger from it. Using our own logger.");
        } else {
            logger = api.getPlugin().getLog();
        }

        String message = customMessage == null ? exception.getMessage() : customMessage;
        logger.severe("========================");
        logger.severe("");
        logger.severe("An exception has been thrown:");
        logger.severe(message);
        logger.severe("");
        logger.info("Please report this error on the GitHub page of the plugin");
        logger.info("or on the Discord server");
        logger.info("");
        logger.info("Stacktrace:");
        exception.printStackTrace();
        logger.info("");
        logger.severe("========================");
    }


}
