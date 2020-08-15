package requests;

import java.awt.Image;
import java.io.IOException;
import java.util.List;
import requests.Configurations;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

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
		// for now - need to think on how to get real name
		return (String.format("%s", ShowID));
	}
	
	private boolean IsExists(int showID) {
		HttpClient client = Configurations.getInstance().getHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(String.format("%s/watch/%s", Configurations.getInstance().getSdarotURI().toString(), showID)))
                .setHeader("User-Agent", Configurations.getInstance().getUserAgent())
                .build();
		try {
			HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
	        return (response.statusCode() == Configurations.getInstance().OK_STATUS);
	    } catch (IOException | InterruptedException e1) {
			e1.printStackTrace();
			return false;
		}
	}

	@Override
	public List<Show> getAll(Root root) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Can't get all shows at once!");
	}

	@Override
	public Show getByID(Root root, int showID) {
		if(!IsExists(showID)) {
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
