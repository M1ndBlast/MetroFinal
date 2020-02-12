package org.no_ip.payan.metrofinal;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.graphics.drawable.PathInterpolatorCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class IntermediaActivity extends AppCompatActivity {
    private static int TIME_OUT = PathInterpolatorCompat.MAX_NUM_POINTS;
    TextView tv;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_intermedia);
        this.tv = (TextView) findViewById(R.id.textView2);
        if (!getIntent().getExtras().getBoolean("Estado")) {
            this.tv.setText(getString(R.string.error));
        }
        new Handler().postDelayed(new Runnable() {
            public void run() {
                IntermediaActivity.this.startActivity(new Intent(IntermediaActivity.this, MainActivity.class));
                IntermediaActivity.this.finish();
            }
        }, (long) TIME_OUT);
    }
}
