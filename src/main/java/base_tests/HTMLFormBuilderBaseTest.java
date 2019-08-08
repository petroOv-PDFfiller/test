package base_tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import org.aeonbits.owner.ConfigFactory;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;
import utils.configs.FormBuilderEnvironmentConfig;
import utils.drivers.SelenoidDriverProvider;
import utils.drivers.WebDriverEventHandler;

import static com.codeborne.selenide.Configuration.*;

public class HTMLFormBuilderBaseTest {

    protected FormBuilderEnvironmentConfig config = ConfigFactory.create(FormBuilderEnvironmentConfig.class,
            System.getProperties(),
            System.getenv()
    );

    @BeforeSuite
    @Parameters()
    protected void setUpConfig() {
        config.reload();
        driverManagerEnabled = false;
        browser = SelenoidDriverProvider.class.getName();
        startMaximized = true;
        Configuration.reportsFolder = "target/allure-results";
        WebDriverRunner.addListener(new WebDriverEventHandler());
    }
}
