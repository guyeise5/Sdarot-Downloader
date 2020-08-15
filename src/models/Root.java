package models;


@SuppressWarnings("rawtypes")
public class Root extends Model<Model, Show> {

	
	private String downloadPath;

	
	
	/**
	 * 
	 * @param f   - will be set to null no matter the value
	 * @param id  - will be set to 0 no matter the value
	 */
	public Root(Model f, int id) {
		super(f, id);
	}
	
	@Override
	public String getDownloadPath() throws NullPointerException {
		if(downloadPath == null) {
			throw new NullPointerException("Download path is undefined");
		}
		
		return this.downloadPath;
	}
	
	@Override
	public Model getFather() {
		return null;
	}
	
	@Override
	public int getID() {
		return 0;
	}
	
	public void setDownloadPath(String path) throws NullPointerException {
		// TODO: Make this test more rigid
		if(path == null ) {
			throw new NullPointerException("path cannot be null");
		}
		
		this.downloadPath = path;
	}

}
