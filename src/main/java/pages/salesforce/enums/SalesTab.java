package pages.salesforce.enums;

import pages.salesforce.app.sf_objects.accounts.AccountConcretePage;
import pages.salesforce.app.sf_objects.campaigns.CampaignConcretePage;
import pages.salesforce.app.sf_objects.contacts.ContactConcretePage;
import pages.salesforce.app.sf_objects.opportunities.OpportunitiesConcretePage;

public enum SalesTab {

    HOME("home_Tab", "Home", null),
    CHATTER("Chatter_Tab", "Chatter", null),
    CAMPAIGNS("Campaign_Tab", "Campaigns", CampaignConcretePage.class),
    LEADS("Lead_Tab", "Leads", null),
    ACCOUNTS("Account_Tab", "Accounts", AccountConcretePage.class),
    CONTACTS("Contact_Tab", "Contacts", ContactConcretePage.class),
    OPPORTUNITIES("Opportunity_Tab", "Opportunities", OpportunitiesConcretePage.class),
    FORECASTS("Forecasting3_Tab", "Forecasts", null),
    CONTRACTS("Contract_Tab", "Contracts", null),
    ORDERS("Order_Tab", "Orders", null),
    CASES("Case_Tab", "Cases", null),
    SOLUTIONS("Solution_Tab", "Solutions", null),
    PRODUCTS("Product2_Tab", "Products", null),
    REPORTS("report_Tab", "Reports", null),
    DASHBOARDS("Dashboard_Tab", "Dashboards", null);

    private String nameOldLayout;
    private String nameNewLayout;
    private Object concretePage;

    SalesTab(String nameOldLayout, String nameNewLayout, Object concretePage) {
        this.nameOldLayout = nameOldLayout;
        this.nameNewLayout = nameNewLayout;
        this.concretePage = concretePage;
    }

    public String getNameOldLayout() {
        return nameOldLayout;
    }

    public String getNameNewLayout() {
        return nameNewLayout;
    }

    public <T extends Object> T getConcretePage() {
        if (concretePage == null) {
            throw new NullPointerException();
        }
        return (T) concretePage;
    }
}
