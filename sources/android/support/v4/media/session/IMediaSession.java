package android.support.v4.media.session;

import android.app.PendingIntent;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.RatingCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import java.util.List;

public interface IMediaSession extends IInterface {
    void addQueueItem(MediaDescriptionCompat mediaDescriptionCompat) throws RemoteException;

    void addQueueItemAt(MediaDescriptionCompat mediaDescriptionCompat, int i) throws RemoteException;

    void adjustVolume(int i, int i2, String str) throws RemoteException;

    void fastForward() throws RemoteException;

    Bundle getExtras() throws RemoteException;

    long getFlags() throws RemoteException;

    PendingIntent getLaunchPendingIntent() throws RemoteException;

    MediaMetadataCompat getMetadata() throws RemoteException;

    String getPackageName() throws RemoteException;

    PlaybackStateCompat getPlaybackState() throws RemoteException;

    List<MediaSessionCompat.QueueItem> getQueue() throws RemoteException;

    CharSequence getQueueTitle() throws RemoteException;

    int getRatingType() throws RemoteException;

    int getRepeatMode() throws RemoteException;

    String getTag() throws RemoteException;

    ParcelableVolumeInfo getVolumeAttributes() throws RemoteException;

    boolean isCaptioningEnabled() throws RemoteException;

    boolean isShuffleModeEnabled() throws RemoteException;

    boolean isTransportControlEnabled() throws RemoteException;

    void next() throws RemoteException;

    void pause() throws RemoteException;

    void play() throws RemoteException;

    void playFromMediaId(String str, Bundle bundle) throws RemoteException;

    void playFromSearch(String str, Bundle bundle) throws RemoteException;

    void playFromUri(Uri uri, Bundle bundle) throws RemoteException;

    void prepare() throws RemoteException;

    void prepareFromMediaId(String str, Bundle bundle) throws RemoteException;

    void prepareFromSearch(String str, Bundle bundle) throws RemoteException;

    void prepareFromUri(Uri uri, Bundle bundle) throws RemoteException;

    void previous() throws RemoteException;

    void rate(RatingCompat ratingCompat) throws RemoteException;

    void registerCallbackListener(IMediaControllerCallback iMediaControllerCallback) throws RemoteException;

    void removeQueueItem(MediaDescriptionCompat mediaDescriptionCompat) throws RemoteException;

    void removeQueueItemAt(int i) throws RemoteException;

    void rewind() throws RemoteException;

    void seekTo(long j) throws RemoteException;

    void sendCommand(String str, Bundle bundle, MediaSessionCompat.ResultReceiverWrapper resultReceiverWrapper) throws RemoteException;

    void sendCustomAction(String str, Bundle bundle) throws RemoteException;

    boolean sendMediaButton(KeyEvent keyEvent) throws RemoteException;

    void setCaptioningEnabled(boolean z) throws RemoteException;

    void setRepeatMode(int i) throws RemoteException;

    void setShuffleModeEnabled(boolean z) throws RemoteException;

    void setVolumeTo(int i, int i2, String str) throws RemoteException;

    void skipToQueueItem(long j) throws RemoteException;

    void stop() throws RemoteException;

    void unregisterCallbackListener(IMediaControllerCallback iMediaControllerCallback) throws RemoteException;

