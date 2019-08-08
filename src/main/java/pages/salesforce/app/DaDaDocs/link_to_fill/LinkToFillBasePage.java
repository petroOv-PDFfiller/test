package pages.salesforce.app.DaDaDocs.link_to_fill;

import data.salesforce.SalesforceTestData;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.SalesAppBasePage;

import static core.check.Check.checkTrue;

public abstract class LinkToFillBasePage extends SalesAppBasePage {

    public LinkToFillBasePage(WebDriver driver) {
        super(driver);
    }

    public <T extends LinkToFillBasePage> T selectTab(String tabName) {
        By tabLocator = By.xpath("//li[descendant::span[text()='" + tabName + "']]");
        checkTrue(isElementDisplayed(tabLocator, 5), tabName + " tab is not presented");
        click(tabLocator);
        checkTrue(isElementContainsStringInClass(tabLocator, "active"), tabName + " tab wasn't selected");

        LinkToFillBasePage page = null;
        switch (tabName) {
            case SalesforceTestData.LinkToFillTabs.CUSTOMIZE: {
                page = new LinkToFillCustomizeTab(driver);
                break;
            }
            case SalesforceTestData.LinkToFillTabs.SELECT_OPTIONS: {
                page = new LinkToFillSelectOptionsTab(driver);
                break;
            }
            case SalesforceTestData.LinkToFillTabs.ACTIVATE: {
                page = new LinkToFillActivateTab(driver);
                break;
            }
        }

        page.isOpened();

        return (T) page;
    }
}
