package requests;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import models.Model;

@SuppressWarnings("rawtypes")
public abstract class Handler <Father extends Model, T extends Model> {

	protected Configurations conf;
	
	protected Handler() {
		this.conf = Configurations.getInstance();
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
                .uri(URI.create(String.format("%s/watch/%s", conf.getSdarotURI(), getSuffixUrl(father, ID))))
                .setHeader("User-Agent", conf.getUserAgent())
                .build();
		return client.send(request, HttpResponse.BodyHandlers.ofString());
	}
	
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
	
	public abstract List<T> getAll(Father father) throws IOException, InterruptedException;
	
	public abstract void download(T model);
}
