package pages.salesforce.app.airSlate_app.admin_tools.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ScheduleFlow {

    private String name;
    private String flow;
    private String frequency;
}