package pages.salesforce.app.airSlate_app.custom_button;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.airslate.AirSlateBasePage;
import pages.airslate.flow_creator.ListSlatesPage;

import static core.check.Check.checkTrue;

public class AirSlateSendSlatePage extends AirSlateBasePage {

    public AirSlateSendSlatePage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        By title = By.xpath("//*[text()='Send Slate']");
        checkTrue(isElementPresent(title, 10), "SendSlate page title is not displayed");
    }

    @Step
    public ListSlatesPage back() {
        By btnBack = By.xpath("//button[.='Back']");
        return goToPage(ListSlatesPage.class, btnBack);
    }
}
