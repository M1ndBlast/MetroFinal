package android.support.v4.app;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompatBase;
import android.support.v4.app.RemoteInputCompatBase;
import android.widget.RemoteViews;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

@RequiresApi(21)
class NotificationCompatApi21 {
    public static final String CATEGORY_ALARM = "alarm";
    public static final String CATEGORY_CALL = "call";
    public static final String CATEGORY_EMAIL = "email";
    public static final String CATEGORY_ERROR = "err";
    public static final String CATEGORY_EVENT = "event";
    public static final String CATEGORY_MESSAGE = "msg";
    public static final String CATEGORY_PROGRESS = "progress";
    public static final String CATEGORY_PROMO = "promo";
    public static final String CATEGORY_RECOMMENDATION = "recommendation";
    public static final String CATEGORY_SERVICE = "service";
    public static final String CATEGORY_SOCIAL = "social";
    public static final String CATEGORY_STATUS = "status";
    public static final String CATEGORY_SYSTEM = "sys";
    public static final String CATEGORY_TRANSPORT = "transport";
    private static final String KEY_AUTHOR = "author";
    private static final String KEY_MESSAGES = "messages";
    private static final String KEY_ON_READ = "on_read";
    private static final String KEY_ON_REPLY = "on_reply";
    private static final String KEY_PARTICIPANTS = "participants";
    private static final String KEY_REMOTE_INPUT = "remote_input";
    private static final String KEY_TEXT = "text";
    private static final String KEY_TIMESTAMP = "timestamp";

    NotificationCompatApi21() {
    }

    public static class Builder implements NotificationBuilderWithBuilderAccessor, NotificationBuilderWithActions {
        private Notification.Builder b;
        private RemoteViews mBigContentView;
        private RemoteViews mContentView;
        private Bundle mExtras;
        private RemoteViews mHeadsUpContentView;

        public Builder(Context context, Notification n, CharSequence contentTitle, CharSequence contentText, CharSequence contentInfo, RemoteViews tickerView, int number, PendingIntent contentIntent, PendingIntent fullScreenIntent, Bitmap largeIcon, int progressMax, int progress, boolean progressIndeterminate, boolean showWhen, boolean useChronometer, int priority, CharSequence subText, boolean localOnly, String category, ArrayList<String> people, Bundle extras, int color, int visibility, Notification publicVersion, String groupKey, boolean groupSummary, String sortKey, RemoteViews contentView, RemoteViews bigContentView, RemoteViews headsUpContentView) {
            Notification notification = n;
            Bundle bundle = extras;
            this.b = new Notification.Builder(context).setWhen(notification.when).setShowWhen(showWhen).setSmallIcon(notification.icon, notification.iconLevel).setContent(notification.contentView).setTicker(notification.tickerText, tickerView).setSound(notification.sound, notification.audioStreamType).setVibrate(notification.vibrate).setLights(notification.ledARGB, notification.ledOnMS, notification.ledOffMS).setOngoing((notification.flags & 2) != 0).setOnlyAlertOnce((notification.flags & 8) != 0).setAutoCancel((notification.flags & 16) != 0).setDefaults(notification.defaults).setContentTitle(contentTitle).setContentText(contentText).setSubText(subText).setContentInfo(contentInfo).setContentIntent(contentIntent).setDeleteIntent(notification.deleteIntent).setFullScreenIntent(fullScreenIntent, (notification.flags & 128) != 0).setLargeIcon(largeIcon).setNumber(number).setUsesChronometer(useChronometer).setPriority(priority).setProgress(progressMax, progress, progressIndeterminate).setLocalOnly(localOnly).setGroup(groupKey).setGroupSummary(groupSummary).setSortKey(sortKey).setCategory(category).setColor(color).setVisibility(visibility).setPublicVersion(publicVersion);
            this.mExtras = new Bundle();
            if (bundle != null) {
                this.mExtras.putAll(bundle);
            }
            Iterator<String> it = people.iterator();
            while (it.hasNext()) {
                this.b.addPerson(it.next());
                Notification notification2 = publicVersion;
                Bundle bundle2 = extras;
            }
            this.mContentView = contentView;
            this.mBigContentView = bigContentView;
            this.mHeadsUpContentView = headsUpContentView;
        }

        public void addAction(NotificationCompatBase.Action action) {
            NotificationCompatApi20.addAction(this.b, action);
        }

        public Notification.Builder getBuilder() {
            return this.b;
        }

