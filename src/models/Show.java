package models;
import java.util.HashMap;

import interfaces.Model;

public class Show implements Model {

	private HashMap<Integer, Season> seasons;

	private String name;
	private int sid;
	private String downloadPath;

	public Show(int sid, String name) {
		this.name = name;
		this.sid = sid;
		seasons = new HashMap<Integer, Season>();
		}

	@Override
	public void Download() {
		if(downloadPath == null) {
			throw new NullPointerException("please execute 'SetDownloadPath' method before downloading");
		}
		
		seasons.values().forEach(s -> s.Download());
	}

	@Override
	public String getDownloadPath() {
		return this.downloadPath;
	}
	
	public void AddSeason(Season s) {
		seasons.put(s.getNumber(), s);
	}

	public void SetDownloadPath(String path) throws NullPointerException {
		// TODO: Make this test more rigid
		if(path == null ) {
			throw new NullPointerException("path cannot be null");
		}
		
		this.downloadPath = path;
	}
}
