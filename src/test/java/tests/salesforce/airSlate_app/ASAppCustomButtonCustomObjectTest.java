package tests.salesforce.airSlate_app;

import api.salesforce.metadata.MetadataApiMan;
import api.salesforce.responses.CreateRecordResponse;
import com.airslate.api.models.packets.Packet;
import com.airslate.api.models.slates.Slate;
import com.airslate.api.models.slates.SlateInvite;
import com.google.common.collect.ImmutableMap;
import com.sforce.soap.metadata.*;
import com.sforce.ws.ConnectionException;
import core.DriverWinMan;
import core.DriverWindow;
import data.TestData;
import imap.ImapClient;
import imap.With;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import listeners.ImapListener;
import listeners.WebTestListener;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.airslate.addons.AirSlateEditor;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.app.airSlate_app.custom_button.*;
import pages.salesforce.app.sf_objects.CustomObjectDefaultConcretePage;
import tests.salesforce.SalesforceAirSlateBaseTest;
import utils.StringMan;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static api.salesforce.entities.SalesforceObject.CUSTOM_OBJECT;
import static api.salesforce.entities.airslate.CustomButton.Action.RUN_FLOW;
import static api.salesforce.entities.airslate.CustomButton.Mode.*;
import static core.check.Check.checkFail;
import static data.salesforce.SalesforceTestData.ASAppErrorMessages.PLEASE_ENTER_A_VALID_EMAIL_ADDRESS;
import static data.salesforce.SalesforceTestData.ASAppPopUpTexts.OH_NO_THIS_EMAIL_ISN_T_IN_OUR_SYSTEM;
import static data.salesforce.SalesforceTestData.EmailSubjects.RESET_YOUR_AIR_SLATE_PASSWORD;
import static java.util.Collections.singletonList;
import static org.testng.Assert.assertEquals;
import static utils.StringMan.getRandomString;

@Feature("airSlate app: custom button")
@Listeners({WebTestListener.class, ImapListener.class})
public class ASAppCustomButtonCustomObjectTest extends SalesforceAirSlateBaseTest {

    private String objectName = getUniqueRecordName(CUSTOM_OBJECT);
    private String objectId;
    private String buttonLabel = "CBonCO" + getRandomString(5);
    private SalesAppBasePage salesAppBasePage;
    private Slate flow;
    private WebDriver driver;
    private DriverWindow currentWindow;
    private DriverWinMan winMan;

    @BeforeTest
    public void setUp() throws IOException, URISyntaxException {
        driver = getDriver();
        winMan = new DriverWinMan(driver);
        currentWindow = winMan.getCurrentWindow();

        configurateSalesforceOrg();

        flow = createFlowWithDocument(fileToUpload);
        createCustomObject();
        createCustomButton(flow.id, flow.name, DEFAULT, getRandomString(5), buttonLabel, RUN_FLOW, singletonList(CUSTOM_OBJECT), null);
        salesAppBasePage = loginToSalesforce(stUserUsername, stUserPassword, getDriver());
    }

    @Step
    @BeforeMethod
    public void switchToDefaultWindow() {
        winMan.keepOnlyWindow(currentWindow);
    }

    @Story("Forgot password flow on custom object")
    @Test
    public void checkForgotPasswordFunctionality() {
        deleteUserConfigurationByUsername(stUserUsername);

        CustomObjectDefaultConcretePage concretePage = salesAppBasePage.openRecordPageById(CUSTOM_OBJECT, objectId);
        concretePage.waitForCustomButton(buttonLabel, 120);
        LoginPage loginPage = concretePage.clickOnCustomButton(buttonLabel, LoginPage.class);
        ForgotPasswordPage forgotPasswordPage = loginPage.forgotPassword();
        forgotPasswordPage = forgotPasswordPage.returnToLogIn().forgotPassword();

        SoftAssert softAssert = new SoftAssert();
        forgotPasswordPage.enterEmail("qwe").recoverMyPassword(ForgotPasswordPage.class);
        softAssert.assertEquals(forgotPasswordPage.getEmailError(), PLEASE_ENTER_A_VALID_EMAIL_ADDRESS, "Incorrect error message");

        forgotPasswordPage.enterEmail(StringMan.makeUniqueEmail(defaultAirSlateUserEmail)).recoverMyPassword(ForgotPasswordPage.class);
        softAssert.assertEquals(forgotPasswordPage.getNotificationMessage(), OH_NO_THIS_EMAIL_ISN_T_IN_OUR_SYSTEM, "incorrect toaster message");

        ImapClient client = new ImapClient(defaultAirSlateUserEmail, TestData.defaultPassword);
        client.deleteAllMessagesWithSubject(RESET_YOUR_AIR_SLATE_PASSWORD);
        RecoverEmailSendPopUp popUp = forgotPasswordPage.enterEmail(standardUser.email).recoverMyPassword(RecoverEmailSendPopUp.class);
        softAssert.assertEquals(client.findMessages(With.subject(RESET_YOUR_AIR_SLATE_PASSWORD)).size(), 1, "Recover message is not send");
        softAssert.assertEquals(popUp.getPopUpText(), popUp.getExpectedPopUpText(standardUser.email), "Incorrect pop up text");
        softAssert.assertAll();
    }

