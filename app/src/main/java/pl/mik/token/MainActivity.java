package pl.mik.token;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    public static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 11;
    public static final int MY_PERMISSIONS_REQUEST_RECEIVED_SMS = 22;
    public static final int MY_PERMISSIONS_REQUEST_SMS = 33;
    private static final String CREDENTIAL_FILE = "token_t-mobile.txt";
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static final int MAX_WAIT = 100;

    private boolean smsAccess = false;
    private TextView tokenView;

    private static Logger l;

    private final SmsData smsData;

    Intent credentialActivity;

    MyCounter silentCounter = new MyCounter(MAX_WAIT * 1800, 1000) {
        @SuppressLint("DefaultLocate")
        @Override
        public void onTick(long millisUntilFinished) {
            super.onTick(millisUntilFinished);
            l.i("Expired after: " + progress);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onFinish() {
            tokenView.setText("Token expired");
        }
    };

    MyCounter counter = new MyCounter(MAX_WAIT * 1000, 1000) {
        @SuppressLint("DefaultLocale")
        @Override
        public void onTick(long millisUntilFinished) {
            super.onTick(millisUntilFinished);
            tokenView.setText(String.format("Waiting: %d", progress));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onFinish() {
            tokenView.setText("Try again");
        }
    };

    TokenReceiver tokenReceiver = new TokenReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            super.onReceive(context, intent);
            if (fromNumber.equals(smsData.getSmsFrom())) {
                counter.cancel();
                tokenView.setText(msg);
                silentCounter.start();
            }
        }
    };

    public MainActivity() {
        String className = MainActivity.class.getName();
        l = new Logger(className);
        l.i("MainActivity class created");
        smsData = SmsData.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(tokenReceiver, new IntentFilter(SMS_RECEIVED));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(tokenReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        l.i("onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        askForSmsAccess();

        ImageButton credentialButton = findViewById(R.id.toolButton);
        Button sendButton = findViewById(R.id.buttonGetToken);
        tokenView = findViewById(R.id.tokenText);
        credentialActivity=new Intent(MainActivity.this, Credentials.class);

        credentialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(credentialActivity);
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSMSMessage();
            }
        });

        smsData.setFilePath(new File(getFilesDir() + CREDENTIAL_FILE));

        l.i("instantToken: " + smsData.isInstantToken());
        if (smsData.isInstantToken()) {
            sendSMSMessage();
        }
    }

    protected void sendSMSMessage() {
        if (smsData != null) {
            if (smsData.dataExist() && smsData.readData()) {
                String phoneTo = smsData.getSmsTo();
                String message = smsData.getMessage();

                if (phoneTo.isEmpty() || message.isEmpty()) {
                    myMessage("Phone numbers or Message can not be empty");
                    return;
                }

                if (smsAccess) {
                    SmsManager smsManager = SmsManager.getDefault();
                    try {
                        l.i("Sending sms");
                        smsManager.sendTextMessage(phoneTo, null, message, null, null);
                        silentCounter.cancel();
                        counter.start();
                    } catch (Exception ex) {
                        myMessage("SMS failed");
                    }
                }
            } else {
                l.i("no data");
                myMessage("Lack of some data");
                startActivity(new Intent(MainActivity.this, Credentials.class));
            }
        } else {
            l.i("smsData is null");
        }

    }

    private void askForSmsAccess() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECEIVE_SMS,
                            Manifest.permission.READ_SMS,
                            Manifest.permission.SEND_SMS},
                    MY_PERMISSIONS_REQUEST_SMS);
        } else {
            smsAccess = true;
            l.i("Permission already granted");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SMS:
            case MY_PERMISSIONS_REQUEST_SEND_SMS:
            case MY_PERMISSIONS_REQUEST_RECEIVED_SMS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    l.i("Permission granted");
                    smsAccess = true;
                } else {
                    l.i("Permission not granted");
                    smsAccess = false;
                }
                break;
            }
            default:
                l.i("Other case");
        }
    }

    private void myMessage(String text) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
        l.i(text);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setSmsAccess(boolean smsAccess) {
        this.smsAccess = smsAccess;
    }

}