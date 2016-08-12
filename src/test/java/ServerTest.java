import org.junit.Assert;
import org.junit.Test;

/**
 * Created by qinwenshi on 8/12/16.
 */
public class ServerTest {
    class SystemExitStub extends SystemExit{
        public int statusCode = -1;
        @Override
        public void invoke(int statusCode) {
            this.statusCode = statusCode;
        }
    }

    @Test
    public void x() throws Exception {
        SystemExitStub systemExitStub = new SystemExitStub();
        Server.setSystemExit(systemExitStub);

        Server.main(new String[]{"a", "b", "c", "d"});;
        Assert.assertEquals(systemExitStub.statusCode, 1);
    }
}
