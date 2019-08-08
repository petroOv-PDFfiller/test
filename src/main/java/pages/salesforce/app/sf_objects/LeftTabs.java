package pages.salesforce.app.sf_objects;

public enum LeftTabs {

    RELATED(1),
    DETAILS(2),
    NEWS(3);

    private int name;

    LeftTabs(int name) {
        this.name = name;
    }

    public int getName() {
        return name;
    }
}
