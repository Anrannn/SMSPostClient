package ltd.nanoda.smspostclient.observer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;


import java.io.IOException;
import java.util.Arrays;


import com.google.gson.Gson;

import ltd.nanoda.smspostclient.Counter;
import ltd.nanoda.smspostclient.LogWriteUtil;
import ltd.nanoda.smspostclient.Message;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SmsDatabaseChaneObserver extends ContentObserver {
        // 只检查收件箱
        final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
        public static final Uri MMSSMS_ALL_MESSAGE_URI = Uri.parse("content://sms/inbox");
        public static final String SORT_FIELD_STRING = "_id asc";  // 排序
        public static final String DB_FIELD_ID = "_id";
        public static final String DB_FIELD_ADDRESS = "address";
        public static final String DB_FIELD_PERSON = "person";
        public static final String DB_FIELD_BODY = "body";
        public static final String DB_FIELD_DATE = "date";
        public static final String DB_FIELD_TYPE = "type";
        public static final String DB_FIELD_THREAD_ID = "thread_id";
        public static final String[] ALL_DB_FIELD_NAME = {
                DB_FIELD_ID, DB_FIELD_ADDRESS, DB_FIELD_PERSON, DB_FIELD_BODY,
                DB_FIELD_DATE, DB_FIELD_TYPE, DB_FIELD_THREAD_ID };
        public static int mMessageCount = -1;

        private static final long DELTA_TIME = 60 * 1000;
        private final Context context;
        public SmsDatabaseChaneObserver(Handler handler, Context context) {
            super(handler);
            this.context = context;
        }

        @Override
        public void onChange(boolean selfChange) {
            try {
                Log.e("observer","start");
                onReceiveSms();
            } catch (Exception e) {
                e.printStackTrace();
                LogWriteUtil.write(Arrays.toString(e.getStackTrace()),"DatabaseObserver");
            }

        }


        private void onReceiveSms() throws Exception{
            Cursor cursor = null;
            // 添加异常捕捉
            try {

                cursor = context.getContentResolver().query(MMSSMS_ALL_MESSAGE_URI, ALL_DB_FIELD_NAME,
                        null, null, SORT_FIELD_STRING);
                final int count = cursor.getCount();
                if (count <= mMessageCount) {
                    mMessageCount = count;
                    return;
                }
                // 发现收件箱的短信总数目比之前大就认为是刚接收到新短信---如果出现意外，请神保佑
                // 同时认为id最大的那条记录为刚刚新加入的短信的id---这个大多数是这样的，发现不一样的情况的时候可能也要求神保佑了
                mMessageCount = count;
                if (cursor != null) {
                    cursor.moveToLast();
                    cursor.getColumnIndex(DB_FIELD_ADDRESS);
                    @SuppressLint("Range") final long smsdate = Long.parseLong(cursor.getString(cursor.getColumnIndex(DB_FIELD_DATE)));
                    final long nowdate = System.currentTimeMillis();
                    // 如果当前时间和短信时间间隔超过60秒,认为这条短信无效
                    if (nowdate - smsdate > DELTA_TIME) {
                        return;
                    }
                    @SuppressLint("Range") final String address = cursor.getString(cursor.getColumnIndex(DB_FIELD_ADDRESS));    // 短信号码
                    @SuppressLint("Range") final String body = cursor.getString(cursor.getColumnIndex(DB_FIELD_BODY));          // 在这里获取短信信息
                    @SuppressLint("Range") final String date = cursor.getString(cursor.getColumnIndex(DB_FIELD_DATE));
                    @SuppressLint("Range") final int smsid = cursor.getInt(cursor.getColumnIndex(DB_FIELD_ID));
                    @SuppressLint("Range") final String person = cursor.getString(cursor.getColumnIndex(DB_FIELD_PERSON));
                    if (TextUtils.isEmpty(address) || TextUtils.isEmpty(body)) {
                        return;
                    }
                    // 得到短信号码和内容之后进行相关处理
                    Message message = new Message();
                    message.setPhoneNumber(address);
                    message.setContent(body);
                    message.setDate(date);

                    String json = new Gson().toJson(message);

                    long byteLength = json.getBytes().length;

                    //发送数据至服务器
                    MediaType JSON = MediaType.get("application/json; charset=utf-8");
                    OkHttpClient client = new OkHttpClient();
                    RequestBody body1 = RequestBody.create(JSON,json);
                    Request request = new Request.Builder()
                            .url("https://nanoda.ltd:8899/InsertSmsServlet")
                            .post(body1)
                            .build();

                    //主线程不能访问网络
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Response response = client.newCall(request).execute();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            //记录发送字节数
                            Counter.setByteSize(byteLength,context);
                        }
                    }).start();

                    Log.e("Observer","post");

                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    try {  // 有可能cursor都没有创建成功
                        cursor.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }