package models;

import java.util.HashMap;


public class Episode extends Model {


	public Episode(Season season, int id) {
		super(season, id);
	}

	@Override
	public HashMap<Integer, Model> getChildren() {
		return null;
	}
	
	@Override
	public void AddChildren(Model child) {
		// TODO: think of a better way to throw exception here
		throw new UnsupportedOperationException("method not implemented yet!");
	}
}
