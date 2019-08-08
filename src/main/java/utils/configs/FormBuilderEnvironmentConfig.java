package utils.configs;

import okhttp3.HttpUrl;
import org.aeonbits.owner.Config;
import org.aeonbits.owner.Converter;
import org.aeonbits.owner.Reloadable;

import java.lang.reflect.Method;

@Config.Sources({
        "file:src/main/resources/environments/form_builder.properties"
})
public interface FormBuilderEnvironmentConfig extends Reloadable {

    @DefaultValue("DEV33")
    @Key("environment")
    String environment();

    @DefaultValue("http://airslate-${environment}.xyz")
    @Key("${environment}.url")
    @ConverterClass(DomainConverter.class)
    String url();

    @Key("${environment}.url")
    @DefaultValue("http://airslate-${environment}.xyz")
    @ConverterClass(ApiConverter.class)
    String api();

    @Key("${environment}.url")
    @DefaultValue("http://airslate-${environment}.xyz")
    @ConverterClass(FormBuilderConverter.class)
    String formUrl();

    @Key("email")
    @DefaultValue("pdf_sf_aqa+hfb@support.pdffiller.com")
    String email();

    class ApiConverter implements Converter<String> {
        public String convert(Method targetMethod, String text) {
            HttpUrl httpUrl = HttpUrl.parse(text.toLowerCase());
            return httpUrl.newBuilder().host("api." + httpUrl.host()).build().toString().replaceAll("/$", "");
        }
    }

    class FormBuilderConverter implements Converter<String> {
        public String convert(Method targetMethod, String text) {
            HttpUrl httpUrl = HttpUrl.parse(text.toLowerCase());
            return httpUrl.newBuilder().host("integrations." + httpUrl.host()).addPathSegment("html-form-builder").build().toString();
        }
    }

    class DomainConverter implements Converter<String> {
        public String convert(Method targetMethod, String text) {
            return text.toLowerCase();
        }
    }
}
