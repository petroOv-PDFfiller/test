package data;

import utils.FileMan;
import utils.Logger;

import java.io.File;
import java.nio.file.Paths;

/**
 * Created by Vladyslav on 02.11.2015.
 */
public class TestData {

    public static final String PATH_TO_WIN_CHROME_DRIVER = new File("src/main/resources/drivers/chromedriver.exe").getAbsolutePath();
    public static final String PATH_TO_MAC_CHROME_DRIVER = new File("src/main/resources/drivers/chromedriver").getAbsolutePath();
    public static final String PATH_TO_FIREFOX_DRIVER = new File("src/main/resources/drivers/geckodriver.exe").getAbsolutePath();
    public static final String PATH_TO_IE_DRIVER = new File("src/main/resources/drivers/iedriver.exe").getAbsolutePath();
    public static final String PATH_TO_LINUX_CHROME_DRIVER = new File("src/main/resources/drivers/linux_chromedriver").getAbsolutePath();
    public static final String PATH_TO_WIN_DOWNLOADS_FOLDER = new File("C:/testResources/downloadDir/").getAbsolutePath();
    public static final String PATH_TO_MAC_DOWNLOADS_FOLDER = new File("/Library/downloadDir/").getAbsolutePath();
    public static final String PATH_TO_LINUX_DOWNLOADS_FOLDER = Paths.get(System.getProperty("user.home"), "Downloads").toString();
    public static final String PATH_TO_WIN_ACCESSORY_TEST_RESOURCES_FOLDER = new File("C:/testResources/").getAbsolutePath();
    public static final String PATH_TO_MAC_ACCESSORY_TEST_RESOURCES_FOLDER = new File("/Library/testResources/").getAbsolutePath();
    public static final String PATH_TO_LINUX_ACCESSORY_TEST_RESOURCES_FOLDER = new File("/testResources").getAbsolutePath();
    public static final String PATH_TO_JACOB_LIB = new File("src/main/resources/jacob/jacob-1.18-M3-x64.dll").getAbsolutePath();
    public static final String PATH_TO_PDFFILLER_CHROME_EXTENSION = new File("src/main/resources/chrome_extensions/gphandlahdpffmccakmbngmbjnjiiahp/0.2.5_0").getAbsolutePath();
    public static final String defaultPassword = "qwe1rty2";
    public static final String REGISTRATION_EMAIL_SUBJECT = "[PDFfiller] Thank you for registering";
    public static final String URL_FOR_UPLOAD = "http://www.pdf995.com/samples/pdf.pdf";
    public static final String URL_FOR_UPLOAD_FROM_CDN = "http://cdn.pdffiller.com/test/pdf.pdf";
    public static final String BROKEN_FILE_TO_UPLOAD = new File("src/test/resources/uploaders/broken.docx").getAbsolutePath();
    public static String PATH_TO_DOWNLOADS_FOLDER;
    public static String PATH_TO_ACCESSORY_TEST_RESOURCES_FOLDER;

