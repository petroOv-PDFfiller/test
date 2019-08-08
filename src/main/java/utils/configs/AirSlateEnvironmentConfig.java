package utils.configs;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Reloadable;

@Config.Sources({
        "file:src/main/resources/environments/airslate/PROD.properties"
})
public interface AirSlateEnvironmentConfig extends Reloadable {
    @DefaultValue("https://airslate.com")
    String url();

    @DefaultValue("https://api.airslate.com/")
    @Key("api")
    String api();

    @DefaultValue("pdf_sf_aqa+hfb@support.pdffiller.com")
    String email();
}
