package pages.salesforce.app.DaDaDocs.full_app.templates.components;

import io.qameta.allure.Step;
import org.openqa.selenium.By;

public interface Mapping {

    By btnAddNewMapping = By.xpath("//button[contains(@class, 'addRecordBtn__')]");
    By popUpTitle = By.xpath("//div[contains(@class, 'popup__title__')]");
    By btnAddField = By.xpath("//button[contains(@class, 'addFieldBtn')]");
    By btnCloseMappingPopUp = By.xpath("//button[contains(@class, 'close__')]");
    By objectSelect = By.id("objectsSelect");
    By selectSalesforceObjects = By.xpath("//select[contains(@id, 'sfFieldSelect')]");
    By selectDocumentsFields = By.xpath("//select[contains(@id, 'docFieldSelect')]");
    By btnDeleteMapping = By.xpath("//div[contains(@class, 'mapRow__')]//button");
    By btnSaveRecord = By.xpath("//div[contains(@class, 'popup__overlay')]//button[contains(@class, 'btn__accent')]");
    By selectObjectType = By.xpath("//select[@id='objectsSelect']");
    By addedRecords = By.xpath("//div[contains(@class, 'record__')]");
    By mappingPopUp = By.xpath("//div[contains(@class, 'popup__overlay')]");
    By btnEditRecord = By.xpath("//*[contains(@class, 'editBtn')]");
    By btnDeleteRecord = By.xpath("//*[contains(@class, 'deleteBtn')]");

    @Step("Add record to ({0})")
    Mapping addRecord(String sfObject);

    @Step("Add field")
    Mapping addField();

    @Step("Map field ({0}) with ({1})")
    Mapping mapField(int index, String sfField, String documentField, int expectedEnabledDocuments);

    @Step("Save record")
    Mapping mapField(int index, String sfField, String documentField);

    @Step("Save template")
    Mapping saveRecord();

    void checkRecordsButtons(int expectedCount);

    void checkRequiredFields();
}
