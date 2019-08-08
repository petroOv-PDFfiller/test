package data.airslate;

import org.testng.annotations.DataProvider;

public class AirSlateSearchData {

    public static String flowTitle;
    public static String flowDescription;

    @DataProvider(name = "titleKeyword")
    public Object[][] provideTitleKeyword() {
        return new Object[][]{
                {flowTitle},
                {flowTitle.substring(0, flowTitle.length() - 3)}
        };
    }

    @DataProvider(name = "descriptionKeyword")
    public Object[][] provideDescriptionKeyword() {
        return new Object[][]{
                {flowDescription},
                {flowDescription.substring(0, flowDescription.length() - 3)}
        };
    }

    @DataProvider(name = "pdfContentKeyword")
    public Object[][] providePdfContentKeyword() {
        return new Object[][]{
                {"Editor"},
                {"Photo"},
                {"ConstructorTest"},
        };
    }

    @DataProvider(name = "incorrectKeyword")
    public Object[][] provideIncorrectKeyword() {
        return new Object[][]{
                {"invalidSearchKeyword"},
        };
    }
}
