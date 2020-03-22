package com.fuwei.aihospital;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.BindingConversion;
import androidx.databinding.DataBindingUtil;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.fuwei.aihospital.databinding.CallPanelBinding;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SocketMsgArrived {

    private final static String TAG = MainActivity.class.getName();
    private final String ApiKey = "rO0ABXNyABlqYXZheC5jcnlwdG8uU2VhbGVkT2JqZWN0PjY9psO3VHACAARbAA1lbmNvZGVkUGFyYW1zdAACW0JbABBlbmNyeXB0ZWRDb250ZW50cQB+AAFMAAlwYXJhbXNBbGd0ABJMamF2YS9sYW5nL1N0cmluZztMAAdzZWFsQWxncQB+AAJ4cHB1cgACW0Ks8xf4BghU4AIAAHhwAAAAkJAG2WxF+ENknExl0IISiKLYKsbyPxb3w1ml+HJYL51evI5Bl20i/oqnAGZdPgs9mRBtcndNbZWUHNF6TdfgTPyxkh0wL/iQLji7ovJWkRn+bwSaV+kq/SRHUaqMQy7IOgj9b/x7DxwzxyhNxNBI4KO0A9snNtXgxLOdh4A74X87yE5Z6OJ7Bh7kndBbN5fDdnB0AANBRVM=";
    private String baseUrl;
    private boolean isRegisterSuccess = false;
    private String token;
    private CallPanelBinding callPanelBinding;


    private LinearLayout colorBlock;
    private TextView callPosition;
    private TextView callType;
    private TextView testblock;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    testblock.setText("连接成功");
                    break;
                case 1:
                    testblock.setText("收到消息："+msg.getData().getString("msg"));
                    break;
                case 2:
                    testblock.setText("连接关闭");
                    break;
                case 3:
                    testblock.setText("连接失败");
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.call_panel);
        callPanelBinding = DataBindingUtil.setContentView(this, R.layout.call_panel);
        testblock = (TextView) findViewById(R.id.testblock);
        EasyHttp.getInstance().setBaseUrl("https://172.20.20.129:9443");
        register();
        AlarmDao alarmDao = new AlarmDao("#ed1941", "紧急", "十五区3床", "紧急", "123");
        callPanelBinding.setAlarmDao(alarmDao);
    }


    private void register() {
        Log.d(TAG, "<----------register---------->");
        Map<String, String> param = new HashMap<>();
        param.put("ApiKey", ApiKey);
        String jsonparam = "{\"apiKey\" : \"rO0ABXNyABlqYXZheC5jcnlwdG8uU2VhbGVkT2JqZWN0PjY9psO3VHACAARbAA1lbmNvZGVkUGFyYW1zdAACW0JbABBlbmNyeXB0ZWRDb250ZW50cQB+AAFMAAlwYXJhbXNBbGd0ABJMamF2YS9sYW5nL1N0cmluZztMAAdzZWFsQWxncQB+AAJ4cHB1cgACW0Ks8xf4BghU4AIAAHhwAAAAkJAG2WxF+ENknExl0IISiKLYKsbyPxb3w1ml+HJYL51evI5Bl20i/oqnAGZdPgs9mRBtcndNbZWUHNF6TdfgTPyxkh0wL/iQLji7ovJWkRn+bwSaV+kq/SRHUaqMQy7IOgj9b/x7DxwzxyhNxNBI4KO0A9snNtXgxLOdh4A74X87yE5Z6OJ7Bh7kndBbN5fDdnB0AANBRVM=\"" +
                ",\"registrations\": [{\"featureName\": \"WS-SIGNAL-EVENT-SUBSCRIBE\"}," +
                "{\"featureName\": \"WS-ALARM-SUBSCRIBE\"},{\"featureName\": \"WS-ALARM-PUBLISH\"}]," +
                "\"clientId\" : \"b4:2e:99:86:6e:b6\"}";
        EasyHttp.getInstance().setCertificates();
        EasyHttp.post("/ws/register")
                .upJson(jsonparam)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                        Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "注册错误！==: " + e.getMessage());
                        isRegisterSuccess = false;
                        testblock.setText(e.getMessage());
                    }

                    @Override
                    public void onSuccess(String response) {
                        if (response != null) {
                            Log.d(TAG, "注册成功，返回消息为: " + response);
                            Toast.makeText(MainActivity.this, "register success", Toast.LENGTH_SHORT).show();
                            isRegisterSuccess = true;
                            if (response != null) {
                                Map<String, Object> info = JSON.parseObject(response);
                                token = info.get("token").toString();
                                Log.d(TAG, "注册成功的token: " + token);
                                subcribe(token);
                            }
                        }
                    }
                });
    }

    private void subcribe(final String token) {
        Log.d(TAG, "<----------subcribe---------->");
        EasyHttp.getInstance().setCertificates();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), "{}");
        EasyHttp.post("/ws/alarms/subscribe")
                .requestBody(body)
                .headers("Authorization", "Bearer " + token)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                        Log.e(TAG, "订阅错误！==: " + e.getMessage());
                        Toast.makeText(MainActivity.this, "subcribe error", Toast.LENGTH_SHORT).show();
                        testblock.setText(e.getMessage());
                    }

                    @Override
                    public void onSuccess(String response) {
                        if (response != null) {
                            Log.d(TAG, "subcribe: " + response);
                            Toast.makeText(MainActivity.this, "subcribe success", Toast.LENGTH_SHORT).show();
                            try {
                                ClientWebSocket client = new ClientWebSocket();
                                client.linkSocket("wss://172.20.20.129:9443/ws/notifications", token, MainActivity.this, mHandler);
                            } catch (URISyntaxException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }


    @Override
    public void recMsg(String msg) {
        Log.d(TAG, "rec socket Msg: " + msg);
        if (msg != null && "".equals(msg)) {
            Map<String, Object> socketMsg = JSON.parseObject(msg);
            AlarmDao alarmDao = new AlarmDao();
            alarmDao.setLocation(socketMsg.get("location").toString());
            alarmDao.setCallType(socketMsg.get("type").toString());
            alarmDao.setNotificationLevel(socketMsg.get("notificationLevel").toString());
            alarmDao.setColor(socketMsg.get("color").toString());
            alarmDao.setCallDuration(socketMsg.get("callDuration").toString());
            callPanelBinding.setAlarmDao(alarmDao);
        }
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
    }


    @BindingConversion
    public static Drawable convertStringToDrawable(String str) {
        return new ColorDrawable(Color.parseColor(str));
    }
}
