package pages.salesforce.app.sf_objects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static core.check.Check.checkTrue;

public class CustomObjectDefaultConcretePage extends ConcreteRecordPage {

    public CustomObjectDefaultConcretePage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        By tableContent = By.xpath("//*[@data-component-id='force_highlightsPanel']");
        checkTrue(waitUntilPageLoaded(), "Custom object page is not loaded");
        checkTrue(isElementPresent(tableContent, 16), "Record page content is not loaded");
    }
}