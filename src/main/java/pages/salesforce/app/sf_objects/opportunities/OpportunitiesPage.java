package pages.salesforce.app.sf_objects.opportunities;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.sf_objects.SalesforceObjectPage;
import pages.salesforce.enums.SalesTab;
import utils.Logger;

import static core.check.Check.checkTrue;

public class OpportunitiesPage extends SalesforceObjectPage {

    public OpportunitiesPage(WebDriver driver) {
        super(driver);
        pageName = SalesTab.OPPORTUNITIES.getNameNewLayout();
    }

    @Step
    public OpportunitiesConcretePage addNewOpportunity(String name, String stage) {
        By btnAddNew = By.xpath("//div[@title = 'New']");

        Logger.info("Add new opportunity: name - " + name + " stage - " + stage);
        checkTrue(isElementPresentAndDisplayed(btnAddNew, 10), "Add new opportunity btutton is not displayed");
        click(btnAddNew);
        AddNewOpportunityPopUp newOpportunityPopUp = new AddNewOpportunityPopUp(driver);
        newOpportunityPopUp.isOpened();
        newOpportunityPopUp.setOpportunityName(name).setTodayAsCloseDate().setStage(stage);
        return newOpportunityPopUp.saveOpportunity();
    }
}
