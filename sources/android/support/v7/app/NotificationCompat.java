package android.support.v7.app;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.v4.app.BundleCompat;
import android.support.v4.app.NotificationBuilderWithBuilderAccessor;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.text.BidiFormatter;
import android.support.v4.view.ViewCompat;
import android.support.v7.appcompat.R;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.widget.RemoteViews;
import java.util.ArrayList;
import java.util.List;

public class NotificationCompat extends android.support.v4.app.NotificationCompat {

    public static class DecoratedCustomViewStyle extends NotificationCompat.Style {
    }

    public static class DecoratedMediaCustomViewStyle extends MediaStyle {
    }

    public static MediaSessionCompat.Token getMediaSession(Notification notification) {
        Bundle extras = getExtras(notification);
        if (extras == null) {
            return null;
        }
        if (Build.VERSION.SDK_INT >= 21) {
            Object tokenInner = extras.getParcelable(android.support.v4.app.NotificationCompat.EXTRA_MEDIA_SESSION);
            if (tokenInner != null) {
                return MediaSessionCompat.Token.fromToken(tokenInner);
            }
            return null;
        }
        IBinder tokenInner2 = BundleCompat.getBinder(extras, android.support.v4.app.NotificationCompat.EXTRA_MEDIA_SESSION);
        if (tokenInner2 == null) {
            return null;
        }
        Parcel p = Parcel.obtain();
        p.writeStrongBinder(tokenInner2);
        p.setDataPosition(0);
        MediaSessionCompat.Token token = MediaSessionCompat.Token.CREATOR.createFromParcel(p);
        p.recycle();
        return token;
    }

    /* access modifiers changed from: private */
    @RequiresApi(24)
    public static void addStyleToBuilderApi24(NotificationBuilderWithBuilderAccessor builder, NotificationCompat.Builder b) {
        if (b.mStyle instanceof DecoratedCustomViewStyle) {
            NotificationCompatImpl24.addDecoratedCustomViewStyle(builder);
        } else if (b.mStyle instanceof DecoratedMediaCustomViewStyle) {
            NotificationCompatImpl24.addDecoratedMediaCustomViewStyle(builder);
        } else if (!(b.mStyle instanceof NotificationCompat.MessagingStyle)) {
            addStyleGetContentViewLollipop(builder, b);
        }
    }

    /* access modifiers changed from: private */
    @RequiresApi(21)
    public static RemoteViews addStyleGetContentViewLollipop(NotificationBuilderWithBuilderAccessor builder, NotificationCompat.Builder b) {
        Object obj;
        NotificationCompat.Builder builder2 = b;
        if (builder2.mStyle instanceof MediaStyle) {
            MediaStyle mediaStyle = (MediaStyle) builder2.mStyle;
            int[] iArr = mediaStyle.mActionsToShowInCompact;
            if (mediaStyle.mToken != null) {
                obj = mediaStyle.mToken.getToken();
            } else {
                obj = null;
            }
            NotificationBuilderWithBuilderAccessor notificationBuilderWithBuilderAccessor = builder;
            NotificationCompatImpl21.addMediaStyle(notificationBuilderWithBuilderAccessor, iArr, obj);
            boolean createCustomContent = false;
            boolean hasContentView = b.getContentView() != null;
            boolean isMorL = Build.VERSION.SDK_INT >= 21 && Build.VERSION.SDK_INT <= 23;
            if (hasContentView || (isMorL && b.getBigContentView() != null)) {
                createCustomContent = true;
            }
            if (!(builder2.mStyle instanceof DecoratedMediaCustomViewStyle) || !createCustomContent) {
                return null;
            }
            boolean z = createCustomContent;
            RemoteViews contentViewMedia = NotificationCompatImplBase.overrideContentViewMedia(notificationBuilderWithBuilderAccessor, builder2.mContext, builder2.mContentTitle, builder2.mContentText, builder2.mContentInfo, builder2.mNumber, builder2.mLargeIcon, builder2.mSubText, builder2.mUseChronometer, b.getWhenIfShowing(), b.getPriority(), builder2.mActions, mediaStyle.mActionsToShowInCompact, false, (PendingIntent) null, hasContentView);
            if (hasContentView) {
                NotificationCompatImplBase.buildIntoRemoteViews(builder2.mContext, contentViewMedia, b.getContentView());
            }
            setBackgroundColor(builder2.mContext, contentViewMedia, b.getColor());
            return contentViewMedia;
        } else if (builder2.mStyle instanceof DecoratedCustomViewStyle) {
            return getDecoratedContentView(b);
        } else {
            return addStyleGetContentViewJellybean(builder, b);
        }
    }

