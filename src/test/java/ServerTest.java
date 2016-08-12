import org.junit.Before;
import org.junit.Test;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

/**
 * Created by qinwenshi on 8/12/16.
 */
public class ServerTest {

    public static final String SMTP_MESSAGE_FOLDER = "/Users/qinwenshi/Desktop/fakeSMTP/messages";
    private SystemExitStub systemExitStub;
    private ByteArrayOutputStream outputStream;

    class SystemExitStub extends SystemExit{
        public int statusCode = -1;
        @Override
        public void invoke(int statusCode) throws Exception {
            this.statusCode = statusCode;
            throw new Exception("System Exit with Status " + statusCode);
        }

    }

    @Before
    public void setUp() throws Exception {
        systemExitStub = new SystemExitStub();

        outputStream = new ByteArrayOutputStream();
        stubbingSystemExiting();
        redirectErrAndOutStream();
    }

    @Test
    public void exitWhenLessThan6Parameters()  {
        try {
            Server.main(new String[]{"a", "b", "c", "d"});
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        assertEquals(outputStream.toString(),
                "Usage: java Server SMTPHost POP3Host user password EmailListFile CheckPeriodFromName\n" +
                "System Exit with Status 1\n");

        assertEquals(systemExitStub.statusCode, 1);
    }

    private void stubbingSystemExiting() {
        Server.setSystemExit(systemExitStub);
    }

    private void redirectErrAndOutStream() {
        System.setErr(new PrintStream(outputStream));
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    public void x() throws Exception {
        setupLoopingAndSleeping();
        int fileCount = getSentMessageCount();

        Server.main(new String[]{"127.0.0.1", "pop.163.com", "fifty5cup@163.com", "shiqinwen01", "emailList.txt","1"});

        assertEquals(outputStream.toString(), "Now sleeping for 1 minutes\n");
        assertEquals(fileCount + 1, getSentMessageCount());

        assertEquals(latestMail(SMTP_MESSAGE_FOLDER).getFrom()[0].toString(), "fifty5cup@163.com");
        assertEquals(latestMail(SMTP_MESSAGE_FOLDER).getReplyTo()[0].toString(), "55 Cup <fifty5cup@163.com>");

    }

    @Test
    public void can_read_mail_message() throws IOException, MessagingException {
        MimeMessage message = latestMail(SMTP_MESSAGE_FOLDER);
        assertEquals(message.getFrom()[0].toString(), "fifty5cup@163.com");
        assertEquals(message.getReplyTo()[0].toString(), "55 Cup <fifty5cup@163.com>");
    }

    private MimeMessage latestMail(String dir) throws IOException, MessagingException {
        File fl = new File(dir);
        File[] files = fl.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return file.isFile();
            }
        });
        long lastMod = Long.MIN_VALUE;
        File choice = null;
        for (File file : files) {
            if (file.lastModified() > lastMod) {
                choice = file;
                lastMod = file.lastModified();
            }
        }

        Properties props = System.getProperties();
        props.put("mail.host", "smtp.dummydomain.com");
        props.put("mail.transport.protocol", "smtp");

        Session mailSession = Session.getDefaultInstance(props, null);
        InputStream source = new FileInputStream(choice);
        MimeMessage message = new MimeMessage(mailSession, source);

        return message;
    }

    private int getSentMessageCount() {
        return new File(SMTP_MESSAGE_FOLDER).list().length;
    }

    private void setupLoopingAndSleeping() {
        Server.setLoopInstance(new LoopOnce());
        Server.setSleepObject(new WakeupImmediately());
    }

    private class WakeupImmediately extends Sleep{
        @Override
        public void forMinutes(int minutes) throws InterruptedException {
            System.out.println("Now sleeping for "+minutes+" minutes");
        }
    }

    private class LoopOnce extends Loop{
        private int remainingLoopingCount = 1;

        @Override
        public boolean shouldContinue() {
            return remainingLoopingCount-- > 0;
        }
    }
}
