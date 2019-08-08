package pages.salesforce.app.DaDaDocs.full_app.templates.components;

import io.qameta.allure.Step;
import org.openqa.selenium.By;


public interface ObjectsTree {

    By btnAddRelatedObject = By.xpath("//*[contains(@class, 'addMainButtonWrapper__')]");
    By headingObject = By.xpath("//*[contains(@class, 'heading__')]");

    @Step
    ObjectsTree addNewRelatedObject();

    @Step
    ObjectsTree addRelatedObjectTo(int objectNumber);

    @Step
    ObjectsTree setRelatedObjectTo(int objectNumber, String relatedObjectName);
}
