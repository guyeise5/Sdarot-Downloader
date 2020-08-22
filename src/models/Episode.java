package models;

import java.util.HashMap;


@SuppressWarnings("rawtypes")
public class Episode extends Model<Season, Model> {

	public Episode(Season season, int id) {
		super(season, id);
	}

	@Override
	public HashMap<Integer, Model> getChildren() {
		return null;
	}
	
	@Override
	public void AddChildren(Model child) {
		throw new UnsupportedOperationException("Episode do not have children");
	}
}
