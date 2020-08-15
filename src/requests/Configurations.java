package requests;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

// Singleton classs
class Configurations {

	private static final String[] SDAROT_URLS = {"https://sdarot.rocks","https://www.hasdarot.net", "http://sdarot.pro", "https://sdarot.world", "https://sdarot.tv", "https://sdarot.work"};
	private static String findSdarotURL() {
		// TODO: find a way to make this generic using SDAROT_URLS parameter and HTTP Requests
		return 	SDAROT_URLS[0];
	}

	private URL sdarotURL;

	private static Configurations instance = null;
	
	public static Configurations getInstance() {
		if(instance==null) {
			instance = new Configurations();
		}
		return instance;
	}
	
	private Configurations() {
		try {
			this.sdarotURL = new URL(findSdarotURL());
		} catch (MalformedURLException e) {
			// Should never get to this section
			e.printStackTrace();
		}
	}
	
	public URL getSdarotURL() {
		return sdarotURL;
	}

}
