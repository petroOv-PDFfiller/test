package data;

import java.io.File;

/**
 * Created by Vladyslav on 02.11.2015.
 */
public class TestData {

    public static final String pathToChromeDriver = new File("src/main/resources/drivers_exe/chromedriver.exe").getAbsolutePath();
    public static final String pathToIEDriver = new File("src/main/resources/drivers_exe/iedriver.exe").getAbsolutePath();

    public static final String pathToDownloadsFolder = new File("C:/testResources/downloadDir/").getAbsolutePath();
}
