package org.no_ip.payan.metrofinal;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import java.io.PrintStream;
import org.no_ip.payan.metrofinal.clases.MCReader;
import org.no_ip.payan.metrofinal.clases.util;

public class MainActivity extends AppCompatActivity {
    MCReader algo;
    private Intent mOldIntent = null;
    private boolean mResume = true;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("Hola");
        setContentView((int) R.layout.activity_main);
        util.setNfcAdapter(NfcAdapter.getDefaultAdapter(this));
        if (util.getNfcAdapter() == null) {
            this.mResume = false;
        }
    }

    public void onPause() {
        super.onPause();
        util.disableNfcForegroundDispatch(this);
    }

    private void checkNfc() {
        System.out.println("lectura");
        if (util.getNfcAdapter() == null || util.getNfcAdapter().isEnabled()) {
            System.out.println("xd");
            if (this.mOldIntent != getIntent()) {
                if (util.treatAsNewTag(getIntent(), this) != -1) {
                }
                this.mOldIntent = getIntent();
            }
            util.enableNfcForegroundDispatch(this);
            return;
        }
        System.out.println("lol");
    }

    public void cambia() {
        PrintStream printStream = System.out;
        printStream.println("TARJETA " + util.byte2HexString(util.getTag().getId()));
        startActivity(new Intent(this, RecargaActivity.class));
        finish();
    }

    public void onNewIntent(Intent intent) {
        int treatAsNewTag = util.treatAsNewTag(intent, this);
        this.algo = MCReader.get(util.getTag());
        cambia();
    }

    public void onResume() {
        super.onResume();
        checkNfc();
    }
}
