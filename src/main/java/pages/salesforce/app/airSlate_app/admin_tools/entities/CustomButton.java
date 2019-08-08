package pages.salesforce.app.airSlate_app.admin_tools.entities;

import lombok.*;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class CustomButton {

    @Getter
    @Setter
    String label;
    @Getter
    @Setter
    String description;
    @Getter
    @Setter
    String flow;
    @Getter
    @Setter
    String date;
}
