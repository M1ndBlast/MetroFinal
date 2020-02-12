package org.no_ip.payan.metrofinal;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import java.nio.ByteBuffer;
import java.util.Locale;
import org.no_ip.payan.metrofinal.clases.MCReader;
import org.no_ip.payan.metrofinal.clases.util;

public class RecargaActivity extends AppCompatActivity {
    /* access modifiers changed from: package-private */
    public void setSaldo(double cd, int escribirEn) {
        double d = cd;
        new util();
        String vb = util.byte2HexString(ByteBuffer.allocate(4).putInt(Integer.reverseBytes((int) d)).array());
        String vbInverted = util.byte2HexString(ByteBuffer.allocate(4).putInt(Integer.reverseBytes(((int) d) ^ -1)).array());
        String addrInverted = Integer.toHexString(Integer.parseInt("02", 16) ^ -1).toUpperCase(Locale.getDefault()).substring(6, 8);
        String salida = vb + vbInverted + vb + "02" + addrInverted + "02" + addrInverted;
        Tag t = util.getTag();
        if (t == null) {
            Toast.makeText(this, "NO HAY UNA TARJETA", 1);
            return;
        }
        MCReader algo = MCReader.get(t);
        algo.writeBlock(2, escribirEn, util.hexStringToByteArray(salida), util.hexStringToByteArray("B3F3A0C5A1CC"), true);
        algo.close();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_recarga);
        boolean estado = true;
        try {
            setSaldo(12000.0d, 0);
        } catch (Exception e) {
            estado = false;
        }
        try {
            setSaldo(12000.0d, 1);
        } catch (Exception e2) {
            estado = false;
        }
        Intent i = new Intent(this, IntermediaActivity.class);
        i.putExtra("Estado", estado);
        startActivity(i);
        finish();
    }
}
