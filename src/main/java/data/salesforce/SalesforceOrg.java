package data.salesforce;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.testng.annotations.DataProvider;

@Getter
@AllArgsConstructor
public enum SalesforceOrg {

    V3("pdf_sf_aqa+v3@support.pdffiller.com", "qwe1rty2", null, null, "3.4", null, "Org for ddd v3 tests run"),
    V2("pdf_sf_aqa+v2@support.pdffiller.com", "qwe1rty2", null, null, "3.4", null, "Org for ddd v2 tests run"),
    AT("pdf_sf_aqa+at@support.pdffiller.com", "qwe1rty2", null, null, "3.4", null, "Org for DaDaDocs app tests creating"),
    DG("pdf_sf_aqa+dg@support.pdffiller.com", "qwe1rty2", "pdf_sf_aqa+dg+guest@support.pdffiller.com", "qwe1rty2", null, "2.20", "Org for airSlate dg service"),
    AS1("pdf_sf_aqa+as@support.pdffiller.com", "qwe1rty2", "pdf_sf_aqa+as+guest@support.pdffiller.com", "Likas_qwe1rty3", null, "2.20", "Org for airSlate app tests creating"),
    AS2("pdf_sf_rnt+as@support.pdffiller.com", "qwe1rty2", "pdf_sf_rnt+as+guest@support.pdffiller.com", "Likas_qwe1rty2", null, "2.20", "Org for airSlate app tests run"),
    BOTS("pdf_sf_rnt+bot@support.pdffiller.com", "qwe1rty2", "pdf_sf_rnt+bot+guest@support.pdffiller.com", "qwe1rty2", null, "2.20", "Org for airSlate bot tests run"),
    STAGE("pdf_sf_rnt+stage@support.pdffiller.com", "qwe1rty2", "pdf_sf_rnt+stage+guest@support.pdffiller.com", "qwe1rty2", null, "2.20", "Org for airSlate stage tests run"),
    RC("pdf_sf_rnt+rc@support.pdffiller.com", "qwe1rty2", "pdf_sf_rnt+rc+guest@support.pdffiller.com", "qwe1rty2", null, "2.20", "Org for airSlate rc tests run"),
    PROD("pdf_sf_rnt+prod@support.pdffiller.com", "qwe1rty2", "pdf_sf_rnt+prod+guest@support.pdffiller.com", "qwe1rty2", null, "2.20", "Org for airSlate prod tests run");

    private String adminUsername;
    private String adminPassword;
    private String standardUserUsername;
    private String standardUserPassword;
    private String daDaDocsPackageVersion;
    private String airSlatePackageVersion;
    private String description;

    @DataProvider(name = "orgList")
    public static Object[][] sortPairwise() {
        Object[][] result = new Object[SalesforceOrg.values().length][1];
        for (int i = 0; i < SalesforceOrg.values().length; i++) {
            result[i][0] = SalesforceOrg.values()[i];
        }
        return result;
    }
}