    /* access modifiers changed from: private */
    @RequiresApi(16)
    public static RemoteViews addStyleGetContentViewJellybean(NotificationBuilderWithBuilderAccessor builder, NotificationCompat.Builder b) {
        if (b.mStyle instanceof NotificationCompat.MessagingStyle) {
            addMessagingFallBackStyle((NotificationCompat.MessagingStyle) b.mStyle, builder, b);
        }
        return addStyleGetContentViewIcs(builder, b);
    }

    /* access modifiers changed from: private */
    public static NotificationCompat.MessagingStyle.Message findLatestIncomingMessage(NotificationCompat.MessagingStyle style) {
        List<NotificationCompat.MessagingStyle.Message> messages = style.getMessages();
        for (int i = messages.size() - 1; i >= 0; i--) {
            NotificationCompat.MessagingStyle.Message m = messages.get(i);
            if (!TextUtils.isEmpty(m.getSender())) {
                return m;
            }
        }
        if (messages.isEmpty() == 0) {
            return messages.get(messages.size() - 1);
        }
        return null;
    }

    /* access modifiers changed from: private */
    public static CharSequence makeMessageLine(NotificationCompat.Builder b, NotificationCompat.MessagingStyle style, NotificationCompat.MessagingStyle.Message m) {
        CharSequence charSequence;
        BidiFormatter bidi = BidiFormatter.getInstance();
        SpannableStringBuilder sb = new SpannableStringBuilder();
        boolean afterLollipop = Build.VERSION.SDK_INT >= 21;
        int color = (afterLollipop || Build.VERSION.SDK_INT <= 10) ? ViewCompat.MEASURED_STATE_MASK : -1;
        CharSequence replyName = m.getSender();
        if (TextUtils.isEmpty(m.getSender())) {
            if (style.getUserDisplayName() == null) {
                charSequence = "";
            } else {
                charSequence = style.getUserDisplayName();
            }
            replyName = charSequence;
            color = (!afterLollipop || b.getColor() == 0) ? color : b.getColor();
        }
        CharSequence senderText = bidi.unicodeWrap(replyName);
        sb.append(senderText);
        sb.setSpan(makeFontColorSpan(color), sb.length() - senderText.length(), sb.length(), 33);
        sb.append("  ").append(bidi.unicodeWrap(m.getText() == null ? "" : m.getText()));
        return sb;
    }

    private static TextAppearanceSpan makeFontColorSpan(int color) {
        return new TextAppearanceSpan((String) null, 0, 0, ColorStateList.valueOf(color), (ColorStateList) null);
    }

    @RequiresApi(16)
    private static void addMessagingFallBackStyle(NotificationCompat.MessagingStyle style, NotificationBuilderWithBuilderAccessor builder, NotificationCompat.Builder b) {
        SpannableStringBuilder completeMessage = new SpannableStringBuilder();
        List<NotificationCompat.MessagingStyle.Message> messages = style.getMessages();
        boolean showNames = style.getConversationTitle() != null || hasMessagesWithoutSender(style.getMessages());
        for (int i = messages.size() - 1; i >= 0; i--) {
            NotificationCompat.MessagingStyle.Message m = messages.get(i);
            CharSequence line = showNames ? makeMessageLine(b, style, m) : m.getText();
            if (i != messages.size() - 1) {
                completeMessage.insert(0, "\n");
            }
            completeMessage.insert(0, line);
        }
        NotificationCompatImplJellybean.addBigTextStyle(builder, completeMessage);
    }

