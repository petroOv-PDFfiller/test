package pages.alto_sites;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static core.check.Check.checkTrue;

public class AltoPdfToJpgPage extends AltoSitesBasePage {
    public By inputRange = By.cssSelector(".action-filter__input");

    public AltoPdfToJpgPage(WebDriver driver) {
        super(driver);
        baseUrlProd = "https://altoconvertpdftojpg.com/";
        baseUrlDev = "https://altopdftojpg.pdffillers.com/";
    }

    @Step
    public void typeJpgRange(String someRange) {
        checkTrue(isElementPresent(inputRange, 25), "input not present");
        type(inputRange, someRange);
    }
}
