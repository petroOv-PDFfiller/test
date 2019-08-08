package utils.drivers;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.WebDriverEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebDriverEventHandler implements WebDriverEventListener {
    private static Logger logger = LoggerFactory.getLogger(WebDriverEventHandler.class.getName() + Thread.currentThread().getName());

    @Override
    public void beforeAlertAccept(WebDriver webDriver) {
        // TODO think of making more informative message
        logger.info("Before Alert Accept - " + webDriver);
    }

    @Override
    public void afterAlertAccept(WebDriver webDriver) {
        // TODO think of making more informative message
        logger.info("After Alert Accept - " + webDriver);
    }

    @Override
    public void afterAlertDismiss(WebDriver webDriver) {
        // TODO think of making more informative message
    }

    @Override
    public void beforeAlertDismiss(WebDriver webDriver) {
        // TODO think of making more informative message
    }

    @Override
    public void beforeNavigateTo(String s, WebDriver webDriver) {
        // TODO think of making more informative message
    }

    @Override
    public void afterNavigateTo(String s, WebDriver webDriver) {
        // TODO think of making more informative message
        logger.info("Navigated to- " + s);
    }

    @Override
    public void beforeNavigateBack(WebDriver webDriver) {
        // TODO think of making more informative message
    }

    @Override
    public void afterNavigateBack(WebDriver webDriver) {
        // TODO think of making more informative message
    }

    @Override
    public void beforeNavigateForward(WebDriver webDriver) {
        // TODO think of making more informative message
    }

    @Override
    public void afterNavigateForward(WebDriver webDriver) {
        // TODO think of making more informative message
    }

    @Override
    public void beforeNavigateRefresh(WebDriver webDriver) {
        // TODO think of making more informative message
    }

    @Override
    public void afterNavigateRefresh(WebDriver webDriver) {
        // TODO think of making more informative message
    }

    @Override
    public void beforeFindBy(By by, WebElement webElement, WebDriver webDriver) {
        // TODO think of making more informative message
    }

    @Override
    public void afterFindBy(By by, WebElement webElement, WebDriver webDriver) {
        // TODO think of making more informative message
    }

    @Override
    public void beforeClickOn(WebElement webElement, WebDriver webDriver) {
        // TODO think of making more informative message
        logger.info("Trying to click on \"" + webElement.getText() + "\" button");
    }

    @Override
    public void afterClickOn(WebElement webElement, WebDriver webDriver) {
        // TODO think of making more informative message
        //Log.info("Clicked on \"" + webElement.getText() + "\" button");
    }

    @Override
    public void beforeChangeValueOf(WebElement webElement, WebDriver webDriver, CharSequence[] charSequences) {
        // TODO think of making more informative message
    }

    @Override
    public void afterChangeValueOf(WebElement webElement, WebDriver webDriver, CharSequence[] charSequences) {
        // TODO think of making more informative message
    }

    @Override
    public void beforeScript(String s, WebDriver webDriver) {
        // TODO think of making more informative message
    }

    @Override
    public void afterScript(String s, WebDriver webDriver) {
        // TODO think of making more informative message
    }

    @Override
    public void beforeSwitchToWindow(String s, WebDriver webDriver) {
        // TODO think of making more informative message
        logger.info("Before window switching- " + s);
    }

    @Override
    public void afterSwitchToWindow(String s, WebDriver webDriver) {
        // TODO think of making more informative message
        logger.info("Switched to window- " + s);
    }

    @Override
    public void onException(Throwable throwable, WebDriver webDriver) {
        // TODO think of making more informative message
    }

    @Override
    public <X> void beforeGetScreenshotAs(OutputType<X> outputType) {
        // TODO think of making more informative message
    }

    @Override
    public <X> void afterGetScreenshotAs(OutputType<X> outputType, X x) {
        // TODO think of making more informative message
    }

    @Override
    public void beforeGetText(WebElement webElement, WebDriver webDriver) {
        // TODO think of making more informative message
    }

    @Override
    public void afterGetText(WebElement webElement, WebDriver webDriver, String s) {
        // TODO think of making more informative message
    }
}
