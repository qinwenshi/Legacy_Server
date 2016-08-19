import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

/**
 * Created by qinwenshi on 8/13/16.
 */
public class SMTPSender {
    private String smtpHost;
    private String user;
    private String password;
    private String fromName;

    private Address[] toList;

    private static final String SMTP_MAIL = "smtp";
    public SMTPSender(String smtpHost, String user, String password, String fromName, Address[] toList) {
        this.smtpHost = smtpHost;
        this.user = user;
        this.password = password;
        this.fromName = fromName;

        this.toList = toList;
    }

    public void doSend(Message message) throws MessagingException, IOException {
        String replyTo = user, subject, xMailer, messageText;
        Date sentDate;
        int size;
        Address[] a = null;

        // Get Headers (from, to, subject, date, etc.)
        //
        if ((a = message.getFrom()) != null)
            replyTo = a[0].toString();

        subject = message.getSubject();
        sentDate = message.getSentDate();
        size = message.getSize();
        String[] hdrs = message.getHeader("X-Mailer");
        if (hdrs != null)
            xMailer = hdrs[0];
        String from = user;

        // Send message
        //
        // create some properties and get the default Session
        //
        Properties props = new Properties();
        props.put("mail.smtp.host", smtpHost);
        Session session1 = Session.getDefaultInstance(props, null);

        // create a message
        //
        Address replyToList[] = {new InternetAddress(replyTo)};
        Message newMessage = new MimeMessage(session1);
        if (fromName != null)
            newMessage.setFrom(new InternetAddress(from, fromName
                    + " on behalf of " + replyTo));
        else
            newMessage.setFrom(new InternetAddress(from));
        newMessage.setReplyTo(replyToList);
        newMessage.setRecipients(Message.RecipientType.BCC, toList);
        newMessage.setSubject(subject);
        newMessage.setSentDate(sentDate);

        // Set message contents
        //
        Object content = message.getContent();
        String debugText = "Subject: " + subject + ", Sent date: " + sentDate;
        if (content instanceof Multipart) {
            MailLogger.getLoggerInstance().log
                (new Date() + "> " + "Sending Multipart message (" + debugText + ")");
            newMessage.setContent((Multipart) message.getContent());
        } else {
            MailLogger.getLoggerInstance().log
                (new Date() + "> " + "Sending Text message (" + debugText + ")");
            newMessage.setText((String) content);
        }
        Template template = new Template();

        newMessage.setText(template.make());

        // Send newMessage
        //
        Transport transport = session1.getTransport(SMTP_MAIL);
        transport.connect(smtpHost, user, password);
        transport.sendMessage(newMessage, toList);
    }
}
