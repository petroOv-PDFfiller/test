package pages.airslate.addons;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.UIAssertionError;
import com.github.javafaker.Faker;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import pages.airslate.AirSlateBasePage;
import pages.airslate.flow_creator.FillSlateFooter;
import pages.airslate.flow_creator.ListSlatesPage;
import pages.airslate.flow_creator.SetupFlowPage;
import pages.pdffiller.components.popups.image.ImageWizardPopUp;
import pages.pdffiller.components.popups.signatures.SignPopUp;
import pages.pdffiller.editors.newJSF.*;
import pages.pdffiller.editors.newJSF.undo_redo.BUTTON;
import utils.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.switchTo;
import static core.check.Check.checkTrue;
import static data.airslate.AirSlateTestData.AIRSLATE_EDITOR_FRAME;

public class AirSlateEditor extends AirSlateBasePage {

    public static List<EditorContentElement> contentElements = new ArrayList<>();
    public static List<EditorContentElement> fillableFields = new ArrayList<>();
    private NewJSFiller jsFiller;
    private By btnComplete = By.xpath("//*[@class='sl-button__text'][.='Complete']");
    private By btnDisabled = By.xpath("//button[contains(@class, 'is-disabled')]");
    private By widjetToDoList = By.xpath("//*[@class='todo__body']");
    private By pageOverlay = By.xpath("//div[contains(@class, 'page-overlay__inner--with-shadow']");

    public AirSlateEditor(WebDriver driver) {
        super(driver);
    }


    @Override
    public void isOpened() {
        By paginationContent = By.xpath("//*[contains(@class,'g-scrollbar ps-container ps-theme-default')]");
        By pageContent = By.xpath("//*[@class='elementsWrapper-Content']");
        By editorLoader = By.xpath("//*[contains(@class,'document-loader__title')]");
        By frameEditor = By.xpath("//*[@title='FullScreenEditor']");
        checkTrue(waitUntilPageLoaded(), "AirSlate editor is not loaded");
        checkTrue(isElementDisappeared(loader, 30), "Loader isn't disappeared");
        checkTrue(isElementDisappeared(editorLoader, 60), "Editor was not loaded");
        skipAgreement();
        if (!getCurrentFrameName().equals(AIRSLATE_EDITOR_FRAME)) {
            checkTrue(switchToFrame(frameEditor), "Cannot switch to frame editor");
        }
        checkTrue(isElementPresent(pageContent, 60), "Editor was not opened");
//        checkTrue(isElementPresent(paginationContent, 30), "Editor was not opened");
    }

    @Step
    public NewJSFiller getJsFiller() {
        jsFiller = new NewJSFiller(driver);
        return jsFiller;
    }

    @Step
    private void skipAgreement() {
        By welcomeToAirSlate = By.xpath("//h2[text()='Welcome to airSlate!']");
        if (isElementDisplayed(welcomeToAirSlate, 4)) {
            By agreeCheckbox = By.xpath("//*[@class='sl-checkbox']");
            By btnLetsGo = By.xpath("//*[@class='sl-page-footer__actions']");
            if (isElementDisplayed(agreeCheckbox, 4)) {
                click(agreeCheckbox);
            }
            click(btnLetsGo);
        }
    }

    @Step
    public DeclineSignaturePopUp declineSignatureRequest() {
        By btnOtherActions = By.xpath("//button[.='Other Actions']");
        By btnDeclineSignature = By.xpath("//button[.='Decline Signature Request']");

        switchToDefaultContent();
        checkTrue(isElementDisplayed(btnOtherActions, 4), "Other actions button is not displayed");
        checkTrue(isElementNotDisplayed(pageOverlay, 4), "Page overlay is not displayed");
        click(btnOtherActions);
        checkTrue(isElementPresent(btnDeclineSignature, 4), "Decline signature button is not displayed");
        return goToPage(btnDeclineSignature, DeclineSignaturePopUp.class);
    }

    @Step
    public FillSlateFooter returnToFillSlate() {
        switchToDefaultContent();
        FillSlateFooter fillSlateFooter = new FillSlateFooter(driver);
        fillSlateFooter.isOpened();
        return fillSlateFooter;
    }

    public List<EditorContentElement> initFillableFields() {
        getJsFiller().initExistingElements();
        List<EditorContentElement> fillableFields = NewJSFiller.fillableFields;

        return fillableFields;
    }

