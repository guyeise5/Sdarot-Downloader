package models;

import java.util.HashMap;

import javax.activity.InvalidActivityException;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class Episode extends Model {


	public Episode(Season season, int id) {
		super(season, id);
	}

	@Override
	public void download() {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}
	
	@Override
	public HashMap<Integer, Model> getChildren() {
		return null;
	}
	
	@Override
	public void AddChildren(Model child) {
		// TODO: think of a better way to throw exception here
		throw new NotImplementedException();
	}
}
