package models;

import java.util.HashMap;

/**
 * <H1>
 * Episode
 * </H1>
 * This class is an Episode, child of {@link Season}
 */
public class Episode extends Model<Season, Model<?,?>> {

	/**
	 * <H1>
	 * Episode
	 * </H1>
	 * Constructor of {@link Episode}
	 * @param season The father of this episode, {@link Season}
	 * @param id The id of this episode (the episode number)
	 */
	public Episode(Season season, int id) {
		super(season, id);
	}

	/**
	 * <H1>
	 * getChildren
	 * </H1>
	 * Episode does not have children
	 * @return null
	 */
	@Override
	public HashMap<Integer, Model<?,?>> getChildren() {
		return null;
	}
	
	/**
	 * <H1>
	 * addChild
	 * </H1>
	 * Episode does not have children
	 * @throws UnsupportedOperationException
	 */
	@Override
	public void addChild(Model<?,?> child) {
		throw new UnsupportedOperationException("Episode do not have children");
	}
}
