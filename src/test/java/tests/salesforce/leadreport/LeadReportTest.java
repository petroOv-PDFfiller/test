package tests.salesforce.leadreport;

import imap.ImapClient;
import imap.With;
import io.qameta.allure.Feature;
import listeners.ImapListener;
import listeners.WebTestListener;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import tests.salesforce.SalesforceBaseTest;
import utils.TimeMan;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.util.List;

import static core.check.Check.checkEquals;
import static core.check.Check.checkTrue;
import static org.testng.Assert.assertEquals;

@Listeners({WebTestListener.class, ImapListener.class})
@Feature("LeadReports : Tests")
public class LeadReportTest extends SalesforceBaseTest {
    private final String EMAIL_FROM = "salesforce_codes@pdffiller.com";
    private final String DATE_FORMAT = "dd/MM/yyyy";
    private final String LEADS_BY_DATE_REPORT_SUBJECT = "Dashboard: DDD Leads by Date";
    private final String TRIALS_REPORT_SUBJECT = "DaDaDocs Trials Report";
    private String email;
    private String password;
    private ImapClient imap;

    @BeforeTest
    public void setUp() {
        this.email = testData.emails.get(0);
        this.password = testData.password;
        this.imap = new ImapClient(email, password);
    }

    @Test
    public void checkIfReportsArePresent() throws MessagingException {
        List<Message> reports = imap.findMessages(With.from(EMAIL_FROM));
        checkEquals(reports.size(), 2, "There should be 2 reports from " + EMAIL_FROM);

        checkTrue(imap.findMessages(With.subject(LEADS_BY_DATE_REPORT_SUBJECT)).size() == 1,
                "DDD Leads By Date report is present");
        checkTrue(imap.findMessages(With.subject(TRIALS_REPORT_SUBJECT)).size() == 1,
                "DaDaDocs Trials Report is present");

        for (Message report : reports) {
            assertEquals(TimeMan.format(report.getReceivedDate(), DATE_FORMAT),
                    TimeMan.format(TimeMan.getCurrentDate(), DATE_FORMAT),
                    "Reports recieved date");
        }
    }

    @AfterTest
    public void tearDown() {
        imap.deleteAllMessagesWithSubject(LEADS_BY_DATE_REPORT_SUBJECT);
        imap.deleteAllMessagesWithSubject(TRIALS_REPORT_SUBJECT);
        imap.closeFolders();
        ImapClient.closeStore();
    }
}
