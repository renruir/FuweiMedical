package com.fuwei.aihospital;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
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
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SocketMsgArrived {

    private final static String TAG = MainActivity.class.getName();
    private final String ApiKey = "rO0ABXNyABlqYXZheC5jcnlwdG8uU2VhbGVkT2JqZWN0PjY9psO3VHACAARbAA1lbmNvZGVkUGFyYW1zdAACW0JbABBlbmNyeXB0ZWRDb250ZW50cQB+AAFMAAlwYXJhbXNBbGd0ABJMamF2YS9sYW5nL1N0cmluZztMAAdzZWFsQWxncQB+AAJ4cHB1cgACW0Ks8xf4BghU4AIAAHhwAAAAkJAG2WxF+ENknExl0IISiKLYKsbyPxb3w1ml+HJYL51evI5Bl20i/oqnAGZdPgs9mRBtcndNbZWUHNF6TdfgTPyxkh0wL/iQLji7ovJWkRn+bwSaV+kq/SRHUaqMQy7IOgj9b/x7DxwzxyhNxNBI4KO0A9snNtXgxLOdh4A74X87yE5Z6OJ7Bh7kndBbN5fDdnB0AANBRVM=";
    private String baseUrl;
    private boolean isRegisterSuccess = false;
    private String token;


    private LinearLayout colorBlock;
    private TextView callPosition;
    private TextView callType;
    private TextView callDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.call_panel);
        CallPanelBinding activityMain2Binding = DataBindingUtil.setContentView(this, R.layout.call_panel);
        EasyHttp.getInstance().setBaseUrl("https://172.20.20.129:9443");

    }


    private void register() {
        Map<String, String> param = new HashMap<>();
        param.put("ApiKey", ApiKey);
        String jsonparam = "{\"apiKey\" : \"rO0ABXNyABlqYXZheC5jcnlwdG8uU2VhbGVkT2JqZWN0PjY9psO3VHACAARbAA1lbmNvZGVkUGFyYW1zdAACW0JbABBlbmNyeXB0ZWRDb250ZW50cQB+AAFMAAlwYXJhbXNBbGd0ABJMamF2YS9sYW5nL1N0cmluZztMAAdzZWFsQWxncQB+AAJ4cHB1cgACW0Ks8xf4BghU4AIAAHhwAAAAkJAG2WxF+ENknExl0IISiKLYKsbyPxb3w1ml+HJYL51evI5Bl20i/oqnAGZdPgs9mRBtcndNbZWUHNF6TdfgTPyxkh0wL/iQLji7ovJWkRn+bwSaV+kq/SRHUaqMQy7IOgj9b/x7DxwzxyhNxNBI4KO0A9snNtXgxLOdh4A74X87yE5Z6OJ7Bh7kndBbN5fDdnB0AANBRVM=\"" +
                ",\"registrations\": [{\"featureName\": \"WS-SIGNAL-EVENT-SUBSCRIBE\"}," +
                "{\"featureName\": \"WS-ALARM-SUBSCRIBE\"},{\"featureName\": \"WS-ALARM-PUBLISH\"}]," +
                "\"clientId\" : \"b4:2e:99:86:6e:b6\"}";
        EasyHttp.put("/ws/register")
                .upJson(jsonparam)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                        Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
                        isRegisterSuccess = false;
                    }

                    @Override
                    public void onSuccess(String response) {
                        if (response != null) {
                            Log.d(TAG, "onSuccess: " + response);
                            isRegisterSuccess = true;
                            if (response != null) {
                                Map<String, Object> info = JSON.parseObject(response);
                                token = info.get("token").toString();
                                Log.d(TAG, "onSuccess: " + token);
                                subcribe(token);
                            }
                        }
                    }
                });
    }

    private void subcribe(final String token) {
        EasyHttp.get("/ws/alarms/subscribe")
                .headers("Authorization", "Bearer " + token)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                        Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(String response) {
                        if (response != null) {
                            Log.d(TAG, "subcribe: " + response);

                            try {
                                ClientWebSocket client = new ClientWebSocket();
                                client.linkSocket("wss://172.20.20.129:9443/ws/notifications", token, MainActivity.this);
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
        if(msg != null && "".equals(msg)){
            Map<String, Object> socketMsg = JSON.parseObject(msg);

        }
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
    }


}
