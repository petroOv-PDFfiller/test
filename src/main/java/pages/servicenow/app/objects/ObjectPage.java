package pages.servicenow.app.objects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import pages.servicenow.ServiceNowBasePage;

import java.util.List;
import java.util.stream.Collectors;

import static core.check.Check.checkTrue;

/**
 * Created by horobets on Aug 01, 2019
 */
public abstract class ObjectPage extends ServiceNowBasePage {

    private By formIdBy = By.cssSelector("#section_form_id");
    private By toolbarButtonsBy = By.cssSelector(".form_action_button");

    public ObjectPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        super.isOpened();
        switchToPageContentFrame();
        checkTrue(isElementPresent(formIdBy, 3), String.format("%s was not found", formIdBy));
        switchToDefaultContent();
    }

    public void clickToolbarButton(int buttonIndex) {
        driver.findElements(toolbarButtonsBy).get(buttonIndex).click();
    }

    public void clickToolbarButton(String buttonLabel) {
        driver.findElements(toolbarButtonsBy).stream().filter(webElement -> buttonLabel.equalsIgnoreCase(webElement.getText())).findFirst().get().click();
    }

    public List<String> getToolbarButtonLabels() {
        return driver.findElements(toolbarButtonsBy).stream().map(WebElement::getText).collect(Collectors.toList());
    }
}
