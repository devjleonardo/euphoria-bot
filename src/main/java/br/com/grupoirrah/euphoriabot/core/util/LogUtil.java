package br.com.grupoirrah.euphoriabot.core.util;

import org.slf4j.Logger;

public final class LogUtil {

    private static final String ERROR_DETAILS = "Detalhes do erro.";

    private LogUtil() {
    }

    public static void logException(Logger logger, String errorMessage, Exception e) {
        if (logger.isErrorEnabled()) {
            logger.error(errorMessage, e);
        }
        if (logger.isDebugEnabled()) {
            logger.debug(ERROR_DETAILS, e);
        }
    }

    public static void logError(Logger logger, String errorMessage, Object... args) {
        if (logger.isErrorEnabled()) {
            logger.error(errorMessage, args);
        }
    }

    public static void logWarn(Logger logger, String warnMessage, Object... args) {
        if (logger.isWarnEnabled()) {
            logger.warn(warnMessage, args);
        }
    }

    public static void logInfo(Logger logger, String infoMessage, Object... args) {
        if (logger.isInfoEnabled()) {
            logger.info(infoMessage, args);
        }
    }

    public static void logDebug(Logger logger, String debugMessage, Object... args) {
        if (logger.isDebugEnabled()) {
            logger.debug(debugMessage, args);
        }
    }

}
