package models;

import java.util.HashMap;


/**
 * <H1>
 * Model
 * </H1>
 * This abstract class is a basic model of an object with father and children
 * It has a father, what it is in and children that are in it
 *
 * @param <Father> The father model that contains this model
 * @param <Child> The child model that are contained by this model
 */
public abstract class Model<Father extends Model<?, ?>, Child extends Model<?, ?>> {
	 
	// Properties 
	
	Father father;
	int id;
	HashMap<Integer, Child> children;

	// Methods
	
	/**
	 * <H1>
	 * Model
	 * </H1>
	 * Constructor of {@link Model}}
	 * @param f The father of this model
	 * @param id The id of this model
	 */
	public Model(Father f, int id) {
		this.father = f;
		this.id = id;
		children = new HashMap<Integer, Child>();
	}
	
	/**
	 * <H1>
	 * getDownloadPath
	 * </H1>
	 * the download path is the path that this model should be download to.
	 * @return the download path of this model
	 * @throws NullPointerException if father download path does not exists
	 */
	public String getDownloadPath() throws NullPointerException {
		return String.format("%s/%s_%s", father.getDownloadPath(), this.getClass().getSimpleName(),this.getID());
	}
	
	/**
	 * <H1>
	 * getFather
	 * </H1>
	 * Getter of property
	 * @return Father
	 */
	public Father getFather() {
		return this.father;
	}
	
	/**
	 * <H1>
	 * getID
	 * </H1>
	 * Getter of property
	 * @return ID
	 */
	public int getID() {
		return this.id;
	}
	
	/**
	 * <H1>
	 * getChildren
	 * </H1>
	 * Getter of property
	 * @return Children 
	 */
	public HashMap<Integer, Child> getChildren() {
		return children;
	}
	
	/**
	 * <H1>
	 * addChild
	 * </H1>
	 * Adding a child
	 * @param child the child to add
	 */
	public void addChild(Child child) {
		this.children.put(child.getID(), child);
	}
}
