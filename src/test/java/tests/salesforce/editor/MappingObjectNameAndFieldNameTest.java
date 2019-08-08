package tests.salesforce.editor;

import base_tests.PDFfillerTest;
import com.pdffiller_old.public_api.requests.JSFiller;
import core.AllureAttachments;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import listeners.WebTestListener;
import org.json.JSONException;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import pages.pdffiller.MainPage;
import pages.pdffiller.editors.newJSF.NewJSFiller;
import pages.pdffiller.editors.newJSF.constructor.ConstructorJSFiller;
import pages.pdffiller.editors.newJSF.constructor.DynamicsSection;
import pages.pdffiller.editors.newJSF.constructor.enums.ConstructorTool;

import java.io.IOException;
import java.net.URISyntaxException;

import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.WebDriverRunner.url;

@Feature("Mapping fields: TEXT, NUMBER, CHECKBOX, DATE, DROP_DOWN")
@Listeners({WebTestListener.class})

public class MappingObjectNameAndFieldNameTest extends PDFfillerTest {

    private DynamicsSection dynamics;
    private ConstructorJSFiller constructorJSFiller;
    private int pageHeight;
    private int pageWidth;

    @BeforeTest
    public void setUp() throws IOException, URISyntaxException, ParseException, JSONException {
        String email = testData.emails.get(0);
        String token = "rmEz8eQKHM7kr7KjHEZDt73ABxGG5ey9lMpH8ozq"; // token for 5 years
        JSFiller jsFiller = new JSFiller(testData.url, email, testData.password);
        jsFiller.setAccessToken(token);
        Long projectId = 290751911L;
        String url = jsFiller.mapDocument(projectId);
        WebDriver driver = getDriver();
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
    }

    @Story("User can view dynamics logo at TEXT, NUMBER, DATE, DROPDOWN, CHECKBOX")
    @Test(priority = 1)
    public void testEnabledMapping() {
        AllureAttachments.jsonAttachment(url(), "url");
        constructorJSFiller.isFillableToolMapped(ConstructorTool.TEXT).shouldBe(visible);
        constructorJSFiller.isFillableToolMapped(ConstructorTool.NUMBER).shouldBe(visible);
        constructorJSFiller.isFillableToolMapped(ConstructorTool.DATE).shouldBe(visible);
        constructorJSFiller.isFillableToolMapped(ConstructorTool.DROP_DOWN).shouldBe(visible);
        constructorJSFiller.isFillableToolMapped(ConstructorTool.CHECKBOX).shouldBe(visible);
        constructorJSFiller.isFillableToolMapped(ConstructorTool.SIGNATURE).shouldBe(not(exist));
        constructorJSFiller.isFillableToolMapped(ConstructorTool.INITIALS).shouldBe(not(exist));
        constructorJSFiller.isFillableToolMapped(ConstructorTool.PHOTO).shouldBe(not(exist));
    }

    @Story("User can open and select object name with filed name for TEXT field")
    @Test
    public void testMappingTextField() {
        constructorJSFiller.putContent(ConstructorTool.TEXT, pageWidth / 8, pageHeight / 8);
        selectContactValue();
        dynamics.openFieldName().getListNames().shouldHaveSize(2);
        dynamics.getListNames().shouldHave(exactTexts("String type", "Memo type"));
        dynamics.selectOption(2).getFieldName().shouldHave(text("Memo type"));
    }

    @Story("User can open and select object name with filed name for NUMBER field")
    @Test
    public void testMappingNumberField() {
        constructorJSFiller.putContent(ConstructorTool.NUMBER, pageWidth * 3 / 5, pageHeight / 8);
        selectContactValue();
        dynamics.openFieldName().getListNames().shouldHaveSize(4);
        dynamics.getListNames().shouldHave(exactTexts("Money type", "BigInt type", "Integer type", "Decimal type"));
        dynamics.selectOption(2).getFieldName().shouldHave(text("BigInt type"));
    }

    @Story("User can open and select object name with filed name for CHECKBOX field")
    @Test
    public void testMappingCheckboxField() {
        constructorJSFiller.putContent(ConstructorTool.CHECKBOX, pageWidth / 2, pageHeight / 8);
        selectContactValue();
        dynamics.openFieldName().getListNames().shouldHaveSize(1);
        dynamics.getListNames().shouldHave(exactTexts("Boolean type"));
        dynamics.selectOption(1).getFieldName().shouldHave(text("Boolean type"));
    }

    @Story("User can open and select object name with filed name for DATE field")
    @Test
    public void testMappingDateField() {
        constructorJSFiller.putContent(ConstructorTool.DATE, pageWidth / 8, pageHeight / 4);
        selectContactValue();
        dynamics.openFieldName().getListNames().shouldHaveSize(1);
        dynamics.getListNames().shouldHave(exactTexts("DateTime type"));
        dynamics.selectOption(1).getFieldName().shouldHave(text("DateTime type"));
    }

    @Story("User can open and select object name with filed name for DROP_DOWN field")
    @Test
    public void testMappingDropDownField() {
        constructorJSFiller.putContent(ConstructorTool.DROP_DOWN, pageWidth / 2, pageHeight / 4);
        selectContactValue();
        dynamics.openFieldName().getListNames().shouldHaveSize(2);
        dynamics.getListNames().shouldHave(exactTexts("PickList type", "PickList type undefined"));
        dynamics.selectOption(2).getFieldName().shouldHave(text("PickList type undefined"));
    }

    private void selectContactValue() {
        dynamics.getObjectName().shouldHave(text("Contact"));
        dynamics.openObjectName().getListNames().shouldHaveSize(1);
        dynamics.getListNames().shouldHave(exactTexts("Contact"));
        dynamics.selectOption(1);
    }

    @AfterClass
    public void deleteFields() {
        constructorJSFiller.deleteAllFieldsOnPage();
    }
}