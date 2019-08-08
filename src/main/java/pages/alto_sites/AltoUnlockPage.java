package pages.alto_sites;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static core.check.Check.checkTrue;

public class AltoUnlockPage extends AltoSitesBasePage {
    public static By inputPass = By.cssSelector(".action-password__input");
    public static By validationError = By.cssSelector(".action-password__warning");
    private By startOverAgain = By.cssSelector(".repeat-link");

    public AltoUnlockPage(WebDriver driver) {
        super(driver);
        baseUrlProd = "https://altounlockpdf.com/";
        baseUrlDev = "https://altounlock.pdffillers.com/";
    }

    @Step
    public void typePassPdf(String password) {
        checkTrue(isElementPresent(inputPass, 25), "input not present");
        type(inputPass, password);
    }

    @Step
    public void checkValidationField() {
        checkTrue(isElementPresent(validationError, 10), "Validation error not present");
    }

    @Override
    public void startOverAgain() {
        checkTrue(isElementPresent(startOverAgain, 20), "Start Over again button not present");
        click(startOverAgain);
    }
}