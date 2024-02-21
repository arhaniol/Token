package pl.mik.token;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

public class Credentials extends Activity {
    private EditText messageText;
    private EditText phoneToText;
    private EditText phoneFromText;
    private Switch instantTokenSwitch;

    private final SmsData smsData;
    private static Logger l;

    public Credentials() {
        String className = Credentials.class.getName();
        smsData = SmsData.getInstance();
        l = new Logger(className);
        l.i("Credentials class created");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        l.i("onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.credentials);

        messageText = findViewById(R.id.messageText);
        phoneToText = findViewById(R.id.phoneToText);
        phoneFromText = findViewById(R.id.phoneFromText);
        instantTokenSwitch = findViewById(R.id.instantMessage);

        readCredentials();

        Button saveButton = findViewById(R.id.safeButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCredentials();
            }
        });
    }

    private void readCredentials() {
        l.i("Reading credentials");
        if (smsData.readData()) {
            String text = smsData.getSmsTo();
            if (text != null && !text.isEmpty()) {
                phoneToText.setText(text);
                l.i("phoneTo: " + phoneToText);
            }
            text = smsData.getMessage();
            if (text != null && !text.isEmpty()) {
                messageText.setText(text);
                l.i("message: " + messageText);
            }
            text = smsData.getSmsFrom();
            if (text != null && !text.isEmpty()) {
                phoneFromText.setText(text);
                l.i("phoneFrom: " + phoneFromText);
            }
            boolean isChecked = smsData.isInstantToken();
            instantTokenSwitch.setChecked(isChecked);
            l.i("instantToken: " + isChecked);
        } else {
            l.i("Nothing to read");
        }
    }

    private void saveCredentials() {
        l.i("Saving credentials");

        String phoneTo = phoneToText.getText().toString();
        String phoneFrom = phoneFromText.getText().toString();
        String message = messageText.getText().toString();
        boolean instantToken = instantTokenSwitch.isChecked();

        smsData.saveData(phoneTo, phoneFrom, message, instantToken);
        onBackPressed();
    }
}
