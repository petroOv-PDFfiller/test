package tests.salesforce.airSlate_app;

import api.salesforce.entities.airslate.CustomButton;
import com.airslate.api.models.packets.Packet;
import com.airslate.api.models.slates.Slate;
import com.sforce.ws.ConnectionException;
import core.DriverWinMan;
import core.DriverWindow;
import data.TestData;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import listeners.WebTestListener;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.app.airSlate_app.custom_button.AlmostDonePage;
import pages.salesforce.app.airSlate_app.custom_button.LoginPage;
import pages.salesforce.app.airSlate_app.custom_button.RunningFlowListViewErrorPage;
import pages.salesforce.app.airSlate_app.custom_button.SorryAboutThatPage;
import pages.salesforce.app.sf_objects.accounts.AccountsPage;
import tests.salesforce.SalesforceAirSlateBaseTest;
import utils.TimeMan;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static api.salesforce.entities.SalesforceObject.ACCOUNT;
import static org.awaitility.Awaitility.await;
import static utils.StringMan.getRandomString;

@Feature("airSlate app: custom button")
@Listeners(WebTestListener.class)
public class ASAppCustomButtonListViewTest extends SalesforceAirSlateBaseTest {

    private String buttonLabel = "buttonLabel" + getRandomString(5);
    private SalesAppBasePage salesAppBasePage;
    private DriverWinMan winMan;
    private DriverWindow currentWindow;
    private AccountsPage accountsPage;
    private String accountUrl;
    private Slate flow;

    @BeforeTest
    public void setUp() throws IOException, URISyntaxException, ConnectionException {
        WebDriver driver = getDriver();
        winMan = new DriverWinMan(driver);
        currentWindow = winMan.getCurrentWindow();

        configurateSalesforceOrg();

        flow = createFlowWithDocument(fileToUpload);
        createCustomButton(flow.id, flow.name, CustomButton.Mode.DEFAULT, getRandomString(5), buttonLabel, CustomButton.Action.RUN_FLOW, null, Collections.singletonList(ACCOUNT));
        createAccountRecordsBulk(100);
        String listViewName = addListViewTOObject(ACCOUNT);

        salesAppBasePage = loginToSalesforce(stUserUsername, stUserPassword, getDriver());
        accountsPage = salesAppBasePage.openObjectPage(ACCOUNT);
        accountsPage.selectListView(listViewName);

        accountsPage.waitForCustomButton(buttonLabel, 120);
        TimeMan.sleep(30);
    }

    @Step
    @BeforeMethod
    public void switchToDefaultWindow() {
        deleteUserConfigurationByUsername(stUserUsername);
        winMan.keepOnlyWindow(currentWindow);
    }

    @Story("Listview error pages")
    @Test
    public void checkListViewErrorPages() {
        deleteUserConfigurationByUsername(stUserUsername);

        SoftAssert softAssert = new SoftAssert();
        accountsPage = salesAppBasePage.openObjectPage(ACCOUNT);
        accountUrl = getDriver().getCurrentUrl();
        LoginPage loginPage = accountsPage.clickOnCustomButton(buttonLabel, LoginPage.class);
        loginPage.enterLogin(standardUser.email)
                .enterPassword(TestData.defaultPassword)
                .clickOnLogIn(RunningFlowListViewErrorPage.class);
        TimeMan.sleep(10);
        accountsPage.isOpened();
        softAssert.assertTrue(accountUrl.equals(getDriver().getCurrentUrl()) || accountUrl.contains(getDriver().getCurrentUrl()), "Didn't redirect back after click on button");
        accountsPage.clickOnCustomButton(buttonLabel, RunningFlowListViewErrorPage.class);
        TimeMan.sleep(10);
        accountsPage.isOpened();
        softAssert.assertTrue(accountUrl.equals(getDriver().getCurrentUrl()) || accountUrl.contains(getDriver().getCurrentUrl()), "Didn't redirect back by timeout");
        accountsPage.selectRecords(100).clickOnCustomButton(buttonLabel, SorryAboutThatPage.class);
        TimeMan.sleep(10);
        accountsPage.isOpened();
        softAssert.assertTrue(accountUrl.equals(getDriver().getCurrentUrl()) || accountUrl.contains(getDriver().getCurrentUrl()), "Didn't redirect back when choose more than 100 records");
        softAssert.assertAll();
    }

    @Story("Custom button: batching")
    @Test
    public void checkBatching() throws IOException {
        deleteUserConfigurationByUsername(stUserUsername);

        SoftAssert softAssert = new SoftAssert();
        accountsPage = salesAppBasePage.openObjectPage(ACCOUNT);
        List<Packet> slates = getSlates(flow.id);
        accountsPage.selectRecords(10);
        accountUrl = getDriver().getCurrentUrl();
        LoginPage loginPage = accountsPage.clickOnCustomButton(buttonLabel, LoginPage.class);
        AlmostDonePage almostDonePage = loginPage.enterLogin(standardUser.email)
                .enterPassword(TestData.defaultPassword)
                .clickOnLogIn(AlmostDonePage.class);
        almostDonePage.backToListView(AccountsPage.class);
        accountsPage.isOpened();
        softAssert.assertTrue(waitForSlates(slates.size() + 10, flow.id), "Slates is not created");
        String currentUrl = getDriver().getCurrentUrl();
        softAssert.assertTrue(accountUrl.contains(currentUrl), "Redirected to wrong page: " + currentUrl);
        softAssert.assertAll();
    }

    @Story("Custom button: batching")
    @Test
    public void checkBatchingFor75Records() throws IOException {
        deleteUserConfigurationByUsername(stUserUsername);

        SoftAssert softAssert = new SoftAssert();
        accountsPage = salesAppBasePage.openObjectPage(ACCOUNT);
        List<Packet> slates = getSlates(flow.id);
        accountsPage.selectRecords(75);
        accountUrl = getDriver().getCurrentUrl();
        LoginPage loginPage = accountsPage.clickOnCustomButton(buttonLabel, LoginPage.class);
        loginPage.enterLogin(standardUser.email)
                .enterPassword(TestData.defaultPassword)
                .clickOnLogIn(AlmostDonePage.class);
        TimeMan.sleep(10);
        accountsPage.isOpened();
        softAssert.assertTrue(waitForSlates(slates.size() + 75, flow.id), "Slates is not created");
        softAssert.assertEquals(getDriver().getCurrentUrl(), accountUrl, "Redirected to wrong page after second click");
        softAssert.assertAll();
    }

    @Step
    protected boolean waitForSlates(int number, String flowId) {
        try {
            await().ignoreExceptions()
                    .pollInterval(3, TimeUnit.SECONDS)
                    .atMost(120, TimeUnit.SECONDS)
                    .until(() -> getSlates(flowId).size() == number);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}