    public static abstract class Stub extends Binder implements IMediaSession {
        private static final String DESCRIPTOR = "android.support.v4.media.session.IMediaSession";
        static final int TRANSACTION_addQueueItem = 41;
        static final int TRANSACTION_addQueueItemAt = 42;
        static final int TRANSACTION_adjustVolume = 11;
        static final int TRANSACTION_fastForward = 22;
        static final int TRANSACTION_getExtras = 31;
        static final int TRANSACTION_getFlags = 9;
        static final int TRANSACTION_getLaunchPendingIntent = 8;
        static final int TRANSACTION_getMetadata = 27;
        static final int TRANSACTION_getPackageName = 6;
        static final int TRANSACTION_getPlaybackState = 28;
        static final int TRANSACTION_getQueue = 29;
        static final int TRANSACTION_getQueueTitle = 30;
        static final int TRANSACTION_getRatingType = 32;
        static final int TRANSACTION_getRepeatMode = 37;
        static final int TRANSACTION_getTag = 7;
        static final int TRANSACTION_getVolumeAttributes = 10;
        static final int TRANSACTION_isCaptioningEnabled = 45;
        static final int TRANSACTION_isShuffleModeEnabled = 38;
        static final int TRANSACTION_isTransportControlEnabled = 5;
        static final int TRANSACTION_next = 20;
        static final int TRANSACTION_pause = 18;
        static final int TRANSACTION_play = 13;
        static final int TRANSACTION_playFromMediaId = 14;
        static final int TRANSACTION_playFromSearch = 15;
        static final int TRANSACTION_playFromUri = 16;
        static final int TRANSACTION_prepare = 33;
        static final int TRANSACTION_prepareFromMediaId = 34;
        static final int TRANSACTION_prepareFromSearch = 35;
        static final int TRANSACTION_prepareFromUri = 36;
        static final int TRANSACTION_previous = 21;
        static final int TRANSACTION_rate = 25;
        static final int TRANSACTION_registerCallbackListener = 3;
        static final int TRANSACTION_removeQueueItem = 43;
        static final int TRANSACTION_removeQueueItemAt = 44;
        static final int TRANSACTION_rewind = 23;
        static final int TRANSACTION_seekTo = 24;
        static final int TRANSACTION_sendCommand = 1;
        static final int TRANSACTION_sendCustomAction = 26;
        static final int TRANSACTION_sendMediaButton = 2;
        static final int TRANSACTION_setCaptioningEnabled = 46;
        static final int TRANSACTION_setRepeatMode = 39;
        static final int TRANSACTION_setShuffleModeEnabled = 40;
        static final int TRANSACTION_setVolumeTo = 12;
        static final int TRANSACTION_skipToQueueItem = 17;
        static final int TRANSACTION_stop = 19;
        static final int TRANSACTION_unregisterCallbackListener = 4;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IMediaSession asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IMediaSession)) {
                return new Proxy(obj);
            }
            return (IMediaSession) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v1, resolved type: android.support.v4.media.session.MediaSessionCompat$ResultReceiverWrapper} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v16, resolved type: android.os.Bundle} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v20, resolved type: android.os.Bundle} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v24, resolved type: android.os.Bundle} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v33, resolved type: android.os.Bundle} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v45, resolved type: android.os.Bundle} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v49, resolved type: android.os.Bundle} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v53, resolved type: android.os.Bundle} */
        /* JADX WARNING: type inference failed for: r2v0 */
        /* JADX WARNING: type inference failed for: r2v5 */
        /* JADX WARNING: type inference failed for: r2v30 */
        /* JADX WARNING: type inference failed for: r2v59 */
        /* JADX WARNING: type inference failed for: r2v62 */
        /* JADX WARNING: type inference failed for: r2v66 */
        /* JADX WARNING: type inference failed for: r2v71 */
        /* JADX WARNING: type inference failed for: r2v72 */
        /* JADX WARNING: type inference failed for: r2v73 */
        /* JADX WARNING: type inference failed for: r2v74 */
        /* JADX WARNING: type inference failed for: r2v75 */
        /* JADX WARNING: type inference failed for: r2v76 */
        /* JADX WARNING: type inference failed for: r2v77 */
        /* JADX WARNING: type inference failed for: r2v78 */
        /* JADX WARNING: type inference failed for: r2v79 */
        /* JADX WARNING: type inference failed for: r2v80 */
        /* JADX WARNING: type inference failed for: r2v81 */
        /* JADX WARNING: type inference failed for: r2v82 */
        /* JADX WARNING: type inference failed for: r2v83 */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTransact(int r6, android.os.Parcel r7, android.os.Parcel r8, int r9) throws android.os.RemoteException {
            /*
                r5 = this;
                r0 = 1598968902(0x5f4e5446, float:1.4867585E19)
                r1 = 1
                if (r6 == r0) goto L_0x041c
                r0 = 0
                r2 = 0
                switch(r6) {
                    case 1: goto L_0x03ec;
                    case 2: goto L_0x03ca;
                    case 3: goto L_0x03b6;
                    case 4: goto L_0x03a2;
                    case 5: goto L_0x0392;
                    case 6: goto L_0x0382;
                    case 7: goto L_0x0372;
                    case 8: goto L_0x0359;
                    case 9: goto L_0x0349;
                    case 10: goto L_0x0330;
                    case 11: goto L_0x0318;
                    case 12: goto L_0x0300;
                    case 13: goto L_0x02f4;
                    case 14: goto L_0x02d4;
                    case 15: goto L_0x02b4;
                    case 16: goto L_0x0288;
                    case 17: goto L_0x0278;
                    case 18: goto L_0x026c;
                    case 19: goto L_0x0260;
                    case 20: goto L_0x0254;
                    case 21: goto L_0x0248;
                    case 22: goto L_0x023c;
                    case 23: goto L_0x0230;
                    case 24: goto L_0x0220;
                    case 25: goto L_0x0202;
                    case 26: goto L_0x01e2;
                    case 27: goto L_0x01c9;
                    case 28: goto L_0x01b0;
                    case 29: goto L_0x01a0;
                    case 30: goto L_0x0187;
                    case 31: goto L_0x016e;
                    case 32: goto L_0x015e;
                    case 33: goto L_0x0152;
                    case 34: goto L_0x0132;
                    case 35: goto L_0x0112;
                    case 36: goto L_0x00e6;
                    case 37: goto L_0x00d6;
                    case 38: goto L_0x00c6;
                    case 39: goto L_0x00b6;
                    case 40: goto L_0x00a2;
                    case 41: goto L_0x0084;
                    case 42: goto L_0x0062;
                    case 43: goto L_0x0044;
                    case 44: goto L_0x0034;
                    case 45: goto L_0x0024;
                    case 46: goto L_0x0010;
                    default: goto L_0x000b;
                }
            L_0x000b:
                boolean r0 = super.onTransact(r6, r7, r8, r9)
                return r0
            L_0x0010:
                java.lang.String r2 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r2)
                int r2 = r7.readInt()
                if (r2 == 0) goto L_0x001d
                r0 = r1
            L_0x001d:
                r5.setCaptioningEnabled(r0)
                r8.writeNoException()
                return r1
            L_0x0024:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                boolean r0 = r5.isCaptioningEnabled()
                r8.writeNoException()
                r8.writeInt(r0)
                return r1
            L_0x0034:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                int r0 = r7.readInt()
                r5.removeQueueItemAt(r0)
                r8.writeNoException()
                return r1
            L_0x0044:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                int r0 = r7.readInt()
                if (r0 == 0) goto L_0x0059
                android.os.Parcelable$Creator<android.support.v4.media.MediaDescriptionCompat> r0 = android.support.v4.media.MediaDescriptionCompat.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r7)
                r2 = r0
                android.support.v4.media.MediaDescriptionCompat r2 = (android.support.v4.media.MediaDescriptionCompat) r2
                goto L_0x005a
            L_0x0059:
            L_0x005a:
                r0 = r2
                r5.removeQueueItem(r0)
                r8.writeNoException()
                return r1
            L_0x0062:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                int r0 = r7.readInt()
                if (r0 == 0) goto L_0x0077
                android.os.Parcelable$Creator<android.support.v4.media.MediaDescriptionCompat> r0 = android.support.v4.media.MediaDescriptionCompat.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r7)
                r2 = r0
                android.support.v4.media.MediaDescriptionCompat r2 = (android.support.v4.media.MediaDescriptionCompat) r2
                goto L_0x0078
            L_0x0077:
            L_0x0078:
                r0 = r2
                int r2 = r7.readInt()
                r5.addQueueItemAt(r0, r2)
                r8.writeNoException()
                return r1
            L_0x0084:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                int r0 = r7.readInt()
                if (r0 == 0) goto L_0x0099
                android.os.Parcelable$Creator<android.support.v4.media.MediaDescriptionCompat> r0 = android.support.v4.media.MediaDescriptionCompat.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r7)
                r2 = r0
                android.support.v4.media.MediaDescriptionCompat r2 = (android.support.v4.media.MediaDescriptionCompat) r2
                goto L_0x009a
            L_0x0099:
            L_0x009a:
                r0 = r2
                r5.addQueueItem(r0)
                r8.writeNoException()
                return r1
            L_0x00a2:
                java.lang.String r2 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r2)
                int r2 = r7.readInt()
                if (r2 == 0) goto L_0x00af
                r0 = r1
            L_0x00af:
                r5.setShuffleModeEnabled(r0)
                r8.writeNoException()
                return r1
            L_0x00b6:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                int r0 = r7.readInt()
                r5.setRepeatMode(r0)
                r8.writeNoException()
                return r1
            L_0x00c6:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                boolean r0 = r5.isShuffleModeEnabled()
                r8.writeNoException()
                r8.writeInt(r0)
                return r1
            L_0x00d6:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                int r0 = r5.getRepeatMode()
                r8.writeNoException()
                r8.writeInt(r0)
                return r1
            L_0x00e6:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                int r0 = r7.readInt()
                if (r0 == 0) goto L_0x00fa
                android.os.Parcelable$Creator r0 = android.net.Uri.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r7)
                android.net.Uri r0 = (android.net.Uri) r0
                goto L_0x00fb
            L_0x00fa:
                r0 = r2
            L_0x00fb:
                int r3 = r7.readInt()
                if (r3 == 0) goto L_0x010a
                android.os.Parcelable$Creator r2 = android.os.Bundle.CREATOR
                java.lang.Object r2 = r2.createFromParcel(r7)
                android.os.Bundle r2 = (android.os.Bundle) r2
                goto L_0x010b
            L_0x010a:
            L_0x010b:
                r5.prepareFromUri(r0, r2)
                r8.writeNoException()
                return r1
            L_0x0112:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                java.lang.String r0 = r7.readString()
                int r3 = r7.readInt()
                if (r3 == 0) goto L_0x012a
                android.os.Parcelable$Creator r2 = android.os.Bundle.CREATOR
                java.lang.Object r2 = r2.createFromParcel(r7)
                android.os.Bundle r2 = (android.os.Bundle) r2
                goto L_0x012b
            L_0x012a:
            L_0x012b:
                r5.prepareFromSearch(r0, r2)
                r8.writeNoException()
                return r1
            L_0x0132:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                java.lang.String r0 = r7.readString()
                int r3 = r7.readInt()
                if (r3 == 0) goto L_0x014a
                android.os.Parcelable$Creator r2 = android.os.Bundle.CREATOR
                java.lang.Object r2 = r2.createFromParcel(r7)
                android.os.Bundle r2 = (android.os.Bundle) r2
                goto L_0x014b
            L_0x014a:
            L_0x014b:
                r5.prepareFromMediaId(r0, r2)
                r8.writeNoException()
                return r1
            L_0x0152:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                r5.prepare()
                r8.writeNoException()
                return r1
            L_0x015e:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                int r0 = r5.getRatingType()
                r8.writeNoException()
                r8.writeInt(r0)
                return r1
            L_0x016e:
                java.lang.String r2 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r2)
                android.os.Bundle r2 = r5.getExtras()
                r8.writeNoException()
                if (r2 == 0) goto L_0x0183
                r8.writeInt(r1)
                r2.writeToParcel(r8, r1)
                goto L_0x0186
            L_0x0183:
                r8.writeInt(r0)
            L_0x0186:
                return r1
            L_0x0187:
                java.lang.String r2 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r2)
                java.lang.CharSequence r2 = r5.getQueueTitle()
                r8.writeNoException()
                if (r2 == 0) goto L_0x019c
                r8.writeInt(r1)
                android.text.TextUtils.writeToParcel(r2, r8, r1)
                goto L_0x019f
            L_0x019c:
                r8.writeInt(r0)
            L_0x019f:
                return r1
            L_0x01a0:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                java.util.List r0 = r5.getQueue()
                r8.writeNoException()
                r8.writeTypedList(r0)
                return r1
            L_0x01b0:
                java.lang.String r2 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r2)
                android.support.v4.media.session.PlaybackStateCompat r2 = r5.getPlaybackState()
                r8.writeNoException()
                if (r2 == 0) goto L_0x01c5
                r8.writeInt(r1)
                r2.writeToParcel(r8, r1)
                goto L_0x01c8
            L_0x01c5:
                r8.writeInt(r0)
            L_0x01c8:
                return r1
            L_0x01c9:
                java.lang.String r2 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r2)
                android.support.v4.media.MediaMetadataCompat r2 = r5.getMetadata()
                r8.writeNoException()
                if (r2 == 0) goto L_0x01de
                r8.writeInt(r1)
                r2.writeToParcel(r8, r1)
                goto L_0x01e1
            L_0x01de:
                r8.writeInt(r0)
            L_0x01e1:
                return r1
            L_0x01e2:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                java.lang.String r0 = r7.readString()
                int r3 = r7.readInt()
                if (r3 == 0) goto L_0x01fa
                android.os.Parcelable$Creator r2 = android.os.Bundle.CREATOR
                java.lang.Object r2 = r2.createFromParcel(r7)
                android.os.Bundle r2 = (android.os.Bundle) r2
                goto L_0x01fb
            L_0x01fa:
            L_0x01fb:
                r5.sendCustomAction(r0, r2)
                r8.writeNoException()
                return r1
            L_0x0202:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                int r0 = r7.readInt()
                if (r0 == 0) goto L_0x0217
                android.os.Parcelable$Creator<android.support.v4.media.RatingCompat> r0 = android.support.v4.media.RatingCompat.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r7)
                r2 = r0
                android.support.v4.media.RatingCompat r2 = (android.support.v4.media.RatingCompat) r2
                goto L_0x0218
            L_0x0217:
            L_0x0218:
                r0 = r2
                r5.rate(r0)
                r8.writeNoException()
                return r1
            L_0x0220:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                long r2 = r7.readLong()
                r5.seekTo(r2)
                r8.writeNoException()
                return r1
            L_0x0230:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                r5.rewind()
                r8.writeNoException()
                return r1
            L_0x023c:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                r5.fastForward()
                r8.writeNoException()
                return r1
            L_0x0248:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                r5.previous()
                r8.writeNoException()
                return r1
            L_0x0254:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                r5.next()
                r8.writeNoException()
                return r1
            L_0x0260:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                r5.stop()
                r8.writeNoException()
                return r1
            L_0x026c:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                r5.pause()
                r8.writeNoException()
                return r1
            L_0x0278:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                long r2 = r7.readLong()
                r5.skipToQueueItem(r2)
                r8.writeNoException()
                return r1
            L_0x0288:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                int r0 = r7.readInt()
                if (r0 == 0) goto L_0x029c
                android.os.Parcelable$Creator r0 = android.net.Uri.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r7)
                android.net.Uri r0 = (android.net.Uri) r0
                goto L_0x029d
            L_0x029c:
                r0 = r2
            L_0x029d:
                int r3 = r7.readInt()
                if (r3 == 0) goto L_0x02ac
                android.os.Parcelable$Creator r2 = android.os.Bundle.CREATOR
                java.lang.Object r2 = r2.createFromParcel(r7)
                android.os.Bundle r2 = (android.os.Bundle) r2
                goto L_0x02ad
            L_0x02ac:
            L_0x02ad:
                r5.playFromUri(r0, r2)
                r8.writeNoException()
                return r1
            L_0x02b4:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                java.lang.String r0 = r7.readString()
                int r3 = r7.readInt()
                if (r3 == 0) goto L_0x02cc
                android.os.Parcelable$Creator r2 = android.os.Bundle.CREATOR
                java.lang.Object r2 = r2.createFromParcel(r7)
                android.os.Bundle r2 = (android.os.Bundle) r2
                goto L_0x02cd
            L_0x02cc:
            L_0x02cd:
                r5.playFromSearch(r0, r2)
                r8.writeNoException()
                return r1
            L_0x02d4:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                java.lang.String r0 = r7.readString()
                int r3 = r7.readInt()
                if (r3 == 0) goto L_0x02ec
                android.os.Parcelable$Creator r2 = android.os.Bundle.CREATOR
                java.lang.Object r2 = r2.createFromParcel(r7)
                android.os.Bundle r2 = (android.os.Bundle) r2
                goto L_0x02ed
            L_0x02ec:
            L_0x02ed:
                r5.playFromMediaId(r0, r2)
                r8.writeNoException()
                return r1
            L_0x02f4:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                r5.play()
                r8.writeNoException()
                return r1
            L_0x0300:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                int r0 = r7.readInt()
                int r2 = r7.readInt()
                java.lang.String r3 = r7.readString()
                r5.setVolumeTo(r0, r2, r3)
                r8.writeNoException()
                return r1
            L_0x0318:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                int r0 = r7.readInt()
                int r2 = r7.readInt()
                java.lang.String r3 = r7.readString()
                r5.adjustVolume(r0, r2, r3)
                r8.writeNoException()
                return r1
            L_0x0330:
                java.lang.String r2 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r2)
                android.support.v4.media.session.ParcelableVolumeInfo r2 = r5.getVolumeAttributes()
                r8.writeNoException()
                if (r2 == 0) goto L_0x0345
                r8.writeInt(r1)
                r2.writeToParcel(r8, r1)
                goto L_0x0348
            L_0x0345:
                r8.writeInt(r0)
            L_0x0348:
                return r1
            L_0x0349:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                long r2 = r5.getFlags()
                r8.writeNoException()
                r8.writeLong(r2)
                return r1
            L_0x0359:
                java.lang.String r2 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r2)
                android.app.PendingIntent r2 = r5.getLaunchPendingIntent()
                r8.writeNoException()
                if (r2 == 0) goto L_0x036e
                r8.writeInt(r1)
                r2.writeToParcel(r8, r1)
                goto L_0x0371
            L_0x036e:
                r8.writeInt(r0)
            L_0x0371:
                return r1
            L_0x0372:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                java.lang.String r0 = r5.getTag()
                r8.writeNoException()
                r8.writeString(r0)
                return r1
            L_0x0382:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                java.lang.String r0 = r5.getPackageName()
                r8.writeNoException()
                r8.writeString(r0)
                return r1
            L_0x0392:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                boolean r0 = r5.isTransportControlEnabled()
                r8.writeNoException()
                r8.writeInt(r0)
                return r1
            L_0x03a2:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                android.os.IBinder r0 = r7.readStrongBinder()
                android.support.v4.media.session.IMediaControllerCallback r0 = android.support.v4.media.session.IMediaControllerCallback.Stub.asInterface(r0)
                r5.unregisterCallbackListener(r0)
                r8.writeNoException()
                return r1
            L_0x03b6:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                android.os.IBinder r0 = r7.readStrongBinder()
                android.support.v4.media.session.IMediaControllerCallback r0 = android.support.v4.media.session.IMediaControllerCallback.Stub.asInterface(r0)
                r5.registerCallbackListener(r0)
                r8.writeNoException()
                return r1
            L_0x03ca:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                int r0 = r7.readInt()
                if (r0 == 0) goto L_0x03df
                android.os.Parcelable$Creator r0 = android.view.KeyEvent.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r7)
                r2 = r0
                android.view.KeyEvent r2 = (android.view.KeyEvent) r2
                goto L_0x03e0
            L_0x03df:
            L_0x03e0:
                r0 = r2
                boolean r2 = r5.sendMediaButton(r0)
                r8.writeNoException()
                r8.writeInt(r2)
                return r1
            L_0x03ec:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r7.enforceInterface(r0)
                java.lang.String r0 = r7.readString()
                int r3 = r7.readInt()
                if (r3 == 0) goto L_0x0404
                android.os.Parcelable$Creator r3 = android.os.Bundle.CREATOR
                java.lang.Object r3 = r3.createFromParcel(r7)
                android.os.Bundle r3 = (android.os.Bundle) r3
                goto L_0x0405
            L_0x0404:
                r3 = r2
            L_0x0405:
                int r4 = r7.readInt()
                if (r4 == 0) goto L_0x0414
                android.os.Parcelable$Creator<android.support.v4.media.session.MediaSessionCompat$ResultReceiverWrapper> r2 = android.support.v4.media.session.MediaSessionCompat.ResultReceiverWrapper.CREATOR
                java.lang.Object r2 = r2.createFromParcel(r7)
                android.support.v4.media.session.MediaSessionCompat$ResultReceiverWrapper r2 = (android.support.v4.media.session.MediaSessionCompat.ResultReceiverWrapper) r2
                goto L_0x0415
            L_0x0414:
            L_0x0415:
                r5.sendCommand(r0, r3, r2)
                r8.writeNoException()
                return r1
            L_0x041c:
                java.lang.String r0 = "android.support.v4.media.session.IMediaSession"
                r8.writeString(r0)
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: android.support.v4.media.session.IMediaSession.Stub.onTransact(int, android.os.Parcel, android.os.Parcel, int):boolean");
        }

        private static class Proxy implements IMediaSession {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            public void sendCommand(String command, Bundle args, MediaSessionCompat.ResultReceiverWrapper cb) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(command);
                    if (args != null) {
                        _data.writeInt(1);
                        args.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (cb != null) {
                        _data.writeInt(1);
                        cb.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean sendMediaButton(KeyEvent mediaButton) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _result = true;
                    if (mediaButton != null) {
                        _data.writeInt(1);
                        mediaButton.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() == 0) {
                        _result = false;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void registerCallbackListener(IMediaControllerCallback cb) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(cb != null ? cb.asBinder() : null);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void unregisterCallbackListener(IMediaControllerCallback cb) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(cb != null ? cb.asBinder() : null);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean isTransportControlEnabled() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _result = false;
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = true;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public String getPackageName() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readString();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public String getTag() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readString();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public PendingIntent getLaunchPendingIntent() throws RemoteException {
                PendingIntent _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (PendingIntent) PendingIntent.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public long getFlags() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readLong();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public ParcelableVolumeInfo getVolumeAttributes() throws RemoteException {
                ParcelableVolumeInfo _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = ParcelableVolumeInfo.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void adjustVolume(int direction, int flags, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(direction);
                    _data.writeInt(flags);
                    _data.writeString(packageName);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setVolumeTo(int value, int flags, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(value);
                    _data.writeInt(flags);
                    _data.writeString(packageName);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public MediaMetadataCompat getMetadata() throws RemoteException {
                MediaMetadataCompat _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(27, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = MediaMetadataCompat.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public PlaybackStateCompat getPlaybackState() throws RemoteException {
                PlaybackStateCompat _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(28, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = PlaybackStateCompat.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public List<MediaSessionCompat.QueueItem> getQueue() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(29, _data, _reply, 0);
                    _reply.readException();
                    return _reply.createTypedArrayList(MediaSessionCompat.QueueItem.CREATOR);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public CharSequence getQueueTitle() throws RemoteException {
                CharSequence _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(30, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public Bundle getExtras() throws RemoteException {
                Bundle _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(31, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (Bundle) Bundle.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getRatingType() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(32, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean isCaptioningEnabled() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _result = false;
                    this.mRemote.transact(45, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = true;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getRepeatMode() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(37, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean isShuffleModeEnabled() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _result = false;
                    this.mRemote.transact(38, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = true;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void addQueueItem(MediaDescriptionCompat description) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (description != null) {
                        _data.writeInt(1);
                        description.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(41, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void addQueueItemAt(MediaDescriptionCompat description, int index) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (description != null) {
                        _data.writeInt(1);
                        description.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(index);
                    this.mRemote.transact(42, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void removeQueueItem(MediaDescriptionCompat description) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (description != null) {
                        _data.writeInt(1);
                        description.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(43, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void removeQueueItemAt(int index) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(index);
                    this.mRemote.transact(44, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void prepare() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(33, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void prepareFromMediaId(String uri, Bundle extras) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(uri);
                    if (extras != null) {
                        _data.writeInt(1);
                        extras.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(34, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void prepareFromSearch(String string, Bundle extras) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(string);
                    if (extras != null) {
                        _data.writeInt(1);
                        extras.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(35, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void prepareFromUri(Uri uri, Bundle extras) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (uri != null) {
                        _data.writeInt(1);
                        uri.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (extras != null) {
                        _data.writeInt(1);
                        extras.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(36, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void play() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void playFromMediaId(String uri, Bundle extras) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(uri);
                    if (extras != null) {
                        _data.writeInt(1);
                        extras.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void playFromSearch(String string, Bundle extras) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(string);
                    if (extras != null) {
                        _data.writeInt(1);
                        extras.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void playFromUri(Uri uri, Bundle extras) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (uri != null) {
                        _data.writeInt(1);
                        uri.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (extras != null) {
                        _data.writeInt(1);
                        extras.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void skipToQueueItem(long id) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(id);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void pause() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void stop() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void next() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void previous() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void fastForward() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void rewind() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void seekTo(long pos) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(pos);
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void rate(RatingCompat rating) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (rating != null) {
                        _data.writeInt(1);
                        rating.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setCaptioningEnabled(boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(enabled);
                    this.mRemote.transact(46, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setRepeatMode(int repeatMode) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(repeatMode);
                    this.mRemote.transact(39, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setShuffleModeEnabled(boolean shuffleMode) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(shuffleMode);
                    this.mRemote.transact(40, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void sendCustomAction(String action, Bundle args) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(action);
                    if (args != null) {
                        _data.writeInt(1);
                        args.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(26, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }
    }
}
