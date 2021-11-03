package ltd.nanoda.smspostclient;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogWriteUtil {
    static public void write(String log, String type) {
        SimpleDateFormat year = new SimpleDateFormat();// 格式化时间
        year.applyPattern("yyyy-MM-dd");// a为am/pm的标记
        Date date = new Date();// 获取当前时间
        SimpleDateFormat day = new SimpleDateFormat();// 格式化时间
        day.applyPattern("HH:mm:ss");// a为am/pm的标记

        String fileName = type + "_" + year.format(date) + ".txt";
        String path = MyApplication.getContext().getExternalFilesDir("log").getAbsolutePath()+"/";

        File file = new File(path+ fileName);
        try {
            FileWriter fw = new FileWriter(file, true);
            fw.write(day.format(date) + " : " + log + "\n");
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