    @Story("Use Create&Open slate on custom object")
    @Test(priority = 1, testName = "Use Create&Open slate on custom object")
    public void useCreateAndOpenSlateButton() throws IOException {
        deleteUserConfigurationByUsername(stUserUsername);
        String newButtonLabel = "CandO" + getRandomString(5);
        List<Packet> packetsBefore = getSlates(flow.id);

        createCustomButton(flow.id, flow.name, OPEN_SLATE, getRandomString(5), newButtonLabel, RUN_FLOW, singletonList(CUSTOM_OBJECT), null);

        CustomObjectDefaultConcretePage concretePage = salesAppBasePage.openRecordPageById(CUSTOM_OBJECT, objectId);
        concretePage.waitForCustomButton(newButtonLabel, 120);
        LoginPage loginPage = concretePage.clickOnCustomButton(newButtonLabel, LoginPage.class);
        loginPage.enterLogin(standardUser.email)
                .enterPassword(TestData.defaultPassword)
                .clickOnLogIn(SuccessPage.class);
        assertEquals(driver.getWindowHandles().size(), 2, "New Window is not opened");
        winMan.switchToWindow(organization.subdomain);
        AirSlateEditor airSlateEditor = new AirSlateEditor(driver);
        airSlateEditor.isOpened();
        airSlateEditor.clickFinishUp();
        List<Packet> packetsCurrent = getSlates(flow.id);
        assertEquals(packetsCurrent.size(), packetsBefore.size() + 1, "Ne Slate is not created");
        packetsCurrent.removeAll(packetsBefore);
        assertEquals(getRevisions(flow.id, packetsCurrent.get(0).id).size(), 2, "Incorrect number of revisions after 'finish up'");
    }

    @Story("Use Create&Open slate on custom object")
    @Test(priority = 1)
    public void useSendSlateButton() throws IOException {
        deleteUserConfigurationByUsername(stUserUsername);
        String newButtonLabel = "CandO" + getRandomString(5);
        List<Packet> packetsBefore = getSlates(flow.id);
        airSlateRestClient().slatesEmailPermissions.updateFlowInvites(flow.id, singletonList(new SlateInvite(standardUser.email, SlateInvite.Access.CAN_USE))).execute();

        createCustomButton(flow.id, flow.name, SEND_SLATE, getRandomString(5), newButtonLabel, RUN_FLOW, singletonList(CUSTOM_OBJECT), null);

        CustomObjectDefaultConcretePage concretePage = salesAppBasePage.openRecordPageById(CUSTOM_OBJECT, objectId);
        concretePage.waitForCustomButton(newButtonLabel, 120);
        LoginPage loginPage = concretePage.clickOnCustomButton(newButtonLabel, LoginPage.class);
        loginPage.enterLogin(standardUser.email)
                .enterPassword(TestData.defaultPassword)
                .clickOnLogIn(SuccessPage.class);
        assertEquals(driver.getWindowHandles().size(), 2, "New Window is not opened");
        winMan.switchToWindow(organization.subdomain);
        AirSlateSendSlatePage sendSlatePage = new AirSlateSendSlatePage(driver);
        sendSlatePage.isOpened();
        sendSlatePage.back();
        List<Packet> packetsCurrent = getSlates(flow.id);
        assertEquals(packetsCurrent.size(), packetsBefore.size() + 1, "Ne Slate is not created");
        packetsCurrent.removeAll(packetsBefore);
        assertEquals(getRevisions(flow.id, packetsCurrent.get(0).id).size(), 1, "Incorrect number of revisions after 'finish up'");
    }

    @Step
    private void createCustomObject() throws IOException, URISyntaxException {
        CustomObject co = new CustomObject();
        co.setFullName(CUSTOM_OBJECT.getAPIName());
        co.setDeploymentStatus(DeploymentStatus.Deployed);
        co.setDescription("Created by the Metadata API Sample");
        co.setEnableActivities(true);
        co.setLabel(CUSTOM_OBJECT.getObjectName());
        co.setPluralLabel(CUSTOM_OBJECT.getObjectName() + "s");
        co.setSharingModel(SharingModel.ReadWrite);
        co.setEnableSearch(true);

        CustomField nf = new CustomField();
        nf.setType(FieldType.Text);
        nf.setDescription("The custom object identifier on page layouts, related lists etc");
        nf.setLabel(CUSTOM_OBJECT.getObjectName());
        nf.setFullName(CUSTOM_OBJECT.getAPIName());
        co.setNameField(nf);

        metadataApi = new MetadataApiMan(salesforceDefaultLogin, salesforceDefaultPassword, baseUrl);
        try {
            UpsertResult[] upsertResults = metadataApi.updateMetadata(new Metadata[]{co});
            for (UpsertResult r : upsertResults) {
                if (!r.isSuccess()) {
                    checkFail("Cannot create custom object");
                }
            }
        } catch (ConnectionException e) {
            e.printStackTrace();
            checkFail("Metadata API connection failed");
        }
        addCustomObjectPermissionToProfile("Standard");
        CreateRecordResponse response = salesforceApi.recordsService().createRecord(CUSTOM_OBJECT, ImmutableMap.of("Name", objectName));
        objectId = response.id;
    }

    @Step
    private void addCustomObjectPermissionToProfile(String profileFullName) {
        metadataApi = new MetadataApiMan(salesforceDefaultLogin, salesforceDefaultPassword, baseUrl);
        ProfileObjectPermissions permissions = new ProfileObjectPermissions();

        permissions.setAllowRead(true);
        permissions.setAllowEdit(true);
        permissions.setAllowCreate(true);
        permissions.setAllowDelete(true);
        permissions.setModifyAllRecords(true);
        permissions.setViewAllRecords(true);
        permissions.setObject(CUSTOM_OBJECT.getAPIName());

        ProfileObjectPermissions[] profileObjectPermissions = new ProfileObjectPermissions[]{permissions};
        Profile profile = new Profile();
        profile.setFullName(profileFullName);
        profile.setObjectPermissions(profileObjectPermissions);
        try {
            metadataApi.updateMetadata(new Metadata[]{profile});
        } catch (ConnectionException e) {
            e.printStackTrace();
        }
    }
}