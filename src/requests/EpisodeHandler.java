package requests;

import java.util.List;

import models.Episode;
import models.Season;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class EpisodeHandler {

	private static EpisodeHandler instance = null;
	
	private Configurations conf;
	
	private EpisodeHandler(Configurations conf)	{
		this.conf = conf;
	}
	
	public static EpisodeHandler getInstance() {
		if(instance == null) {
			instance = new EpisodeHandler(Configurations.getInstance());
		}
		return instance;
	}
	
	public models.Episode getEpisode(Season season, int episodeNumber) {
		// TODO: implement this function
		throw new NotImplementedException();
	}
	
	public List<Episode> getAllEpisodes(Season season) {
		// TODO: implement this function
		throw new NotImplementedException();
	}
	
	
	public boolean IsExists(Season season, int episodeNumber) {
		// TODO: implement this function
		throw new NotImplementedException();
	}
}
