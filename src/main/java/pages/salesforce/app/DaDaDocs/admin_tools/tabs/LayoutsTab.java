package pages.salesforce.app.DaDaDocs.admin_tools.tabs;

import core.check.SoftCheck;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import pages.salesforce.app.DaDaDocs.admin_tools.AdminToolsPage;
import pages.salesforce.app.DaDaDocs.admin_tools.entities.Layout;
import pages.salesforce.enums.admin_tools.LayoutsTabActions;
import utils.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static core.check.Check.checkEquals;
import static core.check.Check.checkTrue;
import static data.salesforce.SalesforceTestData.SalesforceLayoutsGetMethods.LAYOUT_NAME;
import static data.salesforce.SalesforceTestData.SalesforceLayoutsGetMethods.OBJECT_NAME;
import static data.salesforce.SalesforceTestData.SortTypes.ASC_SORT;
import static data.salesforce.SalesforceTestData.SortTypes.DESC_SORT;
import static pages.salesforce.enums.admin_tools.AdminToolTabs.LAYOUTS;

public class LayoutsTab extends AdminToolsPage {

    private List<Layout> layoutList;
    private By pageHeader = By.xpath("//div[contains(@class, 'pageHeader__') and text()='Add DaDaDocs button to your layouts']");

    public LayoutsTab(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        checkTrue(isElementPresentAndDisplayed(pageHeader, 15), "Layouts header is not loaded");
        checkTrue(isElementNotDisplayed(newLoader, 90), "Loader is still present");
        checkEquals(getActiveTabName(), LAYOUTS.getName(), "Layout page is not opened");
    }

    private List<Layout> initLayouts() {
        By layoutRow = By.xpath("//div[contains(@class, 'layoutRow___')]");
        By objectNameRow = By.xpath("//div[contains(@class, 'tableBody__')]//div[contains(@class, 'objectNameCol__')]");
        By layoutNameRow = By.xpath("//div[contains(@class, 'tableBody__')]//div[contains(@class, 'layoutNameCol__')]");
        By showStatusRow = By.xpath("//div[contains(@class, 'tableBody__')]//input[contains(@class, 'checkbox__input__')]");

        Logger.info("Init all layouts");
        layoutList = new ArrayList<>();
        int numberOfLayouts = getNumberOfElements(layoutRow);

        if (numberOfLayouts > 0) {
            checkEquals(getNumberOfElements(objectNameRow), numberOfLayouts, "Wrong number of object names");
            checkEquals(getNumberOfElements(layoutNameRow), numberOfLayouts, "Wrong number of layout names");
            checkEquals(getNumberOfElements(showStatusRow), numberOfLayouts, "Wrong number of show statuses");


            List<WebElement> objectNameList = driver.findElements(objectNameRow);
            List<WebElement> layoutNameList = driver.findElements(layoutNameRow);
            List<WebElement> showStatusList = driver.findElements(showStatusRow);

            for (int i = 0; i < numberOfLayouts; i++) {
                String objectName = objectNameList.get(i).getAttribute("innerText");
                String layoutName = layoutNameList.get(i).getAttribute("innerText");
                boolean showStatus = showStatusList.get(i).isSelected();
                layoutList.add(new Layout(objectName, layoutName, showStatus));
            }
        }
        return layoutList;
    }

    @Step("Find layouts by name {0}")
    public LayoutsTab enterSearchRequest(String searchRequest) {
        By searchField = By.xpath("//input[@name='search']");
        By searchTooltip = By.xpath("//div[contains(@class, 'searchTooltip__')]");

        checkTrue(isElementPresentAndDisplayed(searchField, 5), "Search field is not displayed");
        click(searchField);
        checkTrue(isElementPresentAndDisplayed(searchTooltip, 10), "Search tooltip is not opened");
        type(searchField, searchRequest);
        String expectedToolMessage;
        if (searchRequest.length() > 2) {
            expectedToolMessage = String.format("View results for \"%s\"", searchRequest);
        } else {
            expectedToolMessage = "To view search enter minimum 3 characters";
        }
        checkEquals(getAttribute(searchTooltip, "textContent"), expectedToolMessage,
                "Wrong text in tooltip");
        isOpened();
        return this;
    }

    @Step
    public LayoutsTab findSearchResults() {
        By searchTooltip = By.xpath("//div[contains(@class, 'searchTooltip__')]");

        checkTrue(isElementPresentAndDisplayed(searchTooltip, 10), "Search tooltip is not opened");
        click(searchTooltip);
        checkTrue(isElementNotDisplayed(searchTooltip, 10), "Search tooltip is still displayed");
        isOpened();
        return this;
    }

    @Step
    public int getNumberOfExpectedLayoutsAfterSearch() {
        String text = getTextFromSearchPanel();
        checkTrue(text.contains("Search results for"), "wrong text in search panel");
        int result = 0;
        try {
            result = Integer.valueOf(text.substring(text.indexOf('(') + 1, text.lastIndexOf(')')));
            return result;
        } catch (NumberFormatException e) {
            Logger.error("Cannot parse value from search panel");
            e.printStackTrace();
        }
        return result;
    }

