package pages.salesforce.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum PopUpActions {

    DISCONNECT("Disconnect"),
    SUBMIT("Submit"),
    CANCEL("Cancel"),
    REMOVE("Remove"),
    EDIT("Edit"),
    DELETE("Delete"),
    UPDATE("Update"),
    DONE("Done!"),
    CONTINUE("Continue"),
    LEAVE_NOW("Leave Now"),
    RETURN_TO_LOG_IN("Return to Log in"),
    CREATE("Create");

    @Getter
    private String actionName;
}
