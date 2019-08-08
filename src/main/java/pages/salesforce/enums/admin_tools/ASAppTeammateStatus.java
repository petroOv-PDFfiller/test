package pages.salesforce.enums.admin_tools;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ASAppTeammateStatus {

    JOINED("Joined", "Joined"),
    NOT_INVITED("Not invited", "Not_Invited"),
    INVITE_PENDING("Invite pending", "Invite_Pending");

    @Getter
    private String value;
    @Getter
    private String apiValue;

    public static ASAppTeammateStatus getTeammateStatusByValue(String value) {
        for (int i = 0; i < ASAppTeammateStatus.values().length; i++) {
            if (value.equals(ASAppTeammateStatus.values()[i].getValue())) {
                return ASAppTeammateStatus.valueOf(ASAppTeammateStatus.values()[i].name());
            }
        }
        return null;
    }
}
