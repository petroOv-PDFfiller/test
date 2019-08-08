package tests.salesforce.lightning_component;

import api.salesforce.entities.SalesforceObject;
import api.salesforce.responses.CreateRecordResponse;
import core.DriverWinMan;
import core.DriverWindow;
import data.TestData;
import imap.ImapClient;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import listeners.ImapListener;
import listeners.WebTestListener;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;
import pages.pdffiller.editors.newJSF.NewJSFiller;
import pages.pdffiller.workflow.send_to_sign.SendToSignSuccessPage;
import pages.salesforce.app.DaDaDocs.DaDaDocsLightningComponent;
import pages.salesforce.app.DaDaDocs.editor.DaDaDocsEditor;
import pages.salesforce.app.DaDaDocs.full_app.V3.entities.Document;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.app.sf_objects.accounts.AccountConcretePage;
import pages.salesforce.enums.FileTypes;
import pages.salesforce.enums.lightning_component.DaDaDocsLightningComponentSortParameters;
import pages.salesforce.enums.lightning_component.DaDaDocsLightningComponentTabs;
import tests.salesforce.SalesforceBaseTest;
import utils.StringMan;
import utils.TimeMan;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static core.check.Check.checkTrue;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static pages.salesforce.enums.lightning_component.DaDaDocsLightningComponentActions.EDIT_DOCUMENT;
import static pages.salesforce.enums.lightning_component.DaDaDocsLightningComponentActions.SEND_TO_SIGN;
import static pages.salesforce.enums.lightning_component.DaDaDocsLightningComponentSortParameters.*;
import static utils.ImapMan.getSignLinkFromS2SEmail;

@Feature("DaDaDocs lightning component")
@Listeners({WebTestListener.class, ImapListener.class})
public class LightningComponentTest extends SalesforceBaseTest {

    private static String pdfToUpload = new File("src/test/resources/uploaders/Constructor.pdf").getAbsolutePath();
    private static String docToUpload = new File("src/test/resources/docx/Opp.docx").getAbsolutePath();
    private static String pptToUpload = new File("src/test/resources/pptx/Empty.pptx").getAbsolutePath();
    private String recipientEmail;
    private String accountName = "ATAcc" + StringMan.getRandomString(5);
    private WebDriver driver;
    private DriverWinMan driverWinMan;
    private DriverWindow salesforceWindow;
    private String dadadocsAppURL;
    private AccountConcretePage accountConcretePage;

    @DataProvider(name = "FileTypes")
    public static Object[][] fileTypes() {
        return new Object[][]{{FileTypes.PDF, pdfToUpload},
                {FileTypes.DOC, docToUpload},
                {FileTypes.PPT, pptToUpload}};
    }

    @DataProvider(name = "sortPairwise")
    public static Object[][] sortPairwise() {
        return new Object[][]{{Name, Ascending, Comparator.comparing(o -> ((Document) o).getName())},
                {Name, Descending, (Comparator<Document>) (o1, o2) -> o2.getName().compareTo(o1.getName())},
                {Date, Ascending, Comparator.comparing(o3 -> ((Document) o3).getModifiedDate())},
                {Date, Descending, (Comparator<Document>) (o1, o2) -> o2.getModifiedDate().compareTo(o1.getModifiedDate())}};
    }

    @Parameters({"recipientEmail"})
    @BeforeTest
    public void setUp(@Optional("pdf_sf_rnt@support.pdffiller.com") String recipientEmail) throws IOException, URISyntaxException {
        this.recipientEmail = StringMan.makeUniqueEmail(recipientEmail);
        Map<String, String> accountParameters = new HashMap<>();
        accountParameters.put("Name", accountName);
        CreateRecordResponse response = salesforceApi.recordsService().createRecord(SalesforceObject.ACCOUNT, accountParameters);
        String accountId = response.id;

        driver = getDriver();
        SalesAppBasePage salesAppBasePage = loginWithDefaultCredentials();
        accountConcretePage = salesAppBasePage.openRecordPageById(SalesforceObject.ACCOUNT, accountId);
        dadadocsAppURL = driver.getCurrentUrl();
        driverWinMan = new DriverWinMan(driver);
        salesforceWindow = driverWinMan.getCurrentWindow();
    }

    @Story("Check file edit action in lightning component")
    @Test
    public void editDocument() {
        checkTrue(driverWinMan.switchToWindow(salesforceWindow), "Cannot return to old window");
        getUrl(dadadocsAppURL);
        accountConcretePage.isOpened();
        DaDaDocsLightningComponent daDaDocsLightningComponent = accountConcretePage.switchToDaDaDocsLightningComponent();
        daDaDocsLightningComponent.showInLayout(DaDaDocsLightningComponentTabs.DOCUMENTS);
        accountConcretePage = daDaDocsLightningComponent.uploadNewDocument(pdfToUpload)
                .clickOnDoneButton(AccountConcretePage.class);
        daDaDocsLightningComponent = accountConcretePage.switchToDaDaDocsLightningComponent();
        daDaDocsLightningComponent.selectDadadocsItemByFullName("Constructor");
        DaDaDocsEditor daDaDocsEditor = daDaDocsLightningComponent.selectAnAction(EDIT_DOCUMENT);
        daDaDocsEditor.clickDoneButton();
        accountConcretePage.isOpened();
        assertTrue(driver.getCurrentUrl().contains(dadadocsAppURL), "Didn`t returned to account concrete page");
    }

