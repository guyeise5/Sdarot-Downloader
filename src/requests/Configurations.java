package requests;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;

// Singleton class
class Configurations {

	public static final int OK_STATUS = 200;
	private static final String[] SDAROT_URLS 
	= {"https://sdarot.rocks",
			"https://www.hasdarot.net",
			"http://sdarot.pro",
			"https://sdarot.world",
			"https://sdarot.tv",
			"https://sdarot.work"};
	private static final String[] USER_AGENTS 
	= {"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.105 Safari/537.36", 
			"Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:76.0) Gecko/20100101 Firefox/76.0" };

	private URI sdarotURI;
	private String cookie;
	private String userAgent;

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

		// reusing the client
		// TODO: maybe set the client general for all the handlers ? need to think about it
		HttpClient httpClient = HttpClient.newBuilder()
	            .version(HttpClient.Version.HTTP_2)
	            .build();
		
		for (String url : SDAROT_URLS) {
			URI uri = URI.create(url);
	        HttpRequest request = HttpRequest.newBuilder()
	                .GET()
	                .uri(uri)
	                .setHeader("User-Agent", userAgent)
	                .build();

	        HttpResponse<String> response;
			try {
				response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		        
		       if (response.statusCode() == OK_STATUS) {
		          // We got a successful url
		    	   this.sdarotURI = uri;
    		       // TODO: take the cookie from the good response and set the cookie with it
		    	   // //System.out.println(response.headers().firstValue("set-cookie"));
		    	   this.cookie=null;
		    	   break;
		       }
			} catch (IOException | InterruptedException e1) {
				System.out.printf("%s is not valid%n", url);
				e1.printStackTrace();
			}
		}
	}
	
	public URI getSdarotURL() {
		return this.sdarotURI;
	}
	
	public String getCookies() {
		return this.cookie;
	}

	public String getUserAgent() {
		return this.userAgent;
	}
}
