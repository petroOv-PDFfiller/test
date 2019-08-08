package pages.alto_sites;

import core.DriverWindow;
import core.PageBase;
import data.alto_site.AltoSiteDocs;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.google.GoogleDriveFrame;
import pages.google.chrome.GoogleAuthPopup;
import utils.FileMan;
import utils.TimeMan;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static base_tests.AltoSitesBaseTest.isProduction;
import static core.check.Check.checkTrue;
import static data.TestData.PATH_TO_DOWNLOADS_FOLDER;
import static org.awaitility.Awaitility.await;

public abstract class AltoSitesBasePage extends PageBase {
    public static final String SHARE = "Share";
    public static final String SIGN = "Sign";
    public static final String EDIT = "Edit";
    public static final String SAVE = "Save";
    public static By blockResults = By.cssSelector(".outputResultComponent");
    public static By btnSaveDocumentAs = By.cssSelector(".outputResultComponent--buttonIcon__save-sm");
    public static By btnEditDocuments = By.cssSelector(".outputResultComponent--buttonIcon__editdoc");
    public static By btnSign = By.cssSelector(".outputResultComponent--buttonIcon__sign");
    public static By btnShare = By.cssSelector(".outputResultComponent--buttonIcon__share");
    String baseUrlProd;
    String baseUrlDev;
    String emailForCloud = "qarusyloyuliantest@gmail.com";
    String passwordForCloud = "qa123test";
    private By btnGoogleDrive = By.cssSelector(".google-button");
    private By btnDropboxDrive = By.cssSelector(".dropbox-button");
    private By inputFile = By.id("dropzoneInput");
    private By btnConvertFile = By.cssSelector(".g-btn.g-btn--primary.g-btn--medium");
    private By btnDownloadNow = By.cssSelector(".g-btn.g-btn-secondary.g-btn-auto-width");
    private By notificationError = By.cssSelector(".notification.is-error");
    private By iconCloseNotification = By.cssSelector(".notification__close");
    private By iconTrashForDelete = By.cssSelector(".document-preview__drop.js-drop");
    private By btnChooseFile = By.cssSelector(".js-fileapi-wrapper.g-btn.g-btn--primary.g-btn--choose");
    private By iconXForDelete = By.cssSelector(".icon.icon-drop");
    private By btnCancel = By.cssSelector(".g-btn.g-btn--secondary.g-btn--medium");
    private By btnStartOverAgain = By.cssSelector(".repeat-link");
    private By btnDone = By.cssSelector(".control.control--size--medium-stretched");
    private By blockCloudServices = By.cssSelector(".cloud-services.cloud-services--middle");
    private By inputRange = By.cssSelector(".action-filter__input");

