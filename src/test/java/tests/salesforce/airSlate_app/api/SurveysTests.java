package tests.salesforce.airSlate_app.api;

import api.salesforce.entities.airslate.FeedbackSurvey;
import listeners.WebTestListener;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import tests.salesforce.SalesforceAirSlateBaseTest;

import static org.testng.Assert.assertEquals;

@Listeners({WebTestListener.class})
public class SurveysTests extends SalesforceAirSlateBaseTest {

    @Test
    public void sendFeedbackSurvey() {
        FeedbackSurvey feedbackSurvey = new FeedbackSurvey();
        feedbackSurvey.setSubject("Subject from AUTOTESTS");
        feedbackSurvey.setMessage("Message from AUTOTESTS");
        HttpResponse feedbackSurveyResponse = salesforceAirslateApi.survey().sendFeedbackSurvey(feedbackSurvey);
        assertEquals(feedbackSurveyResponse.getStatusLine().getStatusCode(), HttpStatus.SC_OK, "Response Status code");
    }

    @Test
    public void sendUninstallSurvey() {
        FeedbackSurvey feedbackSurvey = new FeedbackSurvey();
        feedbackSurvey.setSubject(StringUtils.EMPTY);
        feedbackSurvey.setMessage("Message from AUTOTESTS");
        HttpResponse feedbackSurveyResponse = salesforceAirslateApi.survey().sendFeedbackSurvey(feedbackSurvey);
        assertEquals(feedbackSurveyResponse.getStatusLine().getStatusCode(), HttpStatus.SC_OK, "Response Status code");
    }
}
