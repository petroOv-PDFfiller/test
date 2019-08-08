package tests.salesforce.editor;

import base_tests.PDFfillerTest;
import com.pdffiller_old.public_api.ExternalApi;
import com.pdffiller_old.public_api.entities.fillableTemplate.reversed_templates.ReverserdTemplates;
import com.pdffiller_old.public_api.entities.linkToFill.FilledFormAsJson;
import com.pdffiller_old.public_api.entities.linkToFill.FilledFormList;
import com.pdffiller_old.public_api.entities.linkToFill.LinkToFillRequestList;
import com.pdffiller_old.public_api.entities.linkToFill.LinkToFill_FillableField;
import com.pdffiller_old.public_api.requests.JSFiller;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import listeners.WebTestListener;
import org.json.JSONException;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import pages.pdffiller.MainPage;
import pages.pdffiller.components.popups.AreYouSurePopUp;
import pages.pdffiller.components.popups.WelcomePopUp;
import pages.pdffiller.editors.newJSF.DropDownFillableElement;
import pages.pdffiller.editors.newJSF.NewJSFiller;
import pages.pdffiller.editors.newJSF.TextContentElement;
import pages.pdffiller.editors.newJSF.constructor.ConstructorJSFiller;
import pages.pdffiller.editors.newJSF.constructor.DynamicsSection;
import pages.pdffiller.editors.newJSF.constructor.enums.ConstructorTool;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;

@Feature("Mapping template filling: TEXT, NUMBER, CHECKBOX, DATE, DROP_DOWN")
@Listeners({WebTestListener.class})
public class MappingFillingFieldsTest extends PDFfillerTest {

    private WebDriver driver;
    private String urlDocumentTemplate;
    private ConstructorJSFiller constructorJSFiller;
    private DynamicsSection dynamics;
    private JSFiller jsFiller;
    private int pageHeight;
    private int pageWidth;
    private String email;
    private String token;

    @BeforeTest
    public void setUp() throws IOException, URISyntaxException, ParseException, JSONException {
        email = testData.emails.get(0);
        token = "rmEz8eQKHM7kr7KjHEZDt73ABxGG5ey9lMpH8ozq"; // token for 5 years
        jsFiller = new JSFiller(testData.url, email, testData.password);
        jsFiller.setAccessToken(token);
        Long projectId = 296989460L;
        String url = jsFiller.mapDocument(projectId);
        driver = getDriver();
        MainPage mainPage = new MainPage(driver);
        getUrl(testData.url);
        addAutotestCookie();
        refreshPage();
        mainPage.isOpened();
        setUpExperimentCookie(experiment);
        setUpEnvironmentCookie(desk);
        getUrl(url);
        NewJSFiller newJSFiller = new NewJSFiller(driver);
        try {
            newJSFiller.isOpened();
        } catch (Exception e) {
            Assert.fail("Cannot open location link after mapping");
        }
        constructorJSFiller = new ConstructorJSFiller(driver, newJSFiller);
        dynamics = new DynamicsSection(driver, newJSFiller);
        pageHeight = (int) (newJSFiller.getPageHeight(1));
        pageWidth = (int) (newJSFiller.getPageWidth(1));

        urlDocumentTemplate = jsFiller.reverseTemplate(projectId);
    }

    private void selectOption() {
        dynamics.openObjectName();
        dynamics.selectOption(1);
    }

