package pages.salesforce.app.airSlate_app.admin_tools.tabs;

import core.check.SoftCheck;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.airSlate_app.admin_tools.ASAppAdminToolsPage;
import pages.salesforce.app.airSlate_app.admin_tools.popups.DisconnectWorkspacePopUp;
import utils.Logger;

import java.util.HashMap;
import java.util.Map;

import static core.check.Check.checkEquals;
import static core.check.Check.checkTrue;
import static data.salesforce.SalesforceTestData.ASAppPageTexts.*;
import static pages.salesforce.enums.admin_tools.ASAppAdminToolTabs.WORKSPACE;

public class WorkspaceTab extends ASAppAdminToolsPage {

    private By btnDisconnectWorkspace = By.xpath("//button[.='Disconnect Workspace']");

    public WorkspaceTab(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        skipAdminToolLoader(20);
        checkEquals(getActiveTabName(), WORKSPACE.getName(), "Workspace tab is not opened");
    }

    @Step
    public boolean isWorkspaceConnected() {
        return isElementPresent(btnDisconnectWorkspace);
    }

    @Step
    public DisconnectWorkspacePopUp disconnectWorkspace() {
        checkIsElementDisplayed(btnDisconnectWorkspace, 4, "Disconnect workspace button");
        click(btnDisconnectWorkspace);
        return initExpectedPage(DisconnectWorkspacePopUp.class);
    }

    @Step
    public WorkspaceTab selectWorkspace(String name) {
        By workspace = By.xpath("//*[contains(@class, 'workspaceItem__') and contains(.,'" + name + "')]//button");

        checkTrue(isElementPresent(workspace, 5), name + " Workspace is not present");
        scrollTo(workspace);
        click(workspace);
        isOpened();
        checkTrue(isWorkspaceConnected(), name + " Workspace is not connected");
        return this;
    }

    @Step
    public WorkspaceTab setCompanyName(String companyName) {
        By inputCompanyName = By.name("name");

        checkIsElementDisplayed(inputCompanyName, 4, "Company name input");
        type(inputCompanyName, companyName);
        return this;
    }

    @Step
    public WorkspaceTab setCompanySubdomain(String subdomain) {
        By inputCompanySubdomain = By.name("subdomain");

        checkIsElementDisplayed(inputCompanySubdomain, 4, "Company subdomain input");
        type(inputCompanySubdomain, subdomain);
        checkEquals(getAttribute(inputCompanySubdomain, "value"), subdomain.toLowerCase(),
                "Subdomain is not convert to lower case");
        return this;
    }

    @Step
    public WorkspaceTab selectCompanyIndustry(String industry) {
        By selectorIndustry = By.xpath("//label[text()='Great! What industry does your company operate in?']" +
                "/following-sibling::div//div[contains(@class, 'Select-control')]");
        By industryOption = By.xpath("//*[@aria-label='" + industry + "']");


        checkIsElementDisplayed(selectorIndustry, 4, "Industry select");
        click(selectorIndustry);
        checkIsElementDisplayed(industryOption, 4, industry + " industry option");
        click(industryOption);
        return this;
    }

    @Step
    public Map<String, String> getConnectedWorkspaceInfo() {
        By companyName = By.xpath("//*[text()='Company name']/following-sibling::div[contains(@class, 'rowData__')]");
        By companyIndustry = By.xpath("//*[text()='Industry']/following-sibling::div[contains(@class, 'rowData__')]");
        By companySize = By.xpath("//*[text()='Company size']/following-sibling::div[contains(@class, 'rowData__')]");

        checkIsElementDisplayed(companyName, 4, "Company name field");
        checkIsElementDisplayed(companyIndustry, 4, "Company industry field");
        checkIsElementDisplayed(companySize, 4, "Company size field");
        Map<String, String> workspaceInfo = new HashMap<>();
        workspaceInfo.put("companyName", getText(companyName));
        workspaceInfo.put("companyIndustry", getText(companyIndustry));
        workspaceInfo.put("companySize", getText(companySize));
        return workspaceInfo;
    }

    @Step
    public WorkspaceTab selectCompanySize(String size) {
        By selectorSize = By.xpath("//label[text()='How big is your company?']/following::div[contains(@class, 'Select-control')]");
        By sizeOption = By.xpath("//*[@aria-label='" + size + "']");


        checkIsElementDisplayed(selectorSize, 4, "Size select");
        click(selectorSize);
        checkIsElementDisplayed(sizeOption, 4, size + " size option");
        click(sizeOption);
        return this;
    }

    public String getSubdomainErrorText() {
        By error = By.xpath("//*[contains(@class, 'workspaceInput__')]//*[contains(@class, 'error')]");
        checkIsElementDisplayed(error, 4, "Subdomain error");
        return getText(error);
    }

