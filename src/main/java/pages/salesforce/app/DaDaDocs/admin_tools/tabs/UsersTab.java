package pages.salesforce.app.DaDaDocs.admin_tools.tabs;

import core.check.SoftCheck;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.DaDaDocs.admin_tools.AdminToolsPage;
import pages.salesforce.app.DaDaDocs.admin_tools.entities.DaDaDocsUser;
import utils.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static core.check.Check.checkEquals;
import static core.check.Check.checkTrue;
import static data.salesforce.SalesforceTestData.SalesforceUserGetMethods.*;
import static data.salesforce.SalesforceTestData.SortTypes.ASC_SORT;
import static data.salesforce.SalesforceTestData.SortTypes.DESC_SORT;
import static data.salesforce.SalesforceTestData.UserLicense.ACTIVE;
import static data.salesforce.SalesforceTestData.UserLicense.NOT_ACTIVE;

public class UsersTab extends AdminToolsPage {
    public String userId = "";
    public By activeUsers = By.xpath("//div[contains(@class, 'license__') and text()='Active']");
    public List<DaDaDocsUser> users;
    private By usersTable = By.xpath("//div[contains(@class,'table__')]");
    private By deactivateUser = By.xpath("//div[contains(@class, 'license__') and text()='Not active']/parent::div");
    private By btnActivateUsers = By.xpath("//button[contains(@class, 'btnActivate__')]");
    private By btnDeactivateUsers = By.xpath("//button[contains(@class, 'btnDeactivate__')]");
    private By userFiltersDropDown = By.xpath("//div[contains(@class, 'dropdown__')]");
    private String userActivatedMessage = "User has been successfully activated!";
    private String userDeactivatedMessage = "User has been successfully deactivated!";

    public UsersTab(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        super.isOpened();
        checkTrue(isElementNotDisplayed(newLoader, 40), "Loader is still present");
        checkTrue(isElementPresentAndDisplayed(usersTable, 15), "users table is not displayed");
        initAllUsers();
    }

    @Step("Activate user")
    public UsersTab activateUser() {
        checkTrue(isElementPresentAndDisplayed(deactivateUser, 10), "Deactivated user is not present");
        userId = getAttribute(deactivateUser, "data-id");
        return activateUser(userId);
    }

    @Step("Activate user(user-id = {0})")
    public UsersTab activateUser(String userId) {
        checkTrue(isElementPresentAndDisplayed(deactivateUser, 10), "Deactivated user is not present");
        By userCheckBox = By.xpath("//div[@data-id='" + userId + "']//label");
        By userActionButton = By.xpath("//div[@data-id='" + userId + "']//button");
        By userLicenseButton = By.xpath("//div[@data-id='" + userId + "']//div[contains(@class, 'license__')]");

        checkTrue(isElementNotDisplayed(newLoader, 20), "Loader is still present");
        checkTrue(isElementPresentAndDisplayed(userCheckBox, 5), "Check box is not present");
        click(userCheckBox);

        checkTrue(isElementNotDisplayed(newLoader, 20), "Loader is still present");
        checkEquals(getAttribute(btnActivateUsers, "innerText"), "Activate (1)", "Wrong activate button text");
        checkEquals(getText(userActionButton), "Activate", "Wrong user action button text");
        click(btnActivateUsers);

        checkTrue(notificationMessageContainsText(userActivatedMessage), "Wrong notification message, expected: " + userActivatedMessage);

        isOpened();
        checkEquals(getText(userActionButton), "Deactivate", "Wrong user action button text");
        checkEquals(getText(userLicenseButton), ACTIVE, "Wrong user license");
        Logger.info("User " + userId + " is activated");
        return this;
    }

    @Step("Deactivate user by id: ({0})")
    public UsersTab deactivateUser(String userId) {
        By activeUser = By.xpath("//div[@data-id='" + userId + "']");
        checkTrue(isElementPresentAndDisplayed(activeUser, 10), "Active user is not present");
        By userCheckBox = By.xpath("//div[@data-id='" + userId + "']//label");
        By userActionButton = By.xpath("//div[@data-id='" + userId + "']//button");
        By userLicenseButton = By.xpath("//div[@data-id='" + userId + "']//div[contains(@class, 'license__')]");

        checkTrue(isElementNotDisplayed(newLoader, 20), "Loader is still present");
        checkTrue(isElementPresentAndDisplayed(userCheckBox, 5), "Check box is not present");
        click(userCheckBox);

        checkTrue(isElementNotDisplayed(newLoader, 20), "Loader is still present");
        checkEquals(getAttribute(btnDeactivateUsers, "innerText"), "Deactivate (1)", "Wrong deactivate button text");
        checkEquals(getText(userActionButton), "Deactivate", "Wrong user action button text");
        click(btnDeactivateUsers);

        checkTrue(isElementPresentAndDisplayed(popUp, 10), "Popup is not displayed");
        checkTrue(isElementPresentAndDisplayed(btnAcceptPopUp, 10), "Deactivate button in popup is not present");
        click(btnAcceptPopUp);

        checkTrue(notificationMessageContainsText(userDeactivatedMessage), "Wrong notification message, expected: " + userDeactivatedMessage);

        isOpened();
        checkEquals(getText(userActionButton), "Activate", "Wrong user action button text");
        checkEquals(getText(userLicenseButton), NOT_ACTIVE, "Wrong user license");
        Logger.info("User " + userId + " is deactivated");
        return this;
    }

