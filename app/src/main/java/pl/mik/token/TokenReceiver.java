package pl.mik.token;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class TokenReceiver extends BroadcastReceiver {

    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    private static Logger l;

    public String msg;
    public String fromNumber;

    public TokenReceiver() {
        String className = TokenReceiver.class.getName();
        l = new Logger(className);
        l.i("TokenReceiver class created");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
//        workingMethod(context, intent);
        method1(context, intent);
    }

    private void method1(Context context, Intent intent) {
        if (context == null || intent == null) {
            return;
        }

        l.i("Intent Received: " + intent.getAction());

        if (intent.getAction().equals(SMS_RECEIVED)) {
            Bundle dataBundle = intent.getExtras();
            if (dataBundle != null) {
                Object[] myPDU = (Object[]) dataBundle.get("pdus");
                final SmsMessage[] messages = new SmsMessage[myPDU.length];

                for (int i = 0; i < messages.length; i++) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        String format = dataBundle.getString("format");
                        messages[i] = SmsMessage.createFromPdu((byte[]) myPDU[i], format);
                    } else {
                        messages[i] = SmsMessage.createFromPdu((byte[]) myPDU[i]);
                    }
                    msg = messages[i].getMessageBody();
                    fromNumber = messages[i].getOriginatingAddress();
                }
//                Toast.makeText(context, "Message: " + msg + "\nNumber: " + fromNumber, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void workingMethod(Context context, Intent intent) {
        if (context == null || intent == null || intent.getAction() == null) {
            return;
        }
        if (!intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            return;
        }

        SmsMessage[] smsMessages;
        int androidVersion = android.os.Build.VERSION.SDK_INT;

        Toast.makeText(context, "Android version: " + androidVersion, Toast.LENGTH_SHORT).show();

        if (androidVersion >= android.os.Build.VERSION_CODES.KITKAT) {
            smsMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
            for (SmsMessage message : smsMessages) {
                Toast.makeText(context, message.getMessageBody(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
