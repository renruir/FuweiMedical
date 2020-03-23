package com.fuwei.aihospital;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.BindingConversion;
import androidx.databinding.DataBindingUtil;

import com.alibaba.fastjson.JSON;
import com.fuwei.aihospital.databinding.CallPanelBinding;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SocketMsgArrived {

    private final static String TAG = MainActivity.class.getName();
    private final String ApiKey = "rO0ABXNyABlqYXZheC5jcnlwdG8uU2VhbGVkT2JqZWN0PjY9psO3VHACAARbAA1lbmNvZGVkUGFyYW1zdAACW0JbABBlbmNyeXB0ZWRDb250ZW50cQB+AAFMAAlwYXJhbXNBbGd0ABJMamF2YS9sYW5nL1N0cmluZztMAAdzZWFsQWxncQB+AAJ4cHB1cgACW0Ks8xf4BghU4AIAAHhwAAAAkJAG2WxF+ENknExl0IISiKLYKsbyPxb3w1ml+HJYL51evI5Bl20i/oqnAGZdPgs9mRBtcndNbZWUHNF6TdfgTPyxkh0wL/iQLji7ovJWkRn+bwSaV+kq/SRHUaqMQy7IOgj9b/x7DxwzxyhNxNBI4KO0A9snNtXgxLOdh4A74X87yE5Z6OJ7Bh7kndBbN5fDdnB0AANBRVM=";
    private String baseUrl;
    private boolean isRegisterSuccess = false;
    private String token;
    private CallPanelBinding callPanelBinding;

    private boolean isAlarmState = false;
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    callPanelBinding.testblock.setText("连接成功");
                    break;
                case 1:
                    callPanelBinding.testblock.setText("收到消息：" + msg.getData().getString("msg"));
                    break;
                case 2:
                    callPanelBinding.testblock.setText("连接关闭");
                    break;
                case 3:
                    callPanelBinding.testblock.setText("连接失败");
                    break;
                case 4:
                    callPanelBinding.realTime.setText(sdf.format(new Date()));
                    break;
                case 5:
                    updateState();
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callPanelBinding = DataBindingUtil.setContentView(this, R.layout.call_panel);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        callPanelBinding.realTime.setText(sdf.format(new Date()));
        updateTimer();
        changeState(isAlarmState);
//        testblock = (TextView) findViewById(R.id.testblock);
        EasyHttp.getInstance().setBaseUrl("https://172.20.20.129:9443");
        register();
        AlarmDao alarmDao = new AlarmDao("#ed1941", "TEST", "TEST", "TEST", "123");
        callPanelBinding.setAlarmDao(alarmDao);
    }

    private void updateTimer() {
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(4);
            }
        };
        timer.schedule(timerTask, 1000, 1000);
    }


    private void changeState(boolean isAlarmState) {
        if (isAlarmState) {
            callPanelBinding.alarmView.setVisibility(View.VISIBLE);
            callPanelBinding.realTime.setVisibility(View.GONE);
        } else {
            callPanelBinding.alarmView.setVisibility(View.GONE);
            callPanelBinding.realTime.setVisibility(View.VISIBLE);
        }
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
                        callPanelBinding.testblock.setText(e.getMessage());
                    }

                    @Override
                    public void onSuccess(String response) {
                        Log.d(TAG, "onSuccess: " + response);
                        if (response != null) {
                            Map<String, Object> info = JSON.parseObject(response);
                            Log.d(TAG, "register result: " + info.get("status"));
                            if ((boolean)info.get("status")) {
                                Log.d(TAG, "注册成功，返回消息为: " + response);
                                Toast.makeText(MainActivity.this, "register success", Toast.LENGTH_SHORT).show();
                                isRegisterSuccess = true;

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
                        callPanelBinding.testblock.setText(e.getMessage());
                    }

                    @Override
                    public void onSuccess(String response) {
                        if (response != null) {
                            Log.d(TAG, "subcribe: " + response);
                            Map<String, Object> info = JSON.parseObject(response);
                            Log.d(TAG, "subcribe result: " + info.get("status"));
                            if ((boolean)info.get("status")) {
                                Toast.makeText(MainActivity.this, "subcribe success", Toast.LENGTH_SHORT).show();
                                try {
                                    ClientWebSocket client = new ClientWebSocket();
                                    client.linkSocket("wss://172.20.20.129:9443/ws/notifications", token, MainActivity.this, mHandler);
                                } catch (URISyntaxException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });
    }


    @Override
    public void recMsg(String msg) {
        Log.d(TAG, "rec socket Msg: " + msg);
        try {
            if (msg != null && !"".equals(msg)) {
                int idx = msg.indexOf("{");
                if (idx >= 0) {
                    msg = msg.substring(idx);
                }
                Log.d(TAG, "processed Msg: " + msg);
                Map<String, Object> socketMsg = JSON.parseObject(msg);
                Log.d(TAG, "location: " + socketMsg.get("location").toString());
                Log.d(TAG, "type: " + socketMsg.get("type").toString());
                Log.d(TAG, "notificationLevel: " + socketMsg.get("notificationLevel").toString());
                Log.d(TAG, "color: " + socketMsg.get("color").toString());
                Log.d(TAG, "callDuration: " + socketMsg.get("callDuration").toString());
                Log.d(TAG, "transition: " + socketMsg.get("transition").toString());

                if(socketMsg.get("transition").equals("START")){
                    changeState(true);
                    Message mMessage = Message.obtain();
                    Bundle bundle = new Bundle();
                    AlarmDao alarmDao = new AlarmDao();
                    alarmDao.setLocation(socketMsg.get("location").toString());
                    alarmDao.setCallType(socketMsg.get("type").toString());
                    alarmDao.setNotificationLevel(socketMsg.get("notificationLevel").toString());
                    alarmDao.setColor(socketMsg.get("color").toString());
                    alarmDao.setCallDuration(socketMsg.get("callDuration").toString());

                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    void updateState(){
        AlarmDao alarmDao = new AlarmDao();
        alarmDao.setLocation(socketMsg.get("location").toString());
        alarmDao.setCallType(socketMsg.get("type").toString());
        alarmDao.setNotificationLevel(socketMsg.get("notificationLevel").toString());
        alarmDao.setColor(socketMsg.get("color").toString());
        alarmDao.setCallDuration(socketMsg.get("callDuration").toString());
        callPanelBinding.setAlarmDao(alarmDao);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        String msg = "{\"id\":\"574272274986583_~_0_~_100.1.1.0_~_172.20.20.129\",\"type\":\"Patient Call\",\"color\":\"#FFFF00\",\"location\":\"Austco.Building 1.Floor 1.Ward 1.Room 1.Bed\",\"startTime\":\"2020-03-17T12:19:07.609-05:00\",\"priority\":2,\"escalation\":0,\"extension\":\"2010\",\"connector\":\"172.20.20.129\",\"eventId\":574272274986583,\"transition\":\"START\",\"endTime\":\"2020-03-17T12:24:51.574-05:00\",\"acknowledgedBy\":\"\",\"acknowledgeMessage\":\"\",\"activated\":false,\"assetId\":\"-1\",\"assetName\":\"\",\"additionalAssets\":[],\"callDuration\":343,\"chime\":\"bing-bong_660-1_550-2_20\",\"cpid\":\"100.1.1.0\",\"generatedBy\":null,\"identification\":\"ERR1\",\"localReset\":true,\"model\":\"Integration\",\"notificationLevel\":0,\"notifiedDevices\":[\"_m667644951919600\"],\"notifiedDeviceIds\":[\"TSNS1\"],\"notifiedGroups\":[\"\"],\"rejectedBy\":[],\"sourceLocation\":\"\",\"state\":\"RESET\",\"locationStructure\":{\"names\":\"SITE.BUILDING.FLOOR.WARD.ROOM.BED\",\"levels\":\"0.3.5.6.9.11\"},\"oldAlarmState\":{\"id\":\"574272274986583_~_0_~_100.1.1.0_~_172.20.20.129\",\"type\":\"Patient Call\",\"color\":\"#FFFF00\",\"location\":\"Austco.Building 1.Floor 1.Ward 1.Room 1.Bed\",\"startTime\":\"2020-03-17T12:19:07.581-05:00\",\"priority\":2,\"escalation\":0,\"extension\":\"2010\",\"connector\":\"172.20.20.129\",\"eventId\":574272274986583,\"transition\":\"RESEND\",\"endTime\":\"\",\"acknowledgedBy\":\"\",\"acknowledgeMessage\":\"\",\"activated\":true,\"assetId\":\"-1\",\"assetName\":\"\",\"additionalAssets\":[],\"callDuration\":344,\"chime\":\"bing-bong_660-1_550-2_20\",\"cpid\":\"100.1.1.0\",\"generatedBy\":null,\"identification\":\"ERR1\",\"localReset\":true,\"model\":\"Integration\",\"notificationLevel\":0,\"notifiedDevices\":[\"_m667644951919600\"],\"notifiedDeviceIds\":[\"TSNS1\"],\"notifiedGroups\":[\"\"],\"rejectedBy\":[],\"sourceLocation\":\"\",\"state\":\"PENDING\",\"locationStructure\":{\"names\":\"SITE.BUILDING.FLOOR.WARD.ROOM.BED\",\"levels\":\"0.3.5.6.9.11\"},\"oldAlarmState\":null,\"alarmDetail\":null},\"alarmDetail\":null}";
        recMsg(msg);
    }


    @BindingConversion
    public static Drawable convertStringToDrawable(String str) {
        Log.e(TAG, "convertStringToDrawable: "+str );
        return new ColorDrawable(Color.parseColor(str));
    }
}
