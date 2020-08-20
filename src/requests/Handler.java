package requests;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import models.Model;
import models.Root;


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
				if (response.statusCode() == conf.OK_STATUS) {
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
                .uri(URI.create(String.format("%s/watch/%s", conf.getSdarotURI(), getChangePartUrl(father, ID))))
                .setHeader("User-Agent", conf.getUserAgent())
                .build();
		return client.send(request, HttpResponse.BodyHandlers.ofString());
	}
	
	public abstract String getChangePartUrl(Father father, int ID);
	
	public abstract T getByID(Father father, int ID) throws IOException, InterruptedException;
	
	public abstract List<T> getAll(Father father) throws IOException, InterruptedException;
	
	public abstract void download(T model);
	
    protected static HttpRequest.BodyPublisher buildFormDataFromMap(Map<Object, Object> data) {
        var builder = new StringBuilder();
        for (Map.Entry<Object, Object> entry : data.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(URLEncoder.encode(entry.getKey().toString(), StandardCharsets.UTF_8));
            builder.append("=");
            builder.append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
        }
        System.out.println(builder.toString());
        return HttpRequest.BodyPublishers.ofString(builder.toString());
    }
}
