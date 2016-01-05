package core;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedCondition;
import ru.yandex.qatools.allure.annotations.Step;
import utils.Logger;

import java.util.concurrent.TimeUnit;

/**
 * Created by Vladyslav on 26.06.2015.
 */
public class PageBase {

    protected WebDriver driver;

    protected PageBase(WebDriver driver) {
        this.driver = driver;
    }

    @Step("Going to {0}")
    protected void open(String url) {
        Logger.info("Openning " + url);
        driver.get(url);
    }

    @Step("Click on ({0})")
    protected void click(By locator) {
        Wait wait = new FluentWait(driver)
                .withTimeout(15, TimeUnit.SECONDS)
                .pollingEvery(500, TimeUnit.MILLISECONDS)
                .ignoring(ElementNotVisibleException.class).ignoring(NoSuchElementException.class).ignoring(StaleElementReferenceException.class);
        try {
            wait.until(ExpectedConditions.elementToBeClickable(locator));
            Logger.info("Clicking on '" + locator + "'");
            driver.findElement(locator).click();
        } catch (TimeoutException e) {
            Logger.error("Can not click on element '" + locator + "'");
            e.printStackTrace();
        }
    }

    protected void hoverOver(WebElement element) {
        Actions builder = new Actions(driver);
        Actions hoverOverRegistrar = builder.moveToElement(element);
        hoverOverRegistrar.build().perform();
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Step("Safe click on ({0})")
    protected void safeClick(final By locator) {
        try {
            click(locator);
        } catch (WebDriverException e) {
            if (e.getMessage().contains("Element is not clickable at point")) {
                WebDriverWait wait = new WebDriverWait(driver, 15);
                wait.until(new ExpectedCondition<Boolean>() {
                    public Boolean apply(WebDriver d) {
                        try {
                            d.findElement(locator).click();
                            return true;
                        } catch (WebDriverException e) {
                            return false;
                        }
                    }
                });
            } else {
                throw e;
            }
        }
    }

    @Step("Type ({1}) in ({0})")
    protected void type(final By locator, final String text) {
        if (isElementPresent(locator))
            try {
                Wait wait = new WebDriverWait(driver, 15);
                wait.until(new ExpectedCondition<Boolean>() {
                    public Boolean apply(WebDriver d) {
                        try {
                            Logger.info("Typing '" + text + "' in '" + locator + "'");
                            driver.findElement(locator).clear();
                            driver.findElement(locator).sendKeys(text);
                            return true;
                        } catch (InvalidElementStateException e) {
                            return false;
                        }
                    }
                });
            } catch (TimeoutException e) {
                Logger.error("Can not interact with element '" + locator + "'. Invalid state of element");
                e.printStackTrace();
            }
    }

    @Step("Scrolling to element ({0})")
    protected void scrollTo(WebElement element) {
        Logger.info("Scrolling to element '" + element + "'");
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isElementPresent(By locator) {
        Wait wait = new FluentWait(driver)
                .withTimeout(2, TimeUnit.SECONDS)
                .pollingEvery(500, TimeUnit.MILLISECONDS)
                .ignoring(NoSuchElementException.class).ignoring(StaleElementReferenceException.class);
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            return true;
        } catch (TimeoutException e) {
            Logger.warning("Element '" + locator + "' is not present");
            return false;
        }
    }

    public boolean isElementPresent(final By locator, int seconds) {
        Wait wait = new FluentWait(driver)
                .withTimeout(seconds, TimeUnit.SECONDS)
                .pollingEvery(500, TimeUnit.MILLISECONDS)
                .ignoring(NoSuchElementException.class).ignoring(StaleElementReferenceException.class);
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            return true;
        } catch (TimeoutException e) {
            Logger.warning("Element '" + locator + "' is not present");
            return false;
        }
    }

    @Step("Wait until page is loaded")
    protected boolean waitUntilPageLoaded() {
        int time = 60;
        Logger.info("Waiting [" + time + "] sec until page is loaded");
        try {
            WebDriverWait wait = new WebDriverWait(driver, time);
            wait.until(new ExpectedCondition<Boolean>() {
                public Boolean apply(WebDriver d) {
                    return ((JavascriptExecutor) d).executeScript("return document.readyState").equals("complete");
                }
            });
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }
}
