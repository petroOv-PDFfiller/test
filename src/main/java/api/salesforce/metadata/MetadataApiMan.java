package api.salesforce.metadata;

import com.sforce.soap.metadata.Error;
import com.sforce.soap.metadata.*;
import com.sforce.soap.partner.LoginResult;
import com.sforce.ws.ConnectionException;
import lombok.Getter;

import static api.salesforce.metadata.MetadataLoginUtil.createMetadataConnection;
import static api.salesforce.metadata.MetadataLoginUtil.login;
import static core.check.Check.checkFail;

public class MetadataApiMan {

    @Getter
    private MetadataConnection metadataConnection;

    public MetadataApiMan(MetadataConnection metadataConnection) {
        this.metadataConnection = metadataConnection;
    }

    public MetadataApiMan(String email, String password, String baseUrl) {
        this(login(email, password, baseUrl));
    }

    public MetadataApiMan(LoginResult loginResult) {
        try {
            metadataConnection = createMetadataConnection(loginResult);
        } catch (ConnectionException e) {
            e.printStackTrace();
            checkFail("Cannot connect to metadata API");
        }
    }

    public void createCustomObjectSync(CustomObject co) throws Exception {
        SaveResult[] results = createMetadata(new Metadata[]{co});
        for (SaveResult r : results) {
            if (r.isSuccess()) {
                System.out.println("Created component: " + r.getFullName());
            } else {
                System.out
                        .println("Errors were encountered while creating "
                                + r.getFullName());
                for (Error e : r.getErrors()) {
                    System.out.println("Error message: " + e.getMessage());
                    System.out.println("Status code: " + e.getStatusCode());
                }
            }
        }
    }

    public ReadResult readMetadata(String type, String[] fullNames) throws ConnectionException {
        return metadataConnection.readMetadata(type, fullNames);
    }

    public SaveResult[] createMetadata(Metadata[] metadata) throws ConnectionException {
        return metadataConnection.createMetadata(metadata);
    }

    public UpsertResult[] updateMetadata(Metadata[] metadata) throws ConnectionException {
        return metadataConnection.upsertMetadata(metadata);
    }
}
