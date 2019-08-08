package pages.salesforce.app.airSlate_app.lightning_component;


import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.SalesAppBasePage;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.Selenide.$x;
import static pages.salesforce.enums.ButtonNames.SHOW_ALL_REVISIONS;

public class AirSlateLightningComponent extends SalesAppBasePage {

    public LoginPageComponent loginPageComponent;

    public AirSlateLightningComponent(WebDriver driver) {
        super(driver);
        loginPageComponent = new LoginPageComponent(driver);
        loader = By.xpath("//*[contains(@class,'loader__')]");
    }

    @Override
    public void isOpened() {
        By airSlateFrame = By.cssSelector("[data-component-id='airSlateWidget'] iframe");
        if (isElementPresent(airSlateFrame))
            switchToFrame(airSlateFrame);
        skipLoader();
    }

    @Step
    public String getTitleText() {
        By title = By.xpath("//*[contains(@class, 'title__')]");
        return getAttribute(title, "textContent");
    }

    @Step
    public String getSubTitleText() {
        By subTitle = By.xpath("//*[contains(@class, 'subTitle__')]");
        return getAttribute(subTitle, "textContent");
    }

    @Step
    public String getSecondSubTitleText() {
        By subTitle = By.xpath("//*[contains(@class, 'subtitle__')]");
        return getAttribute(subTitle, "textContent");
    }

    @Step
    public AirSlateLightningComponent openFlowDetails(String flowName) {
        By flow = By.xpath("//*[text()='" + flowName + "']/parent::*");
        checkIsElementDisplayed(flow, 4, flowName + " flow is not present");
        click(flow);
        skipLoader();
        return this;
    }

    @Step
    public AirSlateLightningComponent openSlateDetails(int slateNumber) {
        if (slateNumber == 0) {
            slateNumber = 1;
        }
        $x("//*[contains(@class, 'listSlates__')]/li[" + slateNumber + "]").click();
        return this;
    }

    @Step("Return back to previous tab")
    public AirSlateLightningComponent back() {
        $x("//button[contains(@class, 'backButton__')]").shouldBe(Condition.visible).click();
        skipLoader();
        return this;
    }

    @Step
    public AirSlateLightningComponent showAllRevisions() {
        $x("//*[text()='" + SHOW_ALL_REVISIONS.getName() + "']").waitUntil(Condition.visible, 5).click();
        skipLoader();
        return this;
    }

    @Step
    public List<LightningComponentSlateRevision> getAllSlateRevisions() {
        List<LightningComponentSlateRevision> slateRevisions = new ArrayList<>();
        $$x("//*[contains(@class, 'listRevisions__')]/li").forEach(element -> slateRevisions.add(getSlateRevision(element)));
        return slateRevisions;
    }

    @Step
    public List<LightningComponentSlate> getAllSlates() {
        List<LightningComponentSlate> slates = new ArrayList<>();
        $$x("//*[contains(@class, 'listSlates__')]/li").forEach(element -> slates.add(getSlate(element)));
        return slates;
    }

    @Step
    public List<LightningComponentFlow> getAllFlows() {
        List<LightningComponentFlow> flows = new ArrayList<>();
        $$x("//*[contains(@class, 'listFlows__')]/li").forEach(element -> flows.add(getFlow(element)));
        return flows;
    }

    private LightningComponentFlow getFlow(SelenideElement element) {
        LightningComponentFlow flow = new LightningComponentFlow();
        flow.name = element.find("[class^='title__']").getAttribute("textContent");
        flow.description = element.find("[class^='subTitle__']").getAttribute("textContent");
        flow.lastUsed = element.find("[class^='info__']").getAttribute("textContent");
        return flow;
    }

    private LightningComponentSlate getSlate(SelenideElement element) {
        LightningComponentSlate slate = new LightningComponentSlate();
        slate.author = element.find("[class^='title__']").getAttribute("textContent");
        slate.lastRevisionStatus = element.find("[class^='subTitle__']").getAttribute("textContent");
        slate.date = element.find("[class^='info__']").getAttribute("textContent");
        return slate;
    }

    private LightningComponentSlateRevision getSlateRevision(SelenideElement element) {
        LightningComponentSlateRevision revision = new LightningComponentSlateRevision();
        revision.name = element.find("[class^='title__']").getAttribute("textContent");
        revision.date = element.find("[class^='info__']").getAttribute("textContent");
        return revision;
    }
}
