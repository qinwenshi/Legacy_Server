import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

/**
 * Created by qinwenshi on 8/12/16.
 */
public class ServerTest {

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
}
