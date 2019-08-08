package tests.salesforce.apexrest;

import api.salesforce.entities.dadadocs.templates.SalesforceTemplate;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import listeners.WebTestListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static org.testng.Assert.assertFalse;

@Feature("Salesforce Templates apexrest tests")
@Listeners(WebTestListener.class)
public class SalesforceTemplatesTest extends BaseApexrestTest {

    @Story("Get documents and projects")
    @Test
    public void getAllTemplatesTest() throws IOException, URISyntaxException {
        List<SalesforceTemplate> sfTemplates = dadadocsApi.templates()
                .getAllTemplates(SF_RECORD_ID);
        assertFalse(sfTemplates.isEmpty(), "Documents list should be empty");
    }
}
