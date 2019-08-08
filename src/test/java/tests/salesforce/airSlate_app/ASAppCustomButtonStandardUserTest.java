package tests.salesforce.airSlate_app;

import api.salesforce.entities.airslate.CustomButton;
import api.salesforce.entities.airslate.bots.Setting;
import api.salesforce.entities.airslate.bots.salesforce_bots.PrefillBotSettings;
import api.salesforce.entities.airslate.bots.ui_components.DataDefaultObject;
import api.salesforce.entities.airslate.bots.ui_components.DataMappingObject;
import com.airslate.api.models.addons.integration.AddonIntegration;
import com.airslate.api.models.addons.integration.ServiceAccount;
import com.airslate.api.models.documents.Dictionary;
import com.airslate.api.models.documents.Document;
import com.airslate.api.models.files.BaseFile;
import com.airslate.api.models.packets.Packet;
import com.airslate.api.models.slates.Slate;
import com.airslate.api.models.users.User;
import com.google.common.collect.ImmutableMap;
import com.itextpdf.text.Rectangle;
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
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;
import pages.airslate.addons.AirSlateEditor;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.app.airSlate_app.custom_button.*;
import pages.salesforce.app.sf_objects.accounts.AccountConcretePage;
import tests.salesforce.SalesforceAirSaleBotsBaseTest;
import utils.AirSlateWSEditor;
import utils.PDFReadMan;
import utils.StringMan;
import utils.TimeMan;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static api.salesforce.entities.SalesforceObject.ACCOUNT;
import static api.salesforce.entities.airslate.CustomButton.Action.RUN_FLOW;
import static api.salesforce.entities.airslate.CustomButton.Mode.DEFAULT;
import static api.salesforce.entities.airslate.CustomButton.Mode.OPEN_SLATE;
import static api.salesforce.entities.airslate.bots.SettingType.CHOICE;
import static com.airslate.api.models.addons.AddonEnum.PRE_FILL_FROM_SALESFORCE_RECORD;
import static data.salesforce.SalesforceTestData.ASAppErrorMessages.PLEASE_ENTER_A_VALID_EMAIL_ADDRESS;
import static data.salesforce.SalesforceTestData.ASAppPopUpTexts.OH_NO_THIS_EMAIL_ISN_T_IN_OUR_SYSTEM;
import static data.salesforce.SalesforceTestData.EmailSubjects.RESET_YOUR_AIR_SLATE_PASSWORD;
import static java.util.Arrays.asList;
import static org.testng.Assert.assertEquals;
import static utils.StringMan.getRandomString;

@Feature("airSlate app: custom button")
@Listeners({WebTestListener.class, ImapListener.class})
public class ASAppCustomButtonStandardUserTest extends SalesforceAirSaleBotsBaseTest {

    private String accountId;
    private String buttonLabel = "stUserCB" + getRandomString(5);
    private SalesAppBasePage salesAppBasePage;
    private Slate flow;
    private WebDriver driver;
    private DriverWinMan winMan;
    private DriverWindow currentWindow;
    private Slate errorFlow;

    @DataProvider(name = "slateMode")
    public static Object[][] sortPairwise() {
        return new Object[][]{{CustomButton.Mode.DEFAULT}, {CustomButton.Mode.SEND_SLATE}, {CustomButton.Mode.OPEN_SLATE}};
    }

    @BeforeTest
    public void setUp() throws IOException, URISyntaxException {
        driver = getDriver();
        winMan = new DriverWinMan(driver);
        currentWindow = winMan.getCurrentWindow();

        deleteAllCustomButtons();
        configurateSalesforceOrg();
        setUpFlows();
        salesAppBasePage = loginToSalesforce(stUserUsername, stUserPassword, getDriver());
    }

    @Step
    @BeforeMethod
    public void switchToDefaultWindow() {
        winMan.keepOnlyWindow(currentWindow);
    }

    @Story("Login flow")
    @Test(priority = 1)
    public void checkFlowRunAfterLogIn() {

        AccountConcretePage concretePage = salesAppBasePage.openRecordPageById(ACCOUNT, accountId);
        String accountUrl = getDriver().getCurrentUrl();
        concretePage.waitForCustomButton(buttonLabel, 120);
        LoginPage loginPage = concretePage.clickOnCustomButton(buttonLabel, LoginPage.class);
        loginPage.enterLogin(standardUser.email)
                .enterPassword(TestData.defaultPassword)
                .clickOnLogIn(SuccessPage.class);
        TimeMan.sleep(10);
        concretePage.isOpened();
        assertEquals(getDriver().getCurrentUrl(), accountUrl, "Redirected to wrong page");
    }