    @Story("Check file edit action in lightning component")
    @Test
    public void sendToSign() {
        checkTrue(driverWinMan.switchToNewWindow(), "new window is not created or cannot switch to it");
        DriverWindow pdffillerWindow = driverWinMan.getCurrentWindow();
        getUrl(testData.url);
        deleteAllCookies();
        registerUser(recipientEmail);

        checkTrue(driverWinMan.switchToWindow(salesforceWindow), "Cannot return to old window");
        getUrl(dadadocsAppURL);
        accountConcretePage.isOpened();
        DaDaDocsLightningComponent daDaDocsLightningComponent = accountConcretePage.switchToDaDaDocsLightningComponent();
        daDaDocsLightningComponent.showInLayout(DaDaDocsLightningComponentTabs.DOCUMENTS);
        accountConcretePage = daDaDocsLightningComponent.uploadNewDocument(pdfToUpload)
                .clickOnDoneButton(AccountConcretePage.class);
        daDaDocsLightningComponent = accountConcretePage.switchToDaDaDocsLightningComponent();
        daDaDocsLightningComponent = daDaDocsLightningComponent.selectDadadocsItemByFullName("Constructor")
                .selectAnAction(SEND_TO_SIGN);
        ImapClient imap = new ImapClient(recipientEmail, TestData.defaultPassword);
        String subject = "signature request";
        imap.deleteAllMessagesWithSubject(subject);
        daDaDocsLightningComponent.setRecipient(recipientEmail, "Recipient", 1).sendToSign();

        checkTrue(driverWinMan.switchToWindow(pdffillerWindow), "cannot switch to new window");
        getUrl(getSignLinkFromS2SEmail(imap, subject));
        NewJSFiller newJSFiller = new NewJSFiller(driver);
        newJSFiller.isOpened();
        newJSFiller.isReady();
        newJSFiller.clickLetsGoButton();
        newJSFiller.clickDoneButton();
        newJSFiller.clickLetsGoButton();
        SendToSignSuccessPage sendToSignSuccessPage = new SendToSignSuccessPage(driver);
        sendToSignSuccessPage.isOpened();

        checkTrue(driverWinMan.switchToWindow(salesforceWindow), "Cannot return to old window");
        TimeMan.sleep(10);
        refreshPage();
        accountConcretePage.isOpened();
        daDaDocsLightningComponent = accountConcretePage.switchToDaDaDocsLightningComponent();
        String lastDocumentName = daDaDocsLightningComponent.getLastDocumentName();
        assertTrue(lastDocumentName.contains("Constructor") && lastDocumentName.contains("-envelope"),
                "Signed document is not returned to sf");
    }

    @Story("Filter documents by type")
    @Test(dataProvider = "FileTypes")
    public void filterByType(FileTypes type, String pathToFile) {
        checkTrue(driverWinMan.switchToWindow(salesforceWindow), "Cannot return to old window");
        getUrl(dadadocsAppURL);
        accountConcretePage.isOpened();
        DaDaDocsLightningComponent daDaDocsLightningComponent = accountConcretePage.switchToDaDaDocsLightningComponent();
        daDaDocsLightningComponent.showInLayout(DaDaDocsLightningComponentTabs.DOCUMENTS);
        accountConcretePage = daDaDocsLightningComponent.uploadNewDocument(pathToFile)
                .clickOnDoneButton(AccountConcretePage.class);

        daDaDocsLightningComponent = accountConcretePage.switchToDaDaDocsLightningComponent();
        List<Document> filteredDocuments = daDaDocsLightningComponent.filterDocumentsByFileType(type).initDocuments();
        SoftAssert softAssert = new SoftAssert();
        filteredDocuments.forEach(document ->
                softAssert.assertEquals(document.getType(), type, document.getName() + " have wrong type"));
        softAssert.assertAll();
    }

    @Story("Sort documents")
    @Test(priority = 1, dataProvider = "sortPairwise")
    public void sortDocumentsBy(DaDaDocsLightningComponentSortParameters parameter,
                                DaDaDocsLightningComponentSortParameters order,
                                Comparator<Document> documentComparator) {
        checkTrue(driverWinMan.switchToWindow(salesforceWindow), "Cannot return to old window");
        getUrl(dadadocsAppURL);
        accountConcretePage.isOpened();
        DaDaDocsLightningComponent daDaDocsLightningComponent = accountConcretePage.switchToDaDaDocsLightningComponent();
        daDaDocsLightningComponent.showInLayout(DaDaDocsLightningComponentTabs.DOCUMENTS);
        List<Document> sortedDocuments = daDaDocsLightningComponent.sortBy(parameter)
                .sortBy(order)
                .initDocuments();
        List<Document> defaultDocuments = sortedDocuments;
        defaultDocuments.sort(documentComparator);
        assertEquals(sortedDocuments, defaultDocuments, "Document list is not sorted in right way");
    }
}
