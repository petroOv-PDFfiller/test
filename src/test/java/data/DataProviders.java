package data;

import org.testng.annotations.DataProvider;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Vladyslav on 08.07.2015.
 */
public class DataProviders {

    /* Combination of data providers */
    public static Object[][] combine(Object[][] a1, Object[][] a2){
        List<Object[]> objectCodesList = new LinkedList<Object[]>();
        for(Object[] o : a1){
            for(Object[] o2 : a2){
                objectCodesList.add(concatAll(o, o2));
            }
        }
        return objectCodesList.toArray(new Object[0][0]);
    }

    @SafeVarargs
    public static <T> T[] concatAll(T[] first, T[]... rest) {
        //calculate the total length of the final object array after the concat
        int totalLength = first.length;
        for (T[] array : rest) {
            totalLength += array.length;
        }
        //copy the first array to result array and then copy each array completely to result
        T[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (T[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

    @DataProvider
    private Object[][] dataProvider_sample() {
        return new Object[][] {
                { "1" },
                { "2" },
                { "3" },
        };
    }
}
