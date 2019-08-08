package utils.airslate;

import com.airslate.api.AirslateRestClient;
import com.airslate.api.models.documents.Document;
import com.airslate.api.models.slates.Slate;
import com.codeborne.selenide.WebDriverRunner;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.thirdparty.html_form_builder.HTMLFormBuilderClient;
import com.thirdparty.html_form_builder.models.HTMLForm;
import io.qameta.allure.Step;
import lombok.Getter;
import lombok.Setter;
import okhttp3.HttpUrl;
import org.apache.http.client.utils.URIBuilder;
import org.openqa.selenium.Cookie;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static com.codeborne.selenide.Selenide.open;


public class FormBuilderMan {
    @Getter
    @Setter
    private AirslateRestClient airslateRestClient;
    @Getter
    @Setter
    private HTMLFormBuilderClient formBuilderClient;

    public FormBuilderMan(AirslateRestClient airslateRestClient) {
        this.airslateRestClient = airslateRestClient;
    }

    public FormBuilderMan(HTMLFormBuilderClient formBuilderClient) {
        this.formBuilderClient = formBuilderClient;
    }

    public FormBuilderMan(AirslateRestClient airslateRestClient, HTMLFormBuilderClient formBuilderClient) {
        this.airslateRestClient = airslateRestClient;
        this.formBuilderClient = formBuilderClient;
    }

    public static HTMLFormBuilderClient getFormBuilderClient(String url, String token, String orgDomain) {
        HTMLFormBuilderClient htmlFormBuilderClient = new HTMLFormBuilderClient(url);
        htmlFormBuilderClient.setDeserializationFeature(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        htmlFormBuilderClient.interceptors().organizationDomain.setOrganizationDomain(orgDomain);
        htmlFormBuilderClient.interceptors().authenticator.setToken(token);
        return htmlFormBuilderClient;
    }

    @Step
    public String getFormCreatorLink(Slate flow, Document document) {
        HttpUrl url = airslateRestClient.getRetrofit().baseUrl();
        try {
            URI uri = new URIBuilder().setScheme(url.scheme())
                    .setHost(url.host().replace("api", airslateRestClient.interceptors().organizationDomain.getOrganizationDomain()))
                    .setPath(String.format("/sc/form-creator/%s/%s", flow.id, document.id)).build();
            return uri.toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new AssertionError("Cannot get form creator url");
        }
    }

    @Step
    public Document fillForm(HTMLForm form) {
        try {
            return formBuilderClient.htmlFormBuilderService.fillDocument(form).execute().body().get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Step
    public void autoLogin() {
        open(getAirSLateUrl() + "/robots.txt");
        WebDriverRunner.getWebDriver()
                .manage()
                .addCookie(new Cookie.Builder("airSlate.session.token", airslateRestClient.interceptors().authenticator.getToken())
                        .domain("." + getHost())
                        .build());
        WebDriverRunner.getWebDriver()
                .manage()
                .addCookie(new Cookie.Builder("airSlate.header.domain", airslateRestClient.interceptors().organizationDomain.getOrganizationDomain())
                        .domain("." + getHost())
                        .build());
    }

    public String getHost() {
        return airslateRestClient.getRetrofit().baseUrl().host();
    }

    public String getAirSLateUrl() {
        HttpUrl url = airslateRestClient.getRetrofit().baseUrl();
        try {
            return new URIBuilder().setScheme(url.scheme())
                    .setHost(url.host().replace("api.", "")).build().toString();
        } catch (URISyntaxException e) {
            return url.toString();
        }
    }
}
