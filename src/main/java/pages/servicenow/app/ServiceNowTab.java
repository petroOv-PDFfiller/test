package pages.servicenow.app;

import pages.servicenow.app.objects.company.CompaniesListPage;
import pages.servicenow.app.objects.incident.IncidentsListPage;
import pages.servicenow.app.objects.user.UsersListPage;

/**
 * Created by horobets on Aug 05, 2019
 */
public enum ServiceNowTab {

    DASHBOARD("Dashboards", DashboardPage.class),
    COMPANIES("Vendors", CompaniesListPage.class),
    USERS("Users", UsersListPage.class),
    INCIDENTS("Incidents", IncidentsListPage.class);

    private String name;
    private Class pageClass;

    ServiceNowTab(String name, Class pageClass) {
        this.name = name;
        this.pageClass = pageClass;
    }

    @Override
    public String toString() {
        return name;
    }

    public <T extends Class> T getPageClass() {
        if (pageClass == null) {
            throw new NullPointerException();
        }
        return (T) pageClass;
    }
}
