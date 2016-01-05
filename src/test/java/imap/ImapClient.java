package imap;

import org.apache.commons.io.IOUtils;
import org.testng.Assert;
import ru.yandex.qatools.allure.annotations.Step;
import utils.Logger;
import utils.TimeMan;

import javax.mail.*;
import javax.mail.search.SearchTerm;
import java.util.*;

/**
 * Created by Vladyslav on 07.07.2015.
 */
public class ImapClient {

    private static Store store = null;
    private Folder folder = null;

    private String email;
    private String password;
    private String host;

    private int timeout = 6;
    private int retry = 3;

    private static final String NONE = "none";

    /**
     * param String
     *            email
     * param String
     *            password
     */
    public ImapClient(String email, String password) {
        this.email = email;
        this.password = password;
        if (email.contains("hotmail"))
            host = "imap-mail.outlook.com";
        else if (email.contains("gmail"))
            host = "imap.gmail.com";
        else if (email.contains("gentechpartners"))
            host = "imap.gmail.com";
        else if (email.contains("corp.flirchi.com"))
            host = "imap.gmail.com";
        else if (email.contains("yahoo"))
            host = "imap.mail.yahoo.com";
        else if (email.contains("mail.ru"))
            host = "imap.mail.ru";
        else if (email.contains("yandex") || email.contains("corp"))
            host = "imap.yandex.com";
        else
            throw new NullPointerException(
                    "Unable to determine the host for this email [" + email
                            + "]");
    }

    public ImapClient withTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public ImapClient retry(int retry) {
        this.retry = retry;
        return this;
    }

    /**
     * Used for connect to store
     *
     * @return Store
     */
    public Store connect() {

        int counter = 0;
        while (!isConnected() & counter++ < retry) {
            Logger.info("=============================================");
            Logger.info("ImapClient is connecting to: [" + email + ":" + password + "]");
            Properties properties = new Properties();
            Session session = Session.getInstance(properties);
            try {
                store = session.getStore("imaps");
                store.connect(host, email, password);
                Logger.info("Imap connection success");
            } catch (MessagingException e) {
                System.out.println(e.getMessage());
                TimeMan.sleep(timeout);
            }
        }
        return store;
    }

    public boolean isConnected() {
        if (store == null)
            return false;
        return store.isConnected();
    }

    /**
     * Used for searching messages in spam and inbox folders.</br> You can set
     * the frequency search,</br> by setting retry parameter and wait,</br> by
     * setting timeout parameter</br>
     *
     * param SearchTerm
     * @return ArrayList<<code>Message</code>>
     */
    public ArrayList<Message> findMessages(SearchTerm term) {
        ArrayList<Message> messages = new ArrayList<Message>();
        int counter = 0;
        while (messages.size() == 0 && counter++ < retry) {
            Assert.assertTrue(connect().isConnected(),
                    "Failed connect to store");
            Assert.assertTrue(openInboxFolder().isOpen(),
                    "Cant open inbox folder");
            for (Message message : search(term))
                messages.add(message);

            Assert.assertTrue(openSpamFolder().isOpen(),
                    "Cant open spam folder");
            for (Message message : search(term))
                messages.add(message);

            TimeMan.sleep(timeout);
        }
        return messages;
    }

    public ArrayList<Message> findMessages() {
        ArrayList<Message> messages = new ArrayList<Message>();
        Assert.assertTrue(connect().isConnected(), "Failed connect to store");
        Assert.assertTrue(openInboxFolder().isOpen(), "Cant open inbox folder");
        for (Message message : search())
            messages.add(message);

        Assert.assertTrue(openSpamFolder().isOpen(), "Cant open spam folder");
        for (Message message : search())
            messages.add(message);

        return messages;
    }