    @Story("Forgot password flow")
    @Test
    public void checkForgotPasswordFunctionality() {
        deleteUserConfigurationByUsername(stUserUsername);

        AccountConcretePage concretePage = salesAppBasePage.openRecordPageById(ACCOUNT, accountId);
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

    @Story("Run empty flow")
    @Test(priority = 2, dataProvider = "slateMode")
    public void checkFlowErrorPage(CustomButton.Mode mode) {
        buttonLabel = getRandomString(5);
        connectStandardUser(standardUser.email);
        createCustomButton(errorFlow.id, errorFlow.name, mode, getRandomString(5), buttonLabel, RUN_FLOW, Collections.singletonList(ACCOUNT), null);

        AccountConcretePage concretePage = salesAppBasePage.openRecordPageById(ACCOUNT, accountId);
        concretePage.waitForCustomButton(buttonLabel, 120);
        Class<? extends SalesAppBasePage> expectedClass = RunningFlowErrorPage.class;
        if (mode.equals(DEFAULT)) {
            expectedClass = SuccessPage.class;
        }
        concretePage.clickOnCustomButton(buttonLabel, expectedClass);
        assertEquals(getDriver().getWindowHandles().size(), 1, "airSlate page opened in new tab");
    }

    @Story("Use Create&Open slate on Account object")
    @Test(priority = 3, testName = "Use Create&Open slate on Account object")
    public void useCreateAndOpenSlateButton() throws IOException {
        deleteUserConfigurationByUsername(stUserUsername);
        String newButtonLabel = "CandO" + getRandomString(5);
        List<Packet> packetsBefore = getSlates(flow.id);

        createCustomButton(flow.id, flow.name, OPEN_SLATE, getRandomString(5), newButtonLabel, RUN_FLOW, Collections.singletonList(ACCOUNT), null);

        AccountConcretePage concretePage = salesAppBasePage.openRecordPageById(ACCOUNT, accountId);
        concretePage.waitForCustomButton(newButtonLabel, 120);
        LoginPage loginPage = concretePage.clickOnCustomButton(newButtonLabel, LoginPage.class);
        loginPage.enterLogin(standardUser.email)
                .enterPassword(TestData.defaultPassword);
        loginPage.clickOnLogIn(SuccessPage.class);
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

    @Step
    private Slate getErrorFlow() throws IOException {
        Slate flow = createEmptyFlow();
        BaseFile baseFile = new BaseFile(fileToUpload, "name.pdf");
        Document document = uploadDocument(baseFile);
        addDocumentToFlow(flow, document);
        addFillableFieldsToDocument(document, fileToUpload, adminUser, airSlateRestClient().interceptors().organizationDomain.getOrganizationDomain());
        flow = publishFlow(flow);
        document = airSlateApiMan.getDocument(document);
        List<Dictionary> fields = airSlateApiMan.getDocumentFields(document);
        ServiceAccount serviceAccount = getAuthorizedServiceAccount();
        prefillFromSalesforce(document, fields, "Website", serviceAccount, flow);
        return flow;
    }

    @Step
    private void prefillFromSalesforce(Document document, List<Dictionary> fields, String fieldWithEmail, ServiceAccount serviceAccount, Slate flow) throws IOException {
        AddonIntegration addonIntegration = getAddonIntegration(flow, PRE_FILL_FROM_SALESFORCE_RECORD, serviceAccount);
        Dictionary field = fields.get(0);
        Setting leftGroup = new Setting(new DataDefaultObject(ACCOUNT.getObjectName(), ACCOUNT.getAPIName()), CHOICE.getType());
        Setting rightGroup = new Setting(new DataDefaultObject(document.name, document.id), CHOICE.getType());
        List<DataMappingObject.Mapping> conditionsMapping = new ArrayList<>();
        DataMappingObject.Mapping pair = new DataMappingObject.Mapping(new Setting(new DataDefaultObject("Account ID [id]", "Id"), CHOICE.getType()),
                new Setting(new DataDefaultObject(getFieldName(field), field.name), CHOICE.getType()));
        conditionsMapping.add(pair);

        DataMappingObject defaultCondition = new DataMappingObject();
        defaultCondition.leftGroup = leftGroup;
        defaultCondition.rightGroup = rightGroup;
        defaultCondition.mapping = conditionsMapping;

        List<DataMappingObject> conditions = new ArrayList<>();
        conditions.add(defaultCondition);
        List<DataMappingObject> mappingObjects = new ArrayList<>();
        DataMappingObject defaultMappingObject = new DataMappingObject();
        defaultMappingObject.leftGroup = leftGroup;
        defaultMappingObject.rightGroup = rightGroup;
        field = fields.get(1);
        defaultMappingObject.mapping = asList(new DataMappingObject.Mapping(new Setting(new DataDefaultObject(fieldWithEmail + " [url]", fieldWithEmail), CHOICE.getType()),
                new Setting(new DataDefaultObject(getFieldName(field), field.name), CHOICE.getType())));
        mappingObjects.add(defaultMappingObject);

        PrefillBotSettings prefillBotSettings = getPrefillBotSettings(addonIntegration, ACCOUNT, null, conditions, mappingObjects);
        installBotToFlow(flow, PRE_FILL_FROM_SALESFORCE_RECORD, prefillBotSettings);
    }

    @Override
    protected void addFillableFieldsToDocument(com.airslate.api.models.documents.Document document, File fileToUpload, User adminUser, String orgDomain) throws IOException {
        Rectangle pdf = PDFReadMan.getPdfSize(fileToUpload.getAbsolutePath());
        double width = pdf.getWidth();
        double height = pdf.getHeight();

        AirSlateWSEditor ws = null;
        try {
            ws = new AirSlateWSEditor(airSlateRestClient().interceptors().authenticator.getToken(), document, adminUser, orgDomain);
            ws.addText(width / 4, height / 20, "first", true);
            ws.addDate(width / 4, height / 2, "second", false);
        } catch (URISyntaxException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (ws != null) {
                ws.destroy();
            }
        }
    }

    private void setUpFlows() throws IOException {
        flow = createFlowWithDocument(fileToUpload);
        errorFlow = getErrorFlow();
        createCustomButton(flow.id, flow.name, DEFAULT, getRandomString(5), buttonLabel, RUN_FLOW, Collections.singletonList(ACCOUNT), null);
        accountId = createAccountRecord(ImmutableMap.of("Website", getRandomString(6)));
    }
}