package debug;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Debugger {

	private Debugger() {
	}
	
	// Change this to select your log level
	@SuppressWarnings("unused")
	private static Level logLevel = Level.ALL;
	
//	private static String logPath = "./"+ new SimpleDateFormat("dd-MM-yyyy_HH:mm:ss").format(new Date(System.currentTimeMillis()));
	
	private static Logger LOGGER = Logger.getLogger(Debugger.class.getName());
	
	public static void Log(Level level, String message) {
		LOGGER.log(level, message);
	}

}
