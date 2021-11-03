package ltd.nanoda.smspostclient.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import ltd.nanoda.smspostclient.LogWriteUtil;

public class BaseActivity extends AppCompatActivity {


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogWriteUtil.write(this.getClass().getSimpleName() + " is onDestroy", "AppLife");
        LogWriteUtil.write("____________Activity End____________", "ActivityLife");


    }

    @Override
    protected void onResume() {
        super.onResume();
        LogWriteUtil.write(this.getClass().getSimpleName() + " is onResume", "AppLife");

    }

    @Override
    protected void onPause() {
        super.onPause();
        LogWriteUtil.write(this.getClass().getSimpleName() + " is onPause", "AppLife");

    }

    @Override
    protected void onStop() {
        super.onStop();
        LogWriteUtil.write(this.getClass().getSimpleName() + " is onStop", "AppLife");

    }

}
