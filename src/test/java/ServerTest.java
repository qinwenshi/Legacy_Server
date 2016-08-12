import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

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
