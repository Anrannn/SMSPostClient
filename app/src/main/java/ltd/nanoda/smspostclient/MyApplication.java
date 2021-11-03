package ltd.nanoda.smspostclient;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
    static private Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

    }

    static public Context getContext() {
        return context;
    }
}
