package pages.salesforce.app.DaDaDocs.full_app.templates.tabs.x_templates;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.DaDaDocs.full_app.templates.components.FooterNavigation;
import pages.salesforce.app.SalesAppBasePage;
import utils.Logger;

import static core.check.Check.checkTrue;

public class RelatedChildPage extends RelatedObjectsPage implements FooterNavigation {

    public RelatedChildPage(WebDriver driver) {
        super(driver);
        vMappingLoader = By.xpath("//*[contains(@class, 'loader__')]");
        heading = By.xpath("//*[contains(@class, 'title__')]");
        descriptionText = "Select objects you'd like to use in your template";
        description = By.xpath("//*[contains(@class, 'text__')]/p");
        headingText = "Select related сhild objects";
        pageName = "Related child";
    }

    @Override
    public void isOpened() {
        skipMappingLoader();
        checkTrue(isElementPresentAndDisplayed(heading, 5), "Heading is not displayed");
        checkTexts();
        checkTrue(isElementPresentAndDisplayed(footer, 5), "Footer is not displayed");
    }

    @Override
    @Step
    public <T extends SalesAppBasePage> T navigateToPreviousTab() {
        Logger.info("Navigate to previous tab");
        checkTrue(isElementPresentAndDisplayed(btnPreviousDefaultTab, 5), "Previous tab button is not displayed");
        click(btnPreviousDefaultTab);
        RelatedParentPage relatedParentPage = new RelatedParentPage(driver);
        relatedParentPage.isOpened();
        return (T) relatedParentPage;
    }

    @Override
    @Step
    public <T extends SalesAppBasePage> T navigateToNextTab() {
        Logger.info("Navigate to next tab");
        checkTrue(isElementPresentAndDisplayed(btnNextTab, 5), "Next tab button is not displayed");
        click(btnNextTab);
        CopyTagsPage copyTagsPage = new CopyTagsPage(driver);
        copyTagsPage.isOpened();
        return (T) copyTagsPage;
    }
}
