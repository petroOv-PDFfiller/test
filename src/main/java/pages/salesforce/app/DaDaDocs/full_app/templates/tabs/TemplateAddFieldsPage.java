package pages.salesforce.app.DaDaDocs.full_app.templates.tabs;

import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.DaDaDocs.editor.DaDaDocsEditor;
import pages.salesforce.app.DaDaDocs.full_app.templates.CreateTemplateWizardPage;

import static core.check.Check.checkTrue;

public class TemplateAddFieldsPage extends CreateTemplateWizardPage {

    private DaDaDocsEditor DaDaDocsEditor;
    private boolean isEditorFrameActive = false;

    public TemplateAddFieldsPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        switchToDefaultContent();
        if (isElementPresent(iframe)) {
            switchToFrame(iframe);
        }
        skipMappingLoader();
        checkTrue(isElementPresentAndDisplayed(editorFrame, 5), "Editor frame is not displayed");
        isEditorFrameActive = false;
    }

    @Step
    public DaDaDocsEditor workWithEditor() {
        isEditorFrameActive = true;
        DaDaDocsEditor = new DaDaDocsEditor(driver);
        DaDaDocsEditor.isOpened();
        return DaDaDocsEditor;
    }

    @Step
    public TemplateMapToPrefillPage saveTemplate() {
        if (!isEditorFrameActive) {
            workWithEditor();
        }
        DaDaDocsEditor.clickSaveButton();
        checkTrue(switchToParentFrame(), "Cannot switch to parent frame");
        isEditorFrameActive = false;

        TemplateMapToPrefillPage prefillPage = new TemplateMapToPrefillPage(driver);
        prefillPage.isOpened();
        return prefillPage;
    }

    @Step
    public TemplateAddFieldsPage returnToPage() {
        if (isEditorFrameActive) {
            checkTrue(switchToParentFrame(), "Cannot switch to parent frame");
        }
        isOpened();
        return this;
    }
}
