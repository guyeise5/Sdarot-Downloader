package requests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import models.Episode;
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
	public String getSuffixUrl(Season season, int episodeID) {
		return String.format("%s/episode/%s", SeasonHandler.getInstance().getSuffixUrl(season.getFather(), season.getID()), episodeID);
	}

	@Override
	public Episode getByID(Season season, int episodeID) {
		if(!IsExists(season, episodeID)) {
			return null;
		}
		return new Episode(season, episodeID);
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
		// TODO: Find a better way to deal with errors
		try {
			// preWatching and getting video info 
			Map<String, Object> vidInfo = getVideoData(e, preWatch(e));
			// data for the video request
	        @SuppressWarnings("unchecked")
			String data = String.format("token=%s&time=%s&uid=", ((Map<String, Object>)vidInfo.get("watch")).get("480"), vidInfo.get("time"));
	        // creating video request
	        HttpRequest request = HttpRequest.newBuilder()
	                .GET()
	                .uri(URI.create(String.format("https://%s/w/episode/%s/480/%s.mp4?%s", vidInfo.get("url"), e.getFather().getFather().getID(), vidInfo.get("VID"), data)))
	                .setHeader("User-Agent", conf.getUserAgent())
	                .setHeader("Referer", String.format("%s/watch/%s", conf.getSdarotURI().toString(), getSuffixUrl((Season)e.getFather(), e.getID())))
	                .build();
	        
	        // creating file full path to put the video in it and setting it as the output stream
	        File targetFile = new File(e.getDownloadPath());
	        targetFile.getParentFile().mkdirs(); // creating the path if not exists 
	        
	        // sending video request
	        HttpResponse<InputStream> response = conf.getHttpClient().send(request, HttpResponse.BodyHandlers.ofInputStream());
	        
	        // try-with to make sure input and output streams will be closed
	        try (InputStream is = response.body();
			     OutputStream outStream = new FileOutputStream(targetFile)) {
		        System.out.printf("Starting download serie %s season %s episode %s%n", ((Show)e.getFather().getFather()).getName(), e.getFather().getID(), e.getID());
	            byte[] buffer = new byte[8 * 1024];
		        int bytesRead;
		        // getting the video one block at a time
		        while ((bytesRead = is.read(buffer)) != -1) {
		            outStream.write(buffer, 0, bytesRead);
		        }
		        System.out.println("downloaded");
	        } 
		} catch (IOException | InterruptedException e1) {
			System.out.printf("Could not download season %s, episode %s%n", e.getFather().getID(), e.getID());
			e1.printStackTrace();
		}
	}
	
	private String preWatch(Episode e) throws IOException, InterruptedException {
		String token = null;
		
		// Parse the request data
        String data = String.format("preWatch=true&season=%s&ep=%s&SID=%s",e.getFather().getID(), e.getID(), e.getFather().getFather().getID());
        // the request
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(data))
                .uri(conf.getWatchURI())
                .setHeader("User-Agent", conf.getUserAgent())
                .setHeader("Referer", String.format("%s/watch/%s", conf.getSdarotURI().toString(), getSuffixUrl((Season)e.getFather(), e.getID())))
                .setHeader("X-Requested-With", conf.X_REQUESTED_WITH)
                .setHeader("Content-Type", conf.CONTENT_TYPE)
                .build();
        // sending the request
		HttpResponse<String> response = conf.getHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
		// getting the token
        token = response.body();
        // waiting for the token to be valid
        Thread.sleep(conf.PRE_WATCH_DELAY_TIME);
        // The token is valid
		return token;
	}
	
	private Map<String, Object> getVideoData(Episode e, String token) throws IOException, InterruptedException {
		
		// parse request data
        String data = String.format("watch=true&season=%s&episode=%s&serie=%s&token=%s&type=episode",e.getFather().getID(), e.getID(), e.getFather().getFather().getID(), token);
        // request for the video info
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(data))
                .uri(conf.getWatchURI())
                .setHeader("User-Agent", conf.getUserAgent())
                .setHeader("Referer", String.format("%s/watch/%s", conf.getSdarotURI().toString(), getSuffixUrl((Season)e.getFather(), e.getID())))
                .setHeader("X-Requested-With", conf.X_REQUESTED_WITH)
                .setHeader("Content-Type", conf.CONTENT_TYPE)
                .build();
        // sending the request
		HttpResponse<String> response = conf.getHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
		// parsing the response as map
		return jsonStringToHashMap(response.body());
	}
	
	// TODO: think of where to put this maybe handler or a util class
	private Map<String, Object> jsonStringToHashMap(String s){
		Map<String, Object> map = new HashMap<>();
    	Object value;
    	String key;
        for (String pair : s.substring(1, s.length() - 1).split(",")) {
        	key = pair.split(":")[0].replace("\"", "");
        	String after = pair.substring(pair.indexOf(":")+1);
        	if (after.charAt(0) == '{') {
        		value = jsonStringToHashMap(after);
        	} else {
        		value = after.replace("\"", "");
        	}
			map.put(key, value);
        }
        return map;
	}
}
