package tests.salesforce.airSlate_app;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import listeners.WebTestListener;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.app.airSlate_app.admin_tools.ASAppAdminToolsPage;
import pages.salesforce.app.airSlate_app.admin_tools.popups.DeleteCustomButtonPopUp;
import pages.salesforce.app.airSlate_app.admin_tools.tabs.CustomButtonTab;
import pages.salesforce.app.airSlate_app.admin_tools.wizards.LeaveWizardPopUp;
import pages.salesforce.app.airSlate_app.admin_tools.wizards.custom_button.ActionSettingsWizardPage;
import pages.salesforce.app.airSlate_app.admin_tools.wizards.custom_button.CustomButtonInfoWizardPage;
import pages.salesforce.app.airSlate_app.admin_tools.wizards.custom_button.SalesforceLayoutsAndListsWizardPage;
import pages.salesforce.app.sf_objects.accounts.AccountConcretePage;
import pages.salesforce.app.sf_objects.opportunities.OpportunitiesConcretePage;
import tests.salesforce.SalesforceAirSlateBaseTest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static api.salesforce.entities.SalesforceObject.ACCOUNT;
import static api.salesforce.entities.SalesforceObject.OPPORTUNITY;
import static data.salesforce.SalesforceTestData.ASAppPopUpTexts.DELETING_CUSTOM_BUTTON;
import static data.salesforce.SalesforceTestData.NotificationMessages.*;
import static pages.salesforce.enums.admin_tools.ASAppAdminToolTabs.CUSTOM_BUTTONS;
import static utils.StringMan.getRandomString;

@Feature("airSlate app: custom button")
@Listeners(WebTestListener.class)
public class ASAppCustomButtonTest extends SalesforceAirSlateBaseTest {

    private String adminToolURL;
    private ASAppAdminToolsPage adminToolsPage;
    private String accountId;
    private String flowName;
    private String buttonDescription = "buttonName" + getRandomString(245);
    private String buttonLabel = "buttonLabel" + getRandomString(229);
    private String secondFlowName;
    private String opportunityId;
    private String newButtonName = "newButtonName" + getRandomString(30);


    @BeforeTest
    public void setUp() throws IOException, URISyntaxException {
        SalesAppBasePage salesAppBasePage = loginWithDefaultCredentials();
        accountId = createAccountRecord();
        opportunityId = createOpportunityRecord();

        configurateSalesforceOrg();
        flowName = createFlowWithDocument(fileToUpload).name;
        secondFlowName = createFlowWithDocument(fileToUpload).name;
        createFlowWithDocument(fileToUpload);
        adminToolsPage = getAdminToolsPage(salesAppBasePage);
        adminToolURL = getDriver().getCurrentUrl();
    }

    @BeforeMethod
    public void getAdminToolPage() {
        getUrl(adminToolURL);
        adminToolsPage.isOpened();
    }

    @Story("Should create Custom button")
    @Test
    public void createCustomButton() {
        deleteAllCustomButtons();
        CustomButtonTab customButtonPage = adminToolsPage.openTab(CUSTOM_BUTTONS);
        customButtonPage.checkLabelsWhenNoOneCustomButtonIsAdded();

        CustomButtonInfoWizardPage customButtonInfoWizardPage = customButtonPage.setUpNow();
        LeaveWizardPopUp leaveWizardPopUp = customButtonInfoWizardPage.navigateToPreviousTab();
        customButtonPage = leaveWizardPopUp.leaveNow(CustomButtonTab.class);

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(customButtonPage.getNumberOfAddedButtons(), 0, "Custom button is added");

        customButtonInfoWizardPage = customButtonPage.setUpNow();
        softAssert.assertFalse(customButtonInfoWizardPage.isNextTabButtonEnable(), "Fill required tooltip is not displayed");

        customButtonInfoWizardPage.setButtonDescription(buttonDescription + "some text to exceed the limit")
                .changeFocus();
        softAssert.assertTrue(customButtonInfoWizardPage.isButtonDescriptionErrorIsShown(), "Button name error is not shown");

        customButtonInfoWizardPage.setButtonLabel(buttonLabel + "some text to exceed the limit")
                .changeFocus();
        softAssert.assertTrue(customButtonInfoWizardPage.isButtonLabelErrorIsShown(), "Button label error is not shown");
        ActionSettingsWizardPage actionSettingsPage = customButtonInfoWizardPage.setButtonDescription(buttonDescription)
                .setButtonLabel(buttonLabel)
                .navigateToNextTab();
        List<String> flows = actionSettingsPage
                .openSelectFlowDropdown()
                .getAvailableFlows();
        softAssert.assertEquals(flows.stream().sorted(Comparator.comparing(String::toLowerCase)).collect(Collectors.toList()), flows,
                "flows list is not sorted in A-z format");

        SalesforceLayoutsAndListsWizardPage layoutsPage = actionSettingsPage.selectFlow(flowName)
                .navigateToNextTab();
        customButtonPage = layoutsPage.selectLayouts(new String[]{ACCOUNT.getObjectName() + " Layout"})
                .navigateToNextTab();
        softAssert.assertEquals(customButtonPage.getNotificationMessage(), CREATED_A_CUSTOM_BUTTON,
                "Incorrect message on create a custom button");
        customButtonPage.openEditButtonMenu(buttonDescription);
        customButtonPage.waitForCustomButtonEditable(buttonDescription, 60);
        softAssert.assertTrue(customButtonPage.isButtonEditable(), "Edit option is not enabled");
        AccountConcretePage concretePage = adminToolsPage.openRecordPageById(ACCOUNT, accountId);
        concretePage.waitForCustomButton(buttonLabel, 120);
        softAssert.assertTrue(concretePage.isCustomButtonAdded(buttonLabel), "Custom button is not added");
        softAssert.assertAll();
    }

