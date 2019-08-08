package pages.alto_sites;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static core.check.Check.checkTrue;

public class AltoProtectPage extends AltoSitesBasePage {
    public static By inputPass = By.cssSelector(".action-password__input");
    public static By errorInputState = By.cssSelector(".action-password__input.action-password__input--incorrect");

    public AltoProtectPage(WebDriver driver) {
        super(driver);
        baseUrlProd = "https://altoprotectpdf.com/";
        baseUrlDev = "https://altoprotect.pdffillers.com/";
    }

    @Step
    public void typePassPdf(String password) {
        checkTrue(isElementPresent(inputPass, 25), "input not present");
        type(inputPass, password);
    }

    @Step
    public void checkValidationField() {
        checkTrue(isElementPresent(errorInputState, 10), "Validation error not present");
    }
}
