package whatsappbot;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class App {
    private static WebDriver driver;


    public static void startBot(File f, String message, boolean useCopyPaste) throws FileNotFoundException, InterruptedException {
	setUpDriver();
	Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
	Clipboard systemClipboard = defaultToolkit.getSystemClipboard();
	if (useCopyPaste)
	    systemClipboard.setContents(new StringSelection(message), null);

	Scanner sc = new Scanner(f);
	ArrayList<String> nums = new ArrayList<String>();
	while (sc.hasNextLine())
	    nums.add(sc.nextLine());
	sc.close();
	driver = new ChromeDriver();
	driver.navigate().to("https://web.whatsapp.com/");
	waitForSignIn();
	for (String string : nums) {

	    string = fixNumber(string);
	    System.out.println(string);
	    driver.navigate().to(String.format("https://api.whatsapp.com/send?phone=%s", string));
	    WebElement messageBtn = getElement(By.cssSelector("#action-button"));
	    messageBtn.click();
	    waitForSignIn();
	    Thread.sleep(500);
	    WebElement element2 = getElement(By.cssSelector("._2S1VP.copyable-text.selectable-text"));
	    write(element2, message, systemClipboard);
	    // msg-time

	    while (getElement(By.cssSelector("body")).getAttribute("innerHTML").contains("msg-time")) {
		Thread.sleep(100);
	    }
	    Thread.sleep(500);
	}
	driver.close();
	driver.quit();
	System.exit(0);
    }

    private static void setUpDriver() {
	String os = System.getProperty("os.name").toLowerCase();
	String pathToChromeDriver = "";

	if (os.contains("win")) {
	    pathToChromeDriver = "drivers/windowsChrome.exe";
	} else if (os.contains("osx")) {
	    pathToChromeDriver = "drivers/macChrome";
	} else{
	    pathToChromeDriver = "drivers/linuxChrome";
	}
	System.setProperty("webdriver.chrome.driver", pathToChromeDriver);

    }

    final static String s = Keys.chord(Keys.SHIFT, "\n");
    final static String paste = System.getProperty("os.name").toLowerCase().contains("mac") ? Keys.chord(Keys.COMMAND, "v") : Keys.chord(Keys.CONTROL, "v");

    private static void write(WebElement element2, String message, Clipboard cb) {

	try {
	    if (!wasClipBoardChanged(cb, message)) {
		element2.sendKeys(paste);
		element2.sendKeys("\n");
		return;
	    }
	} catch (Exception e) {
	}
	String[] split = message.split("\n");
	for (String string : split) {
	    element2.sendKeys(string);
	    element2.sendKeys(s);
	}
	element2.sendKeys("\n");

    }

    private static void waitForSignIn() {
	while (getElement(By.cssSelector("._3ZW2E")) == null)
	    try {
		Thread.sleep(500);
	    } catch (InterruptedException e) {
	    }
    }

    private static WebElement getElement(By by) {
	try {
	    return driver.findElement(by);
	} catch (NotFoundException e) {
	}
	return null;
    }

    private static String fixNumber(String s) {
	s = s.replace(" ", "");
	while (s.startsWith("0") || s.startsWith("+"))
	    s = s.substring(1);
	if (s.startsWith("966"))
	    return s;
	else
	    return "966" + s;
    }

    private static boolean wasClipBoardChanged(Clipboard cb, String message) throws Exception {
	DataFlavor dataFlavor = DataFlavor.stringFlavor;
	if (cb.isDataFlavorAvailable(dataFlavor)) {
	    Object text = cb.getData(dataFlavor);
	    System.out.println(text);
	    boolean equals = ((String) text).equals(message);
	    System.out.println(equals);
	    return !equals;
	}

	return false;
    }
}
