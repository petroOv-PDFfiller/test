package pages.salesforce.app.DaDaDocs.admin_tools.entities;

import utils.Logger;

import java.util.Objects;

import static core.AllureAttachments.textAttachment;

public class DaDaDocsUser implements Comparable {
    private String fullName;
    private String email;
    private String profile;
    private String license;
    private String id;

    public DaDaDocsUser(String id, String fullName, String email, String profile, String license) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.profile = profile;
        this.license = license;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DaDaDocsUser)) return false;
        DaDaDocsUser user = (DaDaDocsUser) o;

        Logger.info("Check if current sf user object:" + toString() + " is equals to " + user.toString());
        textAttachment("Check if salesforce users are equals",
                "Current: " + this.toString() + " to " + user.toString());

        return Objects.equals(fullName, user.fullName) &&
                Objects.equals(email, user.email) &&
                Objects.equals(profile, user.profile) &&
                Objects.equals(license, user.license) &&
                Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullName, email, profile, license, id);
    }

    @Override
    public int compareTo(Object o) {
        return this.hashCode() - o.hashCode();
    }
}
