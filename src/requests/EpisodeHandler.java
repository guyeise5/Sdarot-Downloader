package requests;

import java.util.List;

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
	public List<Episode> getAll(Season season) {
		// TODO: Implement this function
		throw new UnsupportedOperationException("method not implemented yet!");
	}
	
	public void download(Episode e) {
		// TODO: Implement this function
		throw new UnsupportedOperationException("method not implemented yet!");
	}

}
