package listeners;

import core.AllureAttachments;
import core.TestBase;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BrowserLogsListener extends TestListenerAdapter {

    @Override
    public void onTestFailure(ITestResult result) {
        collectLogs(result);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        collectLogs(result);
    }

    private void collectLogs(ITestResult result) {
        WebDriver driver = ((TestBase) result.getInstance()).getDriver();
        if (driver != null) {
            try {
                LogEntries logEntries = driver.manage()
                        .logs()
                        .get(LogType.BROWSER);
                if (logEntries.getAll().size() > 0) {
                    StringBuilder browserLogs = new StringBuilder();
                    for (LogEntry entry : logEntries) {
                        if (entry.getLevel().intValue() > 900) {
                            browserLogs.append(new Date(entry.getTimestamp()))
                                    .append(" ")
                                    .append(entry.getLevel())
                                    .append(" ")
                                    .append(entry.getMessage())
                                    .append("\n");
                        }
                    }
                    if (browserLogs.length() > 0) {
                        AllureAttachments.textAttachment("Console logs", String.valueOf(browserLogs));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                List<LogEntry> requests = driver.manage().logs().get(LogType.PERFORMANCE).getAll();
                Pattern pattern = Pattern.compile("(https:\\/\\/api[.])[^\\,\\\\]*");
                Matcher matcher = pattern.matcher(requests.stream().map(LogEntry::getMessage).collect(Collectors.toList()).toString());

                List<String> constants = new ArrayList<>();
                while (matcher.find()) {
                    constants.add(matcher.group(0));
                }
                StringBuilder browserLogs = new StringBuilder();
                constants.forEach(c -> browserLogs.append(c).append("\n"));
                AllureAttachments.textAttachment("Performance logs", String.valueOf(browserLogs));
            } catch (Exception ignored) {
            }
        }
    }
}
