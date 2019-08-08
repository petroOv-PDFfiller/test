package pages.salesforce.enums.admin_tools;

public enum LayoutsTabActions {

    SAVE_CHANGES("Save changes"),
    SHOW_IN_ALL_LAYOUTS("Show in all layouts"),
    HIDE_FROM_ALL_LAYOUTS("Hide from all layouts");

    private String actionName;

    LayoutsTabActions(String actionName) {
        this.actionName = actionName;
    }

    public String getActionName() {
        return actionName;
    }
}