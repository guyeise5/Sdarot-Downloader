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
import requests.HTTPStatus;

// Singleton class
public class Configurations {
	
// Constants configurations
	// Network   
	private final String[] SDAROT_URLS // The possible urls for sdarot website 
	= {"https://sdarot.rocks",
			"http://sdarot.pro", 
			"https://sdarot.world",
			"https://sdarot.tv", 
			"https://sdarot.work" };
	

	private final String[] WEBSITE_NOT_CONTAINES = {"אתר זה הינו אתר מפר זכויות יוצרים"};	// Sdarot website page can't contain any of those strings

	private final String[] USER_AGENTS 	// Some options for user agent
	= {"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.105 Safari/537.36", 
			"Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:76.0) Gecko/20100101 Firefox/76.0" };
	
	public final String X_REQUESTED_WITH = "XMLHttpRequest"; 
	
	public final String CONTENT_TYPE= "application/x-www-form-urlencoded";
	

	public final int PRE_WATCH_DELAY_TIME = 30000;	// Delay before episode start in milliseconds

	// Logging
	public final String LOG_FILE=".\\log\\sdarot-downloader.log"; // Location for logfile, set null to disable logging to file
	
	public final LOG_LEVEL LOG_LEVEL = debug.LOG_LEVEL.TRACE;
	
// Variables
	private URI sdarotURI; 	// the sdarot uri


	private URI watchURI;	// the watch uri

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

	    // because of cookie Handler the cookie handling is transparent for this client
		httpClient = HttpClient.newBuilder()
	            .version(HttpClient.Version.HTTP_2)
	            .cookieHandler(CookieHandler.getDefault())
	            .build();
		
		// Find available url to access
		setAvailableURL();
		
	}
	
// Methods
	public URI getSdarotURI() {
		return this.sdarotURI;
	}
	
	public URI getWatchURI() {
		return this.watchURI;
	}
	
	public HttpClient getHttpClient() {
		return this.httpClient;
	}

	public String getUserAgent() {
		return this.userAgent;
	}
	
	private void setAvailableURL() {
		HttpResponse<String> response;
		HttpRequest request;
		URI uri;
		this.sdarotURI = null;
		
		for (String url : SDAROT_URLS) {
			uri = URI.create(url);
	        request = HttpRequest.newBuilder()
	                .GET()
	                .uri(uri)
	                .setHeader("User-Agent", this.userAgent)
	                .build();

			try {
				response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		        
		       if (response.statusCode() == HTTPStatus.OK ) {
		    	   // we got good response - now need to check the page is what we expect
		    	   boolean goodUri = true;
		    	   for (String s : WEBSITE_NOT_CONTAINES) {
		    		   if(response.body().contains(s)) {
		    			  goodUri = false;
		    			  break;
		    		   }
		    	   }
		    	   
		    	   if (goodUri) {
		    		   this.sdarotURI = uri;
		    		   this.watchURI = URI.create(String.format("%s/ajax/watch",uri.toString())).normalize();
		    		   break;
		    	   }
		       }
			} catch (IOException | InterruptedException e1) {
				System.out.printf("%s is not valid%n", url);
				e1.printStackTrace();
			}
		}
		if (this.sdarotURI == null)
		{
			throw new NullPointerException("We could not find any Sdarot site to access, try add more options in ~/configurations/Configurations.java");
		}
	}
}
