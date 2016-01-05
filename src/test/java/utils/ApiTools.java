package utils;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by Vladyslav on 02.11.2015.
 */
public class ApiTools {

    private static String readAll(Reader rd) {
        StringBuilder sb = new StringBuilder();
        int cp;
        try {
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private String executePostMethod(String url) {
        HttpClient httpClient = new HttpClient();
        PostMethod postMethod = new PostMethod(url);
        int tries = 0;
        boolean successFlag = false;
        while(tries < 2 && successFlag == false) {
            try {
                Logger.info("..Trying to execute post method");
                httpClient.executeMethod(postMethod);
                Logger.info("Success");
                successFlag = true;
            } catch (IOException e) {
                TimeMan.sleep(100L);
                tries++;
            }
        }

        String response = null;
        tries = 0;
        successFlag = false;
        while (tries < 2 && successFlag == false) {
            try {
                Logger.info("..Trying to get a response from post method");
                response = postMethod.getResponseBodyAsString();
                Logger.info("Success");
                successFlag = true;
            } catch (IOException e) {
                TimeMan.sleep(100L);
                tries++;
            }
        }
        return response;
    }

    private String getJsonFromGetMethod(String url) {
        InputStream is = null;
        int tries = 0;
        boolean successFlag = false;
        while(tries < 2 && successFlag == false) {
            try {
                Logger.info("..Trying to open input stream");
                is = new URL(url).openStream();
                Logger.info("Success");
            } catch (Exception e) {
                TimeMan.sleep(100L);
                tries++;
            }
        }
        BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
        String jsonText = readAll(rd);
        return jsonText;
    }
}
