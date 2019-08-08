package tests.salesforce.apexrest;

import api.salesforce.entities.dadadocs.documents.LinkToFillResponse;
import api.salesforce.entities.dadadocs.documents.PreviewResponse;
import api.salesforce.entities.dadadocs.documents.SalesforceDocument;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import listeners.WebTestListener;
import org.json.JSONException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import utils.StringMan;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static core.check.Check.*;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

@Feature("Salesforce documents apexrest endpoints tests")
@Listeners(WebTestListener.class)
public class SalesforceDocumentsTest extends BaseApexrestTest {

    @DataProvider(name = "emailRecipients")
    public static Object[][] emailRecipients() {
        return new Object[][]{
                {StringMan.makeUniqueEmail(FIRST_EMAIL_TO)},
                {StringMan.makeUniqueEmail(FIRST_EMAIL_TO) + "," +
                        StringMan.makeUniqueEmail(SECOND_EMAIL_TO)},
        };
    }

    @Story("Get documents and projects")
    @Test
    public void getSfDocumentsAndProjectsTest() throws IOException, URISyntaxException {
        List<SalesforceDocument> sfDocumentsList = dadadocsApi.documents()
                .getSfDocumentsByRecordId(SF_RECORD_ID);
        assertFalse(sfDocumentsList.isEmpty(), "Documents list should be empty");
    }

    @Story("Upload document to PDFfiller")
    @Test
    public void uploadSfDocumentToPdffiller() throws IOException, URISyntaxException {
        List<SalesforceDocument> sfDocumentsList = dadadocsApi.documents()
                .getSfDocumentsByRecordId(SF_RECORD_ID);
        checkFalse(sfDocumentsList.isEmpty(), "Documents list should be empty");

        String sfDocumentId = sfDocumentsList.get(0).id;
        String pdffillerProjectId = dadadocsApi.documents().upload(sfDocumentId);

        assertTrue(pdffillerProjectId.matches(PROJECT_ID_REGEXP),
                "Actual response '" + pdffillerProjectId + "' matched projectId regexp");
    }

    @Story("Get share link for document")
    @Test
    public void getShareLinkForDocument() throws IOException, URISyntaxException {
        List<SalesforceDocument> sfDocumentsList = dadadocsApi.documents()
                .getSfDocumentsByRecordId(SF_RECORD_ID);
        checkFalse(sfDocumentsList.isEmpty(), "Documents list should be empty");

        String sfDocumentId = sfDocumentsList.get(0).id;
        String pdffillerProjectId = dadadocsApi.documents().upload(sfDocumentId);
        checkTrue(pdffillerProjectId.matches(PROJECT_ID_REGEXP),
                "Actual response '" + pdffillerProjectId + "' matched projectId regexp");

        int accessType = 1;
        String shortLink = dadadocsApi.documents()
                .shareViaLink(pdffillerProjectId, accessType);
        assertTrue(shortLink.contains("https://pdf.ac"),
                "Actual response '" + shortLink + "' matches link regexp");
    }

    @Story("Should return preview link")
    @Test
    public void shouldReturnPreviewLink() throws IOException, URISyntaxException {
        List<SalesforceDocument> sfDocumentsList = dadadocsApi.documents()
                .getSfDocumentsByRecordId(SF_RECORD_ID);
        checkFalse(sfDocumentsList.isEmpty(), "Documents list should be empty");

        String sfDocumentId = sfDocumentsList.get(0).id;
        String pdffillerProjectId = dadadocsApi.documents().upload(sfDocumentId);
        checkTrue(pdffillerProjectId.matches(PROJECT_ID_REGEXP),
                "Actual response '" + pdffillerProjectId + "' matched projectId regexp");

        String itemType = "edit";
        boolean storeAsFile = false;
        PreviewResponse previewResponse = dadadocsApi.documents()
                .preview(sfDocumentId, SF_RECORD_ID, itemType, storeAsFile);
        checkTrue(previewResponse.project_id.matches(PROJECT_ID_REGEXP), "Expect pdffiller projectId");
        assertTrue(previewResponse.url.contains("https://www.pdffiller.com/en/login/auto.htm"),
                "Actual url '" + previewResponse.url + "' is autologin");
    }

    @Story("Send document by email")
    @Test(dataProvider = "emailRecipients")
    public void sendDocumentByEmail(String emailTo) throws IOException, URISyntaxException, JSONException {
        List<SalesforceDocument> sfDocumentsList = dadadocsApi.documents()
                .getSfDocumentsByRecordId(SF_RECORD_ID);
        checkFalse(sfDocumentsList.isEmpty(), "Documents list should be empty");

        String sfDocumentId = sfDocumentsList.get(0).id;
        String pdffillerProjectId = dadadocsApi.documents().upload(sfDocumentId);
        checkTrue(pdffillerProjectId.matches(PROJECT_ID_REGEXP),
                "Actual response '" + pdffillerProjectId + "' matched projectId regexp");

        String result = dadadocsApi.documents()
                .sendByEmail(pdffillerProjectId, emailTo, "test subject", "test body");
        assertTrue(result.contains("success"), "Actual response '" + result + "' equals success");
    }

    @Story("Should return link to fill")
    @Test
    public void getLinkToFillForDocumentTest() throws IOException, URISyntaxException {
        List<SalesforceDocument> sfDocumentsList = dadadocsApi.documents()
                .getSfDocumentsByRecordId(SF_RECORD_ID);
        checkFalse(sfDocumentsList.isEmpty(), "Documents list should be empty");

        String sfDocumentId = sfDocumentsList.get(0).id;
        String pdffillerProjectId = dadadocsApi.documents().upload(sfDocumentId);
        checkTrue(pdffillerProjectId.matches(PROJECT_ID_REGEXP),
                "Actual response '" + pdffillerProjectId + "' matched projectId regexp");


        LinkToFillResponse linkToFillResponse = dadadocsApi.documents()
                .getLinkToFill(pdffillerProjectId, SF_RECORD_ID);
        checkEquals(linkToFillResponse.document_id, pdffillerProjectId, "ProjectIds should be equals");
        assertTrue(linkToFillResponse.url.contains("https://pdf.ac"),
                "Actual response '" + linkToFillResponse.url + "' matches link regexp");
    }
}
