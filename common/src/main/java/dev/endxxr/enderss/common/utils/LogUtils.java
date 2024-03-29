package dev.endxxr.enderss.common.utils;

import dev.endxxr.enderss.api.EnderSS;
import dev.endxxr.enderss.api.EnderSSProvider;

import java.util.logging.Logger;

public class LogUtils {

    public static void prettyPrintUserMistake(Exception e, String customMessage) {
        Logger logger = EnderSSProvider.getApi().getPlugin().getLog(); // Get the logger from the API because it's static
        String message = customMessage == null ? e.getMessage() : customMessage;
        logger.severe("========================");
        logger.severe("");
        logger.severe(message);
        logger.info("");
        logger.severe("========================");

    }

    public static void prettyPrintException(Exception exception, String customMessage) {
        EnderSS api = EnderSSProvider.getApi();// Get the logger from the API because it's static

        Logger logger;
        if (api == null) {
            logger = Logger.getLogger("EnderSS");
            logger.info("The API is null, can't get the logger from it. Using our own logger.");
        } else {
            logger = api.getPlugin().getLog();
        }

        String message = customMessage == null ? exception.getMessage() : customMessage;
        logger.severe("========================");
        logger.severe("An exception has been thrown:");
        logger.severe(message);
        logger.info("");
        logger.info("Stacktrace:");
        exception.printStackTrace();
        logger.severe("========================");
    }


}
