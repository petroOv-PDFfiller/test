package tests.salesforce.airSlate_app;

import com.airslate.api.models.slates.Slate;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import listeners.WebTestListener;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.app.airSlate_app.admin_tools.ASAppAdminToolsPage;
import pages.salesforce.app.airSlate_app.admin_tools.entities.CustomButton;
import pages.salesforce.app.airSlate_app.admin_tools.tabs.CustomButtonTab;
import pages.salesforce.enums.admin_tools.ASAppCustomButtonField;
import tests.salesforce.SalesforceAirSlateBaseTest;
import utils.StringMan;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static api.salesforce.entities.SalesforceObject.ACCOUNT;
import static api.salesforce.entities.airslate.CustomButton.Action.RUN_FLOW;
import static api.salesforce.entities.airslate.CustomButton.Mode.DEFAULT;
import static data.salesforce.SalesforceTestData.ASAppCustomButtonSortOrder.ASC;
import static data.salesforce.SalesforceTestData.ASAppCustomButtonSortOrder.DESC;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static pages.salesforce.enums.admin_tools.ASAppAdminToolTabs.CUSTOM_BUTTONS;
import static pages.salesforce.enums.admin_tools.ASAppCustomButtonField.FLOW;
import static pages.salesforce.enums.admin_tools.ASAppCustomButtonField.LABEL;
import static utils.StringMan.getRandomString;

@Feature("airSlate app: custom button")
@Listeners(WebTestListener.class)
public class ASAppCustomButtonSortTest extends SalesforceAirSlateBaseTest {

    private String adminToolURL;
    private ASAppAdminToolsPage adminToolsPage;
    private String buttonName = "buttonName" + getRandomString(5);
    private String buttonLabel = "buttonLabel" + getRandomString(5);
    private String thirdFlowName;

    @DataProvider(name = "sortPairwise")
    public static Object[][] sortPairwise() {
        return new Object[][]{{LABEL, ASC, Comparator.comparing(o -> ((CustomButton) o).getLabel().toLowerCase())},
                {LABEL, DESC, (Comparator<CustomButton>) (o1, o2) -> o2.getLabel().toLowerCase().compareTo(o1.getLabel().toLowerCase())},
                {FLOW, ASC, Comparator.comparing(o -> ((CustomButton) o).getFlow().toLowerCase())},
                {FLOW, DESC, (Comparator<CustomButton>) (o1, o2) -> o2.getFlow().toLowerCase().compareTo(o1.getFlow().toLowerCase())}};
    }

    @BeforeTest
    public void setUp() throws IOException, URISyntaxException {
        SalesAppBasePage salesAppBasePage = loginWithDefaultCredentials();
        configurateSalesforceOrg();
        createCustomButtons();
        adminToolsPage = getAdminToolsPage(salesAppBasePage);
        adminToolURL = getDriver().getCurrentUrl();
    }

    @BeforeMethod
    public void getAdminToolPage() {
        getUrl(adminToolURL);
        adminToolsPage.isOpened();
    }

    @Story("Should correctly sort by all columns in custom buttons list")
    @Test(dataProvider = "sortPairwise")
    public void sortByCustomButton(ASAppCustomButtonField field,
                                   String order,
                                   Comparator<CustomButton> buttonComporator) {
        CustomButtonTab customButtonPage = adminToolsPage.openTab(CUSTOM_BUTTONS);

        if (!(field.equals(LABEL) && order.equals(ASC))) {
            customButtonPage.sortBy(field, order);
        }
        List<CustomButton> customButtons = customButtonPage.initCustomButtons();
        List<CustomButton> sorted = customButtons.stream().sorted(buttonComporator).collect(Collectors.toList());
        assertEquals(sorted, customButtons, "Incorrect sort by " + field.getName() + " by " + order);
    }

