package pages.salesforce.app.airSlate_app.custom_button;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.SalesAppBasePage;

import static core.check.Check.checkEquals;
import static core.check.Check.checkTrue;
import static data.salesforce.SalesforceTestData.ASAppPageTexts.OOPS;
import static pages.salesforce.enums.ButtonNames.BACK_TO_LIST_VIEW;

public class RunningFlowListViewErrorPage extends SalesAppBasePage {

    private By title = By.xpath("//*[contains(@class, 'title__') and text()='Oops!']");

    public RunningFlowListViewErrorPage(WebDriver driver) {
        super(driver);
        loader = By.xpath("//*[contains(@class, 'loaderWrapper__')]");
    }

    @Override
    public void isOpened() {
        By subtitle = By.xpath("//*[contains(@class, 'subtitle__')]");
        if (isElementPresent(iframe)) {
            checkIsElementDisplayed(iframe, 10, "ListView error page frame");
            checkTrue(switchToFrame(iframe, 15), "Cannot switch to ListView error page frame");
        }

        checkIsElementDisplayed(title, 20, "ListView error page frame content");
        checkEquals(getAttribute(subtitle, "textContent"), OOPS, "Incorrect subtitle text");
    }

    @Step
    public <T extends SalesAppBasePage> T backToListView(Class<T> expectedPage) {
        By btnBackToListView = By.xpath("//button[.='" + BACK_TO_LIST_VIEW.getName() + "']");
        checkIsElementDisplayed(btnBackToListView, 4, "Back to list view button");
        click(btnBackToListView);
        return initExpectedPage(expectedPage);
    }
}
