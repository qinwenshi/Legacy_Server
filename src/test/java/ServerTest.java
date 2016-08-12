import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

/**
 * Created by qinwenshi on 8/12/16.
 */
public class ServerTest {
    class SystemExitStub extends SystemExit{
        public int statusCode = -1;
        @Override
        public void invoke(int statusCode) throws Exception {
            this.statusCode = statusCode;
            throw new Exception("System Exit with Status " + statusCode);
        }

    }

    @Test
    public void x()  {
        SystemExitStub systemExitStub = new SystemExitStub();
        Server.setSystemExit(systemExitStub);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();

        System.setErr(new PrintStream(outContent));
        System.setOut(new PrintStream(outContent));

        try {
            Server.main(new String[]{"a", "b", "c", "d"});
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        assertEquals(outContent.toString(),
                "Usage: java Server SMTPHost POP3Host user password EmailListFile CheckPeriodFromName\n" +
                "System Exit with Status 1\n");

        assertEquals(systemExitStub.statusCode, 1);
    }
}
