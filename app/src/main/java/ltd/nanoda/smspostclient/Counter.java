package ltd.nanoda.smspostclient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.text.DecimalFormat;

public class Counter {
    static String end;
    //修改字节数
    static public void setByteSize(long size, Context context){
        SharedPreferences sp =  context.getSharedPreferences("counter",Context.MODE_PRIVATE);
        long oldCount =  sp.getLong("count",0);
        long nowCount = oldCount+size;
        sp.edit().putLong("count",nowCount).apply();
        Intent intent = new Intent("ltd.nanoda.CountModify");
        intent.setPackage(context.getPackageName());
        context.sendBroadcast(intent);
    }
    //获得字节数
    static public String getByteSize(Context context){
        double count = context.getSharedPreferences("counter",Context.MODE_PRIVATE).getLong("count",-1);
        if (count<=1024){
            end = " b";
        }else if (count<=10240){
            count = count/1024;
            end = " kb";
        }else if (count<=102400){
            count = count/1024*1024;
            end = " mb";
        }
        DecimalFormat df = new DecimalFormat("#.00");
        return df.format(count)+end;
    }
}
