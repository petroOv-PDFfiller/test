package api.salesforce.entities.airslate;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import utils.JsonMan;

@Data
@AllArgsConstructor
public class SalesforceAirslateUser {
    private String login;
    private String password;
    @JsonProperty("isAdmin")
    private boolean isAdmin;

    @Override
    public String toString() {
        return JsonMan.convertPojoToJsonPrettyString(this);
    }

    public class Builder {
        private String login;
        private String password;
        private boolean isAdmin;

        public Builder() {
        }

        public Builder setLogin(String login) {
            this.login = login;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder setAdmin(boolean admin) {
            isAdmin = admin;
            return this;
        }

        public SalesforceAirslateUser build() {
            return new SalesforceAirslateUser(login, password, isAdmin);
        }
    }
}