    @Step
    public List<String> getTextFromAllFields() {
        List<String> allText = new ArrayList<>();

        initFillableFields().stream().forEach(item -> {
            if (item instanceof TextContentElement) {
                allText.add(((TextContentElement) item).text);
            }
            if (item instanceof SignContentElement) {
                ((SignContentElement) item).fetchSignValue();
                allText.add(((SignContentElement) item).signValue);
            }
        });

        return allText;
    }

    @Description("Init all fillable text and fill")
    public List<String> fillFieldsWithRandomValues(List<EditorContentElement> fields) {
        Faker faker = new Faker();
        List<String> filledText = new ArrayList<>();

        fields.stream()
                .filter(field -> field instanceof TextContentElement)
                .forEach(field -> {
                    String randomText = faker.pokemon().name().replaceAll("\\W+", "");

                    TextContentElement text = (TextContentElement) field;
                    text.typeText(randomText);
                    filledText.add(randomText);
                });

        Logger.info("Filled text: " + filledText);
        return filledText;
    }

    @Description("Init all fillable signature and fill with random value")
    public List<String> initAndFillSignaturesFields() {
        Logger.info("Fill Signature field");
        getJsFiller().initExistingElements();
        List<EditorContentElement> fields = NewJSFiller.fillableFields;

        Faker faker = new Faker();
        List<String> filledSign = new ArrayList<>();

        fields.stream()
                .filter(field -> field instanceof SignContentElement)
                .forEach(field -> {
                    String randomText = faker.pokemon().name().replaceAll("\\W+", "");

                    SignContentElement sign = (SignContentElement) field;
                    sign.makeActive();
                    sign.createHandWrittenSign(randomText);
                    filledSign.add(randomText);
                });

        Logger.info("Filled signature: " + filledSign);
        return filledSign;
    }

    @Description("Init all fillable fields and fill with random value")
    public List<String> initAndFillTextFields() {
        return fillFieldsWithRandomValues(this.initFillableFields());
    }

    @Step
    public AirSlateEditor clickPreviewButton() {
        By btncPreview = By.xpath("//*[@class='toolbar__group']//*[3]");
        checkTrue(isElementPresent(btncPreview, 4), "Preview button is not present");
        hoverOverAndClick(btncPreview);
        switchToDefaultContent();
        isOpened();

        return this;
    }

    @Step
    public AirSlateEditor clickAddFieldsButton() {
        By btncAddFields = By.xpath("//*[@class='toolbar__group']//*[2]");
        checkTrue(isElementPresent(btncAddFields, 4), "Add Fields button is not present");
        hoverOverAndClick(btncAddFields);
        switchToDefaultContent();
        isOpened();

        return this;
    }

    @Step
    public AirSlateEditor clickBtnEdit() {
        By btncEdit = By.xpath("//*[@class='toolbar__group']//*[1]");
        checkTrue(isElementPresent(btncEdit, 4), "Edit button is not present");
        hoverOverAndClick(btncEdit);
        switchToDefaultContent();
        isOpened();

        return this;
    }

    @Step
    public SetupFlowPage clickComplete() {
        switchToDefaultContent();
        checkTrue(isElementEnabled(btnComplete), "[Complete] button is not present");
        click(btnComplete);
        SetupFlowPage setupFlowPage = new SetupFlowPage(driver);
        setupFlowPage.isOpened();
        return setupFlowPage;
    }

    @Step
    public AirSlateEditor clickNext() {
        By btnNext = By.xpath("//*[@class='sl-button__body sl-button__body--reverse'][.='Next']");
        switchToDefaultContent();
        click(btnNext);
        checkTrue(isElementDisappeared(btnDisabled, 30), "Next button is disabled");
        switchToDefaultContent();
        isOpened();
        return this;
    }

    //TODO When TestFlowPage appears, implement the click close button in it
    @Step
    public ListSlatesPage clickClose() {
        By btnClose = By.xpath("//*[@class='sl-button__text'][.='Close']");
        switchToDefaultContent();
        return goToPage(btnClose, ListSlatesPage.class);
    }

    @Step
    public AirSlateEditor clickPrevious() {
        By btnPrevious = By.xpath("//*[@class='sl-button__text'][.='Previous']");
        switchToDefaultContent();
        click(btnPrevious);
        checkTrue(isElementDisappeared(btnDisabled, 30), "Previous button is disabled");
        switchToDefaultContent();
        isOpened();
        return this;
    }