    @Step
    public WorkspaceTab createNewWorkspace() {
        By btnCreateWorkspace = By.xpath("//*[contains(@class, 'workspaceCreateWrap__')]//button");

        Logger.info("Create new workspace");
        checkIsElementDisplayed(btnCreateWorkspace, 6, "Create workspace button");
        click(btnCreateWorkspace);
        isOpened();
        return this;
    }

    @Step
    public void checkUnconnectedWorkspacePageLabels() {
        By pageTitle = By.xpath("//p[contains(@class, 'title__')]");
        By pageSubtitle = By.xpath("//p[contains(@class, 'subtitle__')]");

        SoftCheck softCheck = new SoftCheck();
        softCheck.checkEquals(getAttribute(pageTitle, "textContent"), CHOOSE_WORKSPACE_TITLE, "Incorrect page title text");
        softCheck.checkEquals(getAttribute(pageSubtitle, "textContent"), CHOOSE_WORKSPACE_SUBTITLE, "Incorrect page subtitle text");
    }

    @Step
    public WorkspaceTab backToChooseWorkspace() {
        By btnBackToChooseWorkspace = By.xpath("//button[.='Back to Choose Workspace']");
        checkIsElementDisplayed(btnBackToChooseWorkspace, 4, "Back to choose workspace button");
        click(btnBackToChooseWorkspace);
        isOpened();
        return this;
    }

    @Step
    public WorkspaceTab finishCreateNewWorkspace() {
        By btnCreateWorkspace = By.xpath("//button[.='Create Workspace']");
        checkIsElementDisplayed(btnCreateWorkspace, 4, "Create workspace button");
        click(btnCreateWorkspace);
        isOpened();
        return this;
    }

    @Step
    public void checkCreateWorkspacePageLabels() {
        By pageTitle = By.xpath("//p[contains(@class, 'title__')]");
        By pageSubtitle = By.xpath("//p[contains(@class, 'subtitle__')]");
        By labelCompanyLogo = By.xpath("//label[text()='Company logo']");
        By labelSubdomain = By.xpath("//label[@for='subdomain']");
        By labelCompanyName = By.xpath("//label[@for='name']");
        By labelCompanyIndustry = By.xpath("//label[text()='Great! What industry does your company operate in?']");
        By labelCompanySize = By.xpath("//label[text()='How big is your company?']");

        SoftCheck softCheck = new SoftCheck();
        softCheck.checkEquals(getAttribute(pageTitle, "textContent"), TELL_US_ABOUT_YOUR_COMPANY_TITLE, "Incorrect page title text");
        softCheck.checkEquals(getAttribute(pageSubtitle, "textContent"), TELL_US_ABOUT_YOUR_COMPANY_SUBTITLE, "Incorrect page subtitle text");
        softCheck.checkTrue(isElementDisplayed(labelCompanyLogo, 2), "Company logo label is not displayed");
        softCheck.checkTrue(isElementDisplayed(labelSubdomain, 2), "Company subdomain label is not displayed");
        softCheck.checkEquals(getAttribute(labelSubdomain, "textContent"), "Your workspace URL(letters" +
                ", numbers and dashes only)", "Incorrect company subdomain text");
        softCheck.checkTrue(isElementDisplayed(labelCompanyName, 2), "Company name label is not displayed");
        softCheck.checkEquals(getAttribute(labelCompanyName, "textContent"), "What's your company name" +
                "?", "Incorrect company name label text");
        softCheck.checkTrue(isElementDisplayed(labelCompanyIndustry, 2), "Company industry label is not displayed");
        softCheck.checkTrue(isElementDisplayed(labelCompanySize, 2), "Company size label is not displayed");
        softCheck.checkAll();
    }

    @Step
    public boolean isExistingWorkspacesListDisplayed() {
        By existingWorkspaces = By.xpath("//*[@data-test='workspaceList']");
        return isElementDisplayed(existingWorkspaces, 4);
    }

    @Step
    public boolean isCreateWorkspaceBlockIsPresent() {
        By newWorkspace = By.xpath("//*[contains(@class,'workspaceCreateWrap__')]");
        return isElementDisplayed(newWorkspace, 4);
    }

    @Step
    public void checkDisconnectWorkspaceText() {
        By disconnectText = By.xpath("//*[contains(@class, 'footerRow__')]//*[contains(@class, 'rowDescription__')]");
        checkEquals(getAttribute(disconnectText, "textContent"), AFTER_DISCONNECTING_WORKSPACE,
                "Incorrect disconnect workspace label");
    }

    @Step
    public boolean isSubdomainErrorIsShown() {
        By error = By.xpath("//*[contains(@class, 'workspaceInput__')]//*[contains(@class, 'error')]");
        return isElementPresentAndDisplayed(error, 4);
    }
}
