package requests;

import java.awt.Image;

import models.Season;
import models.Show;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

// Singleton class
public class ShowHandler {

	private static ShowHandler instance = null;
	
	private Configurations conf;
	
	private ShowHandler(Configurations conf)	{
		this.conf = conf;
	}
	
	public static ShowHandler getInstance() {
		if(instance == null) {
			instance = new ShowHandler(Configurations.getInstance());
		}
		return instance;
	}
	
	public Show getShow(int showID) {
		if(!IsExists(showID)) {
			return null;
		}
		
		models.Show ret = new Show(showID, getShowName(showID));

		SeasonHandler.getInstance().getAllSeasons(ret).forEach(s -> ret.AddSeason(s));
		
		return ret;
	}
	
	public Image getShowImage(int ShowID) {
		// TODO: implement this function
		return null;
	}
	
	private String getShowName(int ShowID) {
		// TODO: implement this function
		throw new NotImplementedException();
	}
	
	private boolean IsExists(int ShowID) {
		// TODO: implement this function
		throw new NotImplementedException();
	}
}
