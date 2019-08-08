package pages.servicenow.app.objects;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import pages.servicenow.ServiceNowBasePage;

import static core.check.Check.checkTrue;

/**
 * Created by horobets on Aug 01, 2019
 */
public abstract class ObjectsListPage extends ServiceNowBasePage {
    protected ServiceNowObject objectType;
    private By listWrapScrollBy = By.cssSelector(".list_wrap_n_scroll");
    private String listItemByNameXpathFormat = "//tr[contains(@class, 'list_row')]/td/a[text()='%s']";
    private String listItemByIndexXpathFormat = "(//tr[contains(@class, 'list_row')][%d]/td/a)[2]";

    public ObjectsListPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        super.isOpened();
        switchToPageContentFrame();
        checkTrue(isElementDisplayed(listWrapScrollBy), String.format("%s not found", listWrapScrollBy.toString()));
        switchToDefaultContent();
    }


    @Step("Open Object by number: {0}")
    public <T extends ObjectPage> T openObject(int listItemNumber) {

        switchToPageContentFrame();

        By listItemBy = By.xpath(String.format(listItemByIndexXpathFormat, listItemNumber));

        checkTrue(isElementPresent(listItemBy, 4), String.format("Object #%d is not present", listItemNumber));
        checkTrue(isElementDisplayed(listItemBy, 4), String.format("Object #%d is not displayed", listItemNumber));

        click(listItemBy);

        ObjectPage objectPage = PageFactory.initElements(driver, objectType.getPageClass());

        objectPage.isOpened();
        return (T) objectPage;
    }
}
