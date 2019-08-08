package pages.alto_sites;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static core.check.Check.checkTrue;

public class AltoIndexPage extends AltoSitesBasePage {
    public AltoIndexPage(WebDriver driver) {
        super(driver);
        baseUrlProd = "https://altopdf.com/";
        baseUrlDev = "https://altopdf.pdffillers.com/";
    }

    @Override
    public void isOpened() {
        checkTrue(waitUntilPageLoaded(), "Page not loaded");
    }

    public void navigateToAnyAltoSites(String nameAlto) {
        By cardsAltoSite = By.cssSelector(".card-" + nameAlto);
        checkTrue(isElementPresent(cardsAltoSite, 5), "Button for redirect to " + nameAlto + " not present");
        click(cardsAltoSite);
    }

    public void checkOpenAltoSites(String linkAlto) {
        isOpened();
        checkTrue(driverWinMan.switchToWindow(linkAlto, 20), "Failed to switch to OneDrive window");
    }
}
