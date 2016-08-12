/**
 * Created by qinwenshi on 8/13/16.
 */
public class MailLogger {
    private boolean ls_debugOn;

    private static MailLogger mailLogger = null;


    public MailLogger(boolean ls_debugOn) {
        this.ls_debugOn = ls_debugOn;

    }

    public static MailLogger getLoggerInstance() {
        if (mailLogger == null)
            mailLogger = new MailLogger(false);
        return mailLogger;
    }

    public static void setLoggerInstance(MailLogger instance) {
        mailLogger = instance;
    }

    public void log(String message) {
        if (ls_debugOn) {
            System.out.println(message);
        }
    }
}
