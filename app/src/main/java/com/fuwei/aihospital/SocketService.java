package com.fuwei.aihospital;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.net.UnknownHostException;

public class SocketService extends Service implements SocketMsgArrived {
    private static final String TAG = SocketService.class.getSimpleName();
    private String ip;
    private String token;
    private MsgCallback msgCallback;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        ip = intent.getStringExtra("ip");
        token = intent.getStringExtra("token");
        Log.d(TAG, "ip: " + ip + ",token: " + token);
        return new MsgBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void recMsg(String msg) {
        getMsgCallback().onMsgArrive(msg);
    }

    public interface MsgCallback {
        void onMsgArrive(String msg);
    }

    public void setMsgCallback(MsgCallback callback) {
        this.msgCallback = callback;
    }

    public MsgCallback getMsgCallback() {
        return msgCallback;
    }


    class MsgBinder extends Binder {
        void initSocket() {
            new InitSocketThread().start();
        }

        SocketService getService() {
            return SocketService.this;
        }

    }

    class InitSocketThread extends Thread {
        final String socketIP = ip;
        final String mToken = token;

        @Override
        public void run() {
            super.run();
            try {
                ClientWebSocket client = new ClientWebSocket();
                client.linkSocket(SocketService.this, "wss://" + socketIP + ":9443/ws/notifications", mToken, SocketService.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
