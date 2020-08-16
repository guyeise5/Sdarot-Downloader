package requests;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import models.Model;
import models.Root;
import models.Season;
import models.Show;

// Singleton class
public class SeasonHandler extends Handler<Show,Season> {

	private SeasonHandler() {}
	
	private static SeasonHandler instance = null;
		
	public static SeasonHandler getInstance() {
		if(instance == null) {
			instance = new SeasonHandler();
		}
		return instance;
	}
	
	@Override
	public String getChangePartUrl(Show show, int seasonID) {
		return String.format("%s/season/%s", ShowHandler.getInstance().getChangePartUrl(show.getFather(), show.getID()), seasonID);
	}
	
	@Override
	public Season getByID(Show show, int seasonID) {
		if(!IsExists(show, seasonID)) {
			return null;
		}
		Season ret = new Season((Show)show, seasonID);
		EpisodeHandler.getInstance().getAll(ret).forEach(e -> ret.AddChildren(e));
		return ret;
	}
	

	@Override
	public List<Season> getAll(Show show) {
		// TODO: Implement this function
		throw new UnsupportedOperationException("method not implemented yet!");
	}

	@Override
	public HttpResponse<String> getPageResponse(Show show, int seasonID) throws IOException, InterruptedException {
		HttpClient client = conf.getHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(String.format("%s/watch/%s/season/%s", conf.getSdarotURI().toString(), show.getID(), seasonID)))
                .setHeader("User-Agent", conf.getUserAgent())
                .build();
		return client.send(request, HttpResponse.BodyHandlers.ofString());
	}

	@Override
	public void download(Season season) {
		EpisodeHandler.getInstance().getAll(season).forEach(e -> EpisodeHandler.getInstance().download(e));
		
	}
	
}
