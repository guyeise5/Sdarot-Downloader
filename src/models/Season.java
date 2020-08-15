package models;

import java.util.ArrayList;
import java.util.HashMap;

import interfaces.Model;

public class Season implements Model {

	private Show show;
	private int number;
	private HashMap<Integer, Episode> episodes;
	
	public Season(Show show, int number) {
		this.show = show;
		this.number = number;
		episodes = new HashMap<Integer, Episode>();
	}

	@Override
	public void Download() {
		episodes.values().forEach(e -> e.Download());
	}

	@Override
	public String getDownloadPath() {
		return show.getDownloadPath() + "/Season " + Integer.toString(this.number);  
	}

	public int getNumber() {
		return this.number;
	}

	public void AddEpisoe(Episode e) {
		episodes.put(e.getNumber(), e);
	}
}
