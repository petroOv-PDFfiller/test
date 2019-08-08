package pages.airslate.addons;

import org.openqa.selenium.WebDriver;

import static pages.airslate.addons.AddonEvents.PostFill.SF_CREATE;

public class SalesforceCreateAddonPage extends SalesforceBaseAddonsPage {

    public SalesforceCreateAddonPage(WebDriver driver) {
        super(driver);
        addonName = SF_CREATE.getAddon();
    }
}