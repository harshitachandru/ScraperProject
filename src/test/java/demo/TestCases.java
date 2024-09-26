package demo;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
// import io.github.bonigarcia.wdm.WebDriverManager;
import demo.wrappers.Wrappers;

public class TestCases extends Wrappers {
    ChromeDriver driver;

    /*
     * TODO: Write your tests here with testng @Test annotation.
     * Follow `testCase01` `testCase02`... format or what is provided in
     * instructions
     */

    /*
     * Do not change the provided methods unless necessary, they will help in
     * automation and assessment
     */
    @BeforeTest
    public void startBrowser() {
        System.setProperty("java.util.logging.config.file", "logging.properties");

        // NOT NEEDED FOR SELENIUM MANAGER
        // WebDriverManager.chromedriver().timeout(30).setup();

        ChromeOptions options = new ChromeOptions();
        LoggingPreferences logs = new LoggingPreferences();

        logs.enable(LogType.BROWSER, Level.ALL);
        logs.enable(LogType.DRIVER, Level.ALL);
        options.setCapability("goog:loggingPrefs", logs);
        options.addArguments("--remote-allow-origins=*");

        System.setProperty(ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY, "build/chromedriver.log");

        driver = new ChromeDriver(options);

        driver.manage().window().maximize();
    }

    @AfterTest
    public void endTest() {
        driver.close();
        driver.quit();

    }

    @Test
    public void testCase01() throws InterruptedException, StreamWriteException, DatabindException, IOException {
        System.out.println("TestCase 01 Starts");
        driver.get("https://www.scrapethissite.com/pages/");

        Thread.sleep(2000);
        System.out.println("Wait 1");

        WebElement hockeyLink = driver
                .findElement(By.xpath("//a[contains(text(),'Hockey Teams: Forms, Searching and Pagination')]"));
        hockeyLink.click();

        Thread.sleep(2000);
        System.out.println("Wait 2");

        WebElement pageone = driver.findElement(By.xpath("//a[@href=\"/pages/forms/?page_num=1\"]"));
        pageone.click();

        Thread.sleep(2000);
        System.out.println("Wait 3");

        ArrayList<HashMap<String, Object>> tabledata = new ArrayList<>();

        for (int i = 0; i <= 4; i++) {
            List<WebElement> rows = driver
                    .findElements(By.xpath("//table[contains(@class,'table')]//tr[contains(@class,'team')]"));

            for (WebElement row : rows) {
                String teamName = row.findElement(By.xpath(".//td[contains(@class,'name')]")).getText();
                String year = row.findElement(By.xpath(".//td[contains(@class,'year')]")).getText();
                String winPercentagestr = row
                        .findElement(By.xpath(
                                ".//td[contains(@class,'pct text-success') or contains(@class,'pct text-danger')]"))
                        .getText();

                double winPercentage = Double.parseDouble(winPercentagestr);

                if (winPercentage < 0.40) {
                    HashMap<String, Object> team = new HashMap<>();
                    team.put("Epoch Time", Instant.now().getEpochSecond());
                    team.put("Team", teamName);
                    team.put("Year", year);
                    team.put("winPercentagestr", winPercentage);
                    tabledata.add(team);

                }
            }

            String nextbutton = "//ul[contains(@class, 'pagination')]//li/a[@aria-label='Next']";
            if (isElementPresent(driver, By.xpath(nextbutton))) {
                click(driver, By.xpath(nextbutton));
            } else {
                break;
            }

            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File("hockey-team-data.json"), tabledata);
        }
        System.out.println("TestCase01 Ends");
    }

    @Test
    public void testCase02() throws InterruptedException, StreamWriteException, DatabindException, IOException {
        System.out.println("TestCase 02 Starts");
        driver.get("https://www.scrapethissite.com/pages/");

        Thread.sleep(2000);
        System.out.println("Wait 1");

        WebElement oscarlink = driver
                .findElement(By.xpath("//a[contains(text(),'Oscar Winning Films: AJAX and Javascript')]"));
        oscarlink.click();

        Thread.sleep(2000);
        System.out.println("Wait 2");

        ArrayList<HashMap<String, Object>> ocsardata = new ArrayList<>();
        SoftAssert sa = new SoftAssert();

        List<WebElement> yearLinks = driver
                .findElements(By.xpath("//div[contains(@class, 'col-md-12 text-center')]//a[@class='year-link']"));

        for (WebElement yearlink : yearLinks) {
            String year = yearlink.getText();
            yearlink.click();
            System.out.println("The year clicked is: " + year);

            Thread.sleep(2000);
            System.out.println("Wait 3");

            List<WebElement> rows = driver
                    .findElements(By.xpath("//table[@class='table']//tbody[@id='table-body']//tr[@class='film']"));
            int limit = Math.min(rows.size(), 5);

            for (int i = 0; i < limit; i++) {
                WebElement row = rows.get(i);

                String title = row.findElement(By.xpath(".//td[@class='film-title']")).getText();
                String nomination = row.findElement(By.xpath(".//td[@class='film-nominations']")).getText();
                String awards = row.findElement(By.xpath(".//td[@class='film-awards']")).getText();

                boolean isWinner = row
                        .findElements(
                                By.xpath(".//td[@class='film-best-picture']/i[contains(@class, 'glyphicon-flag')]"))
                        .size() > 0;

                HashMap<String, Object> movies = new HashMap<>();

                movies.put("Epoch time", Instant.now().getEpochSecond());
                movies.put("Year", year);
                movies.put("Movie Name", title);
                movies.put("Nomaination", nomination);
                movies.put("Awards", awards);
                movies.put("IsWinner", isWinner);

                ocsardata.add(movies);
            }

            driver.navigate().back();
            Thread.sleep(2000);
            System.out.println("Wait 4");

            ObjectMapper mapper = new ObjectMapper();
            File outputFile = new File("output/oscar-winner-data.json");
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File("oscar-winner-data.json"), ocsardata);

            sa.assertTrue(outputFile.exists(), "Output file does not exist.");
            sa.assertTrue(outputFile.length() > 0, "Output file is empty.");
        }

        System.out.println("TestCase02 Ends");
    }
}