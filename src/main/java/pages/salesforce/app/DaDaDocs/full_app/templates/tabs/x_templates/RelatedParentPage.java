package pages.salesforce.app.DaDaDocs.full_app.templates.tabs.x_templates;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.DaDaDocs.full_app.templates.components.FooterNavigation;
import pages.salesforce.app.SalesAppBasePage;
import utils.Logger;

import static core.check.Check.checkTrue;

public class RelatedParentPage extends RelatedObjectsPage implements FooterNavigation {

    public RelatedParentPage(WebDriver driver) {
        super(driver);
        vMappingLoader = By.xpath("//*[contains(@class, 'loader__')]");
        heading = By.xpath("//*[contains(@class, 'title__')]");
        descriptionText = "Select objects you'd like to use in your template";
        description = By.xpath("//*[contains(@class, 'text__')]/p");
        headingText = "Select related parent objects";
        pageName = "Related parent";
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
        XTemplateInfoPage templateInfoPage = new XTemplateInfoPage(driver);
        templateInfoPage.isOpened();
        return (T) templateInfoPage;
    }

    @Override
    @Step
    public <T extends SalesAppBasePage> T navigateToNextTab() {
        Logger.info("Navigate to next tab");
        checkTrue(isElementPresentAndDisplayed(btnNextTab, 5), "Next tab button is not displayed");
        click(btnNextTab);
        RelatedChildPage relatedChildPage = new RelatedChildPage(driver);
        relatedChildPage.isOpened();
        return (T) relatedChildPage;
    }
}
