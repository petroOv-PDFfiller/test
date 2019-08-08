package tests.html_form_builder;

import com.airslate.api.AirslateRestClient;
import com.airslate.api.models.documents.Dictionary;
import com.airslate.api.models.documents.Document;
import com.airslate.api.models.slates.Slate;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.thirdparty.html_form_builder.HTMLFormBuilderClient;
import com.thirdparty.html_form_builder.models.*;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import listeners.WebTestListener;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import utils.StringMan;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static data.salesforce.HTMLFormBuilderData.FieldType.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Feature("HTML form builder:fields")
@Listeners({WebTestListener.class})
public class FormBuilderFieldTests extends FormBuilderBaseTest {

    private final String newFormName = "HtmlForm";
    private AirslateRestClient airslateRestClient;
    private String flowID;
    private HTMLFormBuilderClient formBuilderClient;
    private String textFieldName = "textFiled";
    private String commentFieldName = "commentField";
    private Document formBuilderDocument;

    @Step
    @BeforeMethod
    public void setUp() throws IOException {
        airslateRestClient = new AirslateRestClient(testData.apiUrl);
        airslateRestClient.interceptors()
                .organizationDomain
                .setOrganizationDomain(orgDomain);
        String access_token = Objects.requireNonNull(airslateRestClient.auth
                .login(testData.getEmail(), testData.password)
                .execute()
                .body())
                .access_token;

        airslateRestClient.setDeserializationFeature(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        formBuilderClient = new HTMLFormBuilderClient(getApiUrl());
        formBuilderClient.interceptors()
                .organizationDomain
                .setOrganizationDomain(orgDomain);
        formBuilderClient.interceptors()
                .authenticator
                .setToken(access_token);
        formBuilderClient.setDeserializationFeature(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String flowName = getFlowName();
        Slate slate = new Slate(flowName, flowName, false);
        flowID = Objects.requireNonNull(airslateRestClient.flows
                .createFlow(slate)
                .execute()
                .body())
                .get()
                .id;
        formBuilderDocument = initFormBuilderDocumentData("HFBdoc" + StringMan.getRandomString(5));
        formBuilderDocument = Objects.requireNonNull(airslateRestClient.documents
                .createDocument(formBuilderDocument)
                .execute()
                .body())
                .get();
    }

    @Story("Field types")
    @Test
    public void checkAllFieldTypes() throws IOException {
        String checkboxFieldName = "checkboxField";
        String radioGroupFieldName = "radioGroupField";
        String dropdownFieldName = "dropdownField";
        String expressionFieldName = "expressionField";
        String currencyFieldName = "currency";
        int choicesCount = 0;
        List<Element> pageElements = new ArrayList<>();

        // Add Elements into Template
        pageElements.add(new Element(Element.ElementType.HTML.getType(), Element.ElementType.HTML.getType(), false));
        pageElements.add(new Element(Element.ElementType.COMMENT.getType(), Element.ElementType.COMMENT.getType(), false));
        pageElements.add(new Element(Element.ElementType.TEXT.getType(), Element.ElementType.TEXT.getType(), true));
        pageElements.add(new Element(Element.ElementType.SIGNATUREPAD.getType(), Element.ElementType.SIGNATUREPAD.getType(), true));
        pageElements.add(new Element("single checkbox", Element.ElementType.BOOLEAN.getType(), true));

        Element datePicker = new Element(Element.ElementType.DATEPICKER.getType(), Element.ElementType.DATEPICKER.getType(), false);
        datePicker.config = new DateConfig(true, true);
        datePicker.inputType = "date";
        datePicker.dateFormat = "mm/dd/yy";
        pageElements.add(datePicker);

        Element curr = new Element(currencyFieldName, Element.ElementType.TEXT.getType(), false);
        curr.inputMask = new InputMask("currency", true, "€");
        pageElements.add(curr);

        Element numWithValidator = new Element("Number field with validator", Element.ElementType.TEXT.getType(), false);
        numWithValidator.inputType = "number";
        Validators elem = new Validators(Validators.Type.NUMERIC);
        elem.text = "Validation error text";
        elem.minValue = 3;
        elem.maxValue = 25;
        List<Validators> valid = Arrays.asList(elem);
        numWithValidator.validators = valid;
        pageElements.add(numWithValidator);

        String[] choices = new String[]{"item1", "item2", "item3"};

        Map<String, String> choiceItem = new HashMap<String, String>();
        choiceItem.put("value", "5");
        choiceItem.put("text", "Five");

        List<Object> choicesWithDiffKey = Arrays.asList("item1", "item2", choiceItem);

        Element checkbox = new Element(checkboxFieldName, Element.ElementType.CHECKBOX.getType(), true);
        checkbox.choices = choicesWithDiffKey;
        choicesCount += choicesWithDiffKey.size();

        Element radioGroup = new Element(radioGroupFieldName, Element.ElementType.RADIOGROUP.getType(), true);
        radioGroup.choices = choices;

        Element dropdown = new Element(dropdownFieldName, Element.ElementType.DROPDOWN.getType(), true);
        dropdown.choices = choices;
        pageElements.add(checkbox);
        pageElements.add(radioGroup);
        pageElements.add(dropdown);

        //ADD MATRIX rewrite after new fields into Columns will be added
        Element matrix = new Element("Matrix elem", Element.ElementType.MATRIX.getType(), true);
        matrix.title = "Matrix name for user";
        List<Object> cols = new ArrayList<>();
        Map<String, String> choiceItems = new HashMap<>();
        choiceItems.put("value", "item1");
        choiceItems.put("text", "Col name 1");
        cols.add(choiceItems);
        Map<String, String> choiceItem2 = new HashMap<>();
        choiceItem2.put("value", "item2");
        choiceItem2.put("text", "Col name 2");
        cols.add(choiceItem2);
        Map<String, String> choiceItem3 = new HashMap<>();
        choiceItem3.put("value", "item3");
        choiceItem3.put("text", "Col name 3");
        cols.add(choiceItem3);
        List<Object> rows = new ArrayList<>();
        Map<String, String> row1 = new HashMap<>();
        row1.put("value", "item1");
        row1.put("text", "Row 1");
        rows.add(row1);
        Map<String, String> row2 = new HashMap<>();
        row2.put("value", "item2");
        row2.put("text", "Row 2");
        rows.add(row2);
        matrix.columns = cols;
        matrix.rows = rows;
        pageElements.add(matrix);
        choicesCount += rows.size();

        // ADD TABLE

        Element table = new Element("Table name", Element.ElementType.MATRIXDYNAMIC.getType(), false);
        table.title = "TABLE NAME";
        table.choices = choices;
        List<Object> columnsDM = new ArrayList<>();
        Columns colDM1 = new Columns("Name", "text");
        Columns colDM2 = new Columns("Q", "text");
        colDM2.inputType = "number";
        Columns colDM3 = new Columns("P", "text");
        colDM3.inputType = "number";
        Columns colDM4 = new Columns("Amount", "expression");
        colDM4.totalType = "sum";
        colDM4.totalDisplayStyle = "currency";
        colDM4.expression = "{row.Q} * {row.P}";
        colDM4.displayStyle = "currency";
        columnsDM.add(colDM1);
        columnsDM.add(colDM2);
        columnsDM.add(colDM3);
        columnsDM.add(colDM4);
        table.columns = columnsDM;
        pageElements.add(table);

        choicesCount += columnsDM.size();

        int amountFields = pageElements.size() + choicesCount - 2;

        pageElements.add(new Element(expressionFieldName, Element.ElementType.EXPRESSION.getType(), true));
        List<Page> pages = new ArrayList<>();
        pages.add(new Page("Page1", pageElements));

        HTMLForm form = initFormData(newFormName, formBuilderDocument.id, flowID, new FormPages(pages, "off"));
        formBuilderDocument = Objects.requireNonNull(formBuilderClient.htmlFormBuilderService
                .changeDocument(form)
                .execute()
                .body())
                .get();

        assertTrue(formBuilderDocument.meta.containsKey("fillable_fields_count"), "Fillable fields is not added");
        List<Dictionary> formBuilderFields = Objects.requireNonNull(airslateRestClient.documents
                .getDocumentFields(formBuilderDocument.id)
                .execute()
                .body())
                .get();

        int expectedFieldsCount = amountFields;

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(formBuilderDocument.name, newFormName, "Form name is not updated");
        softAssert.assertNotNull(formBuilderDocument.fields_file, "Fields file is not init");
        softAssert.assertEquals((int) formBuilderDocument.meta.get("fillable_fields_count"), expectedFieldsCount, "Incorrect fillable fields count");
        assertEquals(formBuilderFields.size(), expectedFieldsCount, "Incorrect field list size");

        int sz = choicesWithDiffKey.size() - 1;
        for (int i = 0, j = 0; i < pageElements.size() && j < formBuilderFields.size(); i++, j++) {

            String expectedValue = null;
            String expectedName = pageElements.get(i).name;
            String expectedType = pageElements.get(i).type;

            if (formBuilderFields.get(j).field_type.equals(CHECKBOX)) {
                if (expectedName.equals("single checkbox")) {
                    expectedValue = "";
                    expectedName = "single checkbox"; //TODO костыл ьна single
                    expectedType = "checkbox";
                } else {
                    expectedValue = String.valueOf(false);
                    if (choicesWithDiffKey.get(j - i) instanceof Map) {
                        expectedName = String.format("%s|%s", expectedName, ((Map) choicesWithDiffKey.get(j - i)).get("value"));
                    } else {

                        expectedName = String.format("%s|%s", expectedName, choicesWithDiffKey.get(j - i));
                    }
                }
            } else if (pageElements.get(i).type.equals(COMMENT)) {
                expectedType = TEXT;
            } else if (pageElements.get(i).type.equals(EXPRESSION)) {
                expectedType = "number";
            } else if (pageElements.get(i).type.equals(HTML)) {
                expectedType = "text";
            } else if (pageElements.get(i).type.equals(SIGNATURE)) {
                expectedType = "signature";
            } else if (pageElements.get(i).type.equals(DATEPICKER)) {
                DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                Date date = new Date();
                expectedType = "date";
                expectedValue = dateFormat.format(date); //TODO REMOVE AFTER AUTOFILL DATE FIX
            } else if (pageElements.get(i).type.equals(TEXT) && pageElements.get(i).inputType == "number") {
                expectedType = "number";
            } else if (pageElements.get(i).type.equals(TEXT) && pageElements.get(i).name.equals("currency")) {
                expectedType = "number";
            } else if (pageElements.get(i).type.equals(TABLE)) {
                expectedValue = "[]";
                expectedType = "text[]";
                Columns col = (Columns) columnsDM.get(j - i - sz - 1); // TODO 1 хардкод из Роу
                expectedName = String.format("%s|%s", expectedName, col.name);
            } else if (pageElements.get(i).type.equals(MATRIX)) {
                expectedType = "radiogroup";
                if (rows.get(j - i - sz) instanceof Map) {
                    expectedName = String.format("%s|%s", expectedName, ((Map) rows.get(j - i - sz)).get("value"));
                }
            }

            softAssert.assertEquals(formBuilderFields.get(j).name, expectedName, "Incorrect " + formBuilderFields.get(j) + " field name");
            softAssert.assertEquals(formBuilderFields.get(j).value, expectedValue, "incorrect " + formBuilderFields.get(j) + " default value");
            softAssert.assertEquals(formBuilderFields.get(j).field_type, expectedType, "Incorrect " + formBuilderFields.get(j) + " field type");

            if (formBuilderFields.get(j).field_type.equals(CHECKBOX)
                    && formBuilderFields.get(j + 1).field_type.equals(CHECKBOX)) {
                i--;
            }

            if (formBuilderFields.get(j).field_type.equals(RADIOGROUP)
                    && formBuilderFields.get(j + 1).field_type.equals(RADIOGROUP) && formBuilderFields.get(j).name.contains("Matrix")) {
                i--;
            }

            if (formBuilderFields.get(j).field_type.equals("text[]")
                    && formBuilderFields.get(j + 1).field_type.equals("text[]")) {
                i--;
            }
        }
        softAssert.assertAll();
    }

    @Story("Check incorrect field type")
    @Test
    public void wrongFieldType() throws IOException {
        List<Element> pageElements = new ArrayList<>();
        pageElements.add(new Element(textFieldName, TEXT, true));
        pageElements.add(new Element(commentFieldName, "incorrectType", true));
        List<Page> pages = new ArrayList<>();
        pages.add(new Page("Page1", pageElements));

        HTMLForm form = initFormData(newFormName, formBuilderDocument.id, flowID, new FormPages(pages, "off"));
        formBuilderDocument = Objects.requireNonNull(formBuilderClient.htmlFormBuilderService
                .changeDocument(form)
                .execute()
                .body())
                .get();

        formBuilderDocument = Objects.requireNonNull(airslateRestClient.documents
                .getDocument(formBuilderDocument.id, "")
                .execute()
                .body())
                .get();

        assertTrue(formBuilderDocument.meta.containsKey("fillable_fields_count"), "Fillable fields is not added");
        List<Dictionary> formBuilderFields = Objects.requireNonNull(airslateRestClient.documents
                .getDocumentFields(formBuilderDocument.id)
                .execute()
                .body())
                .get();

        int expectedFieldsCount = 1;
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(formBuilderDocument.name, newFormName, "Form name is not updated");
        softAssert.assertNotNull(formBuilderDocument.fields_file, "Fields file is not init");
        softAssert.assertEquals((int) formBuilderDocument.meta.get("fillable_fields_count"), expectedFieldsCount, "Incorrect fillable fields count");

        assertEquals(formBuilderFields.size(), expectedFieldsCount, "Incorrect field list size");
        softAssert.assertEquals(formBuilderFields.get(0).name, textFieldName, "Incorrect description field name");
        softAssert.assertNull(formBuilderFields.get(0).value, "incorrect default description value");
        softAssert.assertEquals(formBuilderFields.get(0).field_type, TEXT, "Incorrect description field type");

        softAssert.assertAll();
    }
}