    @Story("Should edit Custom button")
    @Test(priority = 1)
    public void editCustomButton() {
        CustomButtonTab customButtonPage = adminToolsPage.openTab(CUSTOM_BUTTONS);
        customButtonPage.waitForCustomButtonEditable(buttonDescription, 60);
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(customButtonPage.isButtonEditable(), "Edit option is not enabled");

        CustomButtonInfoWizardPage customButtonInfoWizardPage = customButtonPage.editButton(buttonDescription);
        softAssert.assertEquals(customButtonInfoWizardPage.getButtonDescription(), buttonDescription, "Incorrect button name loaded");
        softAssert.assertEquals(customButtonInfoWizardPage.getButtonLabel(), buttonLabel, "Incorrect button label loaded");
        customButtonInfoWizardPage.setButtonDescription("");
        softAssert.assertFalse(customButtonInfoWizardPage.isNextTabButtonEnable(), "Update operation is allowed");

        String oldLabel = buttonLabel;
        buttonLabel = "newButtonLabel" + getRandomString(30);

        ActionSettingsWizardPage actionSettingsPage = customButtonInfoWizardPage.setButtonDescription(newButtonName)
                .setButtonLabel(buttonLabel)
                .navigateToNextTab();
        SalesforceLayoutsAndListsWizardPage layoutsPage = actionSettingsPage
                .openSelectFlowDropdown()
                .selectFlow(secondFlowName)
                .navigateToNextTab();
        actionSettingsPage = layoutsPage.selectLayouts(new String[]{ACCOUNT.getObjectName() + " Layout",
                OPPORTUNITY.getObjectName() + " Layout"})
                .navigateToPreviousTab();
        customButtonInfoWizardPage = actionSettingsPage.navigateToPreviousTab();
        LeaveWizardPopUp leaveWizardPopUp = customButtonInfoWizardPage.navigateToPreviousTab();
        customButtonPage = leaveWizardPopUp.leaveNow(CustomButtonTab.class);
        softAssert.assertFalse(customButtonPage.isButtonPresent(newButtonName), "Changes are applied");

        customButtonInfoWizardPage = customButtonPage.editButton(buttonDescription);
        actionSettingsPage = customButtonInfoWizardPage.setButtonDescription(newButtonName)
                .setButtonLabel(buttonLabel)
                .navigateToNextTab();
        layoutsPage = actionSettingsPage
                .openSelectFlowDropdown()
                .selectFlow(secondFlowName)
                .navigateToNextTab();
        customButtonPage = layoutsPage.selectLayouts(new String[]{ACCOUNT.getObjectName() + " Layout",
                OPPORTUNITY.getObjectName() + " Layout"})
                .navigateToNextTab();

        softAssert.assertEquals(customButtonPage.getNotificationMessage(), UPDATED_A_CUSTOM_BUTOON,
                "Incorrect notification on custom button update");
        softAssert.assertTrue(customButtonPage.isButtonPresent(newButtonName), "Button is not updated");
        customButtonPage.waitForCustomButtonEditable(newButtonName, 60);
        customButtonPage.openEditButtonMenu(newButtonName);
        softAssert.assertTrue(customButtonPage.isButtonEditable(), "Edit option is not enabled");
        AccountConcretePage concretePage = adminToolsPage.openRecordPageById(ACCOUNT, accountId);
        concretePage.waitForDeleteCustomButton(oldLabel, 60);
        softAssert.assertFalse(concretePage.isCustomButtonAdded(oldLabel), "Custom button still present");
        softAssert.assertFalse(concretePage.isCustomButtonAdded(buttonLabel), "Custom button still present");
        OpportunitiesConcretePage opportunitiesConcretePage = concretePage.openRecordPageById(OPPORTUNITY, opportunityId);
        opportunitiesConcretePage.waitForCustomButton(buttonLabel, 120);
        softAssert.assertTrue(concretePage.isCustomButtonAdded(buttonLabel), "Custom button is not added");
        softAssert.assertAll();
    }

    @Story("Should remove Custom button")
    @Test(priority = 2)
    public void removeCustomButton() {
        CustomButtonTab customButtonPage = adminToolsPage.openTab(CUSTOM_BUTTONS);
        DeleteCustomButtonPopUp popUp = customButtonPage.deleteButton(newButtonName);
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(popUp.getPopUpBodyText(), DELETING_CUSTOM_BUTTON,
                "Incorrect message is delete custom button popup");
        customButtonPage = popUp.cancel();
        softAssert.assertTrue(customButtonPage.isButtonPresent(newButtonName), "Custom button deleted on cancel");
        customButtonPage = customButtonPage.deleteButton(newButtonName).remove();
        softAssert.assertEquals(customButtonPage.getNotificationMessage(), DELETED_A_CUSTOM_BUTOON,
                "Incorrect notification on delete custom button");
        OpportunitiesConcretePage opportunitiesConcretePage = adminToolsPage.openRecordPageById(OPPORTUNITY, opportunityId);
        opportunitiesConcretePage.waitForDeleteCustomButton(buttonLabel, 60);
        softAssert.assertFalse(opportunitiesConcretePage.isCustomButtonAdded(buttonLabel),
                "Button is not delete");
        softAssert.assertAll();
    }
}