    @Step
    public ListSlatesPage clickFinishUp() {
        By btnFinishUp = By.xpath("//*[@class='sl-button__text' and contains(.,'Finish up!')]");
        switchToDefaultContent();
        checkTrue(isElementDisappeared(btnDisabled, 30), "Finish now button is disabled");
        return goToPage(btnFinishUp, ListSlatesPage.class);
    }

    @Step
    public ListSlatesPage clickFinishNow() {
        By btnSubmitNow = By.xpath("//*[@class='modal-footer__btn'][2]//*[@class='button__text']");
        checkTrue(isElementPresent(btnSubmitNow, 6), "[Submit Now] button is not present");
        hoverOverAndClick(btnSubmitNow);
        ListSlatesPage listSlatesPage = new ListSlatesPage(driver);
        listSlatesPage.isOpened();
        return listSlatesPage;
    }

    public boolean isReadOnlyAccess() {
        By readOnlyAccessElement = By.xpath("//*[@class='text text--size--20 text--line-height--medium']");
        checkTrue(isElementPresent(readOnlyAccessElement, 6), "ReadOnlyAccessElement is not present");
        boolean isReadOnlyAccess = getText(readOnlyAccessElement).contains("Read Only Access");
        Logger.info("Read Only Access mode is = " + isReadOnlyAccess);
        return isReadOnlyAccess;
    }

    @Step
    public ListSlatesPage clickSaveAsDraft() {
        By btnSaveAsDraft = By.xpath("//*[@class='modal-footer__btn'][1]//*[@class='button__text']");
        checkTrue(isElementPresent(btnSaveAsDraft, 6), "[Save as draft] button is not present");
        hoverOverAndClick(btnSaveAsDraft);
        ListSlatesPage listSlatesPage = new ListSlatesPage(driver);
        listSlatesPage.isOpened();

        return listSlatesPage;
    }

    @Step
    public AirSlateEditor waitPresentFillableFields() {
        waitFor(widjetToDoList, 10);
        return this;
    }

    public AirSlateEditor fillFields(TreeMap<Integer, String> fieldsValues) {
        for (Map.Entry<Integer, String> entry : fieldsValues.entrySet()) {
            fillFieldByNumber(entry.getKey(), entry.getValue());
        }
        return this;
    }

    @Step
    private void fillFieldByNumber(int fieldNumber, String value) {
        fillableFields = initFillableFields();
        List<String> filledText = new ArrayList<>();

        if (fillableFields.get(fieldNumber) instanceof DropDownFillableElement) {
            DropDownFillableElement fillableDropField = (DropDownFillableElement) fillableFields.get(fieldNumber);
            fillableDropField.makeActive();
            fillableDropField.selectDropdownItem(Integer.parseInt(value));
        } else if (fillableFields.get(fieldNumber) instanceof TextContentElement) {
            TextContentElement textElement = (TextContentElement) fillableFields.get(fieldNumber);
            textElement.typeText(value);
        }
        filledText.add(value);
        Logger.info("Filled text: " + filledText);
    }

    @Step
    private String getFieldValueByNumber(int fieldNumber) {
        fillableFields = initFillableFields();
        String fieldValue = null;

        if (fillableFields.get(fieldNumber) instanceof DropDownFillableElement) {
            DropDownFillableElement fillableDropField = (DropDownFillableElement) fillableFields.get(fieldNumber);
            fillableDropField.makeActive();
            fieldValue = fillableDropField.getDropDownDefaultItem();
        } else if (fillableFields.get(fieldNumber) instanceof TextContentElement) {
            TextContentElement textElement = (TextContentElement) fillableFields.get(fieldNumber);
            fieldValue = textElement.text;
        }

        Logger.info("Field value: " + fieldValue);
        return fieldValue;
    }

    @Step
    public AirSlateEditor clickFillableField(String fieldDataId) {
        By field = By.xpath("//*[@data-id='1-" + fieldDataId + "']");
        checkTrue(isElementPresent(field), "Fillable field isn't present");
        hoverOverAndClick(field);
        isOpened();
        return this;
    }

    @Step
    public AirSlateEditor fillDateFieldWithDefaultDate(String fieldDataId) {
        By field = By.xpath("//*[@data-id='1-" + fieldDataId + "']");
        checkTrue(isElementPresent(field), "Fillable field isn't present");
        hoverOverAndClick(field);
        type(Keys.ENTER);
        isOpened();
        return this;
    }

