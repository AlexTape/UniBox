package de.unibox.core.provider;

/**
 * This Logger can be replaced with any Log4j Instance.
 */
public class Logger {

	private String mName;

	private static Logger instance;

	private Logger(String name) {
		this.mName = name;
	}

	public static Logger getLogger(String name) {
		if (Logger.instance == null) {
			Logger.instance = new Logger(name);
		}
		return Logger.instance;
	}
	
	public void debug(String msg) {
		System.out.println("[DEBUG] " + msg);
	}
	
	public void info(String msg) {
		System.out.println("[INFO] " + msg);
	}
	
	public void error(String msg) {
		System.err.println("[ERROR] " + msg);
	}
	
	public void warn(String msg) {
		System.out.println("[WARNING] " + msg);
	}

	@Override
	public String toString() {
		return "Logger [mName=" + mName + "]";
	}

}
