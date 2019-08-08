package pages.servicenow.app.objects;

import pages.servicenow.app.objects.company.CompanyPage;
import pages.servicenow.app.objects.incident.IncidentPage;
import pages.servicenow.app.objects.user.UserPage;

/**
 * Created by horobets on Aug 06, 2019
 */
public enum ServiceNowObject {

    COMPANY("Vendor", CompanyPage.class),
    USER("User", UserPage.class),
    INCIDENT("Incident", IncidentPage.class);

    private String name;
    private Class pageClass;

    ServiceNowObject(String name, Class pageClass) {
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
