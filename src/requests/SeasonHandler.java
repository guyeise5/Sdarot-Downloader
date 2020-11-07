package requests;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.regex.Pattern;

import models.Season;
import models.Show;

// Singleton class
public class SeasonHandler extends Handler<Show,Season> {
	
	private SeasonHandler() {
		super();
		setUriPrefix("/season/");
	}
	
	private static SeasonHandler instance = null;
		
	public static SeasonHandler getInstance() {
		if(instance == null) {
			instance = new SeasonHandler();
		}
		return instance;
	}
	
	@Override
	protected HttpResponse<String> getFatherPageResponse(Show show) throws IOException, InterruptedException {
		return ShowHandler.getInstance().getPageResponse(show.getFather(), show.getID());
	}
	
	@Override
	public Pattern getPattern(Show show) {
		return Pattern.compile(String.format("(%s%s-.*?%s(\\d+?)\")", 
				ShowHandler.getInstance().getUriPrefix(),
				show.getID(),
				getUriPrefix()));
	}
	
	@Override
	public String getSuffixUrl(Show show, int seasonID) {
		return String.format("%s%s%s", ShowHandler.getInstance().getSuffixUrl(show.getFather(), show.getID()), getUriPrefix(), seasonID);
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
	public void download(Season season) {
		season.getChildren().forEach((i, e) -> EpisodeHandler.getInstance().download(e));
	}
	
}
