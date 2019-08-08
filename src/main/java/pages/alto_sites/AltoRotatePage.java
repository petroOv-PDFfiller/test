package pages.alto_sites;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.concurrent.TimeUnit;

import static core.check.Check.checkTrue;
import static org.awaitility.Awaitility.await;

public class AltoRotatePage extends AltoSitesBasePage {
    public static By btnRotateLeft = By.cssSelector(".service-action__rotate_left");
    public static By btnRotateRight = By.cssSelector(".service-action__rotate_right");
    public static By btnApplyChanges = By.cssSelector(".g-btn.g-btn--primary.g-btn--bg");
    public static By iconRotateLeftFirstItem = By.cssSelector(".document-preview__rotate_right");
    public static By iconRotateRightFirstItem = By.cssSelector(".document-preview__rotate_left");

    public AltoRotatePage(WebDriver driver) {
        super(driver);
        baseUrlProd = "https://altorotatepdf.com/";
        baseUrlDev = "https://www-altorotate.pdffillers.com/";
    }

    @Step
    public void rotateLeftAllPages() {
        checkTrue(isElementPresent(btnRotateLeft, 20), "btn rotate left not present");
        click(btnRotateLeft);
    }

    @Step
    public void rotateRightAllPages() {
        checkTrue(isElementPresent(btnRotateRight, 20), "btn rotate right not present");
        click(btnRotateRight);
    }

    @Step
    public void applyChanges() {
        checkTrue(isElementPresent(btnApplyChanges, 20), "Button 'Apply changes' not present");
        await().atMost(60, TimeUnit.SECONDS)
                .until(() -> !isElementContainsStringInClass(btnApplyChanges, "is-disabled"));
        click(btnApplyChanges);
    }

    @Step
    public void iconRotateRight() {
        checkTrue(isElementPresent(iconRotateRightFirstItem, 20), "icon rotate left not present");
        hoverOverAndClick(iconRotateRightFirstItem);
    }

    @Step
    public void iconRotateLeft() {
        checkTrue(isElementPresent(iconRotateLeftFirstItem, 20), "icon rotate left not present");
        hoverOverAndClick(iconRotateLeftFirstItem);
    }

    @Step
    public void checkEnabledButtonApplyChanges() {
        checkTrue(isElementPresent(btnApplyChanges, 20), "Button 'Apply changes' disabled");
        await().atMost(60, TimeUnit.SECONDS)
                .until(() -> !isElementContainsStringInClass(btnApplyChanges, "is-disabled"));
    }

    @Step
    public void checkDisabledButtonApplyChanges() {
        checkTrue(isElementPresent(btnApplyChanges, 20), "Button 'Apply changes' enabled");
        await().atMost(60, TimeUnit.SECONDS)
                .until(() -> isElementContainsStringInClass(btnApplyChanges, "is-disabled"));
    }
}

