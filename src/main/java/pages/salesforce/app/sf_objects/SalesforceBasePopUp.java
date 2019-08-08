package pages.salesforce.app.sf_objects;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.enums.PopUpActions;
import utils.Logger;

public abstract class SalesforceBasePopUp extends SalesAppBasePage {

    protected By popUpBody = By.xpath("//div[contains(@class, 'modal-container')]");
    protected By btnClosePopUp = By.xpath("//div[contains(@class, 'popup__header__')]//button");

    public SalesforceBasePopUp(WebDriver driver) {
        super(driver);
    }

    @Step
    public <T extends SalesAppBasePage> T closePopUp(Class<T> expectedPage) {
        Logger.info("Close popUp");
        checkIsElementDisplayed(btnClosePopUp, 2, "Close button in " + getClass().getSimpleName());
        click(btnClosePopUp);
        return initExpectedPage(expectedPage);
    }

    @Step
    protected <T extends SalesAppBasePage> T choosePopUpAction(PopUpActions action, Class<T> expectedPage) {
        By btnAction = By.xpath("//button[.='" + action.getActionName() + "']");

        checkIsElementDisplayed(btnAction, 5, action.getActionName() + " button");
        click(btnAction);
        return initExpectedPage(expectedPage);
    }
}
