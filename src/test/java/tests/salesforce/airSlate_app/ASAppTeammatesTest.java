package tests.salesforce.airSlate_app;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import listeners.WebTestListener;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.app.airSlate_app.admin_tools.ASAppAdminToolsPage;
import pages.salesforce.app.airSlate_app.admin_tools.entities.TeamMate;
import pages.salesforce.app.airSlate_app.admin_tools.tabs.TeammatesTab;
import pages.salesforce.enums.admin_tools.ASAppTeammateSort;
import tests.salesforce.SalesforceAirSlateBaseTest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static pages.salesforce.enums.admin_tools.ASAppAdminToolTabs.TEAMMATES;
import static pages.salesforce.enums.admin_tools.ASAppTeammateSort.OrderBy.ASCENDING;
import static pages.salesforce.enums.admin_tools.ASAppTeammateSort.OrderBy.DESCENDING;
import static pages.salesforce.enums.admin_tools.ASAppTeammateSort.SortBy.*;

@Feature("airSlate app: teammates")
@Listeners(WebTestListener.class)
public class ASAppTeammatesTest extends SalesforceAirSlateBaseTest {

    private String adminToolURL;
    private ASAppAdminToolsPage adminToolsPage;

    @DataProvider(name = "sortPairwise")
    public static Object[][] sortPairwise() {
        return new Object[][]{{NAME, ASCENDING, Comparator.comparing(o -> ((TeamMate) o).getName().toLowerCase())},
                {NAME, DESCENDING, (Comparator<TeamMate>) (o1, o2) -> o2.getName().toLowerCase().compareTo(o1.getName().toLowerCase())},
                {PROFILE, ASCENDING, Comparator.comparing(o -> ((TeamMate) o).getProfile().toLowerCase())},
                {PROFILE, DESCENDING, (Comparator<TeamMate>) (o1, o2) -> o2.getProfile().toLowerCase().compareTo(o1.getProfile().toLowerCase())},
                {STATUS, ASCENDING, Comparator.comparing(o -> ((TeamMate) o).getStatus().getValue().toLowerCase())},
                {STATUS, DESCENDING, (Comparator<TeamMate>) (o1, o2) -> o2.getStatus().getValue().toLowerCase().compareTo(o1.getStatus().getValue().toLowerCase())},
                {EMAIL, ASCENDING, Comparator.comparing(o -> ((TeamMate) o).getEmail().toLowerCase())},
                {EMAIL, DESCENDING, (Comparator<TeamMate>) (o1, o2) -> o2.getEmail().toLowerCase().compareTo(o1.getEmail().toLowerCase())},
        };
    }

    @Parameters({"recipientEmail"})
    @BeforeTest
    public void setUp(@Optional("pdf_sf_rnt@support.pdffiller.com") String recipientEmail) throws IOException, URISyntaxException {
        SalesAppBasePage salesAppBasePage = loginWithDefaultCredentials();
        configurateSalesforceOrg();
        adminToolsPage = getAdminToolsPage(salesAppBasePage);
        adminToolURL = getDriver().getCurrentUrl();
    }

    @BeforeMethod
    public void getAdminToolPage() {
        getUrl(adminToolURL);
        adminToolsPage.isOpened();
    }

    @Story("Should correctly sort by all columns")
    @Test(dataProvider = "sortPairwise", testName = "Sort Teammates by {0} in {1} order")
    public void sortTeammates(ASAppTeammateSort.SortBy sortBy,
                              ASAppTeammateSort.OrderBy orderIn,
                              Comparator<TeamMate> teammateComparator) {
        TeammatesTab teammatesPage = adminToolsPage.openTab(TEAMMATES);

        if (!(sortBy.equals(NAME) && (orderIn.equals(ASCENDING)))) {
            teammatesPage.sort(sortBy)
                    .order(orderIn);
        }

        String sortMessage = teammatesPage.getSortStatusText();
        List<TeamMate> teammateList = teammatesPage.initTeammates();
        List<TeamMate> sortedTeammatesList = teammateList.stream().sorted(teammateComparator).collect(Collectors.toList());
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(teammateList, sortedTeammatesList, "Incorrect sort by " + sortBy + " order in " + orderIn);
        softAssert.assertEquals(sortMessage, expectedSortStatusText(sortBy, orderIn), "Incorrect sort status message");
        softAssert.assertAll();
    }

    private String expectedSortStatusText(ASAppTeammateSort.SortBy sort, ASAppTeammateSort.OrderBy order) {
        return String.format("Sort by: %s (%s)", getEnumCapitalizedName(sort), getEnumCapitalizedName(order));
    }
}
