package pages;

import core.PageBase;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Created by Vladyslav on 26.06.2015.
 */
public class ChromeCookiesPage extends PageBase {

    private String cookies_URL = "chrome://settings/cookies";
    private By cookies_frame = By.xpath("//iframe[@src='chrome://settings-frame/cookies']");
    private By remove_button = By.xpath("//*[@class='remove-all-cookies-button']");

    public ChromeCookiesPage(WebDriver driver) {
        super(driver);
    }

    public void deleteCookies() {
        open(cookies_URL);
        driver.switchTo().frame(driver.findElement(cookies_frame));
        driver.findElement(remove_button).click();
    }
}