    private static boolean hasMessagesWithoutSender(List<NotificationCompat.MessagingStyle.Message> messages) {
        for (int i = messages.size() - 1; i >= 0; i--) {
            if (messages.get(i).getSender() == null) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: private */
    @RequiresApi(14)
    public static RemoteViews addStyleGetContentViewIcs(NotificationBuilderWithBuilderAccessor builder, NotificationCompat.Builder b) {
        NotificationCompat.Builder builder2 = b;
        if (builder2.mStyle instanceof MediaStyle) {
            MediaStyle mediaStyle = (MediaStyle) builder2.mStyle;
            boolean isDecorated = (builder2.mStyle instanceof DecoratedMediaCustomViewStyle) && b.getContentView() != null;
            boolean isDecorated2 = isDecorated;
            RemoteViews contentViewMedia = NotificationCompatImplBase.overrideContentViewMedia(builder, builder2.mContext, builder2.mContentTitle, builder2.mContentText, builder2.mContentInfo, builder2.mNumber, builder2.mLargeIcon, builder2.mSubText, builder2.mUseChronometer, b.getWhenIfShowing(), b.getPriority(), builder2.mActions, mediaStyle.mActionsToShowInCompact, mediaStyle.mShowCancelButton, mediaStyle.mCancelButtonIntent, isDecorated2);
            if (isDecorated2) {
                NotificationCompatImplBase.buildIntoRemoteViews(b.mContext, contentViewMedia, b.getContentView());
                return contentViewMedia;
            }
            NotificationCompat.Builder builder3 = b;
            return null;
        } else if (builder2.mStyle instanceof DecoratedCustomViewStyle) {
            return getDecoratedContentView(b);
        } else {
            return null;
        }
    }

    /* access modifiers changed from: private */
    @RequiresApi(16)
    public static void addBigStyleToBuilderJellybean(Notification n, NotificationCompat.Builder b) {
        RemoteViews innerView;
        NotificationCompat.Builder builder = b;
        if (builder.mStyle instanceof MediaStyle) {
            MediaStyle mediaStyle = (MediaStyle) builder.mStyle;
            if (b.getBigContentView() != null) {
                innerView = b.getBigContentView();
            } else {
                innerView = b.getContentView();
            }
            boolean isDecorated = (builder.mStyle instanceof DecoratedMediaCustomViewStyle) && innerView != null;
            RemoteViews innerView2 = innerView;
            NotificationCompatImplBase.overrideMediaBigContentView(n, builder.mContext, builder.mContentTitle, builder.mContentText, builder.mContentInfo, builder.mNumber, builder.mLargeIcon, builder.mSubText, builder.mUseChronometer, b.getWhenIfShowing(), b.getPriority(), 0, builder.mActions, mediaStyle.mShowCancelButton, mediaStyle.mCancelButtonIntent, isDecorated);
            if (isDecorated) {
                NotificationCompatImplBase.buildIntoRemoteViews(b.mContext, n.bigContentView, innerView2);
                return;
            }
            Notification notification = n;
            NotificationCompat.Builder builder2 = b;
            return;
        }
        Notification notification2 = n;
        if (builder.mStyle instanceof DecoratedCustomViewStyle) {
            addDecoratedBigStyleToBuilderJellybean(n, b);
        }
    }

    private static RemoteViews getDecoratedContentView(NotificationCompat.Builder b) {
        NotificationCompat.Builder builder = b;
        if (b.getContentView() == null) {
            return null;
        }
        RemoteViews remoteViews = NotificationCompatImplBase.applyStandardTemplateWithActions(builder.mContext, builder.mContentTitle, builder.mContentText, builder.mContentInfo, builder.mNumber, builder.mNotification.icon, builder.mLargeIcon, builder.mSubText, builder.mUseChronometer, b.getWhenIfShowing(), b.getPriority(), b.getColor(), R.layout.notification_template_custom_big, false, (ArrayList<NotificationCompat.Action>) null);
        NotificationCompatImplBase.buildIntoRemoteViews(builder.mContext, remoteViews, b.getContentView());
        return remoteViews;
    }

    @RequiresApi(16)
    private static void addDecoratedBigStyleToBuilderJellybean(Notification n, NotificationCompat.Builder b) {
        Notification notification = n;
        NotificationCompat.Builder builder = b;
        RemoteViews bigContentView = b.getBigContentView();
        RemoteViews innerView = bigContentView != null ? bigContentView : b.getContentView();
        if (innerView != null) {
            RemoteViews remoteViews = bigContentView;
            RemoteViews remoteViews2 = NotificationCompatImplBase.applyStandardTemplateWithActions(builder.mContext, builder.mContentTitle, builder.mContentText, builder.mContentInfo, builder.mNumber, notification.icon, builder.mLargeIcon, builder.mSubText, builder.mUseChronometer, b.getWhenIfShowing(), b.getPriority(), b.getColor(), R.layout.notification_template_custom_big, false, builder.mActions);
            NotificationCompatImplBase.buildIntoRemoteViews(builder.mContext, remoteViews2, innerView);
            notification.bigContentView = remoteViews2;
        }
    }

    @RequiresApi(21)
    private static void addDecoratedHeadsUpToBuilderLollipop(Notification n, NotificationCompat.Builder b) {
        Notification notification = n;
        NotificationCompat.Builder builder = b;
        RemoteViews headsUp = b.getHeadsUpContentView();
        RemoteViews innerView = headsUp != null ? headsUp : b.getContentView();
        if (headsUp != null) {
            RemoteViews remoteViews = headsUp;
            RemoteViews remoteViews2 = NotificationCompatImplBase.applyStandardTemplateWithActions(builder.mContext, builder.mContentTitle, builder.mContentText, builder.mContentInfo, builder.mNumber, notification.icon, builder.mLargeIcon, builder.mSubText, builder.mUseChronometer, b.getWhenIfShowing(), b.getPriority(), b.getColor(), R.layout.notification_template_custom_big, false, builder.mActions);
            NotificationCompatImplBase.buildIntoRemoteViews(builder.mContext, remoteViews2, innerView);
            notification.headsUpContentView = remoteViews2;
        }
    }

    /* access modifiers changed from: private */
    @RequiresApi(21)
    public static void addBigStyleToBuilderLollipop(Notification n, NotificationCompat.Builder b) {
        RemoteViews remoteViews;
        Notification notification = n;
        NotificationCompat.Builder builder = b;
        if (b.getBigContentView() != null) {
            remoteViews = b.getBigContentView();
        } else {
            remoteViews = b.getContentView();
        }
        RemoteViews innerView = remoteViews;
        if (!(builder.mStyle instanceof DecoratedMediaCustomViewStyle) || innerView == null) {
            Notification notification2 = notification;
            if (builder.mStyle instanceof DecoratedCustomViewStyle) {
                addDecoratedBigStyleToBuilderJellybean(n, b);
                return;
            }
            return;
        }
        NotificationCompatImplBase.overrideMediaBigContentView(notification, builder.mContext, builder.mContentTitle, builder.mContentText, builder.mContentInfo, builder.mNumber, builder.mLargeIcon, builder.mSubText, builder.mUseChronometer, b.getWhenIfShowing(), b.getPriority(), 0, builder.mActions, false, (PendingIntent) null, true);
        NotificationCompat.Builder builder2 = b;
        Notification notification3 = n;
        NotificationCompatImplBase.buildIntoRemoteViews(builder2.mContext, notification3.bigContentView, innerView);
        setBackgroundColor(builder2.mContext, notification3.bigContentView, b.getColor());
    }

    private static void setBackgroundColor(Context context, RemoteViews views, int color) {
        if (color == 0) {
            color = context.getResources().getColor(R.color.notification_material_background_media_default_color);
        }
        views.setInt(R.id.status_bar_latest_event_content, "setBackgroundColor", color);
    }

    /* access modifiers changed from: private */
    @RequiresApi(21)
    public static void addHeadsUpToBuilderLollipop(Notification n, NotificationCompat.Builder b) {
        RemoteViews innerView;
        Notification notification = n;
        NotificationCompat.Builder builder = b;
        if (b.getHeadsUpContentView() != null) {
            innerView = b.getHeadsUpContentView();
        } else {
            innerView = b.getContentView();
        }
        if ((builder.mStyle instanceof DecoratedMediaCustomViewStyle) && innerView != null) {
            notification.headsUpContentView = NotificationCompatImplBase.generateMediaBigView(builder.mContext, builder.mContentTitle, builder.mContentText, builder.mContentInfo, builder.mNumber, builder.mLargeIcon, builder.mSubText, builder.mUseChronometer, b.getWhenIfShowing(), b.getPriority(), 0, builder.mActions, false, (PendingIntent) null, true);
            NotificationCompatImplBase.buildIntoRemoteViews(builder.mContext, notification.headsUpContentView, innerView);
            setBackgroundColor(builder.mContext, notification.headsUpContentView, b.getColor());
        } else if (builder.mStyle instanceof DecoratedCustomViewStyle) {
            addDecoratedHeadsUpToBuilderLollipop(n, b);
        }
    }

    public static class Builder extends NotificationCompat.Builder {
        public Builder(Context context) {
            super(context);
        }

        /* access modifiers changed from: protected */
        @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
        public CharSequence resolveText() {
            if (this.mStyle instanceof NotificationCompat.MessagingStyle) {
                NotificationCompat.MessagingStyle style = (NotificationCompat.MessagingStyle) this.mStyle;
                NotificationCompat.MessagingStyle.Message m = NotificationCompat.findLatestIncomingMessage(style);
                CharSequence conversationTitle = style.getConversationTitle();
                if (m != null) {
                    if (conversationTitle != null) {
                        return NotificationCompat.makeMessageLine(this, style, m);
                    }
                    return m.getText();
                }
            }
            return super.resolveText();
        }

        /* access modifiers changed from: protected */
        @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
        public CharSequence resolveTitle() {
            if (this.mStyle instanceof NotificationCompat.MessagingStyle) {
                NotificationCompat.MessagingStyle style = (NotificationCompat.MessagingStyle) this.mStyle;
                NotificationCompat.MessagingStyle.Message m = NotificationCompat.findLatestIncomingMessage(style);
                CharSequence conversationTitle = style.getConversationTitle();
                if (!(conversationTitle == null && m == null)) {
                    return conversationTitle != null ? conversationTitle : m.getSender();
                }
            }
            return super.resolveTitle();
        }

        /* access modifiers changed from: protected */
        @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
        public NotificationCompat.BuilderExtender getExtender() {
            if (Build.VERSION.SDK_INT >= 24) {
                return new Api24Extender();
            }
            if (Build.VERSION.SDK_INT >= 21) {
                return new LollipopExtender();
            }
            if (Build.VERSION.SDK_INT >= 16) {
                return new JellybeanExtender();
            }
            if (Build.VERSION.SDK_INT >= 14) {
                return new IceCreamSandwichExtender();
            }
            return super.getExtender();
        }
    }

    @RequiresApi(14)
    private static class IceCreamSandwichExtender extends NotificationCompat.BuilderExtender {
        IceCreamSandwichExtender() {
        }

        public Notification build(NotificationCompat.Builder b, NotificationBuilderWithBuilderAccessor builder) {
            RemoteViews contentView = NotificationCompat.addStyleGetContentViewIcs(builder, b);
            Notification n = builder.build();
            if (contentView != null) {
                n.contentView = contentView;
            } else if (b.getContentView() != null) {
                n.contentView = b.getContentView();
            }
            return n;
        }
    }

    @RequiresApi(16)
    private static class JellybeanExtender extends NotificationCompat.BuilderExtender {
        JellybeanExtender() {
        }

        public Notification build(NotificationCompat.Builder b, NotificationBuilderWithBuilderAccessor builder) {
            RemoteViews contentView = NotificationCompat.addStyleGetContentViewJellybean(builder, b);
            Notification n = builder.build();
            if (contentView != null) {
                n.contentView = contentView;
            }
            NotificationCompat.addBigStyleToBuilderJellybean(n, b);
            return n;
        }
    }

    @RequiresApi(21)
    private static class LollipopExtender extends NotificationCompat.BuilderExtender {
        LollipopExtender() {
        }

        public Notification build(NotificationCompat.Builder b, NotificationBuilderWithBuilderAccessor builder) {
            RemoteViews contentView = NotificationCompat.addStyleGetContentViewLollipop(builder, b);
            Notification n = builder.build();
            if (contentView != null) {
                n.contentView = contentView;
            }
            NotificationCompat.addBigStyleToBuilderLollipop(n, b);
            NotificationCompat.addHeadsUpToBuilderLollipop(n, b);
            return n;
        }
    }

    @RequiresApi(24)
    private static class Api24Extender extends NotificationCompat.BuilderExtender {
        private Api24Extender() {
        }

        public Notification build(NotificationCompat.Builder b, NotificationBuilderWithBuilderAccessor builder) {
            NotificationCompat.addStyleToBuilderApi24(builder, b);
            return builder.build();
        }
    }

    public static class MediaStyle extends NotificationCompat.Style {
        int[] mActionsToShowInCompact = null;
        PendingIntent mCancelButtonIntent;
        boolean mShowCancelButton;
        MediaSessionCompat.Token mToken;

        public MediaStyle() {
        }

        public MediaStyle(NotificationCompat.Builder builder) {
            setBuilder(builder);
        }

        public MediaStyle setShowActionsInCompactView(int... actions) {
            this.mActionsToShowInCompact = actions;
            return this;
        }

        public MediaStyle setMediaSession(MediaSessionCompat.Token token) {
            this.mToken = token;
            return this;
        }

        public MediaStyle setShowCancelButton(boolean show) {
            this.mShowCancelButton = show;
            return this;
        }

        public MediaStyle setCancelButtonIntent(PendingIntent pendingIntent) {
            this.mCancelButtonIntent = pendingIntent;
            return this;
        }
    }
}
