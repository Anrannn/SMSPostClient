package ltd.nanoda.smspostclient.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import ltd.nanoda.smspostclient.Counter;
import ltd.nanoda.smspostclient.LogWriteUtil;
import ltd.nanoda.smspostclient.R;
import ltd.nanoda.smspostclient.ServiceObserver;
import ltd.nanoda.smspostclient.service.SmsListenService;

public class MainActivity extends BaseActivity {
    TextView byteSize;
    TextView lastPostTime;
    CounterModifyReceiver receiver;
    TextView isActive;
    ServiceActiveModifyReceiver samr;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, SmsListenService.class);;
        requestPermission();
        isActive = findViewById(R.id.isActive);
        isActive.setText(ServiceObserver.isIsActive());

        byteSize = findViewById(R.id.byteSize);
        lastPostTime = findViewById(R.id.lastPostTime);
        String count = Counter.getByteSize(this);
        if (!(count.contains("-1"))){
            byteSize.setText(count);
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("ltd.nanoda.CountModify");
        receiver = new CounterModifyReceiver();
        registerReceiver(receiver,intentFilter);

        IntentFilter intentFilter1 = new IntentFilter();
        intentFilter1.addAction("ltd.nanoda.ServiceActiveModify");
        samr = new ServiceActiveModifyReceiver();
        registerReceiver(samr,intentFilter1);






        Button start = findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (requestPermission()) {
                    if (requestPermission()){
                        startService(intent);
                        isActive.setText(ServiceObserver.isIsActive());
                    }
                }
            }
        });



        Log.e("activity","start");


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        unregisterReceiver(samr);
    }

    private boolean requestPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String []{Manifest.permission.READ_SMS},1);

        }
        return true;
    }

    //监听字节数
    class CounterModifyReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            byteSize.setText(Counter.getByteSize(context));
        }
    }
    class ServiceActiveModifyReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            isActive.setText(ServiceObserver.isIsActive());
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                Intent intent = new Intent(this, SmsListenService.class);
                startService(intent);
            } else {
                Log.e("PermissionError", "未授权");
            }
        }
    }


}