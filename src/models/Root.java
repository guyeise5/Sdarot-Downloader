package models;

/**
 * <H1>
 * Root
 * </H1>
 * This class is Root, which is the father of shows
 */
public class Root extends Model<Model<?, ?>, Show> {

	// Properties
	
	private String downloadPath;
	
	// Methods
	
	/**
	 * <H1>
	 * Root
	 * </H1>
	 * Constructor 
	 */
	public Root() {
		super(null, 0);
	}

	/**
	 * <H1>
	 * Root
	 * </H1>
	 * Constructor
	 * @param downloadPath is the location for the downloads of root children 
	 */
	public Root(String downloadPath) {
		super(null, 0);
		this.setDownloadPath(downloadPath);
	}
	
	/**
	 * <H1>
	 * getDownloadPath
	 * </H1>
	 * Getter of property
	 * @return the download path
	 * @throws NullPointerException when download path was not set
	 */
	@Override
	public String getDownloadPath() throws NullPointerException {
		if(downloadPath == null) {
			throw new NullPointerException("Download path is undefined");
		}
		
		return this.downloadPath;
	}
	
	/**
	 * <H1>
	 * setDownloadPath
	 * </H1>
	 * Setter of property
	 * @param path the path to set as download path
	 * @throws NullPointerException when the path is null
	 */
	public void setDownloadPath(String path) throws NullPointerException {
		// TODO: Make this test more rigid
		if(path == null ) {
			throw new NullPointerException("path cannot be null");
		}
		
		this.downloadPath = path;
	}

}
