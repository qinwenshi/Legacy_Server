/**
 * Created by qinwenshi on 8/13/16.
 */
public class Sleep {
    private int checkPeriod;

    public Sleep() {
    }

    public void forMinutes(int minutes) throws InterruptedException {
        Thread.sleep(minutes * 1000 * 60);
    }
}
