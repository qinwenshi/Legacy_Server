import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

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

    @Rule
    public ExpectedException thrown= ExpectedException.none();


    @Test
    public void x() throws Exception {
        SystemExitStub systemExitStub = new SystemExitStub();
        Server.setSystemExit(systemExitStub);
        thrown.expect(Exception.class);
        thrown.expectMessage("System Exit with Status 1");

        Server.main(new String[]{"a", "b", "c", "d"});;
        Assert.assertEquals(systemExitStub.statusCode, 1);
    }
}
