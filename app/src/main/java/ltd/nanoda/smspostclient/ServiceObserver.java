package ltd.nanoda.smspostclient;

import android.content.Intent;

public class ServiceObserver {
    static boolean isActive = false;

    public static void setIsActive(boolean isActive) {
        ServiceObserver.isActive = isActive;

        Intent intent = new Intent("ltd.nanoda.ServiceActiveModify");
        MyApplication.getContext().sendBroadcast(intent);



    }

    public static String isIsActive() {
        if (isActive){
            return "活跃";
        }else {
            return "停止";
        }
    }
}
