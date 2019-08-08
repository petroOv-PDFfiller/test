package tests.servicenow.servicenow_signnow_app;

import io.qameta.allure.Story;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.servicenow.app.ServiceNowTab;
import pages.servicenow.app.objects.company.CompaniesListPage;
import pages.servicenow.app.objects.company.CompanyPage;

import java.util.List;

/**
 * Created by horobets on Aug 05, 2019
 */
public class ServiceNowSignNowTests extends ServiceNowSignNowBaseTest {

    @Story("SignNow for ServiceNow Buttons Test")
    @Test
    public void signNowButtonsTest() {

        // check SignNow App buttons on Vendor record type
        CompaniesListPage vendorsPage = serviceNowBasePage.openTab(ServiceNowTab.COMPANIES);
        CompanyPage companyPage = vendorsPage.openObject(1);

        vendorsPage.switchToPageContentFrame();

        List<String> toolbarButtons = companyPage.getToolbarButtonLabels();

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(toolbarButtons.stream().filter(buttonName -> "SignNow".equals(buttonName)).toArray().length, 2, "2 SignNow Buttons were not found on the page.");

        softAssert.assertEquals(toolbarButtons.stream().filter(buttonName -> "Sign In Person".equals(buttonName)).toArray().length, 2, "2 Sign In Person Buttons were not found on the page.");

        softAssert.assertEquals(toolbarButtons.stream().filter(buttonName -> "SignNow Settings".equals(buttonName)).toArray().length, 2, "2 SignNow Settings Buttons were not found on the page.");

        softAssert.assertEquals(toolbarButtons.stream().filter(buttonName -> "Send Invite".equals(buttonName)).toArray().length, 2, "2 Send Invite Buttons were not found on the page.");

        softAssert.assertEquals(toolbarButtons.stream().filter(buttonName -> "Check Signature Status".equals(buttonName)).toArray().length, 2, "2 Check Signature Status Buttons were not found on the page.");

        softAssert.assertAll();
    }
}
