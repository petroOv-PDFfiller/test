package data.salesforce;

import pages.salesforce.app.sf_setup.custom_object.popups.*;

public class SalesforceTestData {

    public enum SchemaBuilderElements {
        OBJECT("Object", NewObjectFieldsPopUp.class),
        LOOKUP("Lookup", LookupPopUp.class),
        CURRENCY("Currency", CurrencyPopUp.class),
        DATE("Date", DatePopUp.class),
        NUMBER("Number", NumberPopUp.class),
        PHONE("Phone", PhonePopUp.class),
        PICKLIST("Picklist", PicklistPopUp.class);

        private String name;
        private Object expectedPage;

        SchemaBuilderElements(String name, Object expectedPage) {
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

    public enum SchemaBuilderTabs {
        OBJECTS("ObjectHeader"),
        ELEMENTS("SchemaElementHeader");

        private String name;

        SchemaBuilderTabs(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum SalesforceSetupOptions {
        INSTALLED_PACKAGES("Installed Packages"),
        TABS("Tabs"),
        PROCESS_BUILDER("Process Builder"),
        SCHEMA_BUILDER("Schema Builder");

        private String name;

        SalesforceSetupOptions(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static class DocumentsActions {
        public static final String EDIT_DOCUMENT = "Edit Document";
        public static final String SEND_TO_SIGN = "SendToSign";
        public static final String LINK_TO_FILL = "LinkToFill";
        public static final String RENAME = "Rename";
        public static final String SEND_BY_EMAIL = "Send by Email";
        public static final String PRINT_DOCUMENT = "Print Document";
        public static final String SHARE = "Share";
        public static final String DELETE = "Delete";
        public static final String DOCUMENT_PACKAGES = "Document Packages";
        public static final String CREATE_TEMPLATE = "Create Template";
        public static final String FILL_TEMPLATE = "Fill Template";
    }

    public static class DaDaDocsAlerts {
        public static final String DELETED_SUCCESSFULLY = "Document has been successfully deleted";
        public static final String DELETED_SUCCESSFULLY_TEMPLATE = "Template has been successfully deleted";
        public static final String RENAMED_SUCCESSFULLY = "Document has been successfully renamed";
        public static final String SUCCESS_OPERATION_MESSAGE = "Success";

    }

    public static class DaDaDocsTabs {
        public static final String DOCUMENTS_TAB = "Documents";
        public static final String TEMPLATES_TAB = "Templates";
        public static final String DOCX_TEMPLATES_TAB = "DOCX templates";
        public static final String FILE_STATUS_TAB = "File Status";
    }

    public static class DaDaDocsTemplates {
        public static final String NO_MAP_PDF_TEMPLATE = "noMapPDFTemplate";
        public static final String DC_FORM_TEMPLATE = "DCFormTemplate";
        public static final String PDF_TEMPLATE = "PDFTemplate";
    }

    public static class TemplatesActions {
        public static final String PREVIEW_TEMPLATE = "Preview Template";
        public static final String FILL_TEMPLATE = "Fill Template";
        public static final String SEND_TO_SIGN = "SendToSign";
        public static final String LINK_TO_FILL = "LinkToFill";
        public static final String SEND_BY_EMAIL = "Send by Email";
        public static final String PRINT_TEMPLATE = "Print Document";
        public static final String SHORTCUT_BUTTON = "Share";
        public static final String EDIT_TEMPLATE = "Edit Template";
        public static final String GET_INFO = "Get Info";
        public static final String DELETE_TEMPLATE = "Delete Template";
        public static final String TEMPLATE_PACKAGES = "Create Template";
    }

    public static class DaDaDocsSelectFilesActions {
        public static final String MERGE_PDFS = "Merge PDFs";
        public static final String SEND_TO_SIGN = "SendToSign";
        public static final String LINK_TO_FILL = "LinkToFill";
    }

    public static class LinkToFillTabs {
        public static final String CUSTOMIZE = "Customize";
        public static final String SELECT_OPTIONS = "Select Options";
        public static final String ACTIVATE = "Activate";
    }

    public static class SortTypes {
        public static final String DESC_SORT = "DESC";
        public static final String ASC_SORT = "ASC";
    }

    public static class UserLicense {
        public static final String ACTIVE = "Active";
        public static final String NOT_ACTIVE = "Not active";
    }

    public static class UserProfiles {
        public static final String SYSTEM_ADMINISTRATOR = "System Administrator";
        public static final String STANDARD_USER = "Standard User";
        public static final String STANDARD_PLATFORM_USER = "Standard Platform User";
    }

    public static class SalesforceUserGetMethods {
        public static final String GET_FULL_NAME = "getFullName";
        public static final String GET_EMAIL = "getEmail";
        public static final String GET_PROFILE = "getProfile";
        public static final String GET_LICENSE = "getLicense";
    }

    public static class SalesforceLayoutsGetMethods {
        public static final String OBJECT_NAME = "getObjectName";
        public static final String LAYOUT_NAME = "getLayoutName";
    }

    public static class SalesforceLayoutsFilters {
        public static final String ALL = "All layouts ";
        public static final String NOT_ACTIVE = "Only not active ";
        public static final String ACTIVE = "Only active ";

    }

    public static class SalesforceOpportunityStages {
        public static final String PROSPECTING = "Prospecting";
        public static final String QUALIFICATION = "Qualification";
        public static final String NEEDS_ANALYSIS = "Needs Analysis";
        public static final String VALUE_PROPOSITION = "Value Proposition";
        public static final String ID_DECISION_MAKERS = "Id. Decision Makers";
        public static final String PERCEPTION_ANALYSIS = "Perception Analysis";
        public static final String PROPOSAL_PRICE_QUOTE = "Proposal/Price Quote";
        public static final String NEGOTIATION_REVIEW = "Negotiation/Review";
        public static final String CLOSED = "Closed Won";
    }

    public static class SalesforceApexVariables {
        public static final String STRING = "getFullName";
        public static final String ID = "ID";
        public static final String FIELD_REFERENCE = "Field Reference";
        public static final String FORMULA = "Formula";
    }

    public static class SalesforceProcessBuilderActionTypes {
        public static final String APEX = "Apex";
    }

    public static class SalesforceApexClasses {
        public static final String AUTOMATED_TEMPLATES_SENDER = "Automated Templates Sender";
        public static final String OPPORTUNITY_AUTOMATED_TEMPLATES_SENDER = "Opportunity Automated Templates Sender";
    }

    public static class NotificationMessages {
        public static final String THIS_EMAIL_IS_NOT_IN_OUR_SYSTEM = "Oh no! This email isn't in our system.";
        public static final String WRONG_EMAIL_OR_PASSWORD = "Oops.. Wrong email or password. Try another email or reset your password.";
        public static final String WORKSPACE_IS_CONNECTED = "Workspace is connected.";
        public static final String WORKSPACE_IS_DISCONNECTED = "Workspace has been disconnected.";
        public static final String CREATED_A_CUSTOM_BUTTON = "Done! You’ve created a Custom button. Give it some time to appear on all selected layouts.";
        public static final String UPDATED_A_CUSTOM_BUTOON = "Done! Your Custom button will be updated on the selected layouts in a few minutes.";
        public static final String DELETED_A_CUSTOM_BUTOON = "Done! Your Custom button will be deleted from all selected layouts in a few minutes.";
    }

    public static class ASAppPageTexts {
        public static final String CHOOSE_WORKSPACE_TITLE = "Choose workspace";
        public static final String TELL_US_ABOUT_YOUR_COMPANY_TITLE = "Tell us about your company";
        public static final String CHOOSE_WORKSPACE_SUBTITLE = "Choose the airSlate workspace where you'd like to create Flows and add Salesforce teammates to.";
        public static final String TELL_US_ABOUT_YOUR_COMPANY_SUBTITLE = "The information here will help us personalize your workspace for you and your team.";
        public static final String AFTER_DISCONNECTING_WORKSPACE = "After disconnecting an airSlate admin account, teammates will still be able to use airSlate. Disconnect your workspace if you need to limit access to it.Disconnect Workspace";
        public static final String YOU_DO_NOT_HAVE_ENOUGH_PERMISSIONS_TEAMMATES = "You do not have enough permissions to manage users for this Workspace. Please go to the Account tab, and connect Workspace admin account.";
        public static final String YOU_DO_NOT_HAVE_ENOUGH_PERMISSIONS_CUSTOM_BUTTON = "You do not have enough permissions to manage custom buttons for this Workspace. Please go to the Account tab, and connect Workspace admin account.";
        public static final String SET_UP_YOUR_FIRST_CUSTOM_BUTTON = "Set up your first Custom button";
        public static final String YOU_CAN_USE_SALESFORCE_RECORDS_TO = "You can use Salesforce records to create a Document Slate in a single click.\n All Custom buttons you create will be listed here.";
        public static final String SORRY_NOTHING_CAME_UP = "Sorry, nothing came up";
        public static final String TRY_CLEANING_THE_SEARCH = "Try clearing the search and trying again with another keyword.";
        public static final String WELCOME_TO_AIRSLATE = "Welcome to airSlate!";
        public static final String LOG_IN_AS_AIRSLATE_ADMIN = "Log in as airSlate Workspace Admin";
        public static final String LOG_IN_TO_AIRSLATE = "Log in to airSlate";
        public static final String FORGOT_YOUR_PASSWORD = "Forgot your password?";
        public static final String OUCH = "Ouch!";
        public static final String OOPS = "Looks like you haven't selected any records. You’ll be redirected to list view in a few seconds";
        public static final String SORRY_ABOUT_THAT = "Salesforce can process a maximum of 75 records.";
        public static final String ALMOST_DONE = "We’ve started your batch. Once finished, you’ll receive your batch report by email. You’ll be redirected to list view in a few seconds.";
        public static final String WE_VE_NOTIFIED_YOUR_ADMIN_ABOUT_THE_PROBLEM = "Something got in the way. We've notified your Admin about the problem.";
        public static final String ALL_SET = "All set!You’ll be redirected to the Salesforce record in a few seconds.";
        public static final String RECOVERY_INSTRUCTIONS = "No worries! Let us know your email and we'll send you recovery instructions.";
        public static final String IF_YOU_HAVENT_RECEIVED_AN_INVITE = "If you haven’t received an invite to your company’s workspace, please contact your Salesforce Admin ";
        public static final String NOTHING_HERE = "Nothing here";
        public static final String AIR_SLATE_HASN_T_BEEN_USED_WITH_THIS_RECORD_YET = "airSlate hasn't been used with this record yet";
        public static final String NO_WORKSPACE_HAS_BEEN_CONNECTED = "No workspace has been connected";
        public static final String PLEASE_CONTACT_YOUR_ADMIN_TO_CONNECT_AIR_SLATE_WORKSPACE_TO_YOUR_SALESFORCE_ORGANIZATION = "Please contact your admin to connect airSlate workspace to your Salesforce organization.";
    }

    public static class ASAppPopUpTexts {
        public static final String IF_YOU_DISCONNECT_AIRSLATE_WORKSPACE = "If you disconnect airSlate Workspace, your users will no longer be able to access Flows and Slates that were previously available to them in Salesforce.";
        public static final String ENTER_BUTTON_NAME = "Enter button name";
        public static final String ENTER_BUTTON_LABEL = "Enter button label";
        public static final String SELECT_A_FLOW = "Select a Flow";
        public static final String SELECT_AN_ACTION = "Create Slate (default)";
        public static final String SEARH_BY_LAYOUT_AND_LIST_NAME = "Search by layout and list name";
        public static final String DELETING_CUSTOM_BUTTON = "Deleting custom button will remove it from all the Layouts. Are you sure you want to proceed?";
        public static final String WRONG_PIN_CODE = "Oops! Wrong pin code.";
        public static final String NEW_USER_IS_REGISTERED = "New user is registered and automatically logged in to airSlate App.";
        public static final String OH_NO_THIS_EMAIL_ISN_T_IN_OUR_SYSTEM = "Oh no! This email isn't in our system.";
        public static final String DELETING_SCHEDULE_WILL_STOP_SLATES = "Deleting schedule will stop slates being created automatically in a selected Flow. Are you sure you want to proceed?";
    }

    public static class ASAppErrorMessages {
        public static final String WORKSPACE_SUBDOMAIN_ERROR = "Please enter a valid organization domain. Domain names can be up to 63 characters containing: letters, numbers, - (hyphen).";
        public static final String ENTER_AT_LEAST_6_CHARACTERS_PASSWORD = "Please enter at least 6 characters for a password";
        public static final String PLEASE_ENTER_A_VALID_EMAIL_ADDRESS = "Please enter a valid email address";
    }

    public static class ASAppWorkspaceIndustries {
        public static final String HEALTHCARE_AND_SOCIAL_ASSISTANCE = "Healthcare and Social Assistance";
        public static final String INSURANCE = "Insurance";
        public static final String CONSTRUCTIN = "Construction";
        public static final String GOVERNMENT = "Government";
        public static final String EDUCATIONAL_SERVICES = "Educational Services";
        public static final String REAL_ESTATE = "Real Estate";
        public static final String PROFESSIONAL_AND_BUSINESS_SERVICES = "Professional and Business Services";
        public static final String LEISURE_AND_HOSPITALITY = "Leisure and Hospitality";
        public static final String BANKING = "Banking";
        public static final String WHOLESALE_TRADE = "Wholesale Trade";
        public static final String RETAIL_TRADE = "Retail Trade";
        public static final String NONPROFIT = "Nonprofit";
    }

    public static class ASAppWorkspaceSizes {
        public static final String ZERO_FIVE = "0-5";
        public static final String SIX_FIFTY = "6-50";
        public static final String FIFTY_TWOHUNDREDS = "51-200";
        public static final String TWOHUNDREDS_ONETHOUSAND = "0-5";
        public static final String ONETHOUSAND_TWOTHOUSAND = "0-5";
        public static final String TWOTHOUSAND_PLUS = "0-5";
    }

    public static class ASAppCustomButtonSortOrder {
        public static final String ASC = "sortLabelAsc__";
        public static final String DESC = "sortLabel__";
    }

    public static class EmailSubjects {
        public static final String RESET_YOUR_AIR_SLATE_PASSWORD = "Reset your airSlate password";
    }
}

