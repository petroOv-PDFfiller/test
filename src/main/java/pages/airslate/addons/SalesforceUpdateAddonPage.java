package pages.airslate.addons;

import org.openqa.selenium.WebDriver;

import static pages.airslate.addons.AddonEvents.PostFill.SF_UPDATE;

public class SalesforceUpdateAddonPage extends SalesforceBaseAddonsPage {

    public SalesforceUpdateAddonPage(WebDriver driver) {
        super(driver);
        addonName = SF_UPDATE.getAddon();
    }
}