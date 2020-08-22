package requests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
	public String getSuffixUrl(Show show, int seasonID) {
		return String.format("%s/season/%s", ShowHandler.getInstance().getSuffixUrl(show.getFather(), show.getID()), seasonID);
	}
	
	@Override
	public Season getByID(Show show, int seasonID) throws IOException, InterruptedException {
		if(!IsExists(show, seasonID)) {
			return null;
		}
		Season ret = new Season(show, seasonID);
		EpisodeHandler.getInstance().getAll(ret).forEach(e -> ret.AddChildren(e));
		return ret;
	}
	

	@Override
	public List<Season> getAll(Show show) throws IOException, InterruptedException {
		List<Season> seasons = new ArrayList<>();
		Pattern seasonPattern = Pattern.compile(String.format("(/watch/%s-.*?/season/(\\d+?)\")", show.getID()));
        Matcher matcher = seasonPattern.matcher(ShowHandler.getInstance().getPageResponse(show.getFather(), show.getID()).body());
        
        while (matcher.find()) {
        	String seasonurl = matcher.group();
        	seasonurl = seasonurl.substring(0, seasonurl.length() - 1); // removing the " in the end
    		seasons.add(new Season(show, Integer.parseInt(seasonurl.split("/season/")[1])));
        }
		return seasons;
	}

	@Override
	public void download(Season season) {
		try {
			EpisodeHandler.getInstance().getAll(season).forEach(e -> EpisodeHandler.getInstance().download(e));
		} catch (IOException | InterruptedException e) {
		    System.out.printf("Can't download season %s, problem getting the episodes, error:%n", season.getID());
			e.printStackTrace();
		}
	}
	
}
