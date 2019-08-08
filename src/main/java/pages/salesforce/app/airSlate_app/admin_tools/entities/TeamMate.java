package pages.salesforce.app.airSlate_app.admin_tools.entities;

import lombok.*;
import pages.salesforce.enums.admin_tools.ASAppTeammateStatus;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class TeamMate {

    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private String email;
    @Getter
    @Setter
    private String profile;
    @Getter
    @Setter
    private ASAppTeammateStatus status;
}
