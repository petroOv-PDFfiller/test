package pages.salesforce.enums.admin_tools;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pages.salesforce.app.airSlate_app.admin_tools.tabs.*;

import static data.salesforce.SalesforceTestData.ASAppPageTexts.YOU_DO_NOT_HAVE_ENOUGH_PERMISSIONS_CUSTOM_BUTTON;
import static data.salesforce.SalesforceTestData.ASAppPageTexts.YOU_DO_NOT_HAVE_ENOUGH_PERMISSIONS_TEAMMATES;

@AllArgsConstructor
public enum ASAppAdminToolTabs {

    ACCOUNT("Account", "", AccountTab.class),
    SCHEDULED_FLOWS("Scheduled Flows", "", ScheduledFlowsTab.class),
    WORKSPACE("Workspace", "", WorkspaceTab.class),
    TEAMMATES("Teammates", YOU_DO_NOT_HAVE_ENOUGH_PERMISSIONS_TEAMMATES, TeammatesTab.class),
    SETUP_WIZARD("Setup Wizard", "", SetupWizardTab.class),
    CUSTOM_BUTTONS("Custom Buttons", YOU_DO_NOT_HAVE_ENOUGH_PERMISSIONS_CUSTOM_BUTTON, CustomButtonTab.class),
    DASHBOARD("Dashboard", "", DashboardTab.class);

    @Getter
    private String name;
    @Getter
    private String permissionsErrorMessage;
    private Object expectedPage;

    public <T> T getExpectedPage() {
        return (T) expectedPage;
    }
}