    public AltoSitesBasePage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        checkTrue(waitUntilPageLoaded(), "Page not loaded");
        checkTrue(isElementDisplayed(btnChooseFile, 10), "Choose file btn not present");
        checkTrue(isElementDisplayed(blockCloudServices, 10), "Block cloud services not present");
        TimeMan.sleep(2);
    }

    @Step
    public void downloadLinkCloud(String idFile) {
        String baseLinkDownloadFile = "https://drive.google.com/uc?id=" + idFile + "&export=download";
        open(baseLinkDownloadFile);
        TimeMan.sleep(1);
    }

    @Step
    public void downloadFileCloud(List<AltoSiteDocs> docs) {
        docs.forEach(doc -> downloadLinkCloud(doc.getId()));
    }

    @Step
    public void checkUploadFromSelenoid(List<AltoSiteDocs> docs) {
        docs.forEach(doc -> FileMan.isFileDownloaded((PATH_TO_DOWNLOADS_FOLDER), doc.getName()));
    }

    @Step
    public void navigateToPage() {
        if (isProduction) {
            open(baseUrlProd);
        } else {
            open(baseUrlDev);
        }
    }

    @Step
    public void chooseFile(String pathToFile) {
        checkTrue(isElementPresent(inputFile, 5), "File input field is not present");
        sendFileInput(inputFile, pathToFile);
    }

    @Step
    public void convertNow() {
        checkTrue(isElementPresent(btnConvertFile, 20), "Button 'Convert file' not present");
        await().atMost(50, TimeUnit.SECONDS)
                .until(() -> !isElementContainsStringInClass(btnConvertFile, "is-disabled"));
        click(btnConvertFile);
    }

    @Step
    public void download() {
        checkTrue(isElementPresent(blockResults, 30), "Final page not present");
        checkTrue(isElementPresent(btnDownloadNow, 5), "Button 'Download Now' not present");
        TimeMan.sleep(3);
        hoverOverAndClick(btnDownloadNow);
    }

    @Step
    public void checkValidationForPages() {
        checkTrue(isElementPresent(notificationError, 50), "Notification error not present");
    }

    @Step
    public void checkEnabledButtonConvertNow() {
        checkTrue(isElementPresent(btnConvertFile, 20), "Button convert file not present");
        await().atMost(50, TimeUnit.SECONDS)
                .until(() -> !isElementContainsStringInClass(btnConvertFile, "is-disabled"));
    }

    @Step
    public void closeNotificationError() {
        checkTrue(isElementPresent(iconCloseNotification, 5), "Close icon not present");
        click(iconCloseNotification);
    }

    @Step
    public void deleteFileWithUseIconTrash() {
        checkTrue(isElementPresent(iconTrashForDelete, 10), "Icon 'Trash' not present");
        hoverOverAndClick(iconTrashForDelete);
    }

    @Step
    public void checkEnabledButtonChooseFile() {
        checkTrue(isElementPresent(btnChooseFile, 4), "Button 'Choose file' not present");
    }

    @Step
    public void deleteFileWithUseIconX() {
        checkTrue(isElementPresent(iconXForDelete, 5), "Icon 'X' for delete file not present");
        click(iconXForDelete);
    }

    @Step
    public void cancelConvert() {
        checkTrue(isElementPresent(btnCancel, 5), "Button 'Cancel' not present");
        click(btnCancel);
    }

    @Step
    public void startOverAgain() {
        checkTrue(isElementPresent(blockResults, 30), "Final page not present");
        checkTrue(isElementPresent(btnStartOverAgain, 3), "Button 'Start Over Again' not present");
        click(btnStartOverAgain);
    }

    @Step
    public void checkRedirectPdffillerWithPdf() {
        checkTrue(isElementPresent(btnDone, 10), "PDF file not opened in PDFFILLER");
    }

    @Step
    public void goToPdffiller(String btnName) {
        checkTrue(isElementPresent(blockResults, 30), "Final page not present");
        TimeMan.sleep(4);
        switch (btnName) {
            case SAVE:
                checkTrue(isElementPresent(btnSaveDocumentAs, 10), "Button 'Save Document As' not present");
                hoverOverAndClick(btnSaveDocumentAs);
                break;
            case EDIT:
                checkTrue(isElementPresent(btnEditDocuments, 10), "Button 'Edit Documents' not present");
                hoverOverAndClick(btnEditDocuments);
                break;
            case SIGN:
                checkTrue(isElementPresent(btnSign, 10), "Button 'Sign & Secure' not present");
                hoverOverAndClick(btnSign);
                break;
            case SHARE:
                checkTrue(isElementPresent(btnShare, 10), "Button 'Share' not present");
                hoverOverAndClick(btnShare);
                break;
        }
    }

    @Step
    public void typePageRange(String someRange) {
        checkTrue(isElementPresent(inputRange, 20), "Input for type range not present");
        type(inputRange, someRange);
    }

    @Step
    public boolean isDownloadedAndDelete(String fileName, int seconds) {
        File dir = new File(PATH_TO_DOWNLOADS_FOLDER);
        for (int i = 0; i < seconds; i++) {
            File[] dirContents = dir.listFiles();
            for (File dirContent : dirContents) {
                if (dirContent.getName().startsWith(fileName)) {
                    dirContent.delete();
                    return true;
                }
            }
            TimeMan.sleep(1);
        }
        return false;
    }

    @Step
    public void checkDownload(String downloadName) {
        checkTrue(isDownloadedAndDelete(downloadName, 6), "File not download");
    }

    @Step
    public void loginGoogleAccount() {
        DriverWindow altoWindow = driverWinMan.getCurrentWindow();
        driverWinMan.switchToNewWindow();
        open("https://accounts.google.com/");
        GoogleAuthPopup googleDriveAuthPage = new GoogleAuthPopup(driver);
        googleDriveAuthPage.isOpened();
        googleDriveAuthPage.auth(emailForCloud, passwordForCloud);
        checkTrue(driverWinMan.switchToWindow("myaccount", 15), "Failed to open account google");
        driver.close();
        driverWinMan.switchToWindow(altoWindow);
    }

    @Step
    public void chooseFileFromGoogleDrive(String nameFile) {
        checkTrue(isElementPresent(btnGoogleDrive, 5), "Button GDrive first page not present");
        DriverWindow altoWindow = driverWinMan.getCurrentWindow();
        click(btnGoogleDrive);
        GoogleDriveFrame googleDriveFrame = new GoogleDriveFrame(driver);
        By gDriveFrame = By.cssSelector(".picker-frame.picker-dialog-frame");
        switchToFrame(gDriveFrame);
        googleDriveFrame.isOpened();
        googleDriveFrame.selectFileToUpload(nameFile);
        driverWinMan.switchToWindow(altoWindow);
    }

    @Step
    public void saveFileGoogleDrive() {
        By btnRealGoogleDrive = By.id("google-save-button");
        By btnSaveGoogleDrive = By.cssSelector(".saver-authorize-save-button");
        checkTrue(isElementPresent(blockResults, 30), "Final page not present");
        checkTrue(isElementPresent(btnRealGoogleDrive, 5), "Button GDrive final page not present");
        hoverOverAndClick(btnRealGoogleDrive);
        DriverWindow altoWindow = driverWinMan.getCurrentWindow();
        checkTrue(driverWinMan.switchToWindow(".google", 15), "Failed to switch to GoogleDrive window");
        driver.manage().window().maximize();
        click(btnSaveGoogleDrive);
        driverWinMan.switchToWindow(altoWindow);
    }

    @Step
    public void loginAndChoseFileFromDropboxCloud() {
        By btnSignWithGoogle = By.cssSelector(".sign-in-text");
        By accountLogin = By.id("profileIdentifier");
        By btnAllowAccess = By.xpath("//*[@id='submit_approve_access']");
        By fileSelect = By.cssSelector(".mc-checkbox-unchecked");
        By btnSelect = By.cssSelector(".mc-button-primary");
        checkTrue(isElementPresent(btnDropboxDrive, 5), "Button OneDrive first page not present");
        DriverWindow altoWindow = driverWinMan.getCurrentWindow();
        click(btnDropboxDrive);
        checkTrue(driverWinMan.switchToWindow(".dropbox.", 15), "Failed to switch to Dropbox window");
        driver.manage().window().maximize();
        click(btnSignWithGoogle);
        checkTrue(isElementPresent(accountLogin, 7), "Button 'login with GAccount' not present");
        hoverOverAndClick(accountLogin);
        if (isElementPresent(btnAllowAccess, 3))
            click(btnAllowAccess);
        checkTrue(driverWinMan.switchToWindow("dropbox.com/chooser", 15), "Failed to switch to Dropbox window");
        checkTrue(isElementPresent(fileSelect, 10), "file not present");
        click(fileSelect);
        checkTrue(isElementPresent(btnSelect, 10), "btn select not present");
        click(btnSelect);
        checkTrue(driverWinMan.switchToWindow(altoWindow, 15), "Failed to switch to AltoSites window");
    }

    @Step
    public void saveFileDropboxDrive() {
        By folderForSave = By.cssSelector(".dropins-item-row-clickable");
        By btnSave = By.cssSelector(".mc-button-primary");
        checkTrue(isElementPresent(blockResults, 30), "Final page not present");
        checkTrue(isElementPresent(btnDropboxDrive, 5), "Button OneDrive final page not present");
        click(btnDropboxDrive);
        checkTrue(driverWinMan.switchToWindow("dropbox.com/saver", 30), "Failed to switch to Dropbox window");
        checkTrue(isElementPresent(folderForSave, 10), "Folder for save not present");
        click(folderForSave);
        checkTrue(isElementPresent(btnSave, 10), "Button 'Select' not present");
        click(btnSave);
        new WebDriverWait(driver, 50).until(ExpectedConditions.numberOfWindowsToBe(1));
    }
}
