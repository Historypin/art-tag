package sk.eea.arttag;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class BrowserTest {

    private static final String GAME_URL = "http://localhost:8080";

    private static final String CHROME_DRIVER_PATH = "/home/ware/Downloads/chromedriver";

    private static final Map<String, String> CREDENTIALS = new HashMap<>();

    static {
        CREDENTIALS.put("admin", "admin");
        CREDENTIALS.put("user1", "admin");
        CREDENTIALS.put("user2", "admin");
    }

    private WebDriver driver;

    private Map<String, String> usernameHandleMap = new HashMap<>();

    @Before
    public void before() {
        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_PATH);
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

                driver.get("http://localhost:8080/join_game/1");
                usernameHandleMap.put(username, driver.getWindowHandle());
            }
        }

        while (true) {
            Thread.sleep(1000);
        }
    }
}
