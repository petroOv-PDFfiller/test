package pages.salesforce.app.DaDaDocs.full_app.templates.entities;

import java.util.List;
import java.util.Objects;

public class PermissionGroup {
    private String groupName;
    private List<Permission> permissions;

    public PermissionGroup(String groupName, List<Permission> permissions) {
        this.groupName = groupName;
        this.permissions = permissions;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PermissionGroup)) return false;
        PermissionGroup that = (PermissionGroup) o;
        return Objects.equals(groupName, that.groupName) &&
                Objects.equals(permissions, that.permissions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupName, permissions);
    }
}