    @Step
    public AirSlateEditor typeTextToFillableField(String fieldDataId, String text) {
        By field = By.xpath("//*[@data-id='1-" + fieldDataId + "']//textarea");
        checkTrue(isElementPresent(field), "Fillable field isn't present");
        hoverOverAndClick(field);
        type(field, text);
        type(Keys.TAB);
        isOpened();
        return this;
    }

    @Step
    public AirSlateEditor typeImageToFillableField(String fieldDataId, String imagePath) {
        By field = By.xpath("//*[@data-id='1-" + fieldDataId + "']");
        ImageWizardPopUp imageWizardPopUp = goToPage(ImageWizardPopUp.class, field);
        imageWizardPopUp.uploadImage().uploadConcreteImage(imagePath);
        imageWizardPopUp.selectImage(1);
        type(Keys.TAB);
        isOpened();
        return this;
    }

    @Step
    public AirSlateEditor typeSignatureAsTextToField(String fieldDataId, String signatureText) {
        By field = By.xpath("//*[@data-id='1-" + fieldDataId + "']");
        SignPopUp signPopUp = goToPage(SignPopUp.class, field);
        signPopUp.typeSignatureTextAndSign(signatureText, AirSlateEditor.class);
        type(Keys.TAB);
        isOpened();
        return this;
    }

    @Step
    public AirSlateEditor selectDropDownValue(String dropDownValue) {
        By dropDownElement = By.xpath("//*[@aria-label='" + dropDownValue + "']");
        checkTrue(isElementPresent(dropDownElement), "DropDown element isn't present");
        click(dropDownElement);
        type(Keys.TAB);
        isOpened();
        return this;
    }

    @Step
    public AirSlateEditor clickDropdownAndSelectValue(String fieldDataId, String dropDownValue) {
        By field = By.xpath("//*[@data-id='1-" + fieldDataId + "']");
        checkTrue(isElementPresent(field), "DropDown field isn't present");
        hoverOverAndClick(field);
        selectDropDownValue(dropDownValue);
        isOpened();
        return this;
    }

    public void clickUndoRedoButton(BUTTON button) {
        By btnUndoRedo = By.xpath("(//*[@class='grid__cell grid__cell--size--small']//*[@class='toolbar__control'])[" + button.getBtnUndoRedo() + "]");
        checkTrue(isElementEnabled(btnUndoRedo, 4), button + " is not enabled");
        click(btnUndoRedo);
    }

    @Step
    public String getProjectId() {
        String[] parseProject = driver.getCurrentUrl().split("/");
        return parseProject[parseProject.length - 1];
    }

    @Step
    public AirSlateEditor clickOkLetMeFinish() {
        SelenideElement frame = $x("//*[@title='FullScreenEditor']");
        SelenideElement pagination = $x("//*[contains(@class,'g-scrollbar ps-container ps-theme-default')]");
        if (!pagination.exists()) {
            switchTo().frame(frame);
            Logger.info("switched to editor frame");
        }

        By okLetMeFinish = By.xpath("//button[@class='g-btn g-btn--primary g-btn--auto-width']");
        Logger.info("clicking Ok Let Me Finish button...");
        checkTrue(isElementPresent(okLetMeFinish, 5), "ok Let Me Finish button is absent!");
        click(okLetMeFinish);
        return this;
    }

    @Step
    public void clickNextButton() {
        By btnNext = By.xpath("//*[@class='sl-button__body sl-button__body--reverse'][.='Next']");
        switchToDefaultContent();
        click(btnNext);
        checkTrue(isElementPresent(btnDisabled, 30), "Next button was not disabled");
    }

    @Step
    public void clickPreviousButton() {
        By btnPrevious = By.xpath("//*[@class='sl-button__text'][.='Previous']");
        switchToDefaultContent();
        click(btnPrevious);
        checkTrue(isElementPresent(btnDisabled, 30), "Previous button was not disabled");
    }

    @Step
    public void clickFinishUpButton() {
        By btnFinishUp = By.xpath("//*[@class='sl-button__text' and contains(.,'Finish up!')]");
        switchToDefaultContent();
        click(btnFinishUp);
        checkTrue(isElementPresent(btnDisabled, 30), "Finish now button was not disabled");
    }

    @Description("Content processing loader")
    public void skipProcessingLoader() {
        SelenideElement loader = $x("//*[@class='cm__title']");
        try {
            loader.waitUntil(Condition.visible, 3500);
            loader.waitUntil(Condition.disappear, 10000);
            Logger.info("Loader was appear");
        } catch (UIAssertionError error) {
            Logger.info("Loader was not appear or was not disappear");
        }
    }
}