    @Step("Check highlighted layout name to contains - {0}")
    public void isEveryLayoutHasHighlightedSearchPart(String part) {
        By layoutRow = By.xpath("//div[contains(@class, 'layoutRow___')]");

        int numberOfLayouts = getNumberOfElements(layoutRow);
        By layoutNameRow = By.xpath("//div[contains(@class, 'tableBody__')]//div[contains(@class, 'layoutNameCol__')]");
        checkEquals(getNumberOfElements(layoutNameRow), numberOfLayouts, "Wrong number of layout names");
        SoftCheck softCheck = new SoftCheck();
        for (int i = 1; i < numberOfLayouts + 1; i++) {
            By layoutNameRowHighlighted = By.xpath("(//div[contains(@class, 'tableBody__')]" +
                    "//div[contains(@class, 'layoutNameCol__')])[" + i + "]/span[contains(@class, 'highlightName__')]");

            softCheck.checkTrue(isElementPresent(layoutNameRowHighlighted, 5), "layout name " + i + " is not present");
            softCheck.checkEquals(getAttribute(layoutNameRowHighlighted, "textContent").toLowerCase(), part, "Wrong text highlighted for layout " + i);
        }
        softCheck.checkAll();
    }

    @Step
    public LayoutsTab clearEmptySearchResult() {
        By emptyResultTitle = By.xpath("//div[contains(@class, 'title__')]");
        By emptyResultSubTitle = By.xpath("//div[contains(@class, 'subtitle__')]");
        By btnEmptyResultClear = By.xpath("//div[contains(@class, 'tableBody__')]//button");

        Logger.info("Clear empty search results");
        checkTrue(isElementPresentAndDisplayed(emptyResultTitle, 10), "Nothing was found title is not displayed");
        checkEquals(getAttribute(emptyResultTitle, "textContent"), "Nothing was found", "Wrong title text");
        checkTrue(isElementPresentAndDisplayed(emptyResultSubTitle, 10), "Subtitle is not displayed");
        checkEquals(getAttribute(emptyResultSubTitle, "textContent"), "Clear search and try with another keyword",
                "Wrong subtitle message");
        checkTrue(isElementPresentAndDisplayed(btnEmptyResultClear, 10), "Button clear empty result is not displayed");
        click(btnEmptyResultClear);
        checkTrue(isElementNotDisplayed(btnEmptyResultClear, 5), "Clear button is still displayed");
        isOpened();
        return this;
    }

    @Step
    public LayoutsTab clearSearchResult() {
        By btnClear = By.xpath("//div[contains(@class, 'clearButton__3Wj8A')]");

        Logger.info("Clear search result");
        checkTrue(isElementPresentAndDisplayed(btnClear, 5), "Clear search result button is not displayed");
        click(btnClear);
        checkTrue(isElementNotDisplayed(btnClear, 5), "Button clear is still displayed");
        isOpened();
        return this;
    }

    @Step
    public String getTextFromSearchPanel() {
        By searchString = By.xpath("//div[contains(@class, 'text__')]");
        checkTrue(isElementPresent(searchString), "search panel text is not present");
        return getText(searchString).replaceAll("<.*?>", "").replace("&nbsp;", " ");
    }

    @Step
    public LayoutsTab disableSearchFocus() {
        checkTrue(isElementDisplayed(pageHeader, 2), "Header is not displayed");
        click(pageHeader);
        isOpened();
        return this;
    }


    @Step("Perform page action: [{0}]")
    public LayoutsTab pageAction(LayoutsTabActions action) {
        By pageAction = By.xpath("//button[.='" + action.getActionName() + "']");
        checkTrue(isElementPresent(pageAction), "" + action.getActionName() + " is not present");
        Logger.info("Make " + action.getActionName() + " layouts action");
        if (isElementEnabled(pageAction)) {
            click(pageAction);
        } else {
            Logger.warning(action.getActionName() + " button is disabled");
        }
        isOpened();
        return this;
    }

    public List<Layout> getLayouts() {
        return initLayouts();
    }

    @Step
    public LayoutsTab makeSearchByEnterButton() {
        By searchTooltip = By.xpath("//div[contains(@class, 'searchTooltip__')]");
        By searchField = By.xpath("//input[@name='search']");

        checkTrue(isElementPresentAndDisplayed(searchTooltip, 10), "Search tooltip is not opened");
        type(searchField, Keys.ENTER);
        isOpened();
        return this;
    }

    @Step("Set user list to {0}")
    public void setLayoutsTo(List<Layout> defaultLayouts) {
        initLayouts();
        for (Layout defaultLayout : defaultLayouts) {
            String defaultLayoutName = defaultLayout.getLayoutName();
            for (Layout currentLayout : layoutList) {
                if (defaultLayoutName.equals(currentLayout.getLayoutName()) && defaultLayout.getShow() != (currentLayout.getShow())) {
                    if (defaultLayout.getShow()) {
                        showLayout(defaultLayoutName);
                    } else {
                        hideLayout(defaultLayoutName);
                    }
                }
            }
        }
        pageAction(LayoutsTabActions.SAVE_CHANGES);
    }