    @Step("Is user list contains email: {0}")
    public boolean isUserListContainsEmail(String email) {
        for (DaDaDocsUser user : users) {
            if (user.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }

    @Step("Search user by email: {0}")
    public UsersTab searchUserByEmail(String emailPart) {
        By emailSearch = By.xpath("//input[@name='search']");
        By searchTooltip = By.xpath("//div[contains(@class, 'searchTooltip__')]");

        checkTrue(isElementPresentAndDisplayed(emailSearch, 5), "email search input is not present");
        click(emailSearch);

        checkTrue(isElementPresentAndDisplayed(searchTooltip, 5), "Search tooltip is not present");
        checkEquals(getAttribute(searchTooltip, "innerText"), "To view search enter minimum 3 characters", "Search tooltip default text is wrong");
        type(emailSearch, emailPart);
        checkEquals(getAttribute(searchTooltip, "innerText"), "View results for \"" + emailPart + "\"", "Search tooltip text is wrong");
        click(searchTooltip);

        isOpened();
        return this;
    }

    @Step("Clear search parameter")
    public UsersTab clearSearchParameter() {
        By searchText = By.xpath("//div[contains(@class, 'value__')]");
        By searchTooltip = By.xpath("//div[contains(@class, 'searchTooltip__')]");
        By emailSearch = By.xpath("//input[@name='search']");

        checkTrue(isElementPresentAndDisplayed(searchText, 5), "email search input is not present");
        click(searchText);

        checkTrue(isElementPresentAndDisplayed(searchTooltip, 5), "Search tooltip is not present");
        clear(emailSearch);
        checkEquals(getAttribute(searchTooltip, "innerText"), "To view search enter minimum 3 characters", "Search tooltip default text is wrong");
        click(searchTooltip);

        isOpened();
        return this;
    }

    @Step("is highlighted part correctly marked")
    public boolean isHighlightedPartCorrectlyMarked(String emailPart) {
        By highlightedEmails = By.xpath("//div[contains(@class, 'email__')]/span[contains(@class, 'highlightEmail__')]");
        checkTrue(getNumberOfElements(highlightedEmails) >= users.size(), "Highlighted emails < than users count");

        for (int i = 0; i < getNumberOfElements(highlightedEmails); i++) {
            String highlightedText = getText(driver.findElements(highlightedEmails).get(i));
            if (!highlightedText.equalsIgnoreCase(emailPart)) {
                return false;
            }
        }

        return true;
    }

    @Step("Check {0} sort by {1}")
    private boolean checkSortBy(String sortType, String sortCriteria) {
        try {
            Method getMethod = DaDaDocsUser.class.getMethod(sortCriteria);
            if (sortType.equals(DESC_SORT)) {
                return checkDESCSort(getMethod);
            } else {
                return checkASCSort(getMethod);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Step("Deactivate all users")
    public UsersTab deactivateAllUsers() {
        Logger.info("Deactivate all users");
        if (isActiveUserIsPresent()) {
            By btnSelectAllUsers = By.xpath("(//div[contains(@class, 'tableHeader__')]/div)[1]//label");

            checkTrue(isElementPresentAndDisplayed(btnSelectAllUsers, 5), "select all users btn is not present");
            click(btnSelectAllUsers);
            checkTrue(isElementNotDisplayed(newLoader, 20), "Loader is still present");
            click(btnDeactivateUsers);

            checkTrue(isElementPresentAndDisplayed(popUp, 10), "Popup is not displayed");
            checkTrue(isElementPresentAndDisplayed(btnAcceptPopUp, 10), "Deactivate button in popup is not present");
            click(btnAcceptPopUp);
        }

        isOpened();
        for (DaDaDocsUser user : users) {
            checkEquals(user.getLicense(), NOT_ACTIVE, "Wrong license status");
        }

        return this;
    }

    @Step("Set user list to {0}")
    public void setUsersTo(List<DaDaDocsUser> defaultUsers) {
        initAllUsers();
        for (DaDaDocsUser defaultUser : defaultUsers) {
            String defaultUserId = defaultUser.getId();
            for (DaDaDocsUser user : users) {
                if (defaultUserId.equals(user.getId()) && !defaultUser.getLicense().equals(user.getLicense())) {
                    if (defaultUser.getLicense().equals(NOT_ACTIVE)) {
                        deactivateUser(defaultUserId);
                    } else {
                        activateUser(defaultUserId);
                    }
                }
            }
        }
    }

    @Step("Check all user profile filters")
    public void checkAllUserProfileFilters() {
        By profileFilters = By.xpath("((//div[contains(@class, 'listGroup__')])[2])//div[@data-test='listItem']");

        disableUserFilters();
        initAllUsers();
        Map<String, Integer> profiles = new HashMap<>();
        for (DaDaDocsUser user : users) {
            String currentProfile = user.getProfile();

            if (profiles.containsKey(currentProfile)) {
                int currentValue = profiles.get(currentProfile);
                profiles.put(currentProfile, currentValue + 1);
            } else {
                profiles.put(currentProfile, 1);
            }
        }

        for (Map.Entry<String, Integer> profile : profiles.entrySet()) {
            filterByProfile(profile.getKey(), profile.getValue());
        }

        checkEquals(getNumberOfElements(profileFilters), profiles.keySet().size(), "Wrong profile filters count");
    }

    @Step("Check all user license filters")
    public void checkAllUserLicenseFilters() {
        By licenseFilters = By.xpath("((//div[contains(@class, 'listGroup__')])[1])//div[@data-test='listItem']");

        disableUserFilters();
        initAllUsers();
        Map<String, Integer> profiles = new HashMap<>();
        SoftCheck softCheck = new SoftCheck();
        for (DaDaDocsUser user : users) {
            String currentLicense = user.getLicense();

            softCheck.checkTrue(currentLicense.equals(ACTIVE) || currentLicense.equals(NOT_ACTIVE), "Wrong license name");

            if (profiles.containsKey(currentLicense)) {
                int currentValue = profiles.get(currentLicense);
                profiles.put(currentLicense, currentValue + 1);
            } else {
                profiles.put(currentLicense, 1);
            }
        }
        for (Map.Entry<String, Integer> profile : profiles.entrySet()) {
            filterByLicense(profile.getKey(), profile.getValue());
        }

        softCheck.checkEquals(getNumberOfElements(licenseFilters), 3, "Wrong license filters count");
        softCheck.checkAll();
    }

    @Step("filter by profile({0})")
    public void filterByProfile(String profileName, int defaultUserCount) {
        By profileFilter = By.xpath("((//div[contains(@class, 'listGroup__')])[2])//span[contains(text(), '" + profileName + "')]");

        int filterUserCount = selectUserFilter(profileFilter);

        SoftCheck softCheck = new SoftCheck();
        softCheck.checkEquals(filterUserCount, users.size(), "Wrong user count after filter is applied");
        softCheck.checkEquals(filterUserCount, defaultUserCount, "Wrong user count. In assertion to full list of users");
        for (DaDaDocsUser user : users) {
            softCheck.checkEquals(user.getProfile(), profileName, "Wrong user profile is shown");
        }
        softCheck.checkAll();
    }

    @Step("filter by license({0})")
    public void filterByLicense(String licenseName, int defaultUserCount) {
        By licenseFilter = By.xpath("((//div[contains(@class, 'listGroup__')])[1])//span[contains(text(), 'Only " + licenseName.toLowerCase() + "')]");

        int filteredUserCount = selectUserFilter(licenseFilter);

        SoftCheck softCheck = new SoftCheck();
        softCheck.checkEquals(filteredUserCount, users.size(), "Wrong user count after filter is applied");
        softCheck.checkEquals(filteredUserCount, defaultUserCount, "Wrong user count. In assertion to full list of users");
        for (DaDaDocsUser user : users) {
            softCheck.checkEquals(user.getLicense(), licenseName, "Wrong user license is shown");
        }
        softCheck.checkAll();
    }

    @Step
    public int selectUserFilter(By userFilter) {
        openUserFilters();
        checkTrue(isElementPresentAndDisplayed(userFilter, 5), "Profile filter: " + " is not displayed");

        String filterFullText = getAttribute(userFilter, "textContent");
        int filteredUserCount = Integer.parseInt(filterFullText.substring(filterFullText.lastIndexOf('(') + 1,
                filterFullText.lastIndexOf(')')));
        click(userFilter);
        Logger.info("Select " + filterFullText + " user filter");

        isOpened();

        return filteredUserCount;
    }

    @Step("Disable user filters")
    public UsersTab disableUserFilters() {
        if (!isUserFiltersDisabled()) {
            By btnAllUsers = By.xpath("//div[contains(@class, 'listWrapper__')]//span[contains(text(), 'All users')]");

            openUserFilters();
            checkTrue(isElementPresentAndDisplayed(btnAllUsers, 5), "All users filter is not present");
            click(btnAllUsers);

            isOpened();
        }
        Logger.info("User filters is disabled");

        return this;
    }

    @Step
    public UsersTab openUserFilters() {
        By filtersList = By.xpath("//div[contains(@class, 'list__')]");

        checkTrue(isElementPresentAndDisplayed(userFiltersDropDown, 5), "User filters dropdown is not present");
        click(userFiltersDropDown);
        Logger.info("User filters dropdown is opened");

        isOpened();
        checkTrue(isElementPresentAndDisplayed(filtersList, 5), "Filters list is not displayed");
        return this;
    }

    public String getUserCurrentFilter() {
        By selectedFilter = By.xpath("//span[contains(@class, 'show__')]");
        return getAttribute(selectedFilter, "textContent");
    }

    public boolean isUserFiltersDisabled() {
        return getUserCurrentFilter().contains("All users");
    }

    private void initAllUsers() {
        Logger.info("Init user list");
        By userIds = By.xpath("//div[@data-id]");
        users = new ArrayList<>();

        for (int i = 0; i < getNumberOfElements(userIds); i++) {
            String userId = driver.findElements(userIds).get(i).getAttribute("data-id");
            users.add(getUser(userId));
        }
    }

    private DaDaDocsUser getUser(String userId) {
        By userFullName = By.xpath("(//div[@data-id='" + userId + "']/div)[2]");
        By userEmail = By.xpath("(//div[@data-id='" + userId + "']/div)[3]");
        By userProfile = By.xpath("(//div[@data-id='" + userId + "']/div)[4]");
        By userLicense = By.xpath("(//div[@data-id='" + userId + "']/div)[5]");

        checkTrue(isElementPresentAndDisplayed(userFullName, 5), "For user " + userId + " full name value is not present");
        checkTrue(isElementPresentAndDisplayed(userEmail, 5), "For user " + userId + " email value is not present");
        checkTrue(isElementPresentAndDisplayed(userProfile, 5), "For user " + userId + " profile value is not present");
        checkTrue(isElementPresentAndDisplayed(userLicense, 5), "For user " + userId + " license value is not present");

        return new DaDaDocsUser(userId,
                getText(userFullName),
                getText(userEmail),
                getText(userProfile),
                getText(userLicense));
    }

    @Step("Sort user by {0}")
    public boolean sortUsersBy(String name) {
        By criteria = chooseSortCriteria(name);

        checkTrue(isElementPresentAndDisplayed(criteria, 5), criteria + " is mot present");
        click(criteria);
        checkTrue(isElementNotDisplayed(newLoader, 20), "Loader is still present");
        initAllUsers();

        if (getAttribute(criteria, "class").contains("isDescend__")) {
            Logger.info("Check " + DESC_SORT + " sort by " + name);
            return checkSortBy(DESC_SORT, name);
        } else {
            Logger.info("Check " + DESC_SORT + " sort by " + name);
            return checkSortBy(ASC_SORT, name);
        }
    }

    @Step("Choose sort criteria")
    private By chooseSortCriteria(String criteriaName) {
        By criteria = null;
        By fullName = By.xpath("(//div[contains(@class, 'tableHeader__')]/div)[2]");
        By email = By.xpath("(//div[contains(@class, 'tableHeader__')]/div)[3]");
        By profile = By.xpath("(//div[contains(@class, 'tableHeader__')]/div)[4]");
        By license = By.xpath("(//div[contains(@class, 'tableHeader__')]/div)[5]");

        switch (criteriaName) {
            case GET_FULL_NAME:
                criteria = fullName;
                break;
            case GET_EMAIL:
                criteria = email;
                break;
            case GET_PROFILE:
                criteria = profile;
                break;
            case GET_LICENSE:
                criteria = license;
                break;
        }
        return criteria;
    }

    private boolean isActiveUserIsPresent() {
        for (DaDaDocsUser user : users) {
            if (user.getLicense().equals(ACTIVE)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkASCSort(Method getMethod) throws InvocationTargetException, IllegalAccessException {
        for (int i = 1; i < users.size(); i++) {
            if (((String) getMethod.invoke(users.get(i))).compareTo((String) getMethod.invoke(users.get(i - 1))) < 0) {
                return false;
            }
        }

        return true;
    }

    private boolean checkDESCSort(Method getMethod) throws InvocationTargetException, IllegalAccessException {
        for (int i = 0; i < users.size() - 1; i++) {
            if (((String) getMethod.invoke(users.get(i))).compareTo((String) getMethod.invoke(users.get(i + 1))) < 0) {
                return false;
            }
        }

        return true;
    }
}
