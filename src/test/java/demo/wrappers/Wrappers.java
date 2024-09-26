package demo.wrappers;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class Wrappers {
    /*
     * Write your selenium wrappers here
     */
    


    public void click(ChromeDriver driver, By locator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    public boolean isElementPresent(ChromeDriver driver, By locator)
    {
        return !driver.findElements(locator).isEmpty();
    }

    public class TeamData {
        String year;
        String team;
        long epochTime;
        double winPercentage;

        public TeamData(String year, String team, long epochTime, double winPercentage) {
            this.year = year;
            this.team = team;
            this.epochTime = epochTime;
            this.winPercentage = winPercentage;
        }
    }

}