    @Step("Hide button in layout - {0}")
    public void hideLayout(String layoutName) {
        By currentStatus = By.xpath("//div[contains(@class, 'layoutNameCol__') and text()= '" + layoutName + "']/parent::div//input");
        By label = By.xpath("//div[contains(@class, 'layoutNameCol__') and text()= '" + layoutName + "']/parent::div//label");

        Logger.info("Hide button in layout - " + layoutName);
        checkTrue(isElementPresent(currentStatus), "show status is not present");
        if (driver.findElement(currentStatus).isSelected()) {
            checkTrue(isElementPresentAndDisplayed(label, 2), "label is not displayed for layout " + layoutName);
            click(label);
        }
    }

    @Step("Show button in layout - {0}")
    public void showLayout(String layoutName) {
        By currentStatus = By.xpath("//div[contains(@class, 'layoutNameCol__') and text()= '" + layoutName + "']/parent::div//input");
        By label = By.xpath("//div[contains(@class, 'layoutNameCol__') and text()= '" + layoutName + "']/parent::div//label");

        Logger.info("Show button in layout - " + layoutName);
        checkTrue(isElementPresent(currentStatus), "show status is not present");
        if (!driver.findElement(currentStatus).isSelected()) {
            checkTrue(isElementPresentAndDisplayed(label, 2), "label is not displayed for layout " + layoutName);
            click(label);
        }
    }

    @Step("Filter layouts by {0}")
    public LayoutsTab filterLayoutsBy(String filterName) {
        By filter = By.xpath("//div[@data-test='listItem' and contains(. , '" + filterName + "')]");

        Logger.info("Filter layouts by " + filterName);
        openFiltersDropDown();
        checkTrue(isElementPresentAndDisplayed(filter, 5), "filter is not displayed");
        click(filter);
        checkTrue(getCurrentFilter().contains(filterName), "filter is not set");
        isOpened();
        return this;
    }

    @Step
    public LayoutsTab openFiltersDropDown() {
        By btnOpenDropdown = By.xpath("//div[contains(@class, ' dropdown__')]");
        By dropdown = By.xpath("//div[contains(@class, 'listWrapper__')]/div");

        checkTrue(isElementPresentAndDisplayed(btnOpenDropdown, 5), "button for opening dropdown is not displayed");
        click(btnOpenDropdown);
        checkTrue(isElementPresentAndDisplayed(dropdown, 5), "Dropdown is not opened");
        isOpened();
        return this;
    }

    @Step
    public String getCurrentFilter() {
        By selectedFilter = By.xpath("//span[contains(@class, 'control__')]");

        checkTrue(isElementPresentAndDisplayed(selectedFilter, 5), "Selected filter is not displayed");
        return getText(selectedFilter);
    }

    private boolean checkASCSort(Method getMethod) throws InvocationTargetException, IllegalAccessException {
        initLayouts();
        for (int i = 1; i < layoutList.size(); i++) {
            if (((String) getMethod.invoke(layoutList.get(i))).compareTo((String) getMethod.invoke(layoutList.get(i - 1))) < 0) {
                return false;
            }
        }
        return true;
    }

    private boolean checkDESCSort(Method getMethod) throws InvocationTargetException, IllegalAccessException {
        initLayouts();
        for (int i = 0; i < layoutList.size() - 1; i++) {
            if (((String) getMethod.invoke(layoutList.get(i))).compareTo((String) getMethod.invoke(layoutList.get(i + 1))) < 0) {
                return false;
            }
        }
        return true;
    }

    @Step("Choose sort criteria")
    private By chooseSortCriteria(String criteriaName) {
        By criteria = null;
        By objectName = By.xpath("//*[contains(@class, 'tableHeader__')]//div[contains(@class, 'objectNameCol__')]");
        By layoutName = By.xpath("//*[contains(@class, 'tableHeader__')]//div[contains(@class, 'layoutNameCol__')]");
        switch (criteriaName) {
            case OBJECT_NAME:
                criteria = objectName;
                break;
            case LAYOUT_NAME:
                criteria = layoutName;
                break;
        }
        return criteria;
    }

    @Step("Check {0} sort by {1}")
    public boolean checkSortBy(String sortCriteria, String sortType) {
        try {
            Method getMethod = Layout.class.getMethod(sortCriteria);
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

    @Step("Sort user by {0}")
    public void sortLayoutsBy(String sortCriteria, String sortType) {
        By criteria = chooseSortCriteria(sortCriteria);

        checkTrue(isElementPresentAndDisplayed(criteria, 5), criteria + " is mot present");
        if (!isElementContainsStringInClass(criteria, "isAscend__")) {
            click(criteria);
        }
        if (sortType.equals(DESC_SORT) && !isElementContainsStringInClass(criteria, "isDescend__")) {
            click(criteria);
        } else if (sortType.equals(ASC_SORT) && isElementContainsStringInClass(criteria, "isDescend__")) {
            click(criteria);
        }
    }
}
