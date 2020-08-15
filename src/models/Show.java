package models;

public class Show extends Model<Root, Season> {

	private String name;

	public Show(Root root, int id) {
		super(root, id);
	}
	
	public void SetName(String name) {
		this.name = name;
	}
	
	@Override
	public String getDownloadPath() {
		if(this.getName() == null) {
			return super.getDownloadPath();
		}
		return father.getDownloadPath() + "/" + this.getName();
	}

	public String getName() {
		return this.name;
	}
}
