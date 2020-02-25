package com.fuwei.aihospital;

import androidx.appcompat.app.AppCompatActivity;

import android.inputmethodservice.ExtractEditText;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;
import com.zhouyou.http.model.HttpParams;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = MainActivity.class.getName();
    private ExtractEditText ipAdress;
    private TextView responseResult;
    private final String ApiKey = "rO0ABXNyABlqYXZheC5jcnlwdG8uU2VhbGVkT2JqZWN0PjY9psO3VHACAARbAA1lbmNvZGVkUGFyYW1zdAACW0JbABBlbmNyeXB0ZWRDb250ZW50cQB+AAFMAAlwYXJhbXNBbGd0ABJMamF2YS9sYW5nL1N0cmluZztMAAdzZWFsQWxncQB+AAJ4cHB1cgACW0Ks8xf4BghU4AIAAHhwAAAAkJAG2WxF+ENknExl0IISiKLYKsbyPxb3w1ml+HJYL51evI5Bl20i/oqnAGZdPgs9mRBtcndNbZWUHNF6TdfgTPyxkh0wL/iQLji7ovJWkRn+bwSaV+kq/SRHUaqMQy7IOgj9b/x7DxwzxyhNxNBI4KO0A9snNtXgxLOdh4A74X87yE5Z6OJ7Bh7kndBbN5fDdnB0AANBRVM=";
    private String baseUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        EasyHttp.getInstance().setBaseUrl("http://wx.mypraise.cn");
        ipAdress = (ExtractEditText) findViewById(R.id.ip_address);
        responseResult = (TextView) findViewById(R.id.response);
    }

    private void getApiVersion() {
        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();
    }

    private void register() {
        Map<String, String> param =new HashMap<>();
        param.put("ApiKey", ApiKey);
        EasyHttp.put("/ws/register")
                .upJson(JSON.toJSONString(param))
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                        Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(String response) {
                        if (response != null) {
                            Log.d(TAG, "onSuccess: " + response);
                            responseResult.setText(response);
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        if (ipAdress.getText().toString().isEmpty()) {
            Toast.makeText(this, "IP地址为空", Toast.LENGTH_LONG).show();
            return;
        } else {
            baseUrl = ipAdress.getText().toString();
            Log.d(TAG, "baseUrl: " + baseUrl);
            EasyHttp.getInstance().setBaseUrl(baseUrl);
        }
        int viewId = view.getId();
        if (viewId == R.id.register) {
            Log.d(TAG, "onClick: register");
            register();
        } else if (viewId == R.id.get_api_version) {
            Log.d(TAG, "onClick: get api version");
            EasyHttp.get("/ws/version")
                    .execute(new SimpleCallBack<String>() {
                        @Override
                        public void onError(ApiException e) {
                            Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSuccess(String response) {
                            if (response != null) {
                                Log.d(TAG, "onSuccess: " + response);
                                responseResult.setText(response);
                            }
                        }
                    });
        } else if(viewId == R.id.subscribe_alarm){

        }
    }
}
