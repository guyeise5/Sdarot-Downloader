package requests;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;

// Singleton class
class Configurations {

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

	private URL sdarotURL;
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

		// TODO: get requests until we find a valid url - and then define sdarotUrl
		// TODO: take the cookie from the good response and set the cookie with it
		try {

			this.sdarotURL = new URL(SDAROT_URLS[0]);
			this.cookie = null;
		} catch (MalformedURLException e) {
			// Should never get to this section
			e.printStackTrace();
		}
	}
	
	public URL getSdarotURL() {
		return sdarotURL;
	}
	
	public String getCookie() {
		return cookie;
	}

	public String getUserAgent() {
		return userAgent;
	}
}
