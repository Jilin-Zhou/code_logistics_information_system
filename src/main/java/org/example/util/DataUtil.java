package org.example.util;

import java.text.SimpleDateFormat;
import java.util.Date;


public class DataUtil {
//    static SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    static SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
    public static String getTime(Date dt){
        return fmt.format(dt);
    }
}