    @Story("User can fill document template")
    @Test
    public void fillDocument() throws JSONException, IOException, ParseException, URISyntaxException {
        getUrl(urlDocumentTemplate);
        constructorJSFiller.putContent(ConstructorTool.TEXT, pageWidth / 8, pageHeight / 8);
        selectOption();
        constructorJSFiller.putContent(ConstructorTool.TEXT, pageWidth / 2, pageHeight / 8);
        selectOption();
        constructorJSFiller.putContent(ConstructorTool.TEXT, pageWidth * 3 / 4, pageHeight / 8);
        selectOption();
        constructorJSFiller.putContent(ConstructorTool.TEXT, pageWidth / 8, pageHeight / 4);
        selectOption();
        constructorJSFiller.putContent(ConstructorTool.NUMBER, pageWidth / 2, pageHeight / 4);
        selectOption();
        constructorJSFiller.putContent(ConstructorTool.CHECKBOX, pageWidth * 3 / 4, pageHeight / 4);
        selectOption();
        constructorJSFiller.putContent(ConstructorTool.DATE, pageWidth / 8, pageHeight / 3);
        selectOption();
        constructorJSFiller.putContent(ConstructorTool.DROP_DOWN, pageWidth / 2, pageHeight / 3);
        selectOption();

        ReverserdTemplates reverserdTemplates = jsFiller.getReversedTemplate("contact");
        String templateId = reverserdTemplates.items.get(0).id;
        String urlTemplate = jsFiller.getShortLinkTemplate(templateId);
        getUrl(urlTemplate);
        WelcomePopUp welcome = new WelcomePopUp(driver);
        welcome.letsGo();
        NewJSFiller newJSFiller = new NewJSFiller(driver);
        newJSFiller.initExistingElements();
        newJSFiller.fillAllFields(0); // fill all fields without timeout
        newJSFiller.initExistingElements();

        List<LinkToFill_FillableField> expectedFields = new ArrayList<>();
        LinkToFill_FillableField text;
        LinkToFill_FillableField number = new LinkToFill_FillableField();
        LinkToFill_FillableField checkmark = new LinkToFill_FillableField();
        LinkToFill_FillableField date = new LinkToFill_FillableField();
        LinkToFill_FillableField dropdown = new LinkToFill_FillableField();

        int allTextElements = 4;
        for (int i = 1; i <= allTextElements; i++) {
            TextContentElement textContentElement = (TextContentElement) NewJSFiller.fillableFields.get(i - 1);
            text = new LinkToFill_FillableField();
            text.name = "Text_" + i;
            text.content = textContentElement.text;
            expectedFields.add(text);
        }
        TextContentElement numberField = (TextContentElement) NewJSFiller.fillableFields.get(4);
        TextContentElement dateField = (TextContentElement) NewJSFiller.fillableFields.get(6);
        DropDownFillableElement dropdownField = (DropDownFillableElement) NewJSFiller.fillableFields.get(7);

        // prepare expected result
        number.name = "Number_1";
        number.content = numberField.text;
        checkmark.name = "Checkmark_1";
        checkmark.content = "Yes";
        date.name = "Date_1";
        date.content = dateField.text;
        dropdown.name = "Dropdown_1";
        dropdown.content = dropdownField.text;
        expectedFields.add(number);
        expectedFields.add(checkmark);
        expectedFields.add(date);
        expectedFields.add(dropdown);

        newJSFiller.clickDoneButton();
        AreYouSurePopUp areYouSurePopUp = new AreYouSurePopUp(driver);
        areYouSurePopUp.isOpened();
        areYouSurePopUp.clickYes();

        String password = "08feebffbe58a9acb334860112ac9a6135c453b7c54072efa0ca4075743bee8b";
        ExternalApi api = new ExternalApi(testData.url, email, password);
        LinkToFillRequestList linkToFillRequestList = api.linkToFill(token).getAllLinkToFillRequests();
        Long requestId = linkToFillRequestList.items[0].id;
        FilledFormList forms = api.linkToFill(token).getFilledFormsByFillRequests(requestId);
        Long filledFormId = forms.items[0].id;
        FilledFormAsJson filledForm = api.linkToFill(token).exportFilledFormAsJson(requestId, filledFormId);

        List<LinkToFill_FillableField> actualFields = Arrays.asList(filledForm.pages[0].fillable_fields);

        assertEquals(actualFields, expectedFields, "Wrong filling in template");
    }
}