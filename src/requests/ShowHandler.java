package requests;

import java.awt.Image;
import java.io.IOException;
import java.util.List;

import javax.security.auth.login.Configuration;

import requests.Configurations;

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
	
	private static boolean IsExists(int showID) {
		try {
			HttpResponse<String> response = getPageResponse(showID);
			if (response.statusCode() == Configurations.OK_STATUS) {
	        	return true;
	        } 
	        return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}

	public static HttpResponse<String> getPageResponse(int showID) throws IOException, InterruptedException {
		HttpClient client = Configurations.getInstance().getHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(String.format("%s/watch/%s", Configurations.getInstance().getSdarotURI().toString(), showID)))
                .setHeader("User-Agent", Configurations.getInstance().getUserAgent())
                .build();
		return client.send(request, HttpResponse.BodyHandlers.ofString());
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
