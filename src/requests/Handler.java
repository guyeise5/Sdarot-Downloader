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

/**
 * <H1>
 * Handler
 * </H1>
 * 
 * This class is an abstract class for handler for sdarot site requests per model
 * The handler will handle request for model
 *
 * @param <Father> The father of the model of the handler
 * @param <T> The model of the handler 
 */
public abstract class Handler <Father extends Model<?, T>, T extends Model<Father, ?>> {

	// Properties 
	
	private String uriPrefix = ""; // Most of sdarot models has a uri prefix for their download / info
	
	private static Configurations conf; // the configuration class containing static data
	private static Logger logger; // the logger to log the requests
	private static URI sdarotURI = null; // the sdarot uri
	private static URI ajaxURI = null; // the ajax watch uri
	private static URI staticSdarotURI = null; // the static sdarot uri
	
	// methods
	
	/**
	 * <H1>
	 * Handler
	 * </H1>
	 * Constructor
	 */
	protected Handler() {
		conf = Configurations.getInstance();
		logger = Logger.getInstance();
		if (sdarotURI == null || ajaxURI == null || staticSdarotURI == null) {
			setAvailableURL();
		}
	}
	
	/**
	 * <H1>
	 * getSdarorURI
	 * </H1>
	 * Getter for property
	 * @return the sdarot uri
	 */
	protected static URI getSdarotURI() {
		return sdarotURI;
	}
	
	/**
	 * <H1>
	 * getAjaxURI
	 * </H1>
	 * Getter for property
	 * @return the ajax sdarot uri
	 */
	protected static URI getAjaxURI() {
		return ajaxURI;
	}
	
	/**
	 * <H1>
	 * getStaticSdarotURI
	 * </H1>
	 * Getter for property
	 * @return the static sdarot uri
	 */
	protected static URI getStaticSdarotURI() {
		return staticSdarotURI;
	}
	
	/**
	 * <H1>
	 * getConfigurations
	 * </H1>
	 * Getter for property
	 * @return the configuration instance
	 */
	protected static Configurations getConfigurations() {
		return conf;
	}
	
	/**
	 * <H1>
	 * getLogger
	 * </H1>
	 * Getter of property
	 * @return the logger
	 */
	protected static Logger getLogger() {
		return logger;
	}

	/**
	 * <H1>
	 * getUriPrefix
	 * </H1>
	 * Getter of property
	 * @return the uri prefix of the model
	 */
	public String getUriPrefix() {
		return uriPrefix;
	}
	
	/**
	 * <H1>
	 * setUriPrefix
	 * </H1>
	 * Setter of property 
	 * @param uriPre the uri prefix
	 */
	protected void setUriPrefix(String uriPre) {
		uriPrefix = uriPre;
	}
	
	/**
	 * <H1>
	 * setAvailableURL
	 * </H1>
	 * Finding the available urls of sdarot and setting the uri's properties accordingly
	 */
	private static void setAvailableURL() {
		// declare before loop
		HttpResponse<String> response;
		HttpRequest request;
		URI uri;
		// passing on all sdarot urls to check which one is available
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
		    	   // making sure the uri does not contain the forbidden strings
		    	   for (String s : getConfigurations().WEBSITE_NOT_CONTAINES) {
		    		   if(response.body().contains(s)) {
		    			  goodUri = false;
		    			  break;
		    		   }
		    	   }
		    	   
		    	   
		    	   if (goodUri) {
		    		   // We got valid uri, setting the properties accordingly 
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
		
		if (sdarotURI == null) {
			// We could not find valid sdarot uri
			NullPointerException e1 = new NullPointerException("String.format(\"We could not find a vaild sdarot site to access, we tried the following sites: %s\", SDAROT_URLS)");
			getLogger().log(debug.LOG_LEVEL.CRITICAL, e1);
			throw e1;
		}
		
		getLogger().log(debug.LOG_LEVEL.INFORMATION, String.format("The sdarot url is %s", sdarotURI));
		getLogger().log(debug.LOG_LEVEL.INFORMATION, String.format("The static sdarot url is %s", staticSdarotURI));
		getLogger().log(debug.LOG_LEVEL.INFORMATION, String.format("The ajax watch sdarot url is %s", ajaxURI));
	}
	
	/**
	 * <H1>
	 * IsExists
	 * </H1>
	 * This function is checking if the model exist
	 * @param father the father of the model
	 * @param ID the id of the model
	 * @return true if exists, false if not exists
	 */
	public boolean IsExists(Father father, int ID) {
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
	
	/**
	 * <H1>
	 * getPageResponse
	 * </H1>
	 * This function is sending request for the main page of the model  
	 * @param father the father of the model
	 * @param ID the id of the model
	 * @return the page response (body as string)
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public HttpResponse<String> getPageResponse(Father father, int ID) throws IOException, InterruptedException {
		HttpClient client = getConfigurations().getHttpClient();
		Thread.sleep(getConfigurations().DELAY_BETWEEN_REQUESTS);
		HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(String.format("%s%s", getSdarotURI(), getSuffixUrl(father, ID))).normalize())
                .setHeader("User-Agent", getConfigurations().getUserAgent())
                .build();
		return client.send(request, HttpResponse.BodyHandlers.ofString());
	}
	
	/**
	 * <H1>
	 * getAll
	 * </H1>
	 * This function is getting all children of certain father
	 * @param father the father we want to find it's children
	 * @return the children
	 * @throws IOException
	 * @throws InterruptedException
	 */
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

	/**
	 * <H1>
	 * getFatherPageResponse
	 * </H1>
	 * Getting the page of the father model of the handler model 
	 * @param father the father model to get it's page
	 * @return the page response (body as string)
	 * @throws IOException
	 * @throws InterruptedException
	 */
	protected abstract HttpResponse<String> getFatherPageResponse(Father father) throws IOException, InterruptedException;
	
	/**
	 * <H1>
	 * getPattern
	 * </H1>
	 * getting the model pattern to be able to find the model in the father page
	 * @param father the father of the model we want the pattern to
	 * @return the pattern
	 */
	public abstract Pattern getPattern(Father father);
	
	/**
	 * <H1>
	 * getSuffixUrl
	 * </H1>
	 * This function returns the specific part of the URL 
	 * that is different between models. This allows more generic
	 * in "getPageResponse" function
	 * @param father the father of the model
	 * @param ID the id of the model
	 * @return the suffix url of the model
	 */
	public abstract String getSuffixUrl(Father father, int ID);
	
	/**
	 * <H1>
	 * getByID
	 * </H1>
	 * finding the {@link Model} by the model's id
	 * @param father the model's father
	 * @param ID the model's id
	 * @return the model
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public abstract T getByID(Father father, int ID) throws IOException, InterruptedException;
	
	/**
	 * <H1>
	 * download
	 * </H1>
	 * download the {@link Model}
	 * @param model the model to download
	 */
	public abstract void download(T model);
}