    @Story("Should correctly search in custom buttons list")
    @Test(priority = 1)
    public void searchCustomButton() {
        String randomSearchRequest = StringMan.getRandomString(3);
        String partOfButtonName = buttonName.substring(2);

        CustomButtonTab customButtonPage = adminToolsPage.openTab(CUSTOM_BUTTONS);
        List<CustomButton> beforeSearch = customButtonPage.initCustomButtons();
        customButtonPage.enterSearchRequest("XX")
                .pressEnter()
                .changeFocus();
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(customButtonPage.initCustomButtons(), beforeSearch, "Search happened when enter 2 symbols");

        String helperText = customButtonPage.enterSearchRequest(randomSearchRequest)
                .getHelperText();
        softAssert.assertEquals(helperText, getViewResult(randomSearchRequest), "Incorrect helper text");

        customButtonPage = customButtonPage.pressEnter();
        softAssert.assertTrue(customButtonPage.isNothingFoundDisplayed(), "Incorrect message on empty search result");

        customButtonPage.clearSearch()
                .changeFocus();
        customButtonPage.enterSearchRequest(buttonName);
        softAssert.assertEquals(customButtonPage.getHelperText(), getViewResult(buttonName),
                "Incorrect helper text on button name search");

        customButtonPage.viewResultsFor();
        List<CustomButton> searchResult = customButtonPage.initCustomButtons();
        assertEquals(searchResult.size(), 1, "Search by name works incorrect");
        softAssert.assertEquals(searchResult.get(0).getDescription(), buttonName, "Incorrect search result by name");
        softAssert.assertEquals(customButtonPage.getSearchText(), searchResultText(buttonName),
                "Incorrect message in search field");

        customButtonPage.clearSearch()
                .changeFocus();
        softAssert.assertEquals(customButtonPage.initCustomButtons(), beforeSearch, "Search result is not cleaned");

        customButtonPage.enterSearchRequest(buttonLabel)
                .viewResultsFor()
                .changeFocus();
        searchResult = customButtonPage.initCustomButtons();
        assertEquals(searchResult.size(), 1, "Search by name works incorrect");
        softAssert.assertEquals(customButtonPage.getSearchText(), searchResultText(buttonLabel),
                "Incorrect message in search field(label)");
        softAssert.assertEquals(searchResult.get(0).getLabel(), buttonLabel, "Wrong search result by label");

        customButtonPage.clearSearch()
                .changeFocus();
        softAssert.assertEquals(customButtonPage.initCustomButtons(), beforeSearch, "Search result is not cleaned after label search");
        customButtonPage.enterSearchRequest(thirdFlowName)
                .pressEnter()
                .changeFocus();
        searchResult = customButtonPage.initCustomButtons();
        assertEquals(searchResult.size(), 1, "Search by flow works incorrect");
        softAssert.assertEquals(customButtonPage.getSearchText(), searchResultText(thirdFlowName),
                "Incorrect message in search field(flow)");
        softAssert.assertEquals(searchResult.get(0).getFlow(), thirdFlowName, "wrong search result by flow");

        customButtonPage.clearSearch()
                .changeFocus();
        softAssert.assertEquals(customButtonPage.initCustomButtons(), beforeSearch, "Search result is not cleaned(flow)");

        customButtonPage.enterSearchRequest(partOfButtonName)
                .pressEnter()
                .changeFocus();
        searchResult = customButtonPage.initCustomButtons();
        assertTrue(searchResult.size() > 0, "Search by part of button name works incorrect");
        softAssert.assertTrue(searchResult.get(0).getDescription().contains(partOfButtonName),
                "wrong search result by part of button name");
        softAssert.assertAll();
    }

    private String getViewResult(String request) {
        return String.format("View results for \"%s\"", request);
    }

    private String searchResultText(String request) {
        return String.format("Search results for:  %s(%d)", request, 1);
    }

    private void createCustomButtons() throws IOException {
        Slate flow = createFlowWithDocument(fileToUpload);
        Slate secondFlow = createFlowWithDocument(fileToUpload);
        Slate thirdFlow = createFlowWithDocument(fileToUpload);

        thirdFlowName = thirdFlow.name;
        createCustomButton(flow.id, flow.name, DEFAULT, buttonName, getRandomString(5), RUN_FLOW, Collections.singletonList(ACCOUNT), null);
        createCustomButton(secondFlow.id, secondFlow.name, DEFAULT, getRandomString(5), buttonLabel, RUN_FLOW, Collections.singletonList(ACCOUNT), null);
        createCustomButton(thirdFlow.id, thirdFlow.name, DEFAULT, getRandomString(5), getRandomString(5), RUN_FLOW, Collections.singletonList(ACCOUNT), null);
    }
}
