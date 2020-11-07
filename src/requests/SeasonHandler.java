package requests;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.regex.Pattern;

import models.Season;
import models.Show;

/**
 * <H1>
 * SeasonHandler
 * </H1>
 * This class is {@link Handler} for {@link Season}s in sdarot site.
 */
public class SeasonHandler extends Handler<Show, Season> {
	
	// Properties
	
	private static SeasonHandler instance = null;
	
	// Methods
	
	/**
	 * <H1>
	 * SeasonHandler
	 * </H1>
	 * Constructor of {@link SeasonHandler}
	 */
	private SeasonHandler() {
		super();
		setUriPrefix("/season/");
	}	

	/**
	 * <H1>
	 * getInstance
	 * </H1>
	 * 
	 * This function is the only way to access to the {@link SeasonHandler} instance
	 * It creates it if it is not exist.
	 * @return The SeasonHandler instance
	 */
	public static SeasonHandler getInstance() {
		if(instance == null) {
			instance = new SeasonHandler();
		}
		return instance;
	}
	
	/**
	 * <H1>
	 * getFatherPageResponse
	 * </H1>
	 * Getting the page response of the {@link Show}
	 * @param show the show to get it's page
	 * @return the page response
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Override
	protected HttpResponse<String> getFatherPageResponse(Show show) throws IOException, InterruptedException {
		return ShowHandler.getInstance().getPageResponse(show.getFather(), show.getID());
	}
	
	/**
	 * <H1>
	 * getPattern
	 * </H1>
	 * getting the {@link Season} pattern to be able to find the seasons in the {@link Show} page
	 * @param show the show of the seasons we want the pattern to
	 * @return the pattern
	 */
	@Override
	public Pattern getPattern(Show show) {
		return Pattern.compile(String.format("(%s%s-.*?%s(\\d+?)\")", 
				ShowHandler.getInstance().getUriPrefix(),
				show.getID(),
				getUriPrefix()));
	}
	
	/**
	 * <H1>
	 * getSuffixUrl
	 * </H1>
	 * This function returns the specific part of the URL for the {@link Season}
	 * @param show the {@link Show} of the season
	 * @param seasonID the id of the season
	 * @return the suffix url of the season
	 */
	@Override
	public String getSuffixUrl(Show show, int seasonID) {
		return String.format("%s%s%s", ShowHandler.getInstance().getSuffixUrl(show.getFather(), show.getID()), getUriPrefix(), seasonID);
	}
	
	/**
	 * <H1>
	 * getByID
	 * </H1>
	 * finding the {@link Season} by the seasons's id
	 * @param show the season's {@link Show}
	 * @param seasonID the season id
	 * @return the season
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Override
	public Season getByID(Show show, int seasonID) throws IOException, InterruptedException {
		if(!IsExists(show, seasonID)) {
			return null;
		}
		Season ret = new Season(show, seasonID);
		EpisodeHandler.getInstance().getAll(ret).forEach(e -> ret.addChild(e));
		return ret;
	}

	
	/**
	 * <H1>
	 * download
	 * </H1>
	 * download the {@link Season}
	 * @param season the season to download
	 */
	@Override
	public void download(Season season) {
		season.getChildren().forEach((i, e) -> EpisodeHandler.getInstance().download(e));
	}
	
}
