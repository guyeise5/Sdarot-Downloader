package models;


@SuppressWarnings("rawtypes")
public class Root extends Model<Model, Show> {

	
	private String downloadPath;

	
	
	public Root() {
		super(null, 0);
	}

	public Root(String downloadPath) {
		super(null, 0);
		this.setDownloadPath(downloadPath);
	}
	
	@Override
	public String getDownloadPath() throws NullPointerException {
		if(downloadPath == null) {
			throw new NullPointerException("Download path is undefined");
		}
		
		return this.downloadPath;
	}
	
	
	public void setDownloadPath(String path) throws NullPointerException {
		// TODO: Make this test more rigid
		if(path == null ) {
			throw new NullPointerException("path cannot be null");
		}
		
		this.downloadPath = path;
	}

}
