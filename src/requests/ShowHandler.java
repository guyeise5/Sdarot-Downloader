package requests;

import java.awt.Image;
import java.util.List;

import models.Model;
import models.Root;
import models.Show;

// Singleton class
public class ShowHandler extends Handler<Root,Show> {

	private ShowHandler() {}
	
	private static ShowHandler instance = null;
		
	public static ShowHandler getInstance() {
		if(instance == null) {
			instance = new ShowHandler();
		}
		return instance;
	}
	
	public Image getShowImage(int ShowID) {
		// TODO: implement this function
		return null;
	}
	
	private String getShowName(int ShowID) {
		// TODO: implement this function
		throw new UnsupportedOperationException("method not implemented yet!");
	}
	
	private boolean IsExists(int ShowID) {
		// TODO: implement this function
		throw new UnsupportedOperationException("method not implemented yet!");
	}

	@Override
	public List<Show> getAll(Root root) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Show getByID(Root root, int showID) {
		if(!IsExists(showID)) {
			return null;
		}
		
		models.Show ret = new Show((Root)root, showID);
		SeasonHandler.getInstance().getAll(ret).forEach(s -> ret.AddChildren(s));
		ret.SetName(getShowName(showID));
		return ret;

	}

	
	@Override
	public void download(Show show) {
		SeasonHandler.getInstance().getAll(show).forEach(s -> SeasonHandler.getInstance().download(s));
	}
}