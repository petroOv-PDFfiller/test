package pages.salesforce.app.airSlate_app.admin_tools.tabs;

import core.check.SoftCheck;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import pages.salesforce.app.airSlate_app.admin_tools.ASAppAdminToolsPage;
import pages.salesforce.app.airSlate_app.admin_tools.entities.TeamMate;
import pages.salesforce.enums.admin_tools.ASAppTeammateSort;
import pages.salesforce.enums.admin_tools.ASAppTeammateStatus;
import utils.Logger;

import java.util.ArrayList;
import java.util.List;

import static core.ProductBasicTest.getEnumCapitalizedName;
import static core.check.Check.checkEquals;
import static pages.salesforce.enums.admin_tools.ASAppAdminToolTabs.TEAMMATES;

public class TeammatesTab extends ASAppAdminToolsPage {

    public TeammatesTab(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        skipAdminToolLoader(20);
        checkEquals(getActiveTabName(), TEAMMATES.getName(), "Teammates tab is not opened");
    }

    @Step
    public void checkIsTeammatesPageReadOnly() {
        By actionButton = By.xpath("//*[contains(@class, 'action__')]//button");

        SoftCheck softCheck = new SoftCheck();
        for (int i = 0; i < getNumberOfElements(actionButton); i++) {
            softCheck.checkFalse(isElementEnabled(actionButton, 2), "Action button #" + (i + 1) + "is not disabled");
        }
        softCheck.checkAll();
    }

    @Step
    public List<TeamMate> initTeammates() {
        By teammate = By.xpath("//*[@data-test='teammateRow']");

        List<WebElement> teammateElements = driver.findElements(teammate);
        List<TeamMate> teammateList = new ArrayList<>();
        teammateElements.forEach(element -> teammateList.add(getTeammate(element)));
        return teammateList;
    }

    private TeamMate getTeammate(WebElement element) {
        By teammateName = By.xpath(".//*[contains(@class, 'name__')]");
        By teammateEmail = By.xpath(".//*[contains(@class, 'email__')]");
        By teammateProfile = By.xpath(".//*[contains(@class, 'profile__')]");
        By teammateStatus = By.xpath(".//*[contains(@class, 'status__')]");

        SoftCheck softCheck = new SoftCheck();
        softCheck.checkEquals(element.findElements(teammateName).size(), 1, "teammate name not present");
        softCheck.checkEquals(element.findElements(teammateEmail).size(), 1, "teammate email not present");
        softCheck.checkEquals(element.findElements(teammateProfile).size(), 1, "teammate profile not present");
        softCheck.checkEquals(element.findElements(teammateStatus).size(), 1, "teammate status not present");
        softCheck.checkAll();
        String name = element.findElement(teammateName).getAttribute("title");
        String profile = element.findElement(teammateProfile).getAttribute("textContent");
        String email = element.findElement(teammateEmail).getAttribute("title");
        String statusValue = element.findElement(teammateStatus).getAttribute("textContent");
        ASAppTeammateStatus status = ASAppTeammateStatus.getTeammateStatusByValue(statusValue);

        return new TeamMate(name, email, profile, status);
    }

    @Step
    public TeammatesTab sort(ASAppTeammateSort.SortBy sortBy) {
        By sortOption = By.xpath("//*[@data-test='listItem' and .='" + getEnumCapitalizedName(sortBy) + "']");

        Logger.info("Sort teammates by " + sortBy);
        openSortDropdown();
        checkIsElementDisplayed(sortOption, 4, sortBy + " sort option");
        click(sortOption);
        isOpened();
        return this;
    }

    @Step
    public void order(ASAppTeammateSort.OrderBy orderBy) {
        By orderOption = By.xpath("//*[@data-test='listItem' and .='" + getEnumCapitalizedName(orderBy) + "']");

        Logger.info("Order teammates in " + orderBy);
        openSortDropdown();
        checkIsElementDisplayed(orderOption, 4, orderBy + " order option");
        click(orderOption);
        isOpened();
    }

    @Step
    private TeammatesTab openSortDropdown() {
        By sortStatus = By.xpath("//*[contains(@class, 'tableSubHeader___')]//*[contains(@class, 'dropdown__')]");
        By sortMenu = By.xpath("//*[contains(@class, 'tableSubHeader___')]//*[contains(@class, 'listWrapper__')]");

        checkIsElementDisplayed(sortStatus, 2, "Sort status field");
        click(sortStatus);
        checkIsElementDisplayed(sortMenu, 4, "Sort menu dropdown");
        return this;
    }

    public String getSortStatusText() {
        By statusText = By.xpath("//*[contains(@class, 'dropdown__')]//*[contains(@class, 'value__')]");

        checkIsElementDisplayed(statusText, 2, "Sort status text");
        return getAttribute(statusText, "textContent");
    }
}
