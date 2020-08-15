package requests;

import java.util.List;

import models.Model;
import models.Season;
import models.Show;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

// Singleton class
public class SeasonHandler extends Handler<Show,Season> {

	private static SeasonHandler instance = null;
		
	public static SeasonHandler getInstance() {
		if(instance == null) {
			instance = new SeasonHandler();
		}
		return instance;
	}

	
	public boolean IsExists(Show show, int seasonNumber) {
		// TODO: implement this function
		throw new NotImplementedException();
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
		throw new NotImplementedException();
	}
	
}
