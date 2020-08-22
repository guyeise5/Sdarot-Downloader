package debug;

import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger implements Closeable {
		
	// Static variables
	private static Logger instance = null;
	
	// Static methods
	public static Logger getInstance() {
		if(Logger.instance == null) {
			try {
				Logger.instance = new Logger();
			} catch (IOException e) {
				System.out.print(String.format("Unable to open logfile %s", configurations.Configurations.getInstance().LOG_FILE));
				System.exit(1);
			}
		}
		
		return Logger.instance;
	}
	
	// Variables
	private PrintWriter writer; 
		
	// Constructors and Destructors
	private Logger() throws IOException {
		File logFile = new File(configurations.Configurations.getInstance().LOG_FILE);
		logFile.getParentFile().mkdirs(); // creating the path if not exists
		    
		 // If file already exists, this does nothing
		 logFile.createNewFile();
		        
		 // true - means append and not override
		 writer = new PrintWriter(new FileWriter(logFile, true));
	}
	
	
	// Methods

	public void log(LOG_LEVEL level, String message) {
		if (level == LOG_LEVEL.NONE) {
			throw new UnsupportedOperationException("You can not log on NONE log level");
		}
		
		if (level.compareTo(configurations.Configurations.getInstance().LOG_LEVEL) > 0) {
			String msg = String.format("%s - %s\n", currentDate(), message);
			
			System.out.print(msg);
			
			if(writer != null) {
				writer.write(msg);
				writer.flush();
			}
		}
	}

	public void log(LOG_LEVEL level, Exception exception) {
		if (level == LOG_LEVEL.NONE) {
			throw new UnsupportedOperationException("You can not log on NONE log level");
		}
		String date = currentDate();
		
		// Log to console
		System.out.print(String.format("%s\n", date));
		exception.printStackTrace();	
		
		// Log to file
		if(writer != null) {
			writer.write(String.format("%s\n", date));
			exception.printStackTrace(writer);
			writer.flush();
		}
	}
	
	private String currentDate() {
		DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		LocalDateTime  logTime = LocalDateTime.now();
		return df.format(logTime);
	}

	@Override
	public void close() throws IOException {
		if (writer != null) {
			writer.close();
		}
		
	}
}
