package data.airslate;

import org.testng.annotations.DataProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AirSlatePermissionAndRoles {

    private static List<String> organizationRoles = new ArrayList<>(Arrays.asList(
            "documentary",
            "organization-admin",
            "team-admin",
            "financial-admin",
            "organization-owner",
            "organization-owner-guest",
            "organization-owner-private",
            "team-member",
            "team-member-approve-required",
            "team-member-invite",
            "team-member-partner"
    ));

    private static List<String> organizationOwner = new ArrayList<>(Arrays.asList(
            "financials:billings_manage",
            "financials:payment_methods",
            "financials:orders",
            "teams:users_manage",
            "teams:users_invite",
            "teams:users_tags_manage",
            "teams:roles",
            "teams:roles_assing",
            "teams:roles_assing_protected",
            "organizations:manage",
            "organizations:custom_brandings_manage",
            "organizations:customizations_manage",
            "organizations:address_books_manage",
            "organization:invite_settings_manage",
            "slates:manage",
            "slates:distribution_collaborators",
            "slates:distribution_public_link",
            "slates:distribution_channels",
            "slates:distribution_invites",
            "slates:addons_manage",
            "slates:packets_manage_all",
            "slates:packets_use_all",
            "audit_trails:financials",
            "audit_trails:users",
            "audit_trails:teams",
            "audit_trails:organizations",
            "channels:view_all"
    ));

    private static List<String> teamMember = new ArrayList<>(Arrays.asList(
            "organizations:view",
            "organizations:address_books_view",
            "teams:users_view_active",
            "slates:list",
            "slates:create",
            "teams:tags_list"
    ));

    private static List<String> financialAdmin = new ArrayList<>(Arrays.asList(
            "financials:billings_manage",
            "financials:payment_methods",
            "financials:orders",
            "audit_trails:financials"
    ));

    private static List<String> teamAdmin = new ArrayList<>(Arrays.asList(
            "organization:invite_settings_manage",
            "teams:users_manage",
            "teams:users_invite",
            "teams:users_tags_manage",
            "teams:roles",
            "teams:roles_assing",
            "audit_trails:teams",
            "audit_trails:users"
    ));

    private static List<String> documentary = new ArrayList<>(Arrays.asList(
            "slates:manage",
            "slates:distribution_collaborators",
            "slates:distribution_public_link",
            "slates:distribution_channels",
            "slates:distribution_invites",
            "slates:addons_manage",
            "slates:packets_manage_all",
            "slates:packets_use_all",
            "audit_trails:slates",
            "slates:view_all",
            "channels:view_all"
    ));

    private static List<String> organizationAdmin = new ArrayList<>(Arrays.asList(
            "organizations:manage",
            "organizations:custom_brandings_manage",
            "organizations:customizations_manage",
            "organizations:address_books_manage",
            "organization:invite_settings_manage",
            "audit_trails:organizations"
    ));

    public static List<String> getOrganizationRoles() {
        return organizationRoles;
    }

    public static List<String> getOrganizationOwner() {
        return new ArrayList<String>() {{
            addAll(organizationOwner);
            addAll(teamMember);
        }};
    }

    public static List<String> getTeamMember() {
        return teamMember;
    }

    public static List<String> getFinancialAdmin() {
        return new ArrayList<String>() {{
            addAll(financialAdmin);
            addAll(teamMember);
        }};
    }

    public static List<String> getTeamAdmin() {
        return new ArrayList<String>() {{
            addAll(teamAdmin);
            addAll(teamMember);
        }};
    }

    public static List<String> getDocumentary() {
        return new ArrayList<String>() {{
            addAll(documentary);
            addAll(teamMember);
        }};
    }

    public static List<String> getOrganizationAdmin() {
        return new ArrayList<String>() {{
            addAll(organizationAdmin);
            addAll(teamMember);
        }};
    }

    @DataProvider
    public static Object[][] organization() {
        return new Object[][]{
                {"documentary", getDocumentary()},
                {"organization-admin", getOrganizationAdmin()},
                {"team-admin", getTeamAdmin()},
                {"financial-admin", getFinancialAdmin()}
        };
    }

    @DataProvider
    public static Object[] organizationRoles() {
        return new Object[]{
                "documentary",
                "organization-admin",
                "team-admin",
                "financial-admin"
        };
    }

    @DataProvider
    public static Object[][] organizationWithOldUsers() {
        return new Object[][]{
                {"team-member", getTeamMember(), "pd+role+teammember@support.pdffiller.com"},
                {"documentary", getDocumentary(), "pd+role+documentary@support.pdffiller.com"},
                {"organization-admin", getOrganizationAdmin(), "pd+role+orgadmin@support.pdffiller.com"},
                {"team-admin", getTeamAdmin(), "pd+role+teamadmin@support.pdffiller.com"},
                {"financial-admin", getFinancialAdmin(), "pd+role+financialadmin@support.pdffiller.com"},
                {"organization-owner", getOrganizationOwner(), "pd@support.pdffiller.com"}
        };
    }

}
