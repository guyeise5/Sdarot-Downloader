package requests;

import java.util.List;

import models.Episode;
import models.Model;
import models.Season;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class EpisodeHandler extends Handler<Season, Episode> {

	private EpisodeHandler() {}
	private static EpisodeHandler instance = null;
	
	public static EpisodeHandler getInstance() {
		if(instance == null) {
			instance = new EpisodeHandler();
		}
		return instance;
	}
	
	public boolean IsExists(Season season, int episodeNumber) {
		// TODO: implement this function
		throw new NotImplementedException();
	}

	@Override
	public Episode getByID(Season season, int episodeID) {
		// TODO: Implement this function
		throw new NotImplementedException();
	}

	@Override
	public List<Episode> getAll(Season season) {
		// TODO: Implement this function
		throw new NotImplementedException();
	}
	
	public void download(Episode e) {
		// TODO: Implement this function
		throw new NotImplementedException();
	}

}
