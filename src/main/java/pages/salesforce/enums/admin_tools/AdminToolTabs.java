package pages.salesforce.enums.admin_tools;

import pages.salesforce.app.DaDaDocs.admin_tools.tabs.AuthorizationTab;
import pages.salesforce.app.DaDaDocs.admin_tools.tabs.LayoutsTab;
import pages.salesforce.app.DaDaDocs.admin_tools.tabs.SettingsTab;
import pages.salesforce.app.DaDaDocs.admin_tools.tabs.UsersTab;

public enum AdminToolTabs {

    AUTHORIZATION("Authorization", AuthorizationTab.class),
    USERS("Users", UsersTab.class),
    LAYOUTS("Layouts", LayoutsTab.class),
    SETTINGS("Settings", SettingsTab.class);

    private String name;
    private Object expectedPage;

    AdminToolTabs(String name, Object expectedPage) {
        this.name = name;
        this.expectedPage = expectedPage;
    }

    public String getName() {
        return name;
    }

    public <T extends Object> T getExpectedPage() {
        return (T) expectedPage;
    }
}
