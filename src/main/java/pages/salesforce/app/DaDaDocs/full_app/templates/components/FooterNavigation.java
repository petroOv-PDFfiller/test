package pages.salesforce.app.DaDaDocs.full_app.templates.components;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import pages.salesforce.app.SalesAppBasePage;

public interface FooterNavigation {

    By footer = By.xpath("//footer");
    By btnPreviousTab = By.xpath("//footer//button[contains(@class, 'btn__secondary__')]");
    By btnPreviousDefaultTab = By.xpath("//footer//button[contains(@class, 'btn__default__')]");
    By btnNextTab = By.xpath("//footer//button[contains(@class, 'btn__accent__')]");

    @Step
    <T extends SalesAppBasePage> T navigateToPreviousTab();

    @Step
    <T extends SalesAppBasePage> T navigateToNextTab();

}
