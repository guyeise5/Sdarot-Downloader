package requests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        	System.out.println(episodeurl);
    		episodes.add(new Episode(season, Integer.parseInt(episodeurl.split("/episode/")[1])));
        }
		return episodes;
	}
	
	public void download(Episode e)  {
		// TODO: Find a better way to deal with error 
		try {
			Map<String, Object> vidInfo = getVideoData(e, preWatch(e));
	        String data = String.format("token=%s&time=%s&uid=''", ((Map<String, Object>)vidInfo.get("watch")).get("480"), vidInfo.get("time"));
	        
	        HttpRequest request = HttpRequest.newBuilder()
	                .GET()
//	                .POST(HttpRequest.BodyPublishers.ofString(data))         video_url = f"https://{video_info['url']}/w/episode/{self._show_id}/480/{video_info['VID']}.mp4"
	                .uri(URI.create(String.format("https://%s/w/episode/%s/480/%s.mp4?%s", vidInfo.get("url"), e.getFather().getFather().getID(), vidInfo.get("VID"), data)))
	                .setHeader("User-Agent", conf.getUserAgent())
	                .setHeader("Referer", String.format("%s/watch/%s", conf.getSdarotURI().toString(), getChangePartUrl((Season)e.getFather(), e.getID())))
	                //.setHeader("X-Requested-With", conf.X_REQUESTED_WITH)
	                .setHeader("Content-Type", conf.CONTENT_TYPE)
	                .build();
	        System.out.println("downloading ?");
	        HttpResponse<InputStream> response = conf.getHttpClient().send(request, HttpResponse.BodyHandlers.ofInputStream());
	        
	        File targetFile = new File("ep.mp4");
	        OutputStream outStream = new FileOutputStream(targetFile);
	     
	        byte[] buffer = new byte[8 * 1024];
	        int bytesRead;
	        while ((bytesRead = response.body().read(buffer)) != -1) {
	            outStream.write(buffer, 0, bytesRead);
	        }
	        response.body().close();
	        outStream.close();
	    
	        System.out.println();
	        
		} catch (IOException | InterruptedException e1) {
			System.out.printf("Could not download season %s, episode %s%n", e.getFather().getID(), e.getID());
			e1.printStackTrace();
		}
		throw new UnsupportedOperationException("need to implement this");

	}
	
	private String preWatch(Episode e) throws IOException, InterruptedException {
		String token = null;
		
		// request data
        String data = String.format("preWatch=true&season=%s&ep=%s&SID=%s",e.getFather().getID(), e.getID(), e.getFather().getFather().getID());
        System.out.println(String.format("%s/watch/%s", conf.getSdarotURI().toString(), getChangePartUrl((Season)e.getFather(), e.getID())));
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(data))
                .uri(conf.getWatchURI())
                .setHeader("User-Agent", conf.getUserAgent())
                .setHeader("Referer", String.format("%s/watch/%s", conf.getSdarotURI().toString(), getChangePartUrl((Season)e.getFather(), e.getID())))
                .setHeader("X-Requested-With", conf.X_REQUESTED_WITH)
                .setHeader("Content-Type", conf.CONTENT_TYPE)
                .build();
		HttpResponse<String> response = conf.getHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        token = response.body();
        // waiting for the token to be valid
        Thread.sleep(30000);
		return token;
	}
	
	private Map<String, Object> getVideoData(Episode e, String token) throws IOException, InterruptedException {
		Map<String, Object> map = null;
		
		// request 
        String data = String.format("watch=true&season=%s&episode=%s&serie=%s&token=%s&type=episode",e.getFather().getID(), e.getID(), e.getFather().getFather().getID(), token);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(data))
                .uri(conf.getWatchURI())
                .setHeader("User-Agent", conf.getUserAgent())
                .setHeader("Referer", String.format("%s/watch/%s", conf.getSdarotURI().toString(), getChangePartUrl((Season)e.getFather(), e.getID())))
                .setHeader("X-Requested-With", conf.X_REQUESTED_WITH)
                .setHeader("Content-Type", conf.CONTENT_TYPE)
                .build();
		HttpResponse<String> response = conf.getHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        map = jsonStringToHashMap(response.body());
		return map;
	}
	
	// TODO: think of where to put this maybe handler or a util class
	private Map<String, Object> jsonStringToHashMap(String s){
		Map<String, Object> map = new HashMap<>();
    	Object value;
    	String key;
        for (String pair : s.substring(1, s.length() - 1).split(",")) {
        	key = pair.split(":")[0].replace("\"", "");
        	if (pair.split(":")[1].charAt(0) == '{') {
        		value = jsonStringToHashMap(pair.substring(pair.indexOf(":")+1));
        	} else {
        		value = pair.split(":")[1].replace("\"", "");
        	}
			map.put(key, value);
        }
        return map;
	}
}
