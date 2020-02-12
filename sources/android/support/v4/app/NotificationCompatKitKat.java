package android.support.v4.app;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompatBase;
import android.util.SparseArray;
import android.widget.RemoteViews;
import java.util.ArrayList;
import java.util.List;

@RequiresApi(19)
class NotificationCompatKitKat {
    NotificationCompatKitKat() {
    }

    public static class Builder implements NotificationBuilderWithBuilderAccessor, NotificationBuilderWithActions {
        private Notification.Builder b;
        private List<Bundle> mActionExtrasList = new ArrayList();
        private RemoteViews mBigContentView;
        private RemoteViews mContentView;
        private Bundle mExtras;

        public Builder(Context context, Notification n, CharSequence contentTitle, CharSequence contentText, CharSequence contentInfo, RemoteViews tickerView, int number, PendingIntent contentIntent, PendingIntent fullScreenIntent, Bitmap largeIcon, int progressMax, int progress, boolean progressIndeterminate, boolean showWhen, boolean useChronometer, int priority, CharSequence subText, boolean localOnly, ArrayList<String> people, Bundle extras, String groupKey, boolean groupSummary, String sortKey, RemoteViews contentView, RemoteViews bigContentView) {
            Notification notification = n;
            ArrayList<String> arrayList = people;
            Bundle bundle = extras;
            String str = groupKey;
            String str2 = sortKey;
            this.b = new Notification.Builder(context).setWhen(notification.when).setShowWhen(showWhen).setSmallIcon(notification.icon, notification.iconLevel).setContent(notification.contentView).setTicker(notification.tickerText, tickerView).setSound(notification.sound, notification.audioStreamType).setVibrate(notification.vibrate).setLights(notification.ledARGB, notification.ledOnMS, notification.ledOffMS).setOngoing((notification.flags & 2) != 0).setOnlyAlertOnce((notification.flags & 8) != 0).setAutoCancel((notification.flags & 16) != 0).setDefaults(notification.defaults).setContentTitle(contentTitle).setContentText(contentText).setSubText(subText).setContentInfo(contentInfo).setContentIntent(contentIntent).setDeleteIntent(notification.deleteIntent).setFullScreenIntent(fullScreenIntent, (notification.flags & 128) != 0).setLargeIcon(largeIcon).setNumber(number).setUsesChronometer(useChronometer).setPriority(priority).setProgress(progressMax, progress, progressIndeterminate);
            this.mExtras = new Bundle();
            if (bundle != null) {
                this.mExtras.putAll(bundle);
            }
            if (arrayList != null && !people.isEmpty()) {
                this.mExtras.putStringArray(NotificationCompat.EXTRA_PEOPLE, (String[]) arrayList.toArray(new String[people.size()]));
            }
            if (localOnly) {
                this.mExtras.putBoolean(NotificationCompatExtras.EXTRA_LOCAL_ONLY, true);
            }
            if (str != null) {
                this.mExtras.putString(NotificationCompatExtras.EXTRA_GROUP_KEY, str);
                if (groupSummary) {
                    this.mExtras.putBoolean(NotificationCompatExtras.EXTRA_GROUP_SUMMARY, true);
                } else {
                    this.mExtras.putBoolean(NotificationManagerCompat.EXTRA_USE_SIDE_CHANNEL, true);
                }
            }
            if (str2 != null) {
                this.mExtras.putString(NotificationCompatExtras.EXTRA_SORT_KEY, str2);
            }
            this.mContentView = contentView;
            this.mBigContentView = bigContentView;
        }

        public void addAction(NotificationCompatBase.Action action) {
            this.mActionExtrasList.add(NotificationCompatJellybean.writeActionAndGetExtras(this.b, action));
        }

        public Notification.Builder getBuilder() {
            return this.b;
        }

        public Notification build() {
            SparseArray<Bundle> actionExtrasMap = NotificationCompatJellybean.buildActionExtrasMap(this.mActionExtrasList);
            if (actionExtrasMap != null) {
                this.mExtras.putSparseParcelableArray(NotificationCompatExtras.EXTRA_ACTION_EXTRAS, actionExtrasMap);
            }
            this.b.setExtras(this.mExtras);
            Notification notification = this.b.build();
            if (this.mContentView != null) {
                notification.contentView = this.mContentView;
            }
            if (this.mBigContentView != null) {
                notification.bigContentView = this.mBigContentView;
            }
            return notification;
        }
    }

    public static Bundle getExtras(Notification notif) {
        return notif.extras;
    }

    public static int getActionCount(Notification notif) {
        if (notif.actions != null) {
            return notif.actions.length;
        }
        return 0;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v3, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v2, resolved type: android.os.Bundle} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.support.v4.app.NotificationCompatBase.Action getAction(android.app.Notification r9, int r10, android.support.v4.app.NotificationCompatBase.Action.Factory r11, android.support.v4.app.RemoteInputCompatBase.RemoteInput.Factory r12) {
        /*
            android.app.Notification$Action[] r0 = r9.actions
            r0 = r0[r10]
            r1 = 0
            android.os.Bundle r2 = r9.extras
            java.lang.String r3 = "android.support.actionExtras"
            android.util.SparseArray r2 = r2.getSparseParcelableArray(r3)
            if (r2 == 0) goto L_0x0016
            java.lang.Object r3 = r2.get(r10)
            r1 = r3
            android.os.Bundle r1 = (android.os.Bundle) r1
        L_0x0016:
            int r5 = r0.icon
            java.lang.CharSequence r6 = r0.title
            android.app.PendingIntent r7 = r0.actionIntent
            r3 = r11
            r4 = r12
            r8 = r1
            android.support.v4.app.NotificationCompatBase$Action r3 = android.support.v4.app.NotificationCompatJellybean.readAction(r3, r4, r5, r6, r7, r8)
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.app.NotificationCompatKitKat.getAction(android.app.Notification, int, android.support.v4.app.NotificationCompatBase$Action$Factory, android.support.v4.app.RemoteInputCompatBase$RemoteInput$Factory):android.support.v4.app.NotificationCompatBase$Action");
    }

    public static boolean getLocalOnly(Notification notif) {
        return notif.extras.getBoolean(NotificationCompatExtras.EXTRA_LOCAL_ONLY);
    }

    public static String getGroup(Notification notif) {
        return notif.extras.getString(NotificationCompatExtras.EXTRA_GROUP_KEY);
    }

    public static boolean isGroupSummary(Notification notif) {
        return notif.extras.getBoolean(NotificationCompatExtras.EXTRA_GROUP_SUMMARY);
    }

    public static String getSortKey(Notification notif) {
        return notif.extras.getString(NotificationCompatExtras.EXTRA_SORT_KEY);
    }
}
