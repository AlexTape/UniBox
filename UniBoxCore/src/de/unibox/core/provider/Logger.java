package de.unibox.core.provider;

/**
 * This Logger can be replaced with any Log4j Instance.
 */
public class Logger {

	private static boolean debugLogging = false;

	private static boolean errorLogging = true;

	private static boolean infoLogging = true;

	private static Logger instance;

	private static boolean warningsLogging = true;

	public static Logger getLogger(final String name) {
		if (Logger.instance == null) {
			Logger.instance = new Logger(name);
		}
		return Logger.instance;
	}

	public static boolean isDebugLogging() {
		return Logger.debugLogging;
	}

	public static boolean isErrorLogging() {
		return Logger.errorLogging;
	}

	public static boolean isInfoLogging() {
		return Logger.infoLogging;
	}

	public static boolean isWarningsLogging() {
		return Logger.warningsLogging;
	}

	public static void setDebugLogging(final boolean debugLogging) {
		Logger.debugLogging = debugLogging;
	}

	public static void setErrorLogging(final boolean errorLogging) {
		Logger.errorLogging = errorLogging;
	}

	public static void setInfoLogging(final boolean infoLogging) {
		Logger.infoLogging = infoLogging;
	}

	public static void setWarningsLogging(final boolean warningsLogging) {
		Logger.warningsLogging = warningsLogging;
	}

	private final String mName;

	private Logger(final String name) {
		this.mName = name;
	}

	public void debug(final String msg) {
		if (Logger.debugLogging) {
			System.err.println("[DEBUG] " + msg);
		}
	}

	public void error(final String msg) {
		if (Logger.errorLogging) {
			System.err.println("[ERROR] " + msg);
		}
	}

	public void info(final String msg) {
		if (Logger.infoLogging) {
			System.err.println("[INFO] " + msg);
		}
	}

	@Override
	public String toString() {
		return "Logger [mName=" + this.mName + "]";
	}

	public void warn(final String msg) {
		if (Logger.warningsLogging) {
			System.err.println("[WARNING] " + msg);
		}
	}
}
