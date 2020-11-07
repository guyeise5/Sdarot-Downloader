package models;

/**
 * <H1>
 * Show
 * </H1>
 * This class is a show, child of {@link Root} and father of {@link Season}
 */
public class Show extends Model<Root, Season> {

	// Properties
	
	private String name;

	// methods
	
	/**
	 * <H1>
	 * Show
	 * </H1>
	 * Constructor of {@link Show}
	 * @param root the father of the show
	 * @param id the id of the show
	 */
	public Show(Root root, int id) {
		super(root, id);
	}
	
	/**
	 * <H1>
	 * setName
	 * </H1>
	 * Setter of a property
	 * @param name the show name 
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * <H1>
	 * getDownloadPath
	 * </H1>
	 * Getter of a property
	 * For a show the download path should be with the name of the show
	 * @return the show download path
	 */
	@Override
	public String getDownloadPath() {
		if(this.getName() == null) {
			return super.getDownloadPath();
		}
		return String.format("%s/%s", father.getDownloadPath(), this.getName());
	}

	/**
	 * <H1>
	 * getName
	 * </H1>
	 * Getter of a property
	 * @return the show name
	 */
	public String getName() {
		return this.name;
	}
}
