package tests;

import imap.ImapClient;
import imap.MessageContent;
import imap.With;
import io.qameta.allure.Step;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import java.util.List;
import java.util.stream.Collectors;

import static core.check.Check.*;
import static data.TestData.AirSlate.FLOW_NAME;

public class AirSlateMailer {

    private static ImapClient imap;

    private static Message getMessage(String... subject) {

        List<Message> messages = imap.findMessages(With.subject(subject));
        checkTrue(messages.size() > 0, "No messages in mailbox");
        try {
            messages.get(0).setFlag(Flags.Flag.SEEN, true);
        } catch (IndexOutOfBoundsException | MessagingException e) {
            checkFail(e.getMessage());
        }

        return messages.get(0);
    }

    @Step
    public static String getConfirmationPinFromEmail(ImapClient imap) {
        AirSlateMailer.imap = imap;

        String[] subject = {"airSlate code confirmation", "Confirm your email address"};

        String content = imap.getContent(getMessage(subject));

        Document doc = Jsoup.parse(content);
        Elements pinElement = doc.getElementsByClass("pin");

        String code = pinElement.text();
        checkEquals(code.length(), 6, "Code length isn't equals 6");
        imap.closeFolders().closeStore();

        return code;
    }

    @Step
    public static String getVerifyTeamMemberLinkFromMail(ImapClient imap) {
        AirSlateMailer.imap = imap;

        String[] subject = {"Request to verify team member", "New team member requested"};

        Message emailMessage = getMessage(subject);

        List<String> linkUrls = new MessageContent(imap, emailMessage).getLinkUrls();
        checkTrue(linkUrls.size() >= 3, "Less than three links in email");
        String verifyTeamMemberLink = linkUrls.get(2);
        imap.closeFolders().closeStore();

        return verifyTeamMemberLink;
    }

    @Step
    public static String getDirectLinkFromMail(ImapClient imap, String orgName) {
        AirSlateMailer.imap = imap;

        String[] subject = {"you to join the " + orgName + " Workspace", "has invited you to a workspace in Airslate"};

        Message emailMessage = getMessage(subject);

        List<String> linkUrls = new MessageContent(imap, emailMessage).getLinkUrls();
        checkTrue(linkUrls.size() >= 3, "Less than three links in email");
        String directLink = linkUrls.get(2);
        imap.closeFolders().closeStore();

        return directLink;
    }

    @Step
    public static String getOrganizationLinkFromMail(ImapClient imap) {
        AirSlateMailer.imap = imap;

        String[] subject = {"New Workspace was created", "your new airSlate Workspace!"};

        String content = imap.getContent(getMessage(subject));

        Document doc = Jsoup.parse(content);
        Elements linkElement = doc.select("div p.desc-text + p");
        String link = linkElement.text();
        imap.closeFolders().closeStore();

        return link;

    }

    @Step
    public static String getRequestApprovedLinkFromMail(ImapClient imap, String orgDomain) {
        AirSlateMailer.imap = imap;

        String[] subject = {"Your Request to join " + orgDomain + " has been approved!", "Your request has been approved!"};

        Message emailMessage = getMessage(subject);
        List<String> linkUrls = new MessageContent(imap, emailMessage).getLinkUrls();
        checkTrue(linkUrls.size() >= 3, "Less than three links in email");
        String requestApprovedLink = linkUrls.get(2);
        imap.closeFolders().closeStore();

        return requestApprovedLink;
    }

    @Step
    public static String getResetLinkFromMail(ImapClient imap) {
        AirSlateMailer.imap = imap;

        String[] subject = {"Reset password link", "Your airSlate password recovery"};

        Message emailMessage = getMessage(subject);
        String resetLink = new MessageContent(imap, emailMessage)
                .getLinkUrls()
                .get(2);

        imap.closeFolders().closeStore();
        return resetLink;
    }

    @Step
    public static String getSharedFlowLinkFromMail(ImapClient imap, String mailSubject) {
        AirSlateMailer.imap = imap;
        List<String> linkUrls = new MessageContent(imap, getMessage(mailSubject)).getLinkUrls();
        String link = linkUrls.get(2);
        checkTrue(link.matches("(https://link.)(.)*(direct_push)"), "Link not matches invitation format");
        imap.closeFolders().closeStore();
        return link;
    }

    @Step
    public static String getFlowInvitationMessageFromMail(ImapClient imap, String mailSubject) {
        AirSlateMailer.imap = imap;
        Elements textBlocksFromMessage = Jsoup.parse(imap.getContent(getMessage(mailSubject))).getElementsByClass("desc-text");
        checkTrue(textBlocksFromMessage.size() == 3, "Message should have 3 text blocks");
        imap.closeFolders().closeStore();
        return textBlocksFromMessage.get(1).text();
    }

    @Step
    public static String getFlowEditorLinkFromAdminInviteMail(ImapClient imap) {
        AirSlateMailer.imap = imap;
        Message emailMessage = getMessage("You have been invited to administer " + FLOW_NAME + "’s Flow");
        List<String> editorFlowLinks = new MessageContent(imap, emailMessage)
                .getLinkUrls();
        checkTrue(editorFlowLinks.size() >= 3, "Less than three links in email");
        imap.closeFolders().closeStore();
        return editorFlowLinks.get(2);
    }

    @Step
    public static String getFlowAdminInviteMessage(ImapClient imap) {
        AirSlateMailer.imap = imap;
        Message emailMessage = getMessage("You have been invited to administer " + FLOW_NAME + "’s Flow");
        List<String> messageContent = Jsoup.parse(imap.getContent(emailMessage))
                .getElementsByClass("desc-text")
                .stream()
                .map(row -> row.text())
                .collect(Collectors.toList());
        checkTrue(messageContent.size() >= 4, "Less than 4 text blocks in email");
        imap.closeFolders().closeStore();
        return String.join(" ", messageContent);
    }

    @Step
    public static String getPinFromAdditionalAuth(ImapClient client) {
        AirSlateMailer.imap = client;

        String subject = "Additional Authentication code confirmation";

        String content = imap.getContent(getMessage(subject));

        Document doc = Jsoup.parse(content);
        Elements pinElement = doc.getElementsByClass("pin");

        String code = pinElement.text();
        checkEquals(code.length(), 6, "Code length doesn't equal 6");
        imap.closeFolders().closeStore();

        return code;
    }

    @Step
    public static String getRequestConnectionsMessage(ImapClient imap, String userFullName, String flowName) {
        AirSlateMailer.imap = imap;
        Message emailMessage = getMessage(userFullName + " wants to copy the " + flowName + " Flow");
        String link = Jsoup.parse(imap.getContent(emailMessage))
                .getElementsMatchingOwnText("Go to Connected Accounts")
                .attr("href");
        imap.closeFolders().closeStore();
        return link;
    }
}
