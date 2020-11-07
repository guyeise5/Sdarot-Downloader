package requests;

import java.io.IOException;
import configurations.Configurations;
import debug.Logger;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.Model;

@SuppressWarnings("rawtypes")
public abstract class Handler <Father extends Model, T extends Model> {

	private String uriPrefix = ""; // every handler can have his own uriPrefix.
	
	private static Configurations conf; // the configuration class containing static data
	private static Logger logger; // the logger to log the requests
	private static URI sdarotURI = null; // the sdarot uri
	private static URI ajaxURI = null; // the ajax watch uri
	private static URI staticSdarotURI = null; // the static sdarot uri
	
	protected Handler() {
		conf = Configurations.getInstance();
		logger = Logger.getInstance();
		if (sdarotURI == null || ajaxURI == null || staticSdarotURI == null) {
			setAvailableURL();
		}
	}
	
// Getters for the static data
	protected static URI getSdarotURI() {
		return sdarotURI;
	}
	
	protected static URI getAjaxURI() {
		return ajaxURI;
	}
	
	protected static URI getStaticSdarotURI() {
		return staticSdarotURI;
	}
	
	protected static Configurations getConfigurations() {
		return conf;
	}
	
	protected static Logger getLogger() {
		return logger;
	}

// getters and setters
	public String getUriPrefix() {
		return uriPrefix;
	}
	
	protected void setUriPrefix(String uriPre) {
		uriPrefix = uriPre;
	}
	
// setting the sdarot uris to use
	private static void setAvailableURL() {
		HttpResponse<String> response;
		HttpRequest request;
		URI uri;
		
		sdarotURI = null;
		ajaxURI = null;
		staticSdarotURI = null;
		
		for (String url : getConfigurations().SDAROT_URLS) {
			uri = URI.create(url);
	        request = HttpRequest.newBuilder()
	                .GET()
	                .uri(uri)
	                .setHeader("User-Agent", getConfigurations().getUserAgent())
	                .build();

			try {
				response = getConfigurations().getHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
		        
		       if (response.statusCode() == HTTPStatus.OK ) {
		    	   // we got good response - now need to check the page is what we expect
		    	   boolean goodUri = true;
		    	   for (String s : getConfigurations().WEBSITE_NOT_CONTAINES) {
		    		   if(response.body().contains(s)) {
		    			  goodUri = false;
		    			  break;
		    		   }
		    	   }
		    	   
		    	   if (goodUri) {
		    		   sdarotURI = uri;
		    		   staticSdarotURI = URI.create(String.format("%s://static.%s", uri.toString().split("://", 2)[0], uri.toString().split("://", 2)[1]));
		    		   ajaxURI = URI.create(String.format("%s/ajax",uri.toString())).normalize();
		    		   break;
		    	   }
		       }
			} catch (IOException | InterruptedException e1) {
				getLogger().log(debug.LOG_LEVEL.INFORMATION, e1);
				getLogger().log(debug.LOG_LEVEL.INFORMATION, String.format("%s is not valid, trying next url", url));
			}
		}
		
		if (sdarotURI == null)
		{
			NullPointerException e1 = new NullPointerException("String.format(\"We could not find a vaild sdarot site to access, we tried the following sites: %s\", SDAROT_URLS)");
			getLogger().log(debug.LOG_LEVEL.CRITICAL, e1);
			throw e1;
		}
		
		getLogger().log(debug.LOG_LEVEL.INFORMATION, String.format("The sdarot url is %s", sdarotURI));
		getLogger().log(debug.LOG_LEVEL.INFORMATION, String.format("The static sdarot url is %s", staticSdarotURI));
		getLogger().log(debug.LOG_LEVEL.INFORMATION, String.format("The ajax watch sdarot url is %s", ajaxURI));
	}
	
	public boolean IsExists(Father father, int ID) {
		// TODO: LOG BETTER
			HttpResponse<String> response;
			try {
				response = getPageResponse(father, ID);
				if (response.statusCode() == HTTPStatus.OK) {
		        	return true;
		        }
			} catch (IOException | InterruptedException e) {
				getLogger().log(debug.LOG_LEVEL.ERROR, e);
			}
			return false;
	}
	
	public HttpResponse<String> getPageResponse(Father father, int ID) throws IOException, InterruptedException {
		//TODO: LOG
		HttpClient client = getConfigurations().getHttpClient();
		Thread.sleep(getConfigurations().DELAY_BETWEEN_REQUESTS);
		HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(String.format("%s%s", getSdarotURI(), getSuffixUrl(father, ID))).normalize())
                .setHeader("User-Agent", getConfigurations().getUserAgent())
                .build();
		return client.send(request, HttpResponse.BodyHandlers.ofString());
	}
	
	public List<T> getAll(Father father) throws IOException, InterruptedException {
		// TODO: LOG
		List<T> models = new ArrayList<>();
		Pattern modelPattern = getPattern(father);
		Matcher matcher = modelPattern.matcher(getFatherPageResponse(father).body());
        while (matcher.find()) {
        	String modelurl = matcher.group();
        	modelurl = modelurl.substring(0, modelurl.length() - 1); // removing the " in the end
        	T model = getByID(father, Integer.parseInt(modelurl.split(getUriPrefix())[1]));
        	if (model != null) {
        		models.add(model);        		
        	}
        }
		return models;
	}

	protected abstract HttpResponse<String> getFatherPageResponse(Father father) throws IOException, InterruptedException;
	
	public abstract Pattern getPattern(Father father);

	/**
	 * <H1>
	 * getSuffixUrl
	 * </H1>
	 * 
	 * This function returns the specific part of the URL 
	 * that is different between models. This allows more generic
	 * in "getPageResponse" function
	 * 
	 * 
	 */
	public abstract String getSuffixUrl(Father father, int ID);
		
	public abstract T getByID(Father father, int ID) throws IOException, InterruptedException;
	
	public abstract void download(T model);
}
