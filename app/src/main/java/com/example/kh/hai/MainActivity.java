package com.example.kh.hai;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.txtRandomNumber)
    TextView txtRandomNumber;
    private final int GET_COUNT=0;
    private int randomNumber;
    private boolean isBound;
    private Messenger receiveRandomNumber, requestRandomNumber;
    private Intent intentService;

    private class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            randomNumber = 0;
            switch (msg.what){
                case GET_COUNT:
                randomNumber = msg.arg1;
                    Log.i(TAG, "handleMessage: "+randomNumber);
                    txtRandomNumber.setText(""+randomNumber);
                    break;
                default:break;
            }
        }
    }

     ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            requestRandomNumber = new Messenger(service);
            receiveRandomNumber = new Messenger(new MyHandler());
            isBound=true;
            Log.i(TAG, "onServiceConnected: ");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound=false;
            requestRandomNumber = null;
            receiveRandomNumber =null;
            Log.i(TAG, "onServiceDisconnected: ");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        intentService = new Intent();
        intentService.setComponent(new ComponentName("com.example.kh.mot", "com.example.kh.mot.Service.MyService"));
        intentService.setPackage(getPackageName());
    }

    @OnClick(R.id.btnBind)
    public void btnBind(){
        Log.i(TAG, "btnBind: ");
            bindService(intentService, serviceConnection, Service.BIND_AUTO_CREATE);
        if(isBound){
            Log.i(TAG, "btnBind: true");
        }else{
            Log.i(TAG, "btnBind: false");
        }
    }
    @OnClick(R.id.btnUnBind)
    public void btnUnBind(){
        Log.i(TAG, "btnUnBind: ");
        if (isBound){
            unbindService(serviceConnection);
            isBound=false;
        }
    }
    @OnClick(R.id.btnGetRandomNumber)
    public void btnGetRandomNumber(){
        Log.i(TAG, "gerandomnumber: ");
        if (isBound==true){
            Message message  = Message.obtain(null, GET_COUNT);
            message.replyTo = receiveRandomNumber;
            try {
                requestRandomNumber.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }  else{
        Toast.makeText(this, "Service is unbound, can't get the random number", Toast.LENGTH_SHORT).show();
    }
    }
}
