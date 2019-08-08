package pages.salesforce.app.airSlate_app.custom_button;

import com.codeborne.selenide.Condition;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.SalesAppBasePage;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.switchTo;
import static data.salesforce.SalesforceTestData.ASAppPageTexts.ALMOST_DONE;
import static pages.salesforce.enums.ButtonNames.BACK_TO_LIST_VIEW;

public class AlmostDonePage extends SalesAppBasePage {

    private By title = By.xpath("//*[contains(@class, 'title__') and text() ='Almost Done!']");

    public AlmostDonePage(WebDriver driver) {
        super(driver);
        iframe = By.xpath("//iframe[@title = 'airSlate App']");
        loader = By.xpath("//*[contains(@class, 'loaderWrapper__')]");
    }

    @Override
    public void isOpened() {
        By subtitle = By.xpath("//*[contains(@class, 'subtitle__') and text()='" + ALMOST_DONE + "']");
        if ($(iframe).is(Condition.exist)) {
            switchTo().frame($(iframe));
        }
        $(loader).waitUntil(Condition.hidden, 30000);
        $(title).waitUntil(Condition.visible, 30000);
        $(subtitle).waitUntil(Condition.visible, 3000);
    }

    @Step
    public <T extends SalesAppBasePage> T backToListView(Class<T> expectedPage) {
        By btnBackToListView = By.xpath("//button[.='" + BACK_TO_LIST_VIEW.getName() + "']");
        System.out.println(driver.getPageSource());
        $(btnBackToListView)
                .shouldBe(Condition.visible)
                .click();
        return initExpectedPage(expectedPage);
    }

    @Step
    public boolean isTitleDisplayed(int seconds) {
        return isElementDisplayed(title, seconds);
    }
}
