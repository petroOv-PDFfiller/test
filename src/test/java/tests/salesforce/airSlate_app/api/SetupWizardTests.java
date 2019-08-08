package tests.salesforce.airSlate_app.api;

import api.salesforce.entities.airslate.SetupWizardStage;
import api.salesforce.util.HttpResponseUtils;
import listeners.WebTestListener;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import tests.salesforce.SalesforceAirSlateBaseTest;

import static core.check.Check.checkEquals;
import static org.testng.Assert.assertEquals;

@Listeners({WebTestListener.class})
public class SetupWizardTests extends SalesforceAirSlateBaseTest {
    @Test
    public void setup_WizardAuthorizationStage() {
        SetupWizardStage.Stage expectedStage = SetupWizardStage.Stage.AUTHORIZATION;
        HttpResponse wizardResponse = salesforceAirslateApi.setupWizard().setCurrentStage(expectedStage);
        SetupWizardStage setupWizardStage = HttpResponseUtils.parseResponse(wizardResponse, SetupWizardStage.class);
        checkEquals(wizardResponse.getStatusLine().getStatusCode(), HttpStatus.SC_OK, "Response Status code");
        assertEquals(setupWizardStage.getStage(), expectedStage, "Setup Wizard Stage");
    }

    @Test
    public void get_CurrentWizardStep() {
        setSetupWizardStage(SetupWizardStage.Stage.TEAMMATES);

        HttpResponse wizardStepResponse = salesforceAirslateApi.setupWizard().getCurrentStage();
        SetupWizardStage wizardStepResponseBody = HttpResponseUtils.parseResponse(wizardStepResponse, SetupWizardStage.class);
        checkEquals(wizardStepResponse.getStatusLine().getStatusCode(), HttpStatus.SC_OK, "Response Status code");
        assertEquals(wizardStepResponseBody.getStage().getStageName(),
                SetupWizardStage.Stage.TEAMMATES.getStageName(), "Current SetupWizard stage");
    }

    @AfterMethod
    public void setupFinalWizardStep() {
        salesforceAirslateApi.setupWizard().setCurrentStage(SetupWizardStage.Stage.FINAL);
    }
}
