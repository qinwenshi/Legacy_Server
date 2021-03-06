import javax.mail.*;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

/**
 * Created by qinwenshi on 8/13/16.
 */
public class Pop3MailBox {
    private final boolean debugOn;
    private String pop3Host;
    private String ls_user;
    private String ls_password;
    private Store store;
    private Folder folder;
    private boolean done;
    private static final String INBOX = "INBOX", POP_MAIL = "pop3"
            ;
    public Pop3MailBox(String pop3Host, String ls_user, String ls_password, boolean debugOn) {
        this.pop3Host = pop3Host;
        this.ls_user = ls_user;
        this.ls_password = ls_password;
        this.debugOn = debugOn;

    }

    public void closeMailBox() throws MessagingException {
        store.close();
    }

    public boolean isEmpty() throws MessagingException {
        boolean isEmpty = (folder.getMessageCount() == 0);
        if(isEmpty)
            MailLogger.getLoggerInstance().log
                (new Date() + "> " + folder + " is empty");

        return isEmpty;
    }

    public void closeEmptyFolder() throws MessagingException {
        folder.close(false);
    }

    public void closeFolder() throws MessagingException {
        folder.close(true);
    }

    public void batchReplayAllMessagesThrough(SMTPSender smtpSender) throws MessagingException, IOException {
        Message[] messages = this.loadMessages();

        // Process each message
        //
        for (int i = 0; i < messages.length; i++) {
            if (!messages[i].isSet(Flags.Flag.SEEN)) {
                Message message = messages[i];
                smtpSender.doSend(message);
            }
            messages[i].setFlag(Flags.Flag.DELETED, true);
        }
    }

    public Message[] loadMessages() throws MessagingException {
        Message[] messages = folder.getMessages();
        FetchProfile fp = new FetchProfile();
        fp.add(FetchProfile.Item.ENVELOPE);
        fp.add(FetchProfile.Item.FLAGS);
        fp.add("X-Mailer");
        folder.fetch(messages, fp);
        return messages;
    }

    public void open() throws MessagingException {
        Properties sysProperties = System.getProperties();
        Session session = Session.getDefaultInstance(sysProperties, null);
        session.setDebug(debugOn);

        // Connect to host
        //
        store = session.getStore(POP_MAIL);
        store.connect(pop3Host, -1, ls_user, ls_password);

        // Open the default folder
        //
        folder = store.getDefaultFolder();
        if (folder == null)
            throw new NullPointerException("No default mail folder");

        folder = folder.getFolder(INBOX);
        if (folder == null)
            throw new NullPointerException("Unable to get folder: " + folder);

        done = false;
        // Get message count
        //
        folder.open(Folder.READ_WRITE);
    }
}
