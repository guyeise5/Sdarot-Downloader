package requests;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.Episode;
import models.Model;
import models.Season;
import models.Show;

public class EpisodeHandler extends Handler<Season, Episode> {

	private EpisodeHandler() {}
	private static EpisodeHandler instance = null;
	
	public static EpisodeHandler getInstance() {
		if(instance == null) {
			instance = new EpisodeHandler();
		}
		return instance;
	}
	
	@Override
	public String getChangePartUrl(Season season, int episodeID) {
		return String.format("%s/episode/%s", SeasonHandler.getInstance().getChangePartUrl(season.getFather(), season.getID()), episodeID);
	}

	@Override
	public Episode getByID(Season season, int episodeID) {
		if(!IsExists(season, episodeID)) {
			return null;
		}
		return new Episode((Season)season, episodeID);
	}


	
	@Override
	public List<Episode> getAll(Season season) throws IOException, InterruptedException {
		List<Episode> episodes = new ArrayList<>();
		Pattern episodePattern = Pattern.compile(String.format("(/watch/%s-.*?/season/%s/episode/(\\d+?)\")", season.getFather().getID(), season.getID()));
        Matcher matcher = episodePattern.matcher(SeasonHandler.getInstance().getPageResponse(season.getFather(), season.getID()).body());
        while (matcher.find()) {
        	String episodeurl = matcher.group();
        	episodeurl = episodeurl.substring(0, episodeurl.length() - 1); // removing the " in the end
    		episodes.add(new Episode(season, Integer.parseInt(episodeurl.split("/episode/")[1])));
        }
		return episodes;
	}
	
	public void download(Episode e)  {
		// TODO: Implement this function
		//preWatch(e);
		throw new UnsupportedOperationException("need to implement this");

	}
	
	private String preWatch(Episode e) {
        // form parameters
        Map<Object, Object> data = new HashMap<>();
        data.put("preWatch", "true");
        data.put("SID", String.format("%s", e.getFather().getFather().getID()));
        data.put("season", String.format("%s", e.getFather().getID()));
        data.put("ep", String.format("%s", e.getID())); 
        
        HttpClient client = conf.getHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .POST(buildFormDataFromMap(data))
                .uri(conf.getWatchURI())
                .setHeader("User-Agent", conf.getUserAgent())
                .setHeader("Referer", String.format("%s%s", conf.getSdarotURI().toString(), getChangePartUrl((Season)e.getFather(), e.getID())))
                .setHeader("X-Requested-With", conf.X_REQUESTED_WITH)
                //.header("Content-Type", "application/x-www-form-urlencoded")
                .build();

        HttpResponse<String> response;
		try {
			response = client.send(request, HttpResponse.BodyHandlers.ofString());
	        // print status code
	        System.out.println(response.statusCode());
	      
	        // print response body
	        System.out.println(response.body());
	        
		} catch (IOException | InterruptedException e1) {
			System.out.printf("Can't download episode %s failed in prewatch, error:%n", e.getID());
			e1.printStackTrace();
		}
		return null;
	} 
}
