package com.fuwei.aihospital;

import android.util.Log;

import com.alibaba.fastjson.JSONObject;


import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
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
import java.util.HashMap;
import java.util.Map;

public class ClientWebSocket {
    private static final String TAG = ClientWebSocket.class.getName();

    public void linkSocket(String url, String token, final SocketMsgArrived socketMsg) throws URISyntaxException {

        Map<String, String> httpHeaders = new HashMap<String, String>();
        httpHeaders.put("Authorization", "Bearer " + token);
        Log.i("header:", "Bearer " + token);
        try {
            WebSocketClient client = new WebSocketClient(new URI(url), new Draft_6455(), httpHeaders, 20000) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    Log.i("onOpen:", "------连接成功!!!");
                }

                @Override
                public void onMessage(String message) {
                    Log.i("onMessage:", message);
                    socketMsg.recMsg(message);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Log.i("onClose:", "------连接关闭!!!" + reason);
                }

                @Override
                public void onError(Exception ex) {
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
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
