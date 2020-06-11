package com.fuwei.aihospital;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ClientWebSocket {
    private static final String TAG = ClientWebSocket.class.getName();
    private Timer pingTimer;
    private WebSocketClient client;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// HH:mm:ss

    public void linkSocket(final Context context, String url, String token, final SocketMsgArrived socketMsg) {

        Map<String, String> httpHeaders = new HashMap<String, String>();
        httpHeaders.put("Authorization", "Bearer " + token);
        Log.i("header:", "Bearer " + token);
        try {
            client = new WebSocketClient(new URI(url), new Draft_6455(), httpHeaders, 20000) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    Log.i(TAG, "onOpen------连接成功!!!");
                    Log.d(TAG, "onOpen time: " + simpleDateFormat.format(new Date()));
//                    mHandler.sendEmptyMessage(0);
                    Timer pingTimer = new Timer();
                    TimerTask task = new TimerTask() {
                        public void run() {
                            Log.d(TAG, "send a ping packet");
                            client.sendPing();
                        }
                    };
                    pingTimer.schedule(task, 1000, 5 *  1000);
                }

                @Override
                public void onWebsocketPing(WebSocket conn, Framedata f) {
                    super.onWebsocketPing(conn, f);
                    Log.d(TAG, "onWebsocketPing: " + f.getOpcode().name());
                }

                @Override
                public void onWebsocketPong(WebSocket conn, Framedata f) {
                    super.onWebsocketPong(conn, f);
                    Log.d(TAG, "onWebsocketPing: " + f.getOpcode().name());
                }

                @Override
                public void onMessage(String message) {
                    Log.i(TAG, "recMsg: " + message);
//                    Message msg = Message.obtain();
//                    msg.what = 1;
//                    Bundle bundleData = new Bundle();
//                    bundleData.putString("msg", message);
//                    msg.setData(bundleData);
//                    mHandler.sendMessage(msg);

//                    Intent intent = new Intent("com.fuweimedical.updatemsg");
//                    Bundle bundleData = new Bundle();
//                    bundleData.putString("msg", message);
//                    intent.putExtra("newmsg", bundleData);
//                    context.sendBroadcast(intent);

                    socketMsg.recMsg(message);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
//                    mHandler.sendEmptyMessage(2);
                    Log.i(TAG, "------socket close， code: " + code + ", reason: " + reason);
                    Log.d(TAG, "onClose time: " + simpleDateFormat.format(new Date()));
                }

                @Override
                public void onError(Exception ex) {
//                    mHandler.sendEmptyMessage(3);
                    Log.i(TAG, "onError: " + ex.toString());
                    Log.d(TAG, "onError time: " + simpleDateFormat.format(new Date()));
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
            Log.i(TAG, " start connect");
            client.connect();
            Log.i(TAG, "state：" + client.getReadyState());
        } catch (Exception e) {
            Log.d(TAG, "========Websocket Exception=====");
            client.reconnect();
            e.printStackTrace();
        }

    }

}
