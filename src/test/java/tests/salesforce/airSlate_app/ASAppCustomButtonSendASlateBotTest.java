package tests.salesforce.airSlate_app;

import api.salesforce.entities.airslate.bots.salesforce_bots.SendASlateBotSettings;
import api.salesforce.entities.airslate.bots.ui_components.DataDefaultObject;
import com.airslate.api.models.addons.integration.AddonIntegration;
import com.airslate.api.models.addons.integration.ServiceAccount;
import com.airslate.api.models.packets.Packet;
import com.airslate.api.models.slates.Slate;
import com.google.common.collect.ImmutableMap;
import data.TestData;
import imap.ImapClient;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.app.airSlate_app.custom_button.LoginPage;
import pages.salesforce.app.airSlate_app.custom_button.SuccessPage;
import pages.salesforce.app.sf_objects.accounts.AccountConcretePage;
import tests.salesforce.SalesforceAirSaleBotsBaseTest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static api.salesforce.entities.SalesforceObject.ACCOUNT;
import static api.salesforce.entities.airslate.CustomButton.Action.RUN_FLOW;
import static api.salesforce.entities.airslate.CustomButton.Mode.DEFAULT;
import static com.airslate.api.models.addons.AddonEnum.SEND_A_SLATE_TO_SALESFORCE_CONTACT;
import static java.util.Collections.singletonList;
import static org.testng.Assert.assertEquals;
import static utils.StringMan.getRandomString;
import static utils.salesforce.BotsMan.sendASlateSalesforceBot;

@Feature("airSlate app: custom button")
public class ASAppCustomButtonSendASlateBotTest extends SalesforceAirSaleBotsBaseTest {

    private String buttonLabel = "sendASlateBotCB" + getRandomString(5);
    private SalesAppBasePage salesAppBasePage;
    private Slate flow;
    private String accountId;

    @BeforeTest
    public void setUp() throws IOException, URISyntaxException {
        configurateSalesforceOrg();
        setUpSendASlateBot();
        deleteUserConfigurationByUsername(stUserUsername);
        salesAppBasePage = loginToSalesforce(stUserUsername, stUserPassword, getDriver());
    }

    @Story("Custom button: SendASlate bot")
    @Test
    public void sendASlateBotCustomButton() throws IOException {
        AccountConcretePage concretePage = salesAppBasePage.openRecordPageById(ACCOUNT, accountId);
        concretePage.waitForCustomButton(buttonLabel, 120);
        LoginPage loginPage = concretePage.clickOnCustomButton(buttonLabel, LoginPage.class);
        ImapClient imapClient = new ImapClient(defaultAirSlateUserEmail, TestData.defaultPassword);
        imapClient.deleteAllMessagesWithSubject(sendASlateSalesforceBot);
        List<Packet> slatesBefore = getSlates(flow.id);
        loginPage.enterLogin(standardUser.email)
                .enterPassword(TestData.defaultPassword)
                .clickOnLogIn(SuccessPage.class);
        String sendASlateMessage = getSendASlateMessage(imapClient).get(0).text();
        assertEquals(sendASlateMessage, getSendASlateBotBody(standardUser.first_name + " " + standardUser.last_name, flow.name),
                "Incorrect SendASLate bot email message text");
        assertEquals(getSlates(flow.id).size(), slatesBefore.size() + 1, "New Slate is not created");
    }


    private void setUpSendASlateBot() throws IOException {
        flow = createFlowWithDocument(fileToUpload);
        ServiceAccount serviceAccount = getSalesforceServiceAccount(getDriver());
        AddonIntegration addonIntegration = getAddonIntegration(flow, SEND_A_SLATE_TO_SALESFORCE_CONTACT, serviceAccount);
        String fieldWithEmail = "Website";
        SendASlateBotSettings sendASlateBotSettings = getSendASlateBotSettings(addonIntegration, ACCOUNT,
                null, null, new DataDefaultObject(fieldWithEmail + " [url]", fieldWithEmail));
        installBotToFlow(flow, SEND_A_SLATE_TO_SALESFORCE_CONTACT, sendASlateBotSettings);
        createCustomButton(flow.id, flow.name, DEFAULT, getRandomString(5), buttonLabel, RUN_FLOW, singletonList(ACCOUNT), null);
        accountId = createAccountRecord(ImmutableMap.of(fieldWithEmail, defaultAirSlateUserEmail));
    }
}