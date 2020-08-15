package requests;

import java.util.List;

import models.Episode;
import models.Season;
import models.Show;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

// Singleton class
public class SeasonHandler {

	private static SeasonHandler instance = null;
	
	private Configurations conf;
	
	private SeasonHandler(Configurations conf)	{
		this.conf = conf;
	}
	
	public static SeasonHandler getInstance() {
		if(instance == null) {
			instance = new SeasonHandler(Configurations.getInstance());
		}
		return instance;
	}
	
	public models.Season getSeason(Show show, int seasonNumber) {
		if(!IsExists(show, seasonNumber)) {
			return null;
		}
		Season ret = new Season(show, seasonNumber);
		EpisodeHandler.getInstance().getAllEpisodes(ret).forEach(e -> ret.AddEpisoe(e));
		return ret;
	}
	
	public List<models.Season> getAllSeasons(Show show){
		// TODO: implement this function
		throw new NotImplementedException();
	}
	
	public boolean IsExists(Show show, int seasonNumber) {
		// TODO: implement this function
		throw new NotImplementedException();
	}
	
}
