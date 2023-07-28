package VoiceTypingAutomation;

import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.swing.filechooser.FileSystemView;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import CommonAPI.GoogleDriverHandler;
import CommonAPI.YoutubeCommon;

public class AutoVoiceTyping {
	public static Drive driveService;

	public static String youtubeImbedCode1 = "<body><div id=\"player\"></div>   <script>var tag = document.createElement('script');tag.src = \"https://www.youtube.com/iframe_api\";var firstScriptTag = document.getElementsByTagName('script')[0];firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);var player;function onYouTubeIframeAPIReady() {player = new YT.Player('player', {height: '390',width: '640',videoId: '";
	public static String youtubeImbedCode2 = "',events: {'onReady': onPlayerReady,'onStateChange': onPlayerStateChange}});}function onPlayerReady(event) {event.target.playVideo();}var done = false;function onPlayerStateChange(event) {}function stopVideo() {player.stopVideo();}</script>";
//desktop
	public final static String defaultPath = FileSystemView.getFileSystemView().getHomeDirectory().getPath();

//	public static void main(String[] args) {
//		System.out.println(defaultPath);
//	}
	public static void autoVoiceTyping(String id, videoInfo videoInfo)
			throws InterruptedException, IOException, GeneralSecurityException {
		try {
			System.setProperty("webdriver.chrome.driver", defaultPath + "\\chromedriver.exe");
			Robot robot = new Robot();
			
			// ���3�銵outube video
			WebDriver w3schoolDriver = new ChromeDriver();
			w3schoolDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			w3schoolDriver.get("https://www.w3schools.com/html/tryit.asp?filename=tryhtml_default");
			Thread.sleep(500);
			WebElement cc = w3schoolDriver.findElement(By.className("CodeMirror-lines"));
			Thread.sleep(500);
			cc.click();
			String[] arr = new String[3];
			arr[0] = AutoVoiceTyping.youtubeImbedCode1;
			arr[1] = id;
			arr[2] = AutoVoiceTyping.youtubeImbedCode2;

			String s = arr[0] + arr[1] + arr[2];

			StringSelection stringSelection = new StringSelection(s);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(stringSelection, null);

			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_CONTROL);
			robot.keyRelease(KeyEvent.VK_V);
			System.out.println("++++++VK");
			Map<String, Object> prefs = new HashMap<String, Object>();

			// add key and value to map as follow to switch off browser notification
			// Pass the argument 1 to allow and 2 to block
			prefs.put("profile.default_content_setting_values.media_stream_mic", 1);

			// Create an instance of ChromeOptions
			ChromeOptions options = new ChromeOptions();

			// set ExperimentalOption - prefs
			options.setExperimentalOption("prefs", prefs);

			WebDriver gooDocPageDriver = new ChromeDriver(options);

			try {

				// create a google doc

				videoInfo.setId(id);

				if (id != null) {
					String DocName = YoutubeCommon.getTitile(id);
					driveService = GoogleDriverHandler.getService();

					// �撱榫oogle doc
					File gooDoc = GoogleDriverHandler.createDoc(driveService, DocName);
					String Docid = gooDoc.getId();
					videoInfo.setDocId(Docid);
					// ��甈� : ����犖��INK����蝺�
					GoogleDriverHandler.createPermissionForFile(driveService, Docid, "anyone", "writer");

					// ���oogle doc

					gooDocPageDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
					String videoTextLink = "https://docs.google.com/document/d/" + Docid + "/edit";

					try {
						gooDocPageDriver.get(videoTextLink);
					} catch (Exception e) {
						System.out.println("!!!!!!!!!!!!!!!!");
						e.printStackTrace();
						new WebDriverWait(gooDocPageDriver, 100).until(dr -> ((JavascriptExecutor) dr)
								.executeScript("return document.readyState").equals("complete"));
						WebElement micElement = gooDocPageDriver.findElement(By.className("docs-mic-control"));
						micElement.click();
					}

					videoInfo.setVideoTextLink(videoTextLink);

					robot.keyPress(KeyEvent.VK_CONTROL);
					robot.keyPress(KeyEvent.VK_SHIFT);
					robot.keyPress(KeyEvent.VK_S);
					robot.keyRelease(KeyEvent.VK_CONTROL);
					robot.keyRelease(KeyEvent.VK_SHIFT);
					robot.keyRelease(KeyEvent.VK_S);
					new WebDriverWait(gooDocPageDriver, 100).until(dr -> ((JavascriptExecutor) dr)
							.executeScript("return document.readyState").equals("complete"));
					WebElement micElement = gooDocPageDriver.findElement(By.className("docs-mic-control"));
//				micElement.click();

					Thread.sleep(500);
					JavascriptExecutor js = (JavascriptExecutor) gooDocPageDriver;
					js.executeScript(
							"document.getElementsByClassName('goog-flat-menu-button-caption')[0].innerHTML=\"English (US)\";");
					Thread.sleep(500);

//				new WebDriverWait(gooDocPageDriver, 20).until(
//						dr -> ((JavascriptExecutor) dr).executeScript("return document.readyState").equals("complete"));
//				micElement.click();
//				Thread.sleep(500);
					WebElement runB = w3schoolDriver.findElement(By.xpath("/html/body/div[5]/div/button"));
					Thread.sleep(500);
//					System.out.println("RRRRRRRRRRRRB");
					String S_time = YoutubeCommon.getDuration(id);
//					System.out.println(S_time);
//					System.out.println(YoutubeCommon.parseISO8601StringToSecond(S_time));
					int videoDurationInMs = YoutubeCommon.parseISO8601StringToSecond(S_time);
					
//					
					runB.click();

					int loopTime = videoDurationInMs * 2;
//					System.out.println(loopTime);
					for (int k = 0; k < loopTime + 8; k++) {
						try {
							// sending the actual Thread of execution to sleep X milliseconds
							Thread.sleep(500);
							// System.out.println(k);
						} catch (InterruptedException ie) {
						}
//					WebElement checkRecordStatus = gooDocPageDriver.findElement(By.className("docs-mic-control-recording"));
						String isMicon = micElement.getAttribute("aria-pressed");
						if (isMicon.equalsIgnoreCase("false")) {

							micElement.click();

						}
					}

					w3schoolDriver.close();
					gooDocPageDriver.close();
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println(System.lineSeparator() + e);
				w3schoolDriver.close();
				gooDocPageDriver.close();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(System.lineSeparator() + e);

		}

	}
}
