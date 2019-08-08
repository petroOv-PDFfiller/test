package pages.salesforce.app.DaDaDocs.admin_tools.entities;


import java.util.Objects;

public class Layout implements Comparable {
    private String objectName;
    private String layoutName;
    private boolean show;

    public Layout(String objectName, String layoutName, boolean show) {
        this.objectName = objectName;
        this.layoutName = layoutName;
        this.show = show;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getLayoutName() {
        return layoutName;
    }

    public void setLayoutName(String layoutName) {
        this.layoutName = layoutName;
    }

    public boolean getShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Layout)) return false;
        Layout layout = (Layout) o;
        return show == layout.show &&
                Objects.equals(objectName, layout.objectName) &&
                Objects.equals(layoutName, layout.layoutName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(objectName, layoutName, show);
    }

    @Override
    public int compareTo(Object o) {
        return this.hashCode() - o.hashCode();
    }
}
