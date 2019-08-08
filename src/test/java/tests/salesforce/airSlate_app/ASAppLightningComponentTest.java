package tests.salesforce.airSlate_app;

import com.airslate.api.models.slates.Slate;
import core.DriverWinMan;
import core.DriverWindow;
import data.TestData;
import imap.ImapClient;
import imap.With;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import listeners.ImapListener;
import listeners.WebTestListener;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.airslate.addons.AirSlateEditor;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.app.airSlate_app.custom_button.RecoverEmailSendPopUp;
import pages.salesforce.app.airSlate_app.custom_button.SuccessPage;
import pages.salesforce.app.airSlate_app.lightning_component.*;
import pages.salesforce.app.sf_objects.accounts.AccountConcretePage;
import tests.salesforce.SalesforceAirSlateBaseTest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

import static api.salesforce.entities.SalesforceObject.ACCOUNT;
import static api.salesforce.entities.airslate.CustomButton.Action.RUN_FLOW;
import static api.salesforce.entities.airslate.CustomButton.Mode.OPEN_SLATE;
import static data.salesforce.SalesforceTestData.ASAppPageTexts.*;
import static data.salesforce.SalesforceTestData.EmailSubjects.RESET_YOUR_AIR_SLATE_PASSWORD;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static utils.StringMan.getRandomString;

@Feature("airSlate app: lightning component")
@Listeners({WebTestListener.class, ImapListener.class})
public class ASAppLightningComponentTest extends SalesforceAirSlateBaseTest {

    private String accountId;
    private SalesAppBasePage salesAppBasePage;
    private Slate flow;
    private DriverWindow currentWindow;

    @BeforeTest
    public void setUp() throws IOException, URISyntaxException {
        salesAppBasePage = loginToSalesforce(salesforceDefaultLogin, salesforceDefaultPassword, getDriver());
        configurateSalesforceOrg();
        setUpLightningComponent();
        deleteAllCustomButtons();
        salesAppBasePage = loginToSalesforce(stUserUsername, stUserPassword, getDriver());
    }

    @Story("Forgot password")
    @Test
    public void forgotPassword() {
        deleteUserConfigurationByUsername(stUserUsername);

        AccountConcretePage concretePage = salesAppBasePage.openRecordPageById(ACCOUNT, accountId);
        AirSlateLightningComponent lightningComponent = concretePage.switchToAirSlateLightning();
        lightningComponent.loginPageComponent.isOpened();
        ForgotPasswordPageComponent forgotPasswordPageComponent = lightningComponent
                .loginPageComponent
                .forgotPassword();
        ImapClient client = new ImapClient(defaultAirSlateUserEmail, TestData.defaultPassword);
        client.deleteAllMessagesWithSubject(RESET_YOUR_AIR_SLATE_PASSWORD);
        RecoverEmailSendPopUp popUp = forgotPasswordPageComponent.enterEmail(standardUser.email)
                .recoverMyPassword(RecoverEmailSendPopUp.class);
        String popUpText = popUp.getPopUpText();
        popUp.returnToLogIn(LoginPageComponent.class);

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(client.findMessages(With.subject(RESET_YOUR_AIR_SLATE_PASSWORD)).size(), 1, "Recover message is not send");
        softAssert.assertEquals(popUpText, popUp.getExpectedPopUpText(standardUser.email), "Incorrect pop up text");
        softAssert.assertAll();
    }

    @Story("login to airSlate")
    @Test(priority = 1)
    public void logIn() {
        deleteUserConfigurationByUsername(stUserUsername);

        AccountConcretePage concretePage = salesAppBasePage.openRecordPageById(ACCOUNT, accountId);
        AirSlateLightningComponent lightningComponent = concretePage.switchToAirSlateLightning();
        lightningComponent.loginPageComponent.isOpened();
        lightningComponent.loginPageComponent
                .enterLogin(standardUser.email)
                .enterPassword(TestData.defaultPassword)
                .clickOnLogIn(lightningComponent.getClass());
    }

    @Story("record without flows")
    @Test(priority = 2)
    public void noRecord() {
        connectStandardUser(standardUser.email);
        AccountConcretePage concretePage = salesAppBasePage.openRecordPageById(ACCOUNT, accountId);
        AirSlateLightningComponent lightningComponent = concretePage.switchToAirSlateLightning();
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(lightningComponent.getTitleText(), NOTHING_HERE, "Incorrect page title");
        softAssert.assertEquals(lightningComponent.getSubTitleText(), AIR_SLATE_HASN_T_BEEN_USED_WITH_THIS_RECORD_YET, "Incorrect page subtitle");
        softAssert.assertAll();
    }

