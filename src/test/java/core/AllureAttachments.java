package core;

import ru.yandex.qatools.allure.annotations.Attachment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by Pyrozhok on 08.12.2015.
 */
public class AllureAttachments {

    @Attachment(value = "{0}", type = "text/html")
    public static byte[] htmlAttachment(String name, String htmlFileName) {
        return mkongExtract(htmlFileName);
    }

    @Attachment(value = "{0}", type = "video/avi")
    public static byte[] videoAttachment(String videoFileName) {
        return mkongExtract(videoFileName);
    }

    @Attachment(value = "{0}", type = "plain/text")
    public static String textAttachment(String name, String text) {
        return text;
    }

    @Attachment(value = "{0}", type = "application/json")
    public static String jsonAttachment(String name, String json) {
        return json;
    }

    private static byte[] mkongExtract(String filePath){
//        FileInputStream fileInputStream;
//        File file = new File(filePath);
//        byte[] bFile = new byte[(int) file.length()];
//        try {
//            //convert file into array of bytes
//            fileInputStream = new FileInputStream(file);
//            fileInputStream.read(bFile);
//            fileInputStream.close();
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//        try {
//             = Files.readAllBytes(Paths.get(filePath));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return bFile;
        byte[] fileAsByteArray = null;
        try {
            fileAsByteArray = Files.readAllBytes(Paths.get(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileAsByteArray;
    }

}