    private ArrayList<Message> search(SearchTerm term) {
        ArrayList<Message> messages = new ArrayList<Message>();
        try {
            for (Message message : folder.search(term))
                messages.add(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return messages;
    }

    private ArrayList<Message> search() {
        ArrayList<Message> messages = new ArrayList<Message>();
        try {
            for (Message message : folder.getMessages())
                messages.add(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return messages;
    }

    @Step("Delete all messages in mail-box")
    public void deleteAllMessages() {
        Logger.info("ImapClient is deleting all messages");
        Assert.assertTrue(connect().isConnected(), "Failed connect to store");
        try {
            Assert.assertTrue(openInboxFolder().isOpen(),
                    "Cant open inbox folder");
            for (Message message : search())
                message.setFlag(Flags.Flag.DELETED, true);
            closeFolder(true);
            Assert.assertTrue(openSpamFolder().isOpen(),
                    "Cant open spam folder");
            for (Message message : search())
                message.setFlag(Flags.Flag.DELETED, true);
            closeFolder(true);
        } catch (MessagingException e) {
            Logger.info("Failed to delete all messages");
            e.printStackTrace();
        }
    }

    public Folder openInboxFolder() {
        try {
            folder = store.getFolder("Inbox");
            folder.open(Folder.READ_WRITE);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return folder;
    }

    public Folder openSpamFolder() {
        try {
            folder = store.getFolder(getSpamFolderName());
            folder.open(Folder.READ_WRITE);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return folder;
    }

    public void closeFolder() {
        try {
            folder.close(false);
        } catch (MessagingException e) {
            System.err.println(e.getMessage());
        }
    }

    public void closeFolder(boolean expunge) {
        try {
            folder.close(expunge);
        } catch (MessagingException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void closeStore() {
        try {
            store.close();
        } catch (MessagingException e) {
            System.err.println(e.getMessage());
        }
        Logger.info("ImapClient connection is closed");
        Logger.info("================================================");
    }

    public void delete(Message message) {
        try {
            message.setFlag(Flags.Flag.DELETED, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getContent(Message message) {
        try {
            Object body = message.getContent();
            if (body instanceof Multipart) {
                Multipart multipart = (Multipart) message.getContent();
                BodyPart bodyPart = multipart.getBodyPart(1);
                return IOUtils.toString(bodyPart.getInputStream(), "UTF-8");
            } else {
                return body.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return NONE;
    }

    public String getHeader(Message message, String header) {
        try {
            return message.getHeader(header)[0];
        } catch (NullPointerException e) {
            return NONE;
        } catch (MessagingException e) {
            e.printStackTrace();
            return NONE;
        }
    }

    @SuppressWarnings("unchecked")
    public List<String> getAllHeaders(Message message) {
        List<String> headersList = new ArrayList<String>();
        Enumeration<Header> headers = null;
        try {
            headers = message.getAllHeaders();
        } catch (MessagingException e) {
            e.printStackTrace();
            return headersList;
        }
        while (headers.hasMoreElements()) {
            Header h = headers.nextElement();
            headersList.add(h.getName() + ":" + h.getValue());
        }
        return headersList;
    }

    public HashMap<String, String> getAllHeadersMap(Message message) {
        HashMap<String, String> headersMap = new HashMap<String, String>();
        Enumeration<Header> headers = null;
        try {
            headers = message.getAllHeaders();
        } catch (MessagingException e) {
            e.printStackTrace();
            return headersMap;
        }
        while (headers.hasMoreElements()) {
            Header h = headers.nextElement();
            headersMap.put(h.getName(), h.getValue());
        }
        return headersMap;
    }

    public String getSubject(Message message) {
        try {
            return message.getSubject();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return NONE;
    }

    public Date getSentDate(Message message) {
        try {
            return message.getSentDate();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Date getReceivedDate(Message message) {
        try {
            return message.getReceivedDate();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getFolderName(Message message) {
        String folderName = message.getFolder().getName();
        if (folderName.contains("Inbox"))
            return "Inbox";
        return "Spam";
    }

    public List<Message> sortMessagesBySentDate(List<Message> messages) {
        Collections.sort(messages, new Comparator<Message>() {
            public int compare(Message m1, Message m2) {
                Date date1 = getSentDate(m1);
                Date date2 = getSentDate(m2);
                return date1.compareTo(date2);
            }
        });
        return messages;
    }

    public List<Message> sortMessagesByReceivedDate(List<Message> messages) {
        Collections.sort(messages, new Comparator<Message>() {
            public int compare(Message m1, Message m2) {
                Date date1 = getReceivedDate(m1);
                Date date2 = getReceivedDate(m2);
                return date1.compareTo(date2);
            }
        });
        return messages;
    }

    public List<Message> sortDescentMessagesByReceivedDate(List<Message> messages) {

        Collections.reverse(sortMessagesByReceivedDate(messages));
        return messages;
    }

    public boolean isMessageReceivedToInbox(Message message) {
        return message.getFolder().getName().contains("Inbox");
    }

    public boolean isReceivedOneMessage(List<Message> messages) {
        return messages.size() == 1;
    }

    private String getSpamFolderName() {

        if (host.equals("imap-mail.outlook.com")) {
            return "Junk";
        }
        else {
            if (host.equals("imap.gmail.com")) {
                Folder[] folders = null;
                try {
                    folders = store.getDefaultFolder().list();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
                for (Folder f : folders) {
                    if (f.getFullName().contains("Spam")) {
                        return "[Gmail]/Spam";
                    }
                    else if ((f.getFullName().contains("Спам"))) {
                        return "[Gmail]/Спам";
                    }
                }
                return "[Gmail]/Spam";
            }
            else {
                if (host.equals("imap.mail.yahoo.com")) {
                    return "Bulk Mail";
                }
                else {
                    if (host.equals("imap.mail.ru")) {
                        return "Спам";
                    }
                    else {
                        if (host.equals("imap.yandex.com")) {
                            return "Спам";
                        }
                        else {
                            return NONE;
                        }
                    }
                }
            }
        }
    }
}
