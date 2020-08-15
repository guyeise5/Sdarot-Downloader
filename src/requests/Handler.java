package requests;

import java.util.List;

import models.Model;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

@SuppressWarnings("rawtypes")
public abstract class Handler <Father extends Model, T extends Model> {

	protected Configurations conf;
	
	protected Handler() {
		this.conf = Configurations.getInstance();
	}
	
	public boolean IsExists(Father father, int ID) {
		// TODO: implement this function or make is abstract
		throw new NotImplementedException();
	}
	
	public abstract T getByID(Father father, int ID);
	
	public abstract List<T> getAll(Father father);
}
