package ltd.nanoda.smspostclient.recciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import ltd.nanoda.smspostclient.service.SmsListenService;

public class BootReceiver extends BroadcastReceiver {

    @Override

    //启动监听
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.e("reBoot","action");
        Intent intent1 = new Intent(context, SmsListenService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent1);
        }else {
            context.startService(intent1);
        }
        Log.e("reBoot","start");

        throw new UnsupportedOperationException("Not yet implemented");
    }
}