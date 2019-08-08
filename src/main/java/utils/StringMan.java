//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.zjsonpatch.JsonDiff;
import com.github.javafaker.Faker;
import core.check.Check;
import io.qameta.allure.Step;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringMan {
    public StringMan() {
    }

    public static String makeUniqueEmail(String email, String addedText) {
        if (email.contains("@")) {
            int atIndex = email.indexOf(64);
            Date date = new Date();
            Timestamp ts = new Timestamp(date.getTime());
            String result = email.substring(0, atIndex) + "+" + ts.getTime() + addedText + email.substring(atIndex);
            Logger.info("Unique email: [" + result + "]");
            return result;
        } else {
            return "";
        }
    }

    public static String makeUniqueEmail(String email) {
        if (email.contains("@")) {
            int atIndex = email.indexOf(64);
            Date date = new Date();
            Timestamp ts = new Timestamp(date.getTime());
            String result = email.substring(0, atIndex) + "+" + ts.getTime() + email.substring(atIndex);
            Logger.info("Unique email: [" + result + "]");
            return result;
        } else {
            return "";
        }
    }

    public static String getMD5(String input) {
        Logger.info("Getting MD5 from [" + input + "]");

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);

            String hashtext;
            for (hashtext = number.toString(16); hashtext.length() < 32; hashtext = "0" + hashtext) {
            }

            return hashtext;
        } catch (NoSuchAlgorithmException var5) {
            throw new RuntimeException(var5);
        }
    }

    public static String getJSONsDiff(String first, String second) {
        Logger.info("Getting json diff");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode nodeOld = null;
        JsonNode nodeNew = null;

        try {
            nodeOld = mapper.readTree(first);
            nodeNew = mapper.readTree(second);
        } catch (IOException var6) {
            var6.printStackTrace();
        }

        JsonNode patchFolder = JsonDiff.asJson(nodeNew, nodeOld);
        return patchFolder.toString();
    }

    public static String removeSchemeFromUrl(String url) {
        String regex = ".*?:\\/\\/";
        return url.replaceAll(regex, "");
    }

    public static String getFileNameWithoutExtension(String fileName) {
        String regex = "[^/]*(?=\\.[^.]+($|\\?))";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(fileName);
        return matcher.find() ? matcher.group() : fileName;
    }

    public static String getRandomString(int length) {
        Logger.info("Getting random string of length [" + length + "]");
        char[] chars = "abcdefghijklmnopqrstuvwxyz1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; ++i) {
            sb.append(chars[random.nextInt(chars.length)]);
        }

        Logger.info("Generated random string is: " + sb.toString());
        return sb.toString();
    }

    public static String getRandomStringFrom(char[] allowedChars, int length) {
        Logger.info("Getting random string length [" + length + "]");
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; ++i) {
            sb.append(allowedChars[random.nextInt(allowedChars.length)]);
        }

        return sb.toString();
    }

    public static String deleteWideSpacesFromString(String name) {
        return name.replaceAll("^ +| +$|( )+", "$1").replaceAll("\n", "");
    }

    public static String jsonPrettyPrint(String json) {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine scriptEngine = manager.getEngineByName("JavaScript");
        scriptEngine.put("jsonString", json);

        try {
            scriptEngine.eval("result = JSON.stringify(JSON.parse(jsonString), null, 2)");
        } catch (ScriptException var4) {
            return json;
        }

        return (String) scriptEngine.get("result");
    }

    public static int getMatchNumber(String text, String mask) {
        int count = 0;
        Pattern pattern = Pattern.compile(mask);

        for (Matcher matcher = pattern.matcher(text); matcher.find(); ++count) {
        }

        return count;
    }

    public static double absDiff(double value1, double value2) {
        return Math.abs(value1 - value2);
    }

    @Step
    public static String getResponseBody(HttpResponse resp) throws ParseException, IOException {
        String respBody = EntityUtils.toString(resp.getEntity());
        Logger.info("RESPONSE:\n" + jsonPrettyPrint(respBody));
        return respBody;
    }

    public static String nameValuePairPrettyPrint(List<NameValuePair> valuePair) {
        String requestBody = "\n";

        NameValuePair elem;
        for (Iterator var2 = valuePair.iterator(); var2.hasNext(); requestBody = requestBody + elem + "\n") {
            elem = (NameValuePair) var2.next();
        }

        return requestBody;
    }

    public static String ListStringPrettyPrint(List<String> valuePair) {
        String requestBody = "\n";

        String elem;
        for (Iterator var2 = valuePair.iterator(); var2.hasNext(); requestBody = requestBody + elem + "\n") {
            elem = (String) var2.next();
        }

        return requestBody;
    }

    public static ArrayList deleteEmptyElems(ArrayList array) {
        for (int i = array.size() - 1; i >= 0; --i) {
            if (array.get(i).equals("")) {
                array.remove(i);
            }
        }

        return array;
    }

    public static List<String> trimReactText(String reactText) {
        Logger.info("Trim react text in element");
        List<String> text = new ArrayList();
        Pattern pattern = Pattern.compile("->([\\w\\(.*)].*?)<!");
        Matcher matcher = pattern.matcher(reactText);

        while (matcher.find()) {
            text.add(matcher.group(1));
        }

        return text;
    }

    public static String setAttributeToLowercaseValueInXpath(String attribute, String value) {
        return "translate(@" + attribute + ", 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = '" + value + "'";
    }

    public static String encodeFileToBase64Binary(String fileName) {
        File file = new File(fileName);
        byte[] encoded = new byte[0];

        try {
            encoded = Base64.encodeBase64(FileUtils.readFileToByteArray(file));
        } catch (IOException var4) {
            Check.checkFail(var4.getMessage());
        }

        return new String(encoded, StandardCharsets.US_ASCII);
    }

    public static String getNormalPhoneNumber() {
        Faker faker = new Faker();
        String phoneNumber = faker.phoneNumber().phoneNumber().replaceAll("\\D+", "");
        if (phoneNumber.length() >= 15) {
            phoneNumber = phoneNumber.substring(0, 15);
        }

        return phoneNumber;
    }

    @Step("Make All First Symbols Of String To Upper Case")
    public static String upperCaseAllFirstSymbols(String value) {
        char[] array = value.toCharArray();
        array[0] = Character.toUpperCase(array[0]);

        for (int i = 1; i < array.length; ++i) {
            if (Character.isWhitespace(array[i - 1])) {
                array[i] = Character.toUpperCase(array[i]);
            }
        }

        return new String(array);
    }

    @Step
    public static String removeAllNonNumeric(String value) {
        return value.replaceAll("[^\\d.-]", "");
    }

    @Step
    public static String encodeBase64(String string) {
        String encodedString = "";

        encodedString = new String(Base64.encodeBase64(string.getBytes(StandardCharsets.UTF_8)));

        return encodedString;
    }

    @Step
    public static String decodeBase64(String string) {
        String decodedString = "";

        decodedString = new String(Base64.decodeBase64(string.getBytes()), StandardCharsets.UTF_8);

        return decodedString;
    }

    @Step
    public static String removeQoppa(String text) {
        return text.replaceAll("Qoppa Software", "").replaceAll(" - For Evaluation Only - https://www.*oppa.com", "").trim();
    }

    @Step
    public static String removeSolid(String text) {
        String replacedText = text.replace("**********", "");
        return replacedText.replaceAll("This document was created using a Solid Framework Trial License. \nStrictly for trial purposes, the output has been intentionally modified to 1 pages of the source PDF file.\nThe Solid Documents Team - www.SolidDocuments.com\n  This is not licensed for commercial use or resale.", "");
    }

    public static String getRandomStateAbbreviationOfUSA() {
        List<String> listOfStateAbbreviation = Arrays.asList("AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA", "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD", "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ", "NM", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC", "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY", "AS", "DC", "FM", "GU", "MH", "MP", "PW", "PR", "VI");
        return listOfStateAbbreviation.get((new Random()).nextInt(listOfStateAbbreviation.size() - 1));
    }

    public static String getRandomDoubleInSpecificFormat(Locale locale) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
        DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
        decimalFormat.applyPattern("###,###.##");
        return decimalFormat.format((new Random()).nextDouble() * 1.0E9D);
    }

    public static String getRandomIntegerInSpecificFormat(Locale locale) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
        return numberFormat.format((long) RandomUtils.nextInt());
    }

    public static String getRandomCurrencyAmountInSpecificFormat(Locale locale) {
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
        return numberFormat.format((new Random()).nextDouble() * 1.0E7D);
    }

    public static String insertStingAtRandomPosition(String stringToInsert, int numberOfIncluding, String stringToChange) {
        StringBuilder stringBuilder = new StringBuilder(stringToChange);

        for (int i = 0; i < numberOfIncluding; ++i) {
            int randomPosition = (new Random()).nextInt(stringToChange.length());
            stringBuilder.insert(randomPosition, stringToInsert);
        }

        return stringBuilder.toString();
    }
}
