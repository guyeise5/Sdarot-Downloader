package models;

import java.util.HashMap;


@SuppressWarnings("rawtypes")
public abstract class Model<Father extends Model, Child extends Model> {
	 
	Father father;
	int id;
	HashMap<Integer, Child> childrens;
	
	public Model(Father f, int id) {
		this.father = f;
		this.id=id;
		childrens = new HashMap<Integer, Child>();
	}
		
	public String getDownloadPath() throws NullPointerException {
		return String.format("%s/%s_%s", father.getDownloadPath(), this.getClass().getSimpleName(),this.getID());
	}
	
	public Father getFather() {
		return this.father;
	}
	
	public HashMap<Integer, Child> getChildren() {
		return childrens;
	}
	
	public void AddChildren(Child child) {
		this.childrens.put(child.getID(), child);
	}
	
	public int getID() {
		return this.id;
	}
}
