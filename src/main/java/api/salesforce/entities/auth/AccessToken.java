package api.salesforce.entities.auth;


import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AccessToken {
    @NonNull
    public String access_token;
    @NonNull
    public String instance_url;
    public String id;
    @NonNull
    public String token_type;
    public String issued_at;
    public String signature;
}
