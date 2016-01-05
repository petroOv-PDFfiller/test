package listeners;

import imap.ImapClient;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

/**
 * Created by Vladyslav on 13.07.2015.
 */
public class ImapListener extends TestListenerAdapter {

    public void onTestFailure(ITestResult result) {
        ImapClient.closeStore();
        System.out.println(result.getTestClass().getName() + " TEST: " + result.getName() + "........FAILED");
    }
}