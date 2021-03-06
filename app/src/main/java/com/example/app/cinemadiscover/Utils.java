package com.example.app.cinemadiscover;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by iberretta on 10/30/16.
 */
public class Utils {
    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size = 1024;
        try
        {
            byte[] bytes = new byte[buffer_size];
            for(;;)
            {
                int count = is.read(bytes, 0, buffer_size);
                if(count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }

    public static String[] concatStringsArrays(String[] arrayA, String[] arrayB){
        int lengthA = arrayA.length;
        int lengthB = arrayB.length;

        String[] concatedArrays = new String[lengthA + lengthB];
        System.arraycopy(arrayA, 0, concatedArrays, 0, lengthA);
        System.arraycopy(arrayB, 0, concatedArrays, lengthA, lengthB);
        return  concatedArrays;
    }
}
