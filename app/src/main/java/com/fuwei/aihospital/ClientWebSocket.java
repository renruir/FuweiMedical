package com.fuwei.aihospital;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;


import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.framing.PingFrame;
import org.java_websocket.framing.PongFrame;
import org.java_websocket.handshake.ServerHandshake;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.Time;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ClientWebSocket {
    private static final String TAG = ClientWebSocket.class.getName();
    private Timer pingTimer;
    private WebSocketClient client;

    public void linkSocket(String url, String token, final SocketMsgArrived socketMsg, final Handler mHandler) throws URISyntaxException {

        Map<String, String> httpHeaders = new HashMap<String, String>();
        httpHeaders.put("Authorization", "Bearer " + token);
        Log.i("header:", "Bearer " + token);
        try {
            client = new WebSocketClient(new URI(url), new Draft_6455(), httpHeaders, 20000) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    Log.i("onOpen:", "------连接成功!!!");
                    mHandler.sendEmptyMessage(0);
                    Timer pingTimer = new Timer( );
                    TimerTask task = new TimerTask( ) {
                        public void run () {
                            Log.d(TAG, "send a ping packet");
                            sendPing();
                        }
                    };
                    pingTimer.schedule(task, 1000, 5*60*1000);
                }

                @Override
                public void onMessage(String message) {
                    Log.i("onMessage:", message);
                    Message msg = Message.obtain();
                    msg.what = 1;
                    Bundle bundleData = new Bundle();
                    bundleData.putString("msg", message);
                    msg.setData(bundleData);
                    mHandler.sendMessage(msg);
                    socketMsg.recMsg(message);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    mHandler.sendEmptyMessage(2);
                    Log.i("onClose:", "------连接关闭!!!" + reason);

                }

                @Override
                public void onError(Exception ex) {
                    mHandler.sendEmptyMessage(3);
                    Log.i("onError:", ex.toString());
                }
            };
            // wss需添加
            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[]{};
                }

                @Override
                public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                }
            }};
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            SSLSocketFactory factory = sslContext.getSocketFactory();
            client.setSocket(factory.createSocket());
            Log.i(TAG, "开始connect");
            client.connect();
            Log.i(TAG, "状态：" + client.getReadyState());
        } catch (Exception e) {
            Log.d(TAG, "========Websocket Exception=====");
            client.reconnect();
            e.printStackTrace();
        }

    }

}
