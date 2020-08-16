package requests;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import models.Model;
import models.Root;


@SuppressWarnings("rawtypes")
public abstract class Handler <Father extends Model, T extends Model> {

	protected Configurations conf;
	
	protected Handler() {
		this.conf = Configurations.getInstance();
	}
	
	public boolean IsExists(Father father, int ID) {
		try {
			HttpResponse<String> response = getPageResponse(father, ID);
			if (response.statusCode() == conf.OK_STATUS) {
	        	return true;
	        } 
	        return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public HttpResponse<String> getPageResponse(Father father, int ID) throws IOException, InterruptedException {
		HttpClient client = conf.getHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(String.format("%s/watch/%s", conf.getSdarotURI(), getChangePartUrl(father, ID))))
                .setHeader("User-Agent", conf.getUserAgent())
                .build();
		return client.send(request, HttpResponse.BodyHandlers.ofString());
	}
	
	public abstract String getChangePartUrl(Father father, int ID);
	
	public abstract T getByID(Father father, int ID);
	
	public abstract List<T> getAll(Father father);
	
	public abstract void download(T model);
}
