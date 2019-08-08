package tests.salesforce.apexrest;

import api.salesforce.DadadocsApi;
import core.TestBase;
import listeners.WebTestListener;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.io.IOException;
import java.net.URISyntaxException;

@Listeners({WebTestListener.class})
public class BaseApexrestTest extends TestBase {

    protected static final String PROJECT_ID_REGEXP = "\\d{5,}";
    protected static final String SF_RECORD_ID = "00Q0O000010G4Rw"; // Lead:Mr Jeff Glimpse [APEXREST AUTOTESTS]
    protected static final String FIRST_EMAIL_TO = "pd+sf1@support.pdffiller.com";
    protected static final String SECOND_EMAIL_TO = "pd+sf2@support.pdffiller.com";
    protected DadadocsApi dadadocsApi;
    protected String packageNamespace = "pdffiller_sf";

    @Parameters({"tokenUrl", "orgUserEmail", "orgUserPassword"})
    @BeforeTest
    public void setUp(@Optional("https://login.salesforce.com/services/oauth2/token") String tokenUrl,
                      @Optional("savchuk.andrii+autotest@pdffiller.team") String orgUserEmail,
                      @Optional("Likas_qwe1rty3") String orgUserPassword) throws IOException, URISyntaxException {

        this.dadadocsApi = new DadadocsApi(tokenUrl, orgUserEmail, orgUserPassword, packageNamespace);
        dadadocsApi.auth();
    }
}
