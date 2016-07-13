package sk.eea.arttag;

import org.apache.tomcat.jni.Directory;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class BrowserTest {

    private static final String GAME_URL = "http://localhost:8080";

    private static final String OUTPUT_FOLDER = "chromedriver/";

    private static final String CHROME_DRIVER_PATH = "/Users/laszlonagy/Downloads/chromedriver";

    private static final Map<String, String> CREDENTIALS = new HashMap<>();

    static {
        CREDENTIALS.put("admin", "admin");
        CREDENTIALS.put("user1", "admin");
        CREDENTIALS.put("user2", "admin");
    }

    private WebDriver driver;

    private Map<String, String> usernameHandleMap = new HashMap<>();

    public void unzip(String zipFile, String outputFolder)
    {
        byte[] buffer = new byte[1024];

        try
        {

            //create output directory is not exists
            File folder = new File(OUTPUT_FOLDER);
            if(!folder.exists())
            {
                folder.mkdir();
            }

            //get the zip file content
            ZipInputStream zis =
                new ZipInputStream(new FileInputStream(zipFile));
            //get the zipped file list entry
            ZipEntry ze = zis.getNextEntry();

            while(ze!=null)
            {

                String fileName = ze.getName();
                File newFile = new File(outputFolder + File.separator + fileName);

                System.out.println("file unzip : "+ newFile.getAbsoluteFile());

                //create all non exists folders
                //else you will hit FileNotFoundException for compressed folder
                new File(newFile.getParent()).mkdirs();

                FileOutputStream fos = new FileOutputStream(newFile);

                int len;
                while ((len = zis.read(buffer)) > 0)
                {
                    fos.write(buffer, 0, len);
                }

                newFile.setExecutable(true);
                fos.close();
                ze = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
            System.out.println("Done");

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private void downloadFileHTTP(String url)
    {
        URL website = null;
        try {
            website = new URL(url);
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            FileOutputStream fos = new FileOutputStream("chromedriver.zip");
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            unzip("chromedriver.zip", OUTPUT_FOLDER);

            File file = new File("chromedriver.zip");
            file.delete();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Before
    public void before() {

        String osName = System.getProperty("os.name").toLowerCase();
        if (Files.notExists(Paths.get(OUTPUT_FOLDER)))
        {
            switch (osName)
            {
                case "linux":
                {
                    downloadFileHTTP("http://chromedriver.storage.googleapis.com/2.22/chromedriver_linux64.zip");
                    break;
                }
                case "windows":
                {
                    downloadFileHTTP("http://chromedriver.storage.googleapis.com/2.22/chromedriver_win32.zip");
                    break;
                }
                case "mac os x":
                case "macos":
                case "darwin":
                {
                    downloadFileHTTP("http://chromedriver.storage.googleapis.com/2.22/chromedriver_mac32.zip");
                    break;
                }
                default:
                {
                    //bad luck, you should use a current operating system. Don't be a dinosaur
                    break;
                }
            }
        }
        //System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_PATH);

        String chromedriverpath = OUTPUT_FOLDER + "chromedriver";
        System.setProperty("webdriver.chrome.driver", chromedriverpath);
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
    }

    @Test
    public void test() throws InterruptedException {
        driver.get(GAME_URL);
        driver.manage().window().maximize();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                System.out.println("Closing driver...");
                try {
                    driver.quit();
                } catch (Exception ex) {
                    //there is nothing we can do
                }
            }
        });

        for (int i = 0; i < CREDENTIALS.size() - 1; i++) {
            ((JavascriptExecutor) driver).executeScript("window.open('', '_blank', 'resizable=yes, scrollbars=yes');");
            driver.manage().window().maximize();
        }

        final Iterator iterator = CREDENTIALS.keySet().iterator();
        for (String handle : driver.getWindowHandles()) {
            driver.switchTo().window(handle);
            driver.get(GAME_URL);
            if(iterator.hasNext()) {
                final String username = (String) iterator.next();
                final String password = CREDENTIALS.get(username);

                driver.findElement(By.id("signinbutton")).click();
                driver.findElement(By.id("username")).sendKeys(username);
                final WebElement element = driver.findElement(By.id("password"));
                element.clear();
                element.sendKeys(password);
                element.submit();

                //driver.get("http://localhost:8080/join_game/1");
                final WebElement joinelement = driver.findElement(By.linkText("click to join game"));
                joinelement.click();
                usernameHandleMap.put(username, driver.getWindowHandle());
            }
        }

        while (true) {
            Thread.sleep(1000);
        }
    }
}
