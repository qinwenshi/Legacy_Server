import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.Vector;

/**
 * Created by qinwenshi on 8/13/16.
 */
public class EmailAddressLoader {
    private String emailListFile;
    private boolean ls_debugOn;

    public EmailAddressLoader(String emailListFile, boolean ls_debugOn) {
        this.emailListFile = emailListFile;
        this.ls_debugOn = ls_debugOn;
    }

    public Vector load() throws IOException, AddressException {
        // Read in email list file into java.util.Vector
        //
        Vector vList = new Vector(10);
        BufferedReader listFile = new BufferedReader(new FileReader(
                emailListFile));
        String line = null;
        while ((line = listFile.readLine()) != null) {
            vList.addElement(new InternetAddress(line));
        }
        listFile.close();
        if (ls_debugOn)
            System.out.println(new Date() + "> " + "Found " + vList.size() + " email ids in list");
        return vList;
    }
}
