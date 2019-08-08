package utils;

import imap.ImapClient;
import imap.MessageContent;
import imap.With;
import io.qameta.allure.Step;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

import static core.check.Check.checkTrue;

public class ImapMan {

    @Step("Get url from mail")
    public static String getUrlFromPasswordResetMail(String email, ImapClient imap) {
        Logger.info("Getting url from mail [" + email + "]");
        List<Message> messages = imap.findMessages(With.subject("Password reset"));
        if (imap.isConnected()) {
            imap.closeFolders().closeStore();
        }
        checkTrue(messages.size() > 0, "No messages in mailbox");
        Message shareMessage = messages.get(0);
        MessageContent content = new MessageContent(imap, shareMessage);
        List<String> links = content.getLinkUrls();
        checkTrue(links.size() > 1, "No appropriate link");
        return links.get(2);
    }

    @Step
    public static String getSalesforceEmailVerifyCode(String email, String password) {
        TimeMan.sleep(10);
        ImapClient imap = new ImapClient(email, password);
        List<Message> messages = imap.findMessages(With.subject("Verify your identity in Salesforce"));
        if (imap.isConnected()) {
            imap.closeFolders().closeStore();
        }
        String pattern = "Verification Code: ";
        int codeLength = 5;
        checkTrue(messages.size() > 0, "No messages in mailbox");
        for (Message message : messages) {
            try {
                String messageBody = String.valueOf(message.getContent());
                return messageBody.substring(messageBody.indexOf(pattern) + pattern.length(),
                        messageBody.indexOf(pattern) + pattern.length() + codeLength);
            } catch (IOException | MessagingException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    @Step
    public static boolean deleteValidationMessages(String orgOwnerEmail, String orgOwnerPassword) {
        ImapClient imapClient = null;
        try {
            if (!orgOwnerEmail.isEmpty()) {
                imapClient = new ImapClient(orgOwnerEmail, orgOwnerPassword);
                String validationMessage = "Verify your identity in Salesforce";
                imapClient.deleteAllMessagesWithSubject(validationMessage);
            }
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (imapClient != null && imapClient.isConnected()) {
                imapClient.closeFolders().closeStore();
            }
        }
    }


    @Step
    public static String getSignLinkFromS2SEmail(ImapClient imap, String subject) {
        List<Message> messages = imap.findMessages(With.subject(subject));

        checkTrue(messages.size() > 0, "No messages in mailbox");
        MessageContent messageContent = new MessageContent(imap, messages.get(0));
        List<String> urls = messageContent.getLinkUrls();
        checkTrue(urls.size() > 1, "No needful link in message");
        String urlToSign = urls.get(1);
        checkTrue(urlToSign != null, "Wrong link in message");

        return urlToSign;
    }
}
