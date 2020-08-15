package models;

import interfaces.Model;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class Episode implements Model {

	private Season season;
	int number;
	
	public Episode(Season season, int number) {
		this.season = season;
		this.number = number;
	}

	@Override
	public void Download() {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	@Override
	public String getDownloadPath() {
		
		return season.getDownloadPath() + "/Episode " + Integer.toString(this.number);
	}

	public int getNumber() {
		return this.number;
	}

}
