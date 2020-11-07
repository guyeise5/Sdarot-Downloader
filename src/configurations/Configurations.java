package configurations;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.Random;

import debug.LOG_LEVEL;
import debug.Logger;
import requests.HTTPStatus;

// Singleton class
public class Configurations {
	
// Constants configurations
	// Network   
	public final String[] SDAROT_URLS // The possible urls for sdarot website 
	= {"https://sdarot.today",
			"https://sdarot.rocks",
			"http://sdarot.pro",
			"https://sdarot.world",
			"https://sdarot.tv", 
			"https://sdarot.work"};
	

	public final String[] WEBSITE_NOT_CONTAINES // Sdarot website page can't contain any of those strings
	= {"אתר זה הינו אתר מפר זכויות יוצרים",
			"תקלה בשידור",
			"הכתובת ממנה נכנסת אינה פעילה יותר ותיחסם בקרוב"};	

	private final String[] USER_AGENTS 	// Some options for user agent
	= {"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.105 Safari/537.36", 
			"Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:76.0) Gecko/20100101 Firefox/76.0" };
	
	public final String X_REQUESTED_WITH = "XMLHttpRequest"; 
	
	public final String CONTENT_TYPE= "application/x-www-form-urlencoded";
	

	public final int PRE_WATCH_DELAY_TIME = 30000;	// Delay before episode start in milliseconds
	
	public final int DELAY_BETWEEN_REQUESTS = 2000; // Delay before sending requests

	// Logger
	public final String LOG_FILE=".\\log\\sdarot-downloader.log"; // Location for logfile, set null to disable logging to file
	
	public final LOG_LEVEL LOG_LEVEL = debug.LOG_LEVEL.TRACE;

	
	// Files
	public final String IMAGES_PATH = "./images";
	
	public final int BLOCK_SIZE =  1024 * 1024; // Block size  
	
// Variables

	private HttpClient httpClient;	// reusing the client for all requests

	private String userAgent;	// user-agent header (The browser agent)

// Singleton methods
	private static Configurations instance = null;
	
	public static Configurations getInstance() {
		if(instance==null) {
			instance = new Configurations();
		}
		return instance;
	}
	
	private Configurations() {
		// getting random user-agent (browser)
		this.userAgent = USER_AGENTS[new Random().nextInt(USER_AGENTS.length)];
		
		// making the default cookieHandler create a cookie manager which will handle the cookies
	    CookieHandler.setDefault(new CookieManager());

		httpClient = HttpClient.newBuilder()
	            .version(HttpClient.Version.HTTP_2)
	            .cookieHandler(CookieHandler.getDefault())
	            .build();
	}
	
// Methods
	
	public HttpClient getHttpClient() {
		return this.httpClient;
	}

	public String getUserAgent() {
		return this.userAgent;
	}
	
}
