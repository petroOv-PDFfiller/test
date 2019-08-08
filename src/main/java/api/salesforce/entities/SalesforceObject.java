package api.salesforce.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pages.salesforce.app.sf_objects.ConcreteRecordPage;
import pages.salesforce.app.sf_objects.CustomObjectDefaultConcretePage;
import pages.salesforce.app.sf_objects.SalesforceObjectPage;
import pages.salesforce.app.sf_objects.accounts.AccountConcretePage;
import pages.salesforce.app.sf_objects.accounts.AccountsPage;
import pages.salesforce.app.sf_objects.contacts.ContactConcretePage;
import pages.salesforce.app.sf_objects.contacts.ContactsPage;
import pages.salesforce.app.sf_objects.opportunities.OpportunitiesConcretePage;
import pages.salesforce.app.sf_objects.opportunities.OpportunitiesPage;

import static api.salesforce.SalesforceRestApi.isPackageHasNamespace;

@AllArgsConstructor
public enum SalesforceObject {
    ACCOUNT("Account", "Account", "ATAcc", null, AccountsPage.class, AccountConcretePage.class),
    PRODUCT_2("Product2", "Product2", "ATPro", null, null, null),
    PRICEBOOK_2("Pricebook2", "Pricebook2", "ATPrb", null, null, null),
    PRICEBOOK_ENTRY("PricebookEntry", "PricebookEntry", "ATPbe", null, null, null),
    ATTACHMENT("Attachment", "Attachment", "", null, null, null),
    OPPORTUNITY("Opportunity", "Opportunity", "ATOpp", null, OpportunitiesPage.class, OpportunitiesConcretePage.class),
    OPPORTUNITY_LINE_ITEM("OpportunityLineItem", "OpportunityLineItem", "ATOli", null, null, null),
    OPPORTUNITY_CONTACT_ROLE("OpportunityContactRole", "OpportunityContactRole", "ATOcr", null, null, null),
    CONTACT("Contact", "Contact", "ATCLN", null, ContactsPage.class, ContactConcretePage.class),
    CORPORATE_ORDER("Corporate order", "Order_Product__c", "ATCor", null, null, CustomObjectDefaultConcretePage.class),
    USER_CONFIGURATION("User Configuration", "UserConfig__c", "ATUC", Constants.SALESFORCE_AIRSLATE, null, null),
    RELATION_DOCUMENT_ROLES("Relation Document Roles", "DocumentRolesRelations__c", "ATDRR", Constants.SALESFORCE_DADADOCS, null, null),
    CONTENT_DOCUMENT("Content Document", "ContentDocument", "ATCD", null, null, null),
    DOCUMENT("Document", "Document__c", "ATDoc", Constants.SALESFORCE_DADADOCS, null, null),
    FILE_TEMPLATE("File Template", "FileTemplate__c", "ATFT", Constants.SALESFORCE_DADADOCS, null, null),
    CUSTOM_OBJECT("My Custom Object", "MyCustomObject__c", "ATMCO", null, null, CustomObjectDefaultConcretePage.class),
    USER("User", "User", "ATUsr", null, null, null),
    EMAIL_MESSAGE("EmailMessage", "EmailMessage", "ATEmail", null, null, null),
    SLATES_ACTIVITY("SlatesActivity", "SlatesActivity__c", "ATEmail", Constants.SALESFORCE_AIRSLATE, null, null),
    LEAD("Lead", "Lead", "ATLead", null, null, null),
    CAMPAIGN("Campaign", "Campaign", "ATCampaigns", null, null, null);

    @Getter
    private String objectName;
    private String APIName;
    @Getter
    private String uniquePrefix;
    private String objectNamespace;
    @Getter
    private Class<? extends SalesforceObjectPage> objectPage;
    @Getter
    private Class<? extends ConcreteRecordPage> recordPage;

    public String getAPIName() {
        if (isPackageHasNamespace && objectNamespace != null) {
            return objectNamespace + "__" + APIName;
        }
        return APIName;
    }

    @Override
    public String toString() {
        return objectName;
    }

    private static class Constants {
        public static final String SALESFORCE_DADADOCS = "pdffiller_sf";
        public static final String SALESFORCE_AIRSLATE = "pdffiller_sfree";
    }
}
