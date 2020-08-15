package requests;

import java.awt.Image;
import java.util.List;

import models.Model;
import models.Root;
import models.Show;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

// Singleton class
public class ShowHandler extends Handler<Root,Show> {

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
		throw new NotImplementedException();
	}
	
	private boolean IsExists(int ShowID) {
		// TODO: implement this function
		throw new NotImplementedException();
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
}