    public static void setUpOsConfig() {
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            Logger.info("Set win os folder paths");
            PATH_TO_DOWNLOADS_FOLDER = PATH_TO_WIN_DOWNLOADS_FOLDER;
            PATH_TO_ACCESSORY_TEST_RESOURCES_FOLDER = PATH_TO_WIN_ACCESSORY_TEST_RESOURCES_FOLDER;
        } else if (System.getProperty("os.name").toLowerCase().contains("linux")) {
            Logger.info("Set linux os folder paths");
            FileMan.createDirectory(PATH_TO_LINUX_DOWNLOADS_FOLDER);
            PATH_TO_DOWNLOADS_FOLDER = PATH_TO_LINUX_DOWNLOADS_FOLDER;
            PATH_TO_ACCESSORY_TEST_RESOURCES_FOLDER = PATH_TO_LINUX_ACCESSORY_TEST_RESOURCES_FOLDER;
        } else {
            Logger.info("Set mac os folder paths");
            FileMan.createDirectory(PATH_TO_MAC_DOWNLOADS_FOLDER);
            PATH_TO_DOWNLOADS_FOLDER = PATH_TO_MAC_DOWNLOADS_FOLDER;
            PATH_TO_ACCESSORY_TEST_RESOURCES_FOLDER = PATH_TO_MAC_ACCESSORY_TEST_RESOURCES_FOLDER;
        }
    }

    public static class DefaultDocsNames {

        public static final String hostAFillableForm = "Host a Fillable Form";
        public static final String getADocumentSigned = "Get a Document Signed";
        public static final String pdfFillerHowToGuide = "PDFfiller How To Guide";
        public static final String quickGuideToTemplates = "Quick Guide to Templates";
    }

    public static class FilesToUpload {

        public static final String docxFileLink = "https://calibre-ebook.com/downloads/demos/demo.docx";
        public static final String txtFileLink = "http://www.vulnerabilityassessment.co.uk/as400_users.txt";
        public static final String rtfFileLink = "https://jeroen.github.io/files/sample.rtf";
        public static final String pptxFileLink = "https://fyi.uwex.edu/whpe/files/2010/01/Understanding-Default-and-Foreclosure.pptx";
        public static final String tiffFileLink = "http://eeweb.poly.edu/~yao/EL5123/image/lena_color.tiff";
        public static final String tifFileLink = "http://www.aperfectworld.org/clipart/other_formats/animals/cat13.tif";
        public static final String pptFileLink = "https://www.iasted.org/conferences/formatting/Presentations-Tips.ppt";
        public static final String jpegFileLink = "https://upload.wikimedia.org/wikipedia/uk/f/fd/Jpeg%29.jpeg";


        public static final String pageSizeLetterFileLink = new File("src/test/resources/export/paper_sizes/Letter_PaperSizeExample.pdf").getAbsolutePath();
        public static final String pageSizeLegalFileLink = new File("src/test/resources/export/paper_sizes/Legal_PaperSizeExample.pdf").getAbsolutePath();
        public static final String pageSizeA4FileLink = new File("src/test/resources/export/paper_sizes/A4_PaperSizeExample.pdf").getAbsolutePath();

        public static final String pathToSendToSignSupportedImages = new File("C:/testResources/SendToSignUploads/Images/Supported/").getAbsolutePath();
        public static final String pathToSendToSignNotSupportedImages = new File("C:/testResources/SendToSignUploads/Images/NotSupported/").getAbsolutePath();
        public static final String pathToSendToSignSupportedDocs = new File("C:/testResources/SendToSignUploads/Docs/Supported/").getAbsolutePath();
        public static final String pathToSendToSignNotSupportedDocs = new File("C:/testResources/SendToSignUploads/Docs/NotSupported/").getAbsolutePath();
        public static final String fillableFileText = new File("src/test/resources/editor/fillable_text.pdf").getAbsolutePath();
        public static final String fillableFieldsFile = new File("src/test/resources/export/fillable_fields_doc.pdf").getAbsolutePath();
        public static final String constructorTest = new File("src/test/resources/editor/ConstructorTest.pdf").getAbsolutePath();
        public static final String superFillableDocNew = new File("src/test/resources/editor/SuperFillableDocNew.pdf").getAbsolutePath();
        public static final String superFillableDocNewInitial = new File("src/test/resources/editor/SuperFillableDocNew_Initial.pdf").getAbsolutePath();
        public static final String superFillableDocNewWithoutFillableFields = new File("src/test/resources/editor/SuperFillableDocNewWithoutFillableFields.pdf").getAbsolutePath();
        public static final String docFile = new File("src/test/resources/sharepoint_conversion_files/testDocFile.doc").getAbsolutePath();
        public static final String jpgFile = new File("src/test/resources/uploaders/ava.jpg").getAbsolutePath();
        public static final String pngFile = new File("src/test/resources/uploaders/imageUploader.png").getAbsolutePath();
        public static String editorFile = new File("src/test/resources/editor/editor.pdf").getAbsolutePath();
        public static String fillableFile = new File("src/test/resources/editor/fillable_signature.pdf").getAbsolutePath();

        public static class ExportDocsPaths {
            public static final String pdf149PagesFile = new File("src/test/resources/custom_upload/SaveAsUploads/149.pdf").getAbsolutePath();
            public static final String pdf150PagesFile = new File("src/test/resources/custom_upload/SaveAsUploads/150.pdf").getAbsolutePath();
            public static final String pdf151PagesFile = new File("src/test/resources/custom_upload/SaveAsUploads/151.pdf").getAbsolutePath();

            public static final String megabytes5File = new File("src/test/resources/custom_upload/SaveAsUploads/5_mb.pdf").getAbsolutePath();
            public static final String megabytes11File = new File("src/test/resources/custom_upload/SaveAsUploads/11_mb.pdf").getAbsolutePath();
            public static final String megabytes24File = new File("src/test/resources/custom_upload/SaveAsUploads/24_mb.pdf").getAbsolutePath();
            public static final String megabytes31File = new File("src/test/resources/custom_upload/SaveAsUploads/31_mb.pdf").getAbsolutePath();
            public static final String megabytes35File = new File("src/test/resources/custom_upload/SaveAsUploads/35_mb.pdf").getAbsolutePath();
            public static final String anotherFormatFile = new File("src/test/resources/custom_upload/SaveAsUploads/another_format.docx").getAbsolutePath();
            public static final String brokenFile = new File("src/test/resources/custom_upload/SaveAsUploads/broken.pdf").getAbsolutePath();
            public static final String previewTestDocument = new File("src/test/resources/export/preview_test_document.pdf").getAbsolutePath();
            public static final String topFormsCSV = new File("src/test/resources/custom_upload/SaveAsUploads/top_forms1000.csv").getAbsolutePath();
        }
    }

    public static class SimpleChoicePage {

        public static final String printAction = "Send to Print";
        public static final String sendViaUspsAction = "Send via USPS";
        public static final String sendViaSmsAction = "Send via SMS";
        public static final String sendEmailAction = "Send via Email";
        public static final String sendFaxAction = "Send via Fax";

        public static final String shareAction = "Share";
        public static final String linkToFillAction = "LinkToFill";
        public static final String sendToSignAction = "SendToSign";

        public static final String saveAsPdfAction = "PDF";
        public static final String saveAsWordAction = "Word";
        public static final String saveAsExcelAction = "Excel";
        public static final String saveAsPowerPointAction = "Power Point";

        public static final String backToDocumentAction = "Back to Edit";
        public static final String saveChangesAction = "Save changes";
        public static final String addFillableFieldsAction = "Add Fillable Fields";

        public static final String sendToIrs = "Send to IRS";
        public static final String continueOnDesktop = "Continue on Desktop";

        public static final String rewritePDFAction = "Rewrite PDF";
    }

    public static class MyDocs {

        public static class FolderList {
            public static final String myDocuments = "My Documents";
            public static final String share = "Share";
            public static final String sendToSign = "SendToSign";
            public static final String signNow = "SignNow";
            public static final String linkToFill = "LinkToFill";
            public static final String unsorted = "Unsorted";
            public static final String encryptedFolder = "Encrypted";
            public static final String suggestedDocuments = "Suggested Documents";
            public static final String transfer = "Transfer";
            public static final String sharedWithMe = "Shared with Me";
            public static final String signatureRequested = "Signature Requested";
            public static final String email = "Email";
            public static final String fax = "Faxes";
            public static final String emailSms = "Email/SMS";
            public static final String templates = "Templates";
            public static final String dropbox = "Dropbox";
            public static final String googleDrive = "Google Drive";
            public static final String box = "Box";
            public static final String oneDrive = "OneDrive";
            public static final String smartFolders = "Smart Folders";
            public static final String usps = "USPS Mail";
            public static final String notarize = "Notarize";
            public static final String allForms = "All Forms";
            public static final String activeForms = "Active Forms";
            public static final String inactiveForms = "Inactive Forms";
            public static final String rejectedForms = "Rejected Forms";
        }

        public static class MobileFolderList {
            public static final String signatureRequested = "Signature Request";
            public static final String sharedWithMe = "Shared with Me";
            public static final String linkToFill = "LinkToFill";
        }

        public static class SortList {
            public static final String sortByAZ = "File Name: A-Z";
            public static final String sortByZA = "File Name: Z-A";
            public static final String sortByAddedNewest = "Added-Newest";
            public static final String sortByAddedOldest = "Added-Oldest";
            public static final String sortByModifiedNewest = "Modified-Newest";
            public static final String sortByModifiedOldest = "Modified-Oldest";
            // буду дописывать по ходу
        }

        public static class TabList {

            public static final String mybox = "Mybox";
            public static final String inbox = "Inbox";
            public static final String outbox = "Outbox";
            public static final String cloud = "Cloud";
            public static final String trashBin = "Trash Bin";
            public static final String documents = "Documents";
            public static final String templates = "Templates";
            public static final String notifications = "Notifications";
            public static final String flows = "Flows";
            public static final String sellForms = "SellForms";
        }

        public static class StatisticTabsDashBoard {
            public static final String allDocuments = "All Documents";
            public static final String incomingDocuments = "Incoming Documents";
            public static final String templates = "Templates";
            public static final String signatureRequested = "Signature Requested";
        }

        public static class MobileTabsList {
            public static final String mybox = "myBox";
            public static final String inbox = "inBox";
            public static final String outbox = "outbox";
        }

        public static class AndroidTabsList {
            public static final String mybox = "MYBOX";
            public static final String inbox = "INBOX";
            public static final String outbox = "OUTBOX";
            public static final String cloud = "CLOUD";
        }

        public static class ActionList {

            public static final String open = "Open";
            public static final String print = "Print";
            public static final String email = "Email";
            public static final String sms = "SMS";
            public static final String fax = "Fax";
            public static final String saveAs = "Save As";
            public static final String sendUspsMail = "Send via USPS";
            public static final String fillInBulk = "Fill in Bulk";
            public static final String share = "Share";
            public static final String sendToSign = "SendToSign";
            public static final String linkToFill = "LinkToFill";
            public static final String extractInBulk = "Extract in Bulk";
        }

        public static class FolderIds {
            public static final int signatureRequestedFolder_id = -4;
            public static final int sharedWithMeFolder_id = -2;
            public static final int linkToFillOutbox_id = -15;
            public static final int allDocumentsFolderId = -20;
            public static final int encryptedFolder = -14;
            public static final int unsorted = 0;
            public static final int shareFolder_id = -7;
            public static final int sendToSign_id = -3;
        }
    }

    public static class ExportPrint {

        public static class AccordionItems {
            public static final String printSettings = "Print Settings";
        }
    }

    public static class ExportEmail {

        public static class AccordionItems {
            public static final String addRecipients = "Add Recipients";
            public static final String personalizeYourMessage = "Personalize Your Message";
            public static final String setNotifications = "Set Notifications";
        }
    }

    public static class ExportSMS {

        public static class AccordionItems {
            public static final String addRecipient = "Add Recipient";
            public static final String setPassword = "Set Password";
        }
    }

    public static class ExportFax {

        public static class AccordionItems {
            public static final String addRecipient = "Add a Recipient";
            public static final String customizeYourCoverLetter = "Customize Your Cover Letter";
        }
    }

    public static class ExportSaveAs {

        public static class AccordionItems {
            public static final String selectFormatAndDestination = "Select Format and Destination";
            public static final String setExportOptions = "Set Export Options";
            public static final String setDocumentAccessSecurity = "Set Document Access Security";
        }

        public static class FileFormats {
            public static final String pdf = "PDF";
            public static final String word = "Word";
            public static final String powerpoint = "PowerPoint";
            public static final String excel = "Excel";
        }

        public static class Destination {
            public static final String desktop = "Desktop";
            public static final String googleDrive = "Google Drive";
            public static final String oneDrive = "One Drive";
            public static final String dropbox = "Dropbox";
            public static final String box = "Box";
            public static final String[] arrAllDestinations = {"Desktop", "Dropbox", "Google Drive", "Box", "One Drive"};
            public static final String[] arrAllCloudDestinations = {"Dropbox", "Google Drive", "Box", "OneDrive"};
            public static final String[] arrAllDestinationsIcons = {"save", "dropbox", "google-drive", "box", "oneDrive"};
            //ToDo: Will be actual when EX-825 will be done!
            //public static final String[] arrAllCloudDestinationsIcons = {"dropbox", "google-drive", "box", "oneDrive"};
            public static final String[] arrAllCloudDestinationsIcons = {"dropbox", "save", "save", "save"};
        }
    }

    public static class Urls {

        public static final String w9FormUrl = "https://w9.pdffiller.com";
        public static final String pdffillerUrl = "https://www.pdffiller.com";
        public static final String signNowUrl = "https://www.signnow.com/";
        public static final String microSiteW9 = "https://w9form-online.com";
    }

    public static class NewJSFiller {

        public static class MultiDoneActions {

            public static final String print = "Print";
            public static final String email = "Email";
            public static final String sms = "SMS";
            public static final String fax = "Fax";
            public static final String saveAs = "Save As";
            public static final String share = "Share";
            public static final String sendToIrs = "Send to IRS";
            public static final String sendToSign = "SendToSign";
            public static final String linkToFill = "LinkToFill";
            public static final String goToMyDocs = "Go to MY DOCS";
        }
    }

    public static class TestCreditCardSignNow {
        public static final String NUMBER = "4111 1111 1111 1111";
        public static final String EXPIRATION_DATE = "12/25";
        public static final String CVV_CODE = "333";
    }

    public static class PhoneNumbers {
        public static final String PHONE_NUMBER_SMS_RECEIVER = "+15812006553";
    }

    public static class AirSlate {
        public static final String USER_NAME = "Auto";
        public static final String USER_FULL_NAME = "Auto Test";
        public static final String FLOW_NAME = "autotest_flow";
        public static final String DFAULT_FLOW_NAME = "My New Flow";
        public static final String FLOW_DESCRIPTION = "autotest flow description";
        public static final String FLOW_INVITE_DEFAULT_MESSAGE = "Someone thinks you are special and has shared a Flow with you! Here you can change or edit this Flow and send it back to the owner when you are done.";
        public static final String trueEditorEmptyText = "&nbsp;";
        public static final String DEFAULT_SLATE_NAME = "You created this Slate";
        public static final String OVERSIZE_DOC_NOTIFICATION = " We’re sorry. This document’s size exceeds the allowable limit. The maximum document size is 30 mb.";
        public static final String UNSUPPORTED_DOC_NOTIFICATION = " We’re sorry. The document you are uploading is in an unsupported format.";
        public static final String BROKEN_DOC_NOTIFICATION = "We’re sorry. The document you're uploading (broken) seems to be corrupted. Please choose a different document and try again.";
        public static final String EXPORT_SMS_TEXT = "airSlate: You have received a new document";
        public static final String EXPORT_EMAIL_SUBJECT = "New document from Auto Test";
        public static final String FINISH_REVISION_STATUS = "You completed Revision";
        public static final String DRAFT_REVISION_STATUS = "You are working on Revision";
        private static final String UPLOAD_DOCS_REACH_LIMIT_NOTIFICATION = "You can't upload more than 10 files in a flow.";
    }

    public static class AWS {
        public static final String AWS_S3_PATH = "https://s3.amazonaws.com/cinfra-prod-qa-autotest-data";
        public static final String AWS_WORKERS = AWS_S3_PATH + "/workers/";
    }
}
