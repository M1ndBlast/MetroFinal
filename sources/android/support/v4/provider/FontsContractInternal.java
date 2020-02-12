package android.support.v4.provider;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.GuardedBy;
import android.support.annotation.RestrictTo;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.res.FontResourcesParserCompat;
import android.support.v4.os.ResultReceiver;
import android.util.Log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class FontsContractInternal {
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public static final String PARCEL_FONT_RESULTS = "font_results";
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public static final int RESULT_CODE_PROVIDER_NOT_FOUND = -1;
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public static final int RESULT_CODE_WRONG_CERTIFICATES = -2;
    private static final String TAG = "FontsContractCompat";
    private static final int THREAD_RENEWAL_THRESHOLD_MS = 10000;
    private static final Comparator<byte[]> sByteArrayComparator = new Comparator<byte[]>() {
        public int compare(byte[] l, byte[] r) {
            if (l.length != r.length) {
                return l.length - r.length;
            }
            for (int i = 0; i < l.length; i++) {
                if (l[i] != r[i]) {
                    return l[i] - r[i];
                }
            }
            return 0;
        }
    };
    private final Context mContext;
    /* access modifiers changed from: private */
    @GuardedBy("mLock")
    public Handler mHandler;
    /* access modifiers changed from: private */
    public final Object mLock = new Object();
    private final PackageManager mPackageManager;
    private final Runnable mReplaceDispatcherThreadRunnable = new Runnable() {
        public void run() {
            synchronized (FontsContractInternal.this.mLock) {
                if (FontsContractInternal.this.mThread != null) {
                    FontsContractInternal.this.mThread.quit();
                    HandlerThread unused = FontsContractInternal.this.mThread = null;
                    Handler unused2 = FontsContractInternal.this.mHandler = null;
                }
            }
        }
    };
    /* access modifiers changed from: private */
    @GuardedBy("mLock")
    public HandlerThread mThread;

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public FontsContractInternal(Context context) {
        this.mContext = context.getApplicationContext();
        this.mPackageManager = this.mContext.getPackageManager();
    }

    @VisibleForTesting
    FontsContractInternal(Context context, PackageManager packageManager) {
        this.mContext = context;
        this.mPackageManager = packageManager;
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public void getFont(final FontRequest request, final ResultReceiver receiver) {
        synchronized (this.mLock) {
            if (this.mHandler == null) {
                this.mThread = new HandlerThread("fonts", 10);
                this.mThread.start();
                this.mHandler = new Handler(this.mThread.getLooper());
            }
            this.mHandler.post(new Runnable() {
                public void run() {
                    ProviderInfo providerInfo = FontsContractInternal.this.getProvider(request, receiver);
                    if (providerInfo != null) {
                        FontsContractInternal.this.getFontFromProvider(request, receiver, providerInfo.authority);
                    }
                }
            });
            this.mHandler.removeCallbacks(this.mReplaceDispatcherThreadRunnable);
            this.mHandler.postDelayed(this.mReplaceDispatcherThreadRunnable, 10000);
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public ProviderInfo getProvider(FontRequest request, ResultReceiver receiver) {
        String providerAuthority = request.getProviderAuthority();
        ProviderInfo info = this.mPackageManager.resolveContentProvider(providerAuthority, 0);
        if (info == null) {
            Log.e(TAG, "Can't find content provider " + providerAuthority);
            receiver.send(-1, (Bundle) null);
            return null;
        } else if (!info.packageName.equals(request.getProviderPackage())) {
            Log.e(TAG, "Found content provider " + providerAuthority + ", but package was not " + request.getProviderPackage());
            receiver.send(-1, (Bundle) null);
            return null;
        } else {
            try {
                List<byte[]> signatures = convertToByteArrayList(this.mPackageManager.getPackageInfo(info.packageName, 64).signatures);
                Collections.sort(signatures, sByteArrayComparator);
                List<List<byte[]>> requestCertificatesList = getCertificates(request);
                for (int i = 0; i < requestCertificatesList.size(); i++) {
                    List<byte[]> requestSignatures = new ArrayList<>(requestCertificatesList.get(i));
                    Collections.sort(requestSignatures, sByteArrayComparator);
                    if (equalsByteArrayList(signatures, requestSignatures)) {
                        return info;
                    }
                }
                Log.e(TAG, "Certificates don't match for given provider " + providerAuthority);
                receiver.send(-2, (Bundle) null);
                return null;
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "Can't find content provider " + providerAuthority, e);
                receiver.send(-1, (Bundle) null);
                return null;
            }
        }
    }

    private List<List<byte[]>> getCertificates(FontRequest request) {
        if (request.getCertificates() != null) {
            return request.getCertificates();
        }
        return FontResourcesParserCompat.readCerts(this.mContext.getResources(), request.getCertificatesArrayResId());
    }

    private boolean equalsByteArrayList(List<byte[]> signatures, List<byte[]> requestSignatures) {
        if (signatures.size() != requestSignatures.size()) {
            return false;
        }
        for (int i = 0; i < signatures.size(); i++) {
            if (!Arrays.equals(signatures.get(i), requestSignatures.get(i))) {
                return false;
            }
        }
        return true;
    }

    private List<byte[]> convertToByteArrayList(Signature[] signatures) {
        List<byte[]> shas = new ArrayList<>();
        for (Signature byteArray : signatures) {
            shas.add(byteArray.toByteArray());
        }
        return shas;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x020c  */
    @android.support.annotation.VisibleForTesting
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void getFontFromProvider(android.support.v4.provider.FontRequest r34, android.support.v4.os.ResultReceiver r35, java.lang.String r36) {
        /*
            r33 = this;
            r1 = r33
            r2 = r35
            r3 = r36
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>()
            android.net.Uri$Builder r5 = new android.net.Uri$Builder
            r5.<init>()
            java.lang.String r6 = "content"
            android.net.Uri$Builder r5 = r5.scheme(r6)
            android.net.Uri$Builder r5 = r5.authority(r3)
            android.net.Uri r5 = r5.build()
            android.net.Uri$Builder r6 = new android.net.Uri$Builder
            r6.<init>()
            java.lang.String r7 = "content"
            android.net.Uri$Builder r6 = r6.scheme(r7)
            android.net.Uri$Builder r6 = r6.authority(r3)
            java.lang.String r7 = "file"
            android.net.Uri$Builder r6 = r6.appendPath(r7)
            android.net.Uri r12 = r6.build()
            r13 = 0
            r14 = r13
            android.content.Context r6 = r1.mContext     // Catch:{ all -> 0x0205 }
            android.content.ContentResolver r6 = r6.getContentResolver()     // Catch:{ all -> 0x0205 }
            r7 = 7
            java.lang.String[] r8 = new java.lang.String[r7]     // Catch:{ all -> 0x0205 }
            java.lang.String r7 = "_id"
            r15 = 0
            r8[r15] = r7     // Catch:{ all -> 0x0205 }
            java.lang.String r7 = "file_id"
            r11 = 1
            r8[r11] = r7     // Catch:{ all -> 0x0205 }
            r7 = 2
            java.lang.String r9 = "font_ttc_index"
            r8[r7] = r9     // Catch:{ all -> 0x0205 }
            r7 = 3
            java.lang.String r9 = "font_variation_settings"
            r8[r7] = r9     // Catch:{ all -> 0x0205 }
            r7 = 4
            java.lang.String r9 = "font_weight"
            r8[r7] = r9     // Catch:{ all -> 0x0205 }
            r7 = 5
            java.lang.String r9 = "font_italic"
            r8[r7] = r9     // Catch:{ all -> 0x0205 }
            r7 = 6
            java.lang.String r9 = "result_code"
            r8[r7] = r9     // Catch:{ all -> 0x0205 }
            java.lang.String r9 = "query = ?"
            java.lang.String[] r10 = new java.lang.String[r11]     // Catch:{ all -> 0x0205 }
            java.lang.String r7 = r34.getQuery()     // Catch:{ all -> 0x0205 }
            r10[r15] = r7     // Catch:{ all -> 0x0205 }
            r16 = 0
            r7 = r5
            r15 = r11
            r11 = r16
            android.database.Cursor r6 = r6.query(r7, r8, r9, r10, r11)     // Catch:{ all -> 0x0205 }
            r14 = r6
            if (r14 == 0) goto L_0x01dc
            int r6 = r14.getCount()     // Catch:{ all -> 0x01d2 }
            if (r6 <= 0) goto L_0x01dc
            java.lang.String r6 = "result_code"
            int r6 = r14.getColumnIndex(r6)     // Catch:{ all -> 0x01d2 }
            java.lang.String r7 = "_id"
            int r7 = r14.getColumnIndex(r7)     // Catch:{ all -> 0x01d2 }
            java.lang.String r8 = "file_id"
            int r8 = r14.getColumnIndex(r8)     // Catch:{ all -> 0x01d2 }
            java.lang.String r9 = "font_ttc_index"
            int r9 = r14.getColumnIndex(r9)     // Catch:{ all -> 0x01d2 }
            java.lang.String r10 = "font_variation_settings"
            int r10 = r14.getColumnIndex(r10)     // Catch:{ all -> 0x01d2 }
            java.lang.String r11 = "font_weight"
            int r11 = r14.getColumnIndex(r11)     // Catch:{ all -> 0x01d2 }
            java.lang.String r15 = "font_italic"
            int r15 = r14.getColumnIndex(r15)     // Catch:{ all -> 0x01d2 }
        L_0x00ac:
            boolean r16 = r14.moveToNext()     // Catch:{ all -> 0x01d2 }
            if (r16 == 0) goto L_0x01dc
            r13 = -1
            if (r6 == r13) goto L_0x00c1
            int r16 = r14.getInt(r6)     // Catch:{ all -> 0x00ba }
            goto L_0x00c3
        L_0x00ba:
            r0 = move-exception
            r1 = r0
            r31 = r5
            r3 = r14
            goto L_0x020a
        L_0x00c1:
            r16 = 0
        L_0x00c3:
            if (r16 == 0) goto L_0x00f9
            if (r16 >= 0) goto L_0x00c9
            r16 = 1
        L_0x00c9:
            r13 = r16
            r17 = 0
        L_0x00cd:
            r18 = r17
            r19 = r6
            int r6 = r4.size()     // Catch:{ all -> 0x00ba }
            r3 = r18
            if (r3 >= r6) goto L_0x00ef
            java.lang.Object r6 = r4.get(r3)     // Catch:{ IOException -> 0x00e7 }
            android.support.v4.graphics.fonts.FontResult r6 = (android.support.v4.graphics.fonts.FontResult) r6     // Catch:{ IOException -> 0x00e7 }
            android.os.ParcelFileDescriptor r6 = r6.getFileDescriptor()     // Catch:{ IOException -> 0x00e7 }
            r6.close()     // Catch:{ IOException -> 0x00e7 }
            goto L_0x00e8
        L_0x00e7:
            r0 = move-exception
        L_0x00e8:
            int r17 = r3 + 1
            r6 = r19
            r3 = r36
            goto L_0x00cd
        L_0x00ef:
            r3 = 0
            r2.send(r13, r3)     // Catch:{ all -> 0x00ba }
            if (r14 == 0) goto L_0x00f8
            r14.close()
        L_0x00f8:
            return
        L_0x00f9:
            r19 = r6
            if (r8 != r13) goto L_0x0122
            long r20 = r14.getLong(r7)     // Catch:{ all -> 0x0118 }
            r22 = r20
            r24 = r14
            r13 = r22
            android.net.Uri r3 = android.content.ContentUris.withAppendedId(r5, r13)     // Catch:{ all -> 0x0110 }
            r6 = r3
            r3 = r24
            goto L_0x012e
        L_0x0110:
            r0 = move-exception
            r1 = r0
            r31 = r5
            r3 = r24
            goto L_0x020a
        L_0x0118:
            r0 = move-exception
            r24 = r14
            r1 = r0
            r31 = r5
            r3 = r24
            goto L_0x020a
        L_0x0122:
            r24 = r14
            r3 = r24
            long r13 = r3.getLong(r8)     // Catch:{ all -> 0x01cb }
            android.net.Uri r6 = android.content.ContentUris.withAppendedId(r12, r13)     // Catch:{ all -> 0x01cb }
        L_0x012e:
            android.content.Context r13 = r1.mContext     // Catch:{ FileNotFoundException -> 0x0199, all -> 0x0195 }
            android.content.ContentResolver r13 = r13.getContentResolver()     // Catch:{ FileNotFoundException -> 0x0199, all -> 0x0195 }
            java.lang.String r14 = "r"
            android.os.ParcelFileDescriptor r13 = r13.openFileDescriptor(r6, r14)     // Catch:{ FileNotFoundException -> 0x0199, all -> 0x0195 }
            if (r13 == 0) goto L_0x0190
            r14 = -1
            if (r9 == r14) goto L_0x0151
            int r14 = r3.getInt(r9)     // Catch:{ FileNotFoundException -> 0x014c, all -> 0x0146 }
            r27 = r14
            goto L_0x0153
        L_0x0146:
            r0 = move-exception
            r1 = r0
            r31 = r5
            goto L_0x020a
        L_0x014c:
            r0 = move-exception
            r1 = r0
            r31 = r5
            goto L_0x019d
        L_0x0151:
            r27 = 0
        L_0x0153:
            r14 = -1
            if (r10 == r14) goto L_0x015d
            java.lang.String r14 = r3.getString(r10)     // Catch:{ FileNotFoundException -> 0x014c, all -> 0x0146 }
            r28 = r14
            goto L_0x015f
        L_0x015d:
            r28 = 0
        L_0x015f:
            r14 = -1
            if (r11 == r14) goto L_0x0177
            if (r15 == r14) goto L_0x0177
            int r14 = r3.getInt(r11)     // Catch:{ FileNotFoundException -> 0x0199, all -> 0x0195 }
            int r1 = r3.getInt(r15)     // Catch:{ FileNotFoundException -> 0x0199, all -> 0x0195 }
            r31 = r5
            r5 = 1
            if (r1 != r5) goto L_0x0173
            r1 = 1
            goto L_0x0174
        L_0x0173:
            r1 = 0
        L_0x0174:
            r30 = r1
            goto L_0x017e
        L_0x0177:
            r31 = r5
            r1 = 400(0x190, float:5.6E-43)
            r14 = r1
            r30 = 0
        L_0x017e:
            android.support.v4.graphics.fonts.FontResult r1 = new android.support.v4.graphics.fonts.FontResult     // Catch:{ FileNotFoundException -> 0x018d }
            r25 = r1
            r26 = r13
            r29 = r14
            r25.<init>(r26, r27, r28, r29, r30)     // Catch:{ FileNotFoundException -> 0x018d }
            r4.add(r1)     // Catch:{ FileNotFoundException -> 0x018d }
            goto L_0x0192
        L_0x018d:
            r0 = move-exception
            r1 = r0
            goto L_0x019d
        L_0x0190:
            r31 = r5
        L_0x0192:
            r14 = r36
            goto L_0x01b5
        L_0x0195:
            r0 = move-exception
            r31 = r5
            goto L_0x01d0
        L_0x0199:
            r0 = move-exception
            r31 = r5
            r1 = r0
        L_0x019d:
            java.lang.String r5 = "FontsContractCompat"
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ all -> 0x01c6 }
            r13.<init>()     // Catch:{ all -> 0x01c6 }
            java.lang.String r14 = "FileNotFoundException raised when interacting with content provider "
            r13.append(r14)     // Catch:{ all -> 0x01c6 }
            r14 = r36
            r13.append(r14)     // Catch:{ all -> 0x01c4 }
            java.lang.String r13 = r13.toString()     // Catch:{ all -> 0x01c4 }
            android.util.Log.e(r5, r13, r1)     // Catch:{ all -> 0x01c4 }
        L_0x01b5:
            r6 = r19
            r5 = r31
            r1 = r33
            r13 = 0
            r32 = r14
            r14 = r3
            r3 = r32
            goto L_0x00ac
        L_0x01c4:
            r0 = move-exception
            goto L_0x01c9
        L_0x01c6:
            r0 = move-exception
            r14 = r36
        L_0x01c9:
            r1 = r0
            goto L_0x020a
        L_0x01cb:
            r0 = move-exception
            r31 = r5
            r14 = r36
        L_0x01d0:
            r1 = r0
            goto L_0x020a
        L_0x01d2:
            r0 = move-exception
            r31 = r5
            r32 = r14
            r14 = r3
            r3 = r32
            r1 = r0
            goto L_0x020a
        L_0x01dc:
            r31 = r5
            r32 = r14
            r14 = r3
            r3 = r32
            if (r3 == 0) goto L_0x01e8
            r3.close()
        L_0x01e8:
            if (r4 == 0) goto L_0x01ff
            boolean r1 = r4.isEmpty()
            if (r1 != 0) goto L_0x01ff
            android.os.Bundle r1 = new android.os.Bundle
            r1.<init>()
            java.lang.String r5 = "font_results"
            r1.putParcelableArrayList(r5, r4)
            r5 = 0
            r2.send(r5, r1)
            return
        L_0x01ff:
            r1 = 1
            r5 = 0
            r2.send(r1, r5)
            return
        L_0x0205:
            r0 = move-exception
            r31 = r5
            r1 = r0
            r3 = r14
        L_0x020a:
            if (r3 == 0) goto L_0x020f
            r3.close()
        L_0x020f:
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.provider.FontsContractInternal.getFontFromProvider(android.support.v4.provider.FontRequest, android.support.v4.os.ResultReceiver, java.lang.String):void");
    }
}
