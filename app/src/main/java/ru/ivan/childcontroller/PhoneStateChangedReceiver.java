package ru.ivan.childcontroller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class PhoneStateChangedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        MyPhoneStateListener phoneListener = new MyPhoneStateListener();
        TelephonyManager telephony = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    public static class MyPhoneStateListener extends PhoneStateListener {

        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    Utils.sendCallInfo(incomingNumber, "IDLE");
                    break;

                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Utils.sendCallInfo(incomingNumber, "OFFHOOK");
                    break;

                case TelephonyManager.CALL_STATE_RINGING:
                    Utils.sendCallInfo(incomingNumber, "RINGING");
                    break;

                default:
                    break;
            }

        }
    }
}