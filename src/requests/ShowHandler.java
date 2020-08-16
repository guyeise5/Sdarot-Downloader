package requests;

import java.awt.Image;
import java.io.IOException;
import java.util.List;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

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
	
	private static String getShowName(int ShowID) {
		// TODO: implement this function
		// for now - need to think on how to get real name
		return (String.format("%s", ShowID));
	}

	public String getChangePartUrl(Root root, int showID) {
		return String.format("%s", showID);
	}
	
	@Override
	public List<Show> getAll(Root root) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Can't get all shows at once!");
	}

	@Override
	public Show getByID(Root root, int showID) {
		if(!IsExists(root, showID)) {
			return null;
		}
		Show ret = new Show((Root)root, showID);
		SeasonHandler.getInstance().getAll(ret).forEach(s -> ret.AddChildren(s));
		ret.SetName(getShowName(showID));
		return ret;
	}

	
	@Override
	public void download(Show show) {
		SeasonHandler.getInstance().getAll(show).forEach(s -> SeasonHandler.getInstance().download(s));
	}

}
