package pages.salesforce.app.interfaces;

import org.openqa.selenium.By;

public interface AdminTool {

    By newLoader = By.xpath("//*[contains(@class,'loader__')]");

    default void skipAdminToolLoader() {
        skipAdminToolLoader(40);
    }

    void skipAdminToolLoader(int seconds);
}
