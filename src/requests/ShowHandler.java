package requests;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import models.Root;
import models.Show;

/**
 * <H1>
 * ShowHandler
 * </H1>
 * This class is {@link Handler} for {@link Show}s in sdarot site.
 */
public class ShowHandler extends Handler<Root,Show> {
	
	// Properties
	
	private static ShowHandler instance = null;
	
	// Methods
	
	/**
	 * <H1>
	 * ShowHandler
	 * </H1>
	 * Constructor of {@link ShowHandler}
	 */
	private ShowHandler() {
		super();
		setUriPrefix("/watch/");
	}
	
	/**
	 * <H1>
	 * getInstance
	 * </H1>
	 * 
	 * This function is the only way to access to the {@link ShowHandler} instance
	 * It creates it if it is not exist.
	 * @return The ShowHandler instance
	 */
	public static ShowHandler getInstance() {
		if(instance == null) {
			instance = new ShowHandler();
		}
		return instance;
	}
	
	/**
	 * <H1>
	 * getShowImage
	 * </H1>
	 * 
	 * Downloading and returning the {@link Show} image
	 * @param ShowID the show id
	 * @return buffered image of the show
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public BufferedImage getShowImage(int ShowID) throws InterruptedException, IOException {
		File targetFile = new File(String.format("%s/%s.jpg", getConfigurations().IMAGES_PATH, ShowID));
		BufferedImage showImage;
		if (targetFile.exists()) {
			showImage = ImageIO.read(targetFile);
		} else {
			// if image not available getting it from website
			showImage = ImageIO.read(new URL(String.format("%s/series/%s.jpg", getStaticSdarotURI(), ShowID)));
			targetFile.getParentFile().mkdirs();
			ImageIO.write(showImage, "jpg", targetFile);
		}
		return showImage;
	}
	
	/**
	 * <H1>
	 * getShowName
	 * </H1>
	 * Getting the {@link Show} name
	 * @param r the root of the show
	 * @param ShowID the show id
	 * @return
	 */
	private String getShowName(Root r, int ShowID) {
		String name = String.format("%s", ShowID); 
		try {
			HttpResponse<String> response = ShowHandler.getInstance().getPageResponse(r, ShowID);
			Pattern showNameVar = Pattern.compile("(var Sname.*=.*)");
			Matcher m = showNameVar.matcher(response.body());
			if (m.find()) {
				name = m.group().split("=")[1].split(",")[1].replace("\"", "").replace("[","").replace("]", "");
			} else {
				System.out.println("could not get show name... setting the show name as the id");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return name;
	}
	
	/**
	 * <H1>
	 * getAll
	 * </H1>
	 * can't get all {@link Show}s at once - it's too much
	 * @param father the father we want to find it's children
	 * @throws UnsupportedOperationException
	 */
	@Override
	public List<Show> getAll(Root root) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Can't get all shows at once!");
	}
	
	/**
	 * <H1>
	 * getShowsIDs
	 * </H1>
	 * This functions getting some ids of shows
	 * @param root the root 
	 * @param load the number of ids to load
	 * @param start the id to start from, if null start from 1
	 * @return set of the shows ids
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public HashSet<Integer> getShowsIDs(Root root, int load, Integer start) throws InterruptedException, IOException {
		if (start == null) {
			start = 1;
		}
		
		HttpClient client = getConfigurations().getHttpClient();
		Thread.sleep(getConfigurations().DELAY_BETWEEN_REQUESTS);
		HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(String.format("%s/series?loadMore=%s&start=%s&search[from]=&search[to]=&search[order]=releaseYear&search[dir]=DESC", getAjaxURI(), load, start)).normalize())
                .setHeader("User-Agent", getConfigurations().getUserAgent())
                .setHeader("Referer", String.format("%s/series", getSdarotURI()))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        HashSet<Integer> showIDs = new HashSet<Integer>();
		Pattern modelPattern = getPattern(root);
		Matcher matcher = modelPattern.matcher(response.body());
        while (matcher.find()) {
        	String modelurl = matcher.group();
        	showIDs.add(Integer.parseInt(modelurl.split(getUriPrefix())[1].split("-")[0]));
        }
        
		return showIDs;
		
	}
	
	/**
	 * <H1>
	 * getFatherPageResponse
	 * </H1>
	 * {@link Show} does not have father page! 
	 * @param root the father model to get it's page
	 * @throws UnsupportedOperationException
	 */
	@Override
	protected HttpResponse<String> getFatherPageResponse(Root root) throws IOException, InterruptedException {
		throw new UnsupportedOperationException("This function is not implemented yet");
	}
	
	/**
	 * <H1>
	 * getPattern
	 * </H1>
	 * getting the {@link Show} pattern to be able to find the shows in the {@link Root}
	 * @param root the root of the shows we want the pattern to
	 * @return the pattern
	 */
	@Override
	public Pattern getPattern(Root root) {
		return Pattern.compile(String.format("href=\"[^>]*\">", 
				getUriPrefix()));
	}
	
	/**
	 * <H1>
	 * getSuffixUrl
	 * </H1>
	 * This function returns the specific part of the URL for the {@link Show}
	 * @param root the {@link Root} of the show
	 * @param showID the id of the show
	 * @return the suffix url of the show
	 */
	@Override
	public String getSuffixUrl(Root root, int showID) {
		return String.format("%s%s", getUriPrefix(), showID);
	}

	/**
	 * <H1>
	 * getByID
	 * </H1>
	 * finding the {@link Show} by the show's id
	 * @param root the show's {@link Root}
	 * @param showID the show id
	 * @return the show
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Override
	public Show getByID(Root root, int showID) throws IOException, InterruptedException {
		if(!IsExists(root, showID)) {
			return null;
		}
		Show ret = new Show(root, showID);
		SeasonHandler.getInstance().getAll(ret).forEach(s -> ret.addChild(s));
		ret.setName(getShowName(root, showID));
		System.out.println(ret.getName());
		return ret;
	}
	
	/**
	 * <H1>
	 * download
	 * </H1>
	 * download the {@link Show}
	 * @param show the show to download
	 */
	@Override
	public void download(Show show) {
		show.getChildren().forEach((i, s) -> SeasonHandler.getInstance().download(s));
	}
}
