package pages.salesforce.app.DaDaDocs.full_app.V3;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.sf_objects.SalesforceBasePopUp;

public class DaDaDocsV3BasePopUp extends SalesforceBasePopUp {

    protected By popUpHeader = By.xpath("//div[contains(@class, 'popup__header__')]//*[contains(@class, 'popup__title__')]");

    public DaDaDocsV3BasePopUp(WebDriver driver) {
        super(driver);
        popUpBody = By.xpath("//div[contains(@class, 'popup__container__')]");
        loader = By.xpath("//*[contains(@class,'loader__')]");
    }
}
