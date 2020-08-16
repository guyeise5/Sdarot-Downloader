package requests;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.Random;

// Singleton class
class Configurations {

	public static final int OK_STATUS = 200;
	public static final String[] WEBSITE_NOT_CONTAINES = {"אתר זה הינו אתר מפר זכויות יוצרים"};
	
	// The possible urls for sdarot website 
	private static final String[] SDAROT_URLS 
	= {"https://sdarot.rocks",
			//"https://www.hasdarot.net", // This one has diffrent api - by show name not by id
			"http://sdarot.pro", 
			"https://sdarot.world",
			"https://sdarot.tv", 
			"https://sdarot.work" };
	// Some options for user agent
	private static final String[] USER_AGENTS 
	= {"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.105 Safari/537.36", 
			"Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:76.0) Gecko/20100101 Firefox/76.0" };

	// the sdarot uri
	private static URI sdarotURI;
	// reusing the client for all requests
	private static HttpClient httpClient;
	// user-agent header (The browser agent)
	private static String userAgent;

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
		
		// Checking every sdarot url until we find a valid one
		
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
		        
		       if (response.statusCode() == OK_STATUS) {
		    	   // we got good response - now need to check the page is what we expect
		    	   
		    	   int index = 0;
		    	   boolean goodUri = true;
		    	   while (goodUri && index < WEBSITE_NOT_CONTAINES.length) {
		    		   goodUri = !response.body().toString().contains(WEBSITE_NOT_CONTAINES[index]);
		    		   index++;
		    	   }
		    	   
		    	   if (goodUri) {
		    		   this.sdarotURI = uri;
		    		   break;
		    	   }
		       }
			} catch (IOException | InterruptedException e1) {
				System.out.printf("%s is not valid%n", url);
				e1.printStackTrace();
			}
		}
		
		if (this.sdarotURI == null) {
			throw new NullPointerException("there is no valid sdarot url");
		}
	}
	
	public URI getSdarotURI() {
		return this.sdarotURI;
	}
	
	public HttpClient getHttpClient() {
		return this.httpClient;
	}

	public String getUserAgent() {
		return this.userAgent;
	}
}
