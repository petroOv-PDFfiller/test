package listeners;

import imap.ImapClient;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import utils.Logger;

/**
 * Created by Vladyslav on 13.07.2015.
 */
public class ImapListener extends TestListenerAdapter {

    public void onTestFailure(ITestResult result) {
        if (ImapClient.store != null) {
            ImapClient.closeStore();
        }
        Logger.info(result.getTestClass().getName() + " TEST: " + result.getName() + "........FAILED");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        if (ImapClient.store != null) {
            ImapClient.closeStore();
        }
        Logger.info(result.getTestClass().getName() + " TEST: " + result.getName() + "........SUCCESS");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        if (ImapClient.store != null) {
            ImapClient.closeStore();
        }
        Logger.warning(result.getTestClass().getName() + " TEST: " + result.getName() + "........SKIPPED");
    }

    @Override
    public void onFinish(ITestContext result) {
        if (ImapClient.store != null) {
            ImapClient.closeStore();
        }
        Logger.info(result.getClass().getName() + " TEST: " + result.getName() +
                "\n------------------------ TEST FINISH ------------------------");
    }
}