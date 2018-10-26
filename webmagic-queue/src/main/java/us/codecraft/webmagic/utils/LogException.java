package us.codecraft.webmagic.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;

public class LogException {
	public static void appendExceptionLog(Log log, Exception exception) {
		exception.printStackTrace();
		log.error(exception.toString());
		log.error(StringUtils.join(exception.getStackTrace(), "\n"));
	}

	public static void appendExceptionLog(Log log, Throwable throwable) {
		throwable.printStackTrace();
		log.error(throwable.toString());
		log.error(StringUtils.join(throwable.getStackTrace(), "\n"));
	}
}
