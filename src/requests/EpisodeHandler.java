package requests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;

import models.Episode;
import models.Season;
import models.Show;

/**
 * <H1>
 * EpisodeHandler
 * </H1>
 * This class is {@link Handler} for {@link Episode}s in sdarot site.
 */
public class EpisodeHandler extends Handler<Season, Episode> {

	// Properties
	
	private static EpisodeHandler instance = null;

	// Methods
	
	/**
	 * <H1>
	 * EpisodeHandler
	 * </H1>
	 * Constructor of {@link EpisodeHandler}
	 */
	private EpisodeHandler() {
		super();
		setUriPrefix("/episode/");
	}
	
	/**
	 * <H1>
	 * getInstance
	 * </H1>
	 * 
	 * This function is the only way to access to the {@link EpisodeHandler} instance
	 * It creates it if it is not exist.
	 * @return The EpisodeHandler instance
	 */
	public static EpisodeHandler getInstance() {
		if(instance == null) {
			instance = new EpisodeHandler();
		}
		return instance;
	}
	
	/**
	 * <H1>
	 * getFatherPageResponse
	 * </H1>
	 * Getting the page response of the {@link Season}
	 * @param season the season to get it's page
	 * @return the page response
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Override
	protected HttpResponse<String> getFatherPageResponse(Season season) throws IOException, InterruptedException {
		return SeasonHandler.getInstance().getPageResponse(season.getFather(), season.getID());
	}
	
	/**
	 * <H1>
	 * getPattern
	 * </H1>
	 * getting the {@link Episode} pattern to be able to find the episodes in the {@link Season} page
	 * @param season the season of the episodes we want the pattern to
	 * @return the pattern
	 */
	@Override
	public Pattern getPattern(Season season) {
		return Pattern.compile(String.format("(%s%s-.*?%s%s%s(\\d+?)\")", 
				ShowHandler.getInstance().getUriPrefix(),
				season.getFather().getID(), 
				SeasonHandler.getInstance().getUriPrefix(),
				season.getID(),
				getUriPrefix()));
	}
	
	/**
	 * <H1>
	 * getSuffixUrl
	 * </H1>
	 * This function returns the specific part of the URL for the {@link Episode}
	 * @param season the {@link Season} of the episode
	 * @param episodeID the id of the episode
	 * @return the suffix url of the episode
	 */
	@Override
	public String getSuffixUrl(Season season, int episodeID) {
		return String.format("%s%s%s", SeasonHandler.getInstance().getSuffixUrl(season.getFather(), season.getID()), getUriPrefix(), episodeID);
	}

	/**
	 * <H1>
	 * getByID
	 * </H1>
	 * finding the {@link Episode} by the model's id
	 * @param season the episode's season
	 * @param episodeID the episode id
	 * @return the Episode
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Override
	public Episode getByID(Season season, int episodeID) {
		if(!IsExists(season, episodeID)) {
			return null;
		}
		return new Episode(season, episodeID);
	}
	
	/**
	 * <H1>
	 * download
	 * </H1>
	 * download the {@link Episode}
	 * @param e the episode to download
	 */
	public void download(Episode e)  {
		// TODO: find a better way to deal with errors
		// TODO: logger
		// TODO: Think about cookie more
		
		// Doing the pre watch in selenium because they keep changing the requests
		System.setProperty("webdriver.chrome.driver","chromedriver.exe");

		ChromeOptions options = new ChromeOptions();
		options.addArguments("headless");
		WebDriver driver = new ChromeDriver(options);
		// setting timeout of 1 minute
		WebDriverWait wait = new WebDriverWait(driver, 45);
		try {
			// The pre watch wait
			driver.get(String.format("%s%s", getSdarotURI(), getSuffixUrl(e.getFather(), e.getID())));
			System.out.printf("prewatching serie %s season %s episode %s%n", ((Show)(e.getFather().getFather())).getName(), e.getFather().getID(), e.getID());
			WebElement continueBtn = wait.until(elementToBeClickable(By.id("proceed")));
			continueBtn.click();
			
			// Getting the video source url
			WebElement video = driver.findElement(By.tagName("video"));
			URI video_uri = URI.create(video.getAttribute("src"));
			
			// creating file full path to put the video in it
	        File targetFile = new File(String.format("%s.downloading", e.getDownloadPath()));
	        targetFile.getParentFile().mkdirs(); 
	        
	        // getting the cookies
	        String cookie = driver.manage().getCookieNamed("Sdarot").getValue();
	        
			// requesting video data
			HttpRequest request	= HttpRequest.newBuilder()
					.GET()
					.uri(video_uri)
					.setHeader("User-Agent", getConfigurations().getUserAgent())
					.setHeader("Referer", String.format("%s%s", getSdarotURI().toString(), getSuffixUrl((Season)e.getFather(), e.getID())))
					.setHeader("Cookie", String.format("Sdarot=%s", cookie))
					.build();
			HttpResponse<InputStream> response = getConfigurations().getHttpClient().send(request, HttpResponse.BodyHandlers.ofInputStream());
			// Using response as input stream and file as output stream
			// Putting the video in the file chunk by chunk
			try (InputStream is = response.body();
					OutputStream outStream = new FileOutputStream(targetFile)) {
				
		        System.out.printf("Starting download serie %s season %s episode %s%n", ((Show)(e.getFather().getFather())).getName(), e.getFather().getID(), e.getID());
	            
		        byte[] buffer = new byte[getConfigurations().BLOCK_SIZE];
		        int bytesRead;

		        while ((bytesRead = is.read(buffer)) != -1) {
		            outStream.write(buffer, 0, bytesRead);
		        }
		        System.out.println("downloaded");
			}
			// rename file
	        targetFile.renameTo(new File(String.format("%s.mp4", e.getDownloadPath())));
		} catch (IOException | InterruptedException | TimeoutException e1) {
			System.out.printf("Could not download serie %s season %s episode %s%n", ((Show)(e.getFather().getFather())).getName(), e.getFather().getID(), e.getID());
			e1.printStackTrace();
			//this.download(e);
		} finally {
			driver.close();	
		}
	}
}