        public Notification build() {
            this.b.setExtras(this.mExtras);
            Notification notification = this.b.build();
            if (this.mContentView != null) {
                notification.contentView = this.mContentView;
            }
            if (this.mBigContentView != null) {
                notification.bigContentView = this.mBigContentView;
            }
            if (this.mHeadsUpContentView != null) {
                notification.headsUpContentView = this.mHeadsUpContentView;
            }
            return notification;
        }
    }

    public static String getCategory(Notification notif) {
        return notif.category;
    }

    static Bundle getBundleForUnreadConversation(NotificationCompatBase.UnreadConversation uc) {
        if (uc == null) {
            return null;
        }
        Bundle b = new Bundle();
        String author = null;
        if (uc.getParticipants() != null && uc.getParticipants().length > 1) {
            author = uc.getParticipants()[0];
        }
        Parcelable[] messages = new Parcelable[uc.getMessages().length];
        for (int i = 0; i < messages.length; i++) {
            Bundle m = new Bundle();
            m.putString(KEY_TEXT, uc.getMessages()[i]);
            m.putString(KEY_AUTHOR, author);
            messages[i] = m;
        }
        b.putParcelableArray(KEY_MESSAGES, messages);
        RemoteInputCompatBase.RemoteInput remoteInput = uc.getRemoteInput();
        if (remoteInput != null) {
            b.putParcelable(KEY_REMOTE_INPUT, fromCompatRemoteInput(remoteInput));
        }
        b.putParcelable(KEY_ON_REPLY, uc.getReplyPendingIntent());
        b.putParcelable(KEY_ON_READ, uc.getReadPendingIntent());
        b.putStringArray(KEY_PARTICIPANTS, uc.getParticipants());
        b.putLong(KEY_TIMESTAMP, uc.getLatestTimestamp());
        return b;
    }

    static NotificationCompatBase.UnreadConversation getUnreadConversationFromBundle(Bundle b, NotificationCompatBase.UnreadConversation.Factory factory, RemoteInputCompatBase.RemoteInput.Factory remoteInputFactory) {
        Bundle bundle = b;
        RemoteInputCompatBase.RemoteInput remoteInput = null;
        if (bundle == null) {
            return null;
        }
        Parcelable[] parcelableMessages = bundle.getParcelableArray(KEY_MESSAGES);
        String[] messages = null;
        if (parcelableMessages != null) {
            String[] tmp = new String[parcelableMessages.length];
            boolean success = true;
            int i = 0;
            while (true) {
                if (i >= tmp.length) {
                    break;
                } else if (!(parcelableMessages[i] instanceof Bundle)) {
                    success = false;
                    break;
                } else {
                    tmp[i] = ((Bundle) parcelableMessages[i]).getString(KEY_TEXT);
                    if (tmp[i] == null) {
                        success = false;
                        break;
                    }
                    i++;
                }
            }
            if (!success) {
                return null;
            }
            messages = tmp;
        }
        PendingIntent onRead = (PendingIntent) bundle.getParcelable(KEY_ON_READ);
        PendingIntent onReply = (PendingIntent) bundle.getParcelable(KEY_ON_REPLY);
        RemoteInput remoteInput2 = (RemoteInput) bundle.getParcelable(KEY_REMOTE_INPUT);
        String[] participants = bundle.getStringArray(KEY_PARTICIPANTS);
        if (participants == null || participants.length != 1) {
            return null;
        }
        if (remoteInput2 != null) {
            remoteInput = toCompatRemoteInput(remoteInput2, remoteInputFactory);
        } else {
            RemoteInputCompatBase.RemoteInput.Factory factory2 = remoteInputFactory;
        }
        return factory.build(messages, remoteInput, onReply, onRead, participants, bundle.getLong(KEY_TIMESTAMP));
    }

    private static RemoteInput fromCompatRemoteInput(RemoteInputCompatBase.RemoteInput src) {
        return new RemoteInput.Builder(src.getResultKey()).setLabel(src.getLabel()).setChoices(src.getChoices()).setAllowFreeFormInput(src.getAllowFreeFormInput()).addExtras(src.getExtras()).build();
    }

    private static RemoteInputCompatBase.RemoteInput toCompatRemoteInput(RemoteInput remoteInput, RemoteInputCompatBase.RemoteInput.Factory factory) {
        return factory.build(remoteInput.getResultKey(), remoteInput.getLabel(), remoteInput.getChoices(), remoteInput.getAllowFreeFormInput(), remoteInput.getExtras(), (Set<String>) null);
    }
}
