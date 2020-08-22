package requests;

import java.io.IOException;
import configurations.Configurations;
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

	private String uriPrefix = "";
	
	protected Configurations conf;
	
	protected Handler() {
		this.conf = Configurations.getInstance();
	}
	
	public String getUriPrefix() {
		return uriPrefix;
	}
	
	protected void setUriPrefix(String uriPre) {
		uriPrefix = uriPre;
	}
	
	public boolean IsExists(Father father, int ID) {
			HttpResponse<String> response;
			try {
				response = getPageResponse(father, ID);
				if (response.statusCode() == HTTPStatus.OK) {
		        	return true;
		        }
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
			return false;
	}
	
	public HttpResponse<String> getPageResponse(Father father, int ID) throws IOException, InterruptedException {
		HttpClient client = conf.getHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(String.format("%s%s", conf.getSdarotURI(), getSuffixUrl(father, ID))).normalize())
                .setHeader("User-Agent", conf.getUserAgent())
                .build();
		return client.send(request, HttpResponse.BodyHandlers.ofString());
	}
	
	public List<T> getAll(Father father) throws IOException, InterruptedException {
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
