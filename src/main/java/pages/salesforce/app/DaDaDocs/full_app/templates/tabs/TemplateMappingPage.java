package pages.salesforce.app.DaDaDocs.full_app.templates.tabs;

import core.check.SoftCheck;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import pages.salesforce.app.DaDaDocs.full_app.templates.CreateTemplateWizardPage;
import pages.salesforce.app.DaDaDocs.full_app.templates.components.FooterNavigation;
import pages.salesforce.app.DaDaDocs.full_app.templates.components.Mapping;
import pages.salesforce.app.DaDaDocs.full_app.templates.entities.MapRow;
import pages.salesforce.app.DaDaDocs.full_app.templates.entities.Record;
import utils.Logger;

import java.util.ArrayList;
import java.util.List;

import static core.check.Check.*;

public abstract class TemplateMappingPage extends CreateTemplateWizardPage implements FooterNavigation, Mapping {

    private List<Record> records;

    TemplateMappingPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        skipMappingLoader();
        checkTrue(isElementPresentAndDisplayed(heading, 5), "Heading is not displayed");
        checkTexts();
        checkTrue(isElementPresentAndDisplayed(footer, 5), "Footer is not displayed");
    }

    @Override
    @Step
    public Mapping addRecord(String sfObject) {
        Logger.info("Add record " + sfObject);
        checkTrue(isElementPresentAndDisplayed(btnAddNewMapping, 5), "Add new record popup is not displayed");
        skipMappingLoader();
        click(btnAddNewMapping);
        checkTrue(isElementPresent(selectObjectType, 5), "Object type select is not present");
        Select objectType = new Select(driver.findElement(selectObjectType));
        objectType.selectByVisibleText(sfObject);
        checkRequiredFields();
        return this;
    }

    @Override
    @Step
    public Mapping addField() {
        Logger.info("Add field in mapping");
        checkTrue(isElementPresentAndDisplayed(btnAddNewMapping, 5), "Add new record popup is not displayed");
        int numberOfElementsBeforeClick = getNumberOfElements(selectSalesforceObjects);
        click(btnAddField);
        checkEquals(getNumberOfElements(selectSalesforceObjects), numberOfElementsBeforeClick + 1, "New record was not added");
        return this;
    }

    @Override
    @Step
    public Mapping mapField(int index, String sfField, String documentField, int expectedEnabledDocuments) {
        checkTrue(areElementsPresentMoreThan(selectSalesforceObjects, 5, index), "Select sf object is not present");
        WebElement selectObject = driver.findElements(selectSalesforceObjects).get(index);
        if (isElementEnabled(selectObject)) {
            Logger.info("Salesforce field Select " + index + " is enable, set value to Salesforce field  " + sfField);
            Select salesforceFieldSelect = new Select(selectObject);
            salesforceFieldSelect.selectByVisibleText(sfField);
            checkFalse(salesforceFieldSelect.getAllSelectedOptions().isEmpty(), "Option was not selected");
        }
        Logger.info("Document field Select " + index + "  set value to document field " + documentField);
        checkTrue(areElementsPresentMoreThan(selectSalesforceObjects, 5, index), "Select document object is not present");
        Select documentFieldSelect = new Select(driver.findElements(selectDocumentsFields).get(index));
        if (expectedEnabledDocuments > -1) {
            checkEquals(documentFieldSelect.getOptions().size(), expectedEnabledDocuments + 1, "Not all document fields are available");
        }
        documentFieldSelect.selectByVisibleText(documentField);
        checkFalse(documentFieldSelect.getAllSelectedOptions().isEmpty(), "Option was not selected");
        return this;
    }

    @Override
    @Step
    public Mapping mapField(int index, String sfField, String documentField) {
        return mapField(index, sfField, documentField, -1);
    }

    @Override
    @Step
    public Mapping saveRecord() {
        Logger.info("Save record");
        int numberOfRecordsBeforeSave = getNumberOfElements(addedRecords);
        checkTrue(isElementPresentAndDisplayed(btnSaveRecord, 5), "Can not save record");
        click(btnSaveRecord);
        checkTrue(isElementNotDisplayed(mappingPopUp, 5), "Add record popUp is still displayed");
        checkEquals(getNumberOfElements(addedRecords), numberOfRecordsBeforeSave + 1, "Record was not added");
        checkRecordsButtons(numberOfRecordsBeforeSave + 1);
        return this;
    }

    @Override
    public void checkRecordsButtons(int expectedCount) {
        checkTrue(areElementsPresent(btnEditRecord, 5, expectedCount), "Wrong count of edit buttons");
        SoftCheck softCheck = new SoftCheck();
        for (int i = 0; i < getNumberOfElements(btnEditRecord); i++) {
            softCheck.checkTrue(isElementDisplayed(driver.findElements(btnEditRecord).get(i), 2), "Edit button for record # " + i + " is not displayed");
        }
        checkTrue(areElementsPresent(btnDeleteRecord, 5, expectedCount), "Wrong count of delete buttons");
        for (int i = 0; i < getNumberOfElements(btnDeleteRecord); i++) {
            softCheck.checkTrue(isElementDisplayed(driver.findElements(btnDeleteRecord).get(i), 2), "Delete button for record # " + i + " is not displayed");
        }
        softCheck.checkAll();
    }

    @Override
    public void checkRequiredFields() {
        checkEquals(getNumberOfElements(selectSalesforceObjects), getNumberOfElements(selectDocumentsFields), "Fields is not paired");
    }

    public List<Record> initRecords() {
        Logger.info("Init records list");
        By recordIds = By.xpath("//div[contains(@class, 'record__')]");

        records = new ArrayList<>();
        for (int i = 0; i < getNumberOfElements(recordIds); i++) {
            By record = By.xpath("(//div[contains(@class, 'record__')])[" + (i + 1) + "]//strong");
            By recordMapping = By.xpath("(//div[contains(@class, 'record__')])[" + (i + 1) + "]//div[contains(@class, 'mapRow__')]");
            List<MapRow> mapping = new ArrayList<>();
            for (int j = 0; j < getNumberOfElements(recordMapping); j++) {
                By sfObject = By.xpath(".//div");
                By documentObject = By.xpath("//div[contains(@data-test, 'documentField')]");
                String sfFieldName = driver.findElements(recordMapping).get(j).findElement(sfObject).getText();
                String documentFieldName = getText(driver.findElements(recordMapping).get(j).findElement(documentObject));
                mapping.add(new MapRow(sfFieldName, documentFieldName));
            }
            String recordName = getText(record);
            records.add(new Record(recordName, mapping));
        }
        return records;
    }
}
