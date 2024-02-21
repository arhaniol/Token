package pl.mik.token;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import androidx.core.app.ActivityCompat;

import static pl.mik.token.MainActivity.MY_PERMISSIONS_REQUEST_SEND_SMS;

public class ExplainWindow extends Activity {
    private Context baseContext;
    private boolean allow = false;

    public ExplainWindow() {

    }

    public ExplainWindow(Context baseContext) {
        this.baseContext = baseContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.explain_window);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * 1), (int) (height * 1));

        Button allowButton = findViewById(R.id.buttonAllow);
        allowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allow = true;
                onBackPressed();

            }
        });


//        WindowMetrics metrics = windowManager.getCurrentWindowMetrics();
//        // Gets all excluding insets
//        final WindowInsets windowInsets = metrics.getWindowInsets();
//        Insets insets = windowInsets.getInsetsIgnoreVisibility(WindowInsets.Type.navigationBars()
//                | WindowInsets.Type.displayCutout());
//
//        int insetsWidth = insets.right + insets.left;
//        int insetsHeight = insets.top + insets.bottom;
//
//        // Legacy size that Display#getSize reports
//        final Rect bounds = metrics.getBounds();
//        final Size legacySize = new Size(bounds.width() - insetsWidth,
//                bounds.height() - insetsHeight);    }
    }

    public boolean isAllow() {
        return allow;
    }
}