    @Story("disconnected workspace")
    @Test(priority = 2)
    public void disconnectedWorkspace() {
        try {
            disconnectWorkspace();
            AccountConcretePage concretePage = salesAppBasePage.openRecordPageById(ACCOUNT, accountId);
            AirSlateLightningComponent lightningComponent = concretePage.switchToAirSlateLightning();
            SoftAssert softAssert = new SoftAssert();
            softAssert.assertEquals(lightningComponent.getTitleText(),
                    NO_WORKSPACE_HAS_BEEN_CONNECTED, "Incorrect page title");
            softAssert.assertEquals(lightningComponent.getSecondSubTitleText(),
                    PLEASE_CONTACT_YOUR_ADMIN_TO_CONNECT_AIR_SLATE_WORKSPACE_TO_YOUR_SALESFORCE_ORGANIZATION, "Incorrect page subtitle");
            softAssert.assertAll();
        } finally {
            connectWorkspace(organization);
        }
    }

    @Story("declined revision")
    @Test(priority = 3)
    public void declinedRevision() {
        connectStandardUser(standardUser.email);

        String newButtonLabel = "CandO" + getRandomString(5);
        createCustomButton(flow.id, flow.name, OPEN_SLATE, getRandomString(5), newButtonLabel, RUN_FLOW, Collections.singletonList(ACCOUNT), null);
        WebDriver driver = getDriver();
        DriverWinMan winMan = new DriverWinMan(driver);
        currentWindow = winMan.getCurrentWindow();

        AccountConcretePage concretePage = salesAppBasePage.openRecordPageById(ACCOUNT, accountId);
        concretePage.waitForCustomButton(newButtonLabel, 120);
        concretePage.clickOnCustomButton(newButtonLabel, SuccessPage.class);
        assertEquals(driver.getWindowHandles().size(), 2, "New Window is not opened");
        winMan.switchToWindow(organization.subdomain);
        AirSlateEditor airSlateEditor = new AirSlateEditor(driver);
        airSlateEditor.isOpened();
        airSlateEditor.declineSignatureRequest().enterDeclineReason("Test case").declineSignature();
        winMan.keepOnlyWindow(currentWindow);
        refreshPage();
        concretePage.isOpened();
        AirSlateLightningComponent lightningComponent = concretePage.switchToAirSlateLightning();
        List<LightningComponentSlateRevision> revisions = lightningComponent.openFlowDetails(flow.name)
                .openSlateDetails(1)
                .showAllRevisions()
                .getAllSlateRevisions();
        assertEquals(revisions.size(), 2, "Unexpected revisions list size");
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(revisions.get(0).name, "You declined revision 1", "Incorrect name for last revision");
        softAssert.assertEquals(revisions.get(1).name, "You created blank revision", "Incorrect name for blank revision");
        softAssert.assertAll();
    }

    @Story("flows list")
    @Test(priority = 3)
    public void flowList() throws IOException {
        //SetUp
        connectStandardUser(standardUser.email);
        String newButtonLabel = "CandO" + getRandomString(5);
        Slate flow = createFlowWithDocument(fileToUpload);
        Slate secondFlow = createFlowWithDocument(fileToUpload);
        createCustomButton(flow.id, flow.name, OPEN_SLATE, getRandomString(5), newButtonLabel, RUN_FLOW, Collections.singletonList(ACCOUNT), null);
        String accountId = createAccountRecord();
        createBlankFlowPacketFromContext(accountId, ACCOUNT, stUserUsername, stUserPassword, flow, newButtonLabel);
        createBlankFlowPacketFromContext(accountId, ACCOUNT, stUserUsername, stUserPassword, secondFlow, newButtonLabel);
        createBlankFlowPacketFromContext(accountId, ACCOUNT, salesforceDefaultLogin, salesforceDefaultPassword, flow, newButtonLabel);

        //Test
        DriverWinMan winMan = new DriverWinMan(getDriver());
        if (currentWindow != null) {
            winMan.keepOnlyWindow(currentWindow);
        } else {
            getUrl(baseUrl);
        }
        AccountConcretePage concretePage = salesAppBasePage.openRecordPageById(ACCOUNT, accountId);
        AirSlateLightningComponent lightningComponent = concretePage.switchToAirSlateLightning();
        List<LightningComponentFlow> flows = lightningComponent.getAllFlows();
        assertEquals(flows.size(), 2, "eqe");
        lightningComponent.openFlowDetails(flow.name).back();
        assertTrue(lightningComponent.getAllFlows().containsAll(flows), "Flows list changed");
    }

    private void setUpLightningComponent() throws IOException {
        flow = createFlowWithDocument(fileToUpload);
        accountId = createAccountRecord();
        AccountConcretePage concretePage = salesAppBasePage.openRecordPageById(ACCOUNT, accountId);
        concretePage.switchToAirSlateLightning();
        concretePage.logOutFromSalesforce();
    }
}