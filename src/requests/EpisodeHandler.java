package requests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Pattern;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;

import models.Episode;
import models.Season;
import models.Show;


public class EpisodeHandler extends Handler<Season, Episode> {

	private EpisodeHandler() {
		setUriPrefix("/episode/");
	}
	private static EpisodeHandler instance = null;
	
	public static EpisodeHandler getInstance() {
		if(instance == null) {
			instance = new EpisodeHandler();
		}
		return instance;
	}
	
	@Override
	protected HttpResponse<String> getFatherPageResponse(Season season) throws IOException, InterruptedException {
		return SeasonHandler.getInstance().getPageResponse(season.getFather(), season.getID());
	}
	
	@Override
	public Pattern getPattern(Season season) {
		return Pattern.compile(String.format("(%s%s-.*?%s%s%s(\\d+?)\")", 
				ShowHandler.getInstance().getUriPrefix(),
				season.getFather().getID(), 
				SeasonHandler.getInstance().getUriPrefix(),
				season.getID(),
				getUriPrefix()));
	}
	
	@Override
	public String getSuffixUrl(Season season, int episodeID) {
		return String.format("%s%s%s", SeasonHandler.getInstance().getSuffixUrl(season.getFather(), season.getID()), getUriPrefix(), episodeID);
	}

	@Override
	public Episode getByID(Season season, int episodeID) {
		if(!IsExists(season, episodeID)) {
			return null;
		}
		return new Episode(season, episodeID);
	}
	
	public void download(Episode e)  {
		// TODO: find a better way to deal with errors
		// TODO: logger
		
		// Doing the pre watch in selenium because they keep changing the requests
		System.setProperty("webdriver.chrome.driver","chromedriver.exe");

		ChromeOptions options = new ChromeOptions();
		options.addArguments("headless");
		WebDriver driver = new ChromeDriver(options);
		// setting timeout of 1 minute
		WebDriverWait wait = new WebDriverWait(driver, 60);
		try {
			// The pre watch wait
			driver.get(String.format("%s/%s", conf.getSdarotURI(), getSuffixUrl(e.getFather(), e.getID())));
			System.out.printf("prewatching serie %s season %s episode %s%n", ((Show)(e.getFather().getFather())).getName(), e.getFather().getID(), e.getID());
			WebElement continueBtn = wait.until(elementToBeClickable(By.id("proceed")));
			continueBtn.click();
			
			// Getting the video source url
			WebElement video = driver.findElement(By.tagName("video"));
			URI video_uri = URI.create(video.getAttribute("src"));
			
			
			// creating file full path to put the video in it and setting it as the output stream
	        File targetFile = new File(String.format("%s.downloading", e.getDownloadPath()));
	        targetFile.getParentFile().mkdirs(); // creating the path if not exists
	        
	        //driver.manage().getCookies();
	        // TODO: GETTING COOKIES AND PUTTING IN CLIENT ..
	        
			// requesting video data and putting it in to file chunk by chunk
			HttpRequest request	= HttpRequest.newBuilder()
					.GET()
					.uri(video_uri)
					.setHeader("User-Agent", conf.getUserAgent())
					.setHeader("Referer", String.format("%s%s", conf.getSdarotURI().toString(), getSuffixUrl((Season)e.getFather(), e.getID())))
					.build();
			HttpResponse<InputStream> response = conf.getHttpClient().send(request, HttpResponse.BodyHandlers.ofInputStream());
			try (InputStream is = response.body();
					OutputStream outStream = new FileOutputStream(targetFile)) {
		        System.out.printf("Starting download serie %s season %s episode %s%n", ((Show)(e.getFather().getFather())).getName(), e.getFather().getID(), e.getID());
	            byte[] buffer = new byte[8 * 1024];
		        int bytesRead;
		        // getting the video one block at a time
		        while ((bytesRead = is.read(buffer)) != -1) {
		            outStream.write(buffer, 0, bytesRead);
		        }
		        System.out.println("downloaded");
			}
			// rename file
	        targetFile.renameTo(new File(String.format("%s.mp4", e.getDownloadPath())));
		} catch (IOException | InterruptedException e1) {
			System.out.printf("Could not download serie %s season %s episode %s%n", ((Show)(e.getFather().getFather())).getName(), e.getFather().getID(), e.getID());
			e1.printStackTrace();
		} finally {
			driver.close();	
		}
	}
}
