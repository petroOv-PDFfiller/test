package tests;

import core.TestBase;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;
import utils.TimeMan;

import static org.testng.Assert.assertTrue;

/**
 * Created by Vladyslav on 02.11.2015.
 */
public class Sample_Test extends TestBase {

    @Test
    public void test() {
        WebDriver driver = getDriver();
        getUrl("http://google.com");
        TimeMan.sleep(5);
        assertTrue(driver.getCurrentUrl().contains("google"));
    }
}
