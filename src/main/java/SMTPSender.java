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
    private Session session;

    public SMTPSender(String smtpHost, String user, String password, String fromName, Address[] toList) {
        this.smtpHost = smtpHost;
        this.user = user;
        this.password = password;
        this.fromName = fromName;

        this.toList = toList;

        Properties props = new Properties();
        props.put("mail.smtp.host", smtpHost);
        session = Session.getDefaultInstance(props, null);

    }

    public void doSend(Message message) throws MessagingException, IOException {
        String replyTo = user, from = user, subject;
        Date sentDate;

        Address[] fromAddresses = null;

        if ((fromAddresses = message.getFrom()) != null)
            replyTo = fromAddresses[0].toString();

        subject = message.getSubject();
        sentDate = message.getSentDate();

        Address replyToList[] = {new InternetAddress(replyTo)};

        Message newMessage = composeMessage(message, replyTo, subject, sentDate, from, replyToList);

        send(newMessage);
    }

    private Message composeMessage(Message message, String replyTo, String subject, Date sentDate, String from, Address[] replyToList) throws MessagingException, IOException {
        Message newMessage = new MimeMessage(session);
        if (fromName != null)
            newMessage.setFrom(new InternetAddress(from, fromName
                    + " on behalf of " + replyTo));
        else
            newMessage.setFrom(new InternetAddress(from));
        newMessage.setReplyTo(replyToList);
        newMessage.setRecipients(Message.RecipientType.BCC, toList);
        newMessage.setSubject(subject);
        newMessage.setSentDate(sentDate);

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
        return newMessage;
    }

    private void send(Message newMessage) throws MessagingException {
        Transport transport = session.getTransport(SMTP_MAIL);
        transport.connect(smtpHost, user, password);
        transport.sendMessage(newMessage, toList);
    }
}
