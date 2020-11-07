package models;

/**
 * <H1>
 * Season
 * </H1>
 * This class is a season, child of {@link Show} and father of {@link Episode}
 */
public class Season extends Model<Show, Episode> {

	/**
	 * <H1>
	 * Season
	 * </H1>
	 * Constructor of {@link Season}
	 * @param show The father of this season, {@link Show}
	 * @param id The id of this season (number of the season)
	 */
	public Season(Show show, int id) {
		super(show, id);
	}
}
