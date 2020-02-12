package android.support.v7.app;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationBuilderWithBuilderAccessor;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompatBase;
import android.support.v7.appcompat.R;
import android.widget.RemoteViews;
import java.util.ArrayList;
import java.util.List;

@RequiresApi(9)
class NotificationCompatImplBase {
    private static final int MAX_ACTION_BUTTONS = 3;
    static final int MAX_MEDIA_BUTTONS = 5;
    static final int MAX_MEDIA_BUTTONS_IN_COMPACT = 3;

    NotificationCompatImplBase() {
    }

    @RequiresApi(11)
    public static <T extends NotificationCompatBase.Action> RemoteViews overrideContentViewMedia(NotificationBuilderWithBuilderAccessor builder, Context context, CharSequence contentTitle, CharSequence contentText, CharSequence contentInfo, int number, Bitmap largeIcon, CharSequence subText, boolean useChronometer, long when, int priority, List<T> actions, int[] actionsToShowInCompact, boolean showCancelButton, PendingIntent cancelButtonIntent, boolean isDecoratedCustomView) {
        RemoteViews views = generateContentViewMedia(context, contentTitle, contentText, contentInfo, number, largeIcon, subText, useChronometer, when, priority, actions, actionsToShowInCompact, showCancelButton, cancelButtonIntent, isDecoratedCustomView);
        builder.getBuilder().setContent(views);
        if (showCancelButton) {
            builder.getBuilder().setOngoing(true);
        }
        return views;
    }

    @RequiresApi(11)
    private static <T extends NotificationCompatBase.Action> RemoteViews generateContentViewMedia(Context context, CharSequence contentTitle, CharSequence contentText, CharSequence contentInfo, int number, Bitmap largeIcon, CharSequence subText, boolean useChronometer, long when, int priority, List<T> actions, int[] actionsToShowInCompact, boolean showCancelButton, PendingIntent cancelButtonIntent, boolean isDecoratedCustomView) {
        int[] iArr = actionsToShowInCompact;
        RemoteViews view = applyStandardTemplate(context, contentTitle, contentText, contentInfo, number, 0, largeIcon, subText, useChronometer, when, priority, 0, isDecoratedCustomView ? R.layout.notification_template_media_custom : R.layout.notification_template_media, true);
        int numActions = actions.size();
        int N = iArr == null ? 0 : Math.min(iArr.length, 3);
        view.removeAllViews(R.id.media_actions);
        if (N > 0) {
            for (int i = 0; i < N; i++) {
                if (i >= numActions) {
                    throw new IllegalArgumentException(String.format("setShowActionsInCompactView: action %d out of bounds (max %d)", new Object[]{Integer.valueOf(i), Integer.valueOf(numActions - 1)}));
                }
                view.addView(R.id.media_actions, generateMediaActionButton(context, (NotificationCompatBase.Action) actions.get(iArr[i])));
            }
        }
        Context context2 = context;
        List<T> list = actions;
        if (showCancelButton) {
            view.setViewVisibility(R.id.end_padder, 8);
            view.setViewVisibility(R.id.cancel_action, 0);
            view.setOnClickPendingIntent(R.id.cancel_action, cancelButtonIntent);
            view.setInt(R.id.cancel_action, "setAlpha", context.getResources().getInteger(R.integer.cancel_button_image_alpha));
        } else {
            PendingIntent pendingIntent = cancelButtonIntent;
            view.setViewVisibility(R.id.end_padder, 0);
            view.setViewVisibility(R.id.cancel_action, 8);
        }
        return view;
    }

    @RequiresApi(16)
    public static <T extends NotificationCompatBase.Action> void overrideMediaBigContentView(Notification n, Context context, CharSequence contentTitle, CharSequence contentText, CharSequence contentInfo, int number, Bitmap largeIcon, CharSequence subText, boolean useChronometer, long when, int priority, int color, List<T> actions, boolean showCancelButton, PendingIntent cancelButtonIntent, boolean decoratedCustomView) {
        Notification notification = n;
        notification.bigContentView = generateMediaBigView(context, contentTitle, contentText, contentInfo, number, largeIcon, subText, useChronometer, when, priority, color, actions, showCancelButton, cancelButtonIntent, decoratedCustomView);
        if (showCancelButton) {
            notification.flags |= 2;
        }
    }

    @RequiresApi(11)
    public static <T extends NotificationCompatBase.Action> RemoteViews generateMediaBigView(Context context, CharSequence contentTitle, CharSequence contentText, CharSequence contentInfo, int number, Bitmap largeIcon, CharSequence subText, boolean useChronometer, long when, int priority, int color, List<T> actions, boolean showCancelButton, PendingIntent cancelButtonIntent, boolean decoratedCustomView) {
        int actionCount = Math.min(actions.size(), 5);
        RemoteViews big = applyStandardTemplate(context, contentTitle, contentText, contentInfo, number, 0, largeIcon, subText, useChronometer, when, priority, color, getBigMediaLayoutResource(decoratedCustomView, actionCount), false);
        big.removeAllViews(R.id.media_actions);
        if (actionCount > 0) {
            for (int i = 0; i < actionCount; i++) {
                big.addView(R.id.media_actions, generateMediaActionButton(context, (NotificationCompatBase.Action) actions.get(i)));
            }
        }
        Context context2 = context;
        List<T> list = actions;
        if (showCancelButton) {
            big.setViewVisibility(R.id.cancel_action, 0);
            big.setInt(R.id.cancel_action, "setAlpha", context.getResources().getInteger(R.integer.cancel_button_image_alpha));
            big.setOnClickPendingIntent(R.id.cancel_action, cancelButtonIntent);
        } else {
            PendingIntent pendingIntent = cancelButtonIntent;
            big.setViewVisibility(R.id.cancel_action, 8);
        }
        return big;
    }

    @RequiresApi(11)
    private static RemoteViews generateMediaActionButton(Context context, NotificationCompatBase.Action action) {
        boolean tombstone = action.getActionIntent() == null;
        RemoteViews button = new RemoteViews(context.getPackageName(), R.layout.notification_media_action);
        button.setImageViewResource(R.id.action0, action.getIcon());
        if (!tombstone) {
            button.setOnClickPendingIntent(R.id.action0, action.getActionIntent());
        }
        if (Build.VERSION.SDK_INT >= 15) {
            button.setContentDescription(R.id.action0, action.getTitle());
        }
        return button;
    }

    @RequiresApi(11)
    private static int getBigMediaLayoutResource(boolean decoratedCustomView, int actionCount) {
        return actionCount <= 3 ? decoratedCustomView ? R.layout.notification_template_big_media_narrow_custom : R.layout.notification_template_big_media_narrow : decoratedCustomView ? R.layout.notification_template_big_media_custom : R.layout.notification_template_big_media;
    }

    public static RemoteViews applyStandardTemplateWithActions(Context context, CharSequence contentTitle, CharSequence contentText, CharSequence contentInfo, int number, int smallIcon, Bitmap largeIcon, CharSequence subText, boolean useChronometer, long when, int priority, int color, int resId, boolean fitIn1U, ArrayList<NotificationCompat.Action> actions) {
        int N;
        ArrayList<NotificationCompat.Action> arrayList = actions;
        RemoteViews remoteViews = applyStandardTemplate(context, contentTitle, contentText, contentInfo, number, smallIcon, largeIcon, subText, useChronometer, when, priority, color, resId, fitIn1U);
        remoteViews.removeAllViews(R.id.actions);
        boolean actionsVisible = false;
        int actionVisibility = 0;
        if (arrayList != null && (N = actions.size()) > 0) {
            actionsVisible = true;
            if (N > 3) {
                N = 3;
            }
            for (int i = 0; i < N; i++) {
                remoteViews.addView(R.id.actions, generateActionButton(context, arrayList.get(i)));
            }
        }
        Context context2 = context;
        if (!actionsVisible) {
            actionVisibility = 8;
        }
        remoteViews.setViewVisibility(R.id.actions, actionVisibility);
        remoteViews.setViewVisibility(R.id.action_divider, actionVisibility);
        return remoteViews;
    }

    private static RemoteViews generateActionButton(Context context, NotificationCompat.Action action) {
        int i;
        boolean tombstone = action.actionIntent == null;
        String packageName = context.getPackageName();
        if (tombstone) {
            i = getActionTombstoneLayoutResource();
        } else {
            i = getActionLayoutResource();
        }
        RemoteViews button = new RemoteViews(packageName, i);
        button.setImageViewBitmap(R.id.action_image, createColoredBitmap(context, action.getIcon(), context.getResources().getColor(R.color.notification_action_color_filter)));
        button.setTextViewText(R.id.action_text, action.title);
        if (!tombstone) {
            button.setOnClickPendingIntent(R.id.action_container, action.actionIntent);
        }
        if (Build.VERSION.SDK_INT >= 15) {
            button.setContentDescription(R.id.action_container, action.title);
        }
        return button;
    }

    private static Bitmap createColoredBitmap(Context context, int iconId, int color) {
        return createColoredBitmap(context, iconId, color, 0);
    }

    private static Bitmap createColoredBitmap(Context context, int iconId, int color, int size) {
        Drawable drawable = context.getResources().getDrawable(iconId);
        int width = size == 0 ? drawable.getIntrinsicWidth() : size;
        int height = size == 0 ? drawable.getIntrinsicHeight() : size;
        Bitmap resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        drawable.setBounds(0, 0, width, height);
        if (color != 0) {
            drawable.mutate().setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
        }
        drawable.draw(new Canvas(resultBitmap));
        return resultBitmap;
    }

    private static int getActionLayoutResource() {
        return R.layout.notification_action;
    }

    private static int getActionTombstoneLayoutResource() {
        return R.layout.notification_action_tombstone;
    }

    /* JADX WARNING: Removed duplicated region for block: B:60:0x0183  */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x01a6  */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x01e0  */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x01e5  */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x01e7  */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x01f1  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.widget.RemoteViews applyStandardTemplate(android.content.Context r25, java.lang.CharSequence r26, java.lang.CharSequence r27, java.lang.CharSequence r28, int r29, int r30, android.graphics.Bitmap r31, java.lang.CharSequence r32, boolean r33, long r34, int r36, int r37, int r38, boolean r39) {
        /*
            r0 = r25
            r1 = r26
            r2 = r27
            r3 = r28
            r4 = r29
            r5 = r30
            r6 = r31
            r7 = r32
            r8 = r34
            r10 = r37
            android.content.res.Resources r11 = r25.getResources()
            android.widget.RemoteViews r12 = new android.widget.RemoteViews
            java.lang.String r13 = r25.getPackageName()
            r14 = r38
            r12.<init>(r13, r14)
            r13 = 0
            r15 = 0
            r21 = r13
            r13 = -1
            r14 = r36
            if (r14 >= r13) goto L_0x002f
            r16 = 1
            goto L_0x0031
        L_0x002f:
            r16 = 0
        L_0x0031:
            r22 = r16
            int r13 = android.os.Build.VERSION.SDK_INT
            r14 = 16
            if (r13 < r14) goto L_0x006b
            int r13 = android.os.Build.VERSION.SDK_INT
            r14 = 21
            if (r13 >= r14) goto L_0x006b
            if (r22 == 0) goto L_0x0056
            int r13 = android.support.v7.appcompat.R.id.notification_background
            java.lang.String r14 = "setBackgroundResource"
            r23 = r15
            int r15 = android.support.v7.appcompat.R.drawable.notification_bg_low
            r12.setInt(r13, r14, r15)
            int r13 = android.support.v7.appcompat.R.id.icon
            java.lang.String r14 = "setBackgroundResource"
            int r15 = android.support.v7.appcompat.R.drawable.notification_template_icon_low_bg
            r12.setInt(r13, r14, r15)
            goto L_0x006d
        L_0x0056:
            r23 = r15
            int r13 = android.support.v7.appcompat.R.id.notification_background
            java.lang.String r14 = "setBackgroundResource"
            int r15 = android.support.v7.appcompat.R.drawable.notification_bg
            r12.setInt(r13, r14, r15)
            int r13 = android.support.v7.appcompat.R.id.icon
            java.lang.String r14 = "setBackgroundResource"
            int r15 = android.support.v7.appcompat.R.drawable.notification_template_icon_bg
            r12.setInt(r13, r14, r15)
            goto L_0x006d
        L_0x006b:
            r23 = r15
        L_0x006d:
            r14 = 8
            if (r6 == 0) goto L_0x00bb
            int r13 = android.os.Build.VERSION.SDK_INT
            r15 = 16
            if (r13 < r15) goto L_0x0083
            int r13 = android.support.v7.appcompat.R.id.icon
            r15 = 0
            r12.setViewVisibility(r13, r15)
            int r13 = android.support.v7.appcompat.R.id.icon
            r12.setImageViewBitmap(r13, r6)
            goto L_0x0088
        L_0x0083:
            int r13 = android.support.v7.appcompat.R.id.icon
            r12.setViewVisibility(r13, r14)
        L_0x0088:
            if (r5 == 0) goto L_0x00f0
            int r13 = android.support.v7.appcompat.R.dimen.notification_right_icon_size
            int r13 = r11.getDimensionPixelSize(r13)
            int r15 = android.support.v7.appcompat.R.dimen.notification_small_icon_background_padding
            int r15 = r11.getDimensionPixelSize(r15)
            int r15 = r15 * 2
            int r15 = r13 - r15
            int r14 = android.os.Build.VERSION.SDK_INT
            r8 = 21
            if (r14 < r8) goto L_0x00aa
            android.graphics.Bitmap r8 = createIconWithBackground(r0, r5, r13, r15, r10)
            int r9 = android.support.v7.appcompat.R.id.right_icon
            r12.setImageViewBitmap(r9, r8)
            goto L_0x00b4
        L_0x00aa:
            int r8 = android.support.v7.appcompat.R.id.right_icon
            r9 = -1
            android.graphics.Bitmap r9 = createColoredBitmap(r0, r5, r9)
            r12.setImageViewBitmap(r8, r9)
        L_0x00b4:
            int r8 = android.support.v7.appcompat.R.id.right_icon
            r9 = 0
            r12.setViewVisibility(r8, r9)
            goto L_0x00f0
        L_0x00bb:
            r9 = 0
            if (r5 == 0) goto L_0x00f0
            int r8 = android.support.v7.appcompat.R.id.icon
            r12.setViewVisibility(r8, r9)
            int r8 = android.os.Build.VERSION.SDK_INT
            r9 = 21
            if (r8 < r9) goto L_0x00e6
            int r8 = android.support.v7.appcompat.R.dimen.notification_large_icon_width
            int r8 = r11.getDimensionPixelSize(r8)
            int r9 = android.support.v7.appcompat.R.dimen.notification_big_circle_margin
            int r9 = r11.getDimensionPixelSize(r9)
            int r8 = r8 - r9
            int r9 = android.support.v7.appcompat.R.dimen.notification_small_icon_size_as_large
            int r9 = r11.getDimensionPixelSize(r9)
            android.graphics.Bitmap r13 = createIconWithBackground(r0, r5, r8, r9, r10)
            int r14 = android.support.v7.appcompat.R.id.icon
            r12.setImageViewBitmap(r14, r13)
            goto L_0x00f0
        L_0x00e6:
            int r8 = android.support.v7.appcompat.R.id.icon
            r9 = -1
            android.graphics.Bitmap r9 = createColoredBitmap(r0, r5, r9)
            r12.setImageViewBitmap(r8, r9)
        L_0x00f0:
            if (r1 == 0) goto L_0x00f7
            int r8 = android.support.v7.appcompat.R.id.title
            r12.setTextViewText(r8, r1)
        L_0x00f7:
            if (r2 == 0) goto L_0x0101
            int r8 = android.support.v7.appcompat.R.id.text
            r12.setTextViewText(r8, r2)
            r13 = 1
            r21 = r13
        L_0x0101:
            int r8 = android.os.Build.VERSION.SDK_INT
            r9 = 21
            if (r8 >= r9) goto L_0x010b
            if (r6 == 0) goto L_0x010b
            r8 = 1
            goto L_0x010c
        L_0x010b:
            r8 = 0
        L_0x010c:
            if (r3 == 0) goto L_0x011d
            int r9 = android.support.v7.appcompat.R.id.info
            r12.setTextViewText(r9, r3)
            int r9 = android.support.v7.appcompat.R.id.info
            r13 = 0
            r12.setViewVisibility(r9, r13)
            r21 = 1
            r8 = 1
            goto L_0x0152
        L_0x011d:
            if (r4 <= 0) goto L_0x014b
            int r9 = android.support.v7.appcompat.R.integer.status_bar_notification_info_maxnum
            int r9 = r11.getInteger(r9)
            if (r4 <= r9) goto L_0x0133
            int r13 = android.support.v7.appcompat.R.id.info
            int r14 = android.support.v7.appcompat.R.string.status_bar_notification_info_overflow
            java.lang.String r14 = r11.getString(r14)
            r12.setTextViewText(r13, r14)
            goto L_0x0141
        L_0x0133:
            java.text.NumberFormat r13 = java.text.NumberFormat.getIntegerInstance()
            int r14 = android.support.v7.appcompat.R.id.info
            long r0 = (long) r4
            java.lang.String r0 = r13.format(r0)
            r12.setTextViewText(r14, r0)
        L_0x0141:
            int r0 = android.support.v7.appcompat.R.id.info
            r1 = 0
            r12.setViewVisibility(r0, r1)
            r21 = 1
            r8 = 1
            goto L_0x0152
        L_0x014b:
            int r0 = android.support.v7.appcompat.R.id.info
            r1 = 8
            r12.setViewVisibility(r0, r1)
        L_0x0152:
            if (r7 == 0) goto L_0x016f
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 16
            if (r0 < r1) goto L_0x016f
            int r0 = android.support.v7.appcompat.R.id.text
            r12.setTextViewText(r0, r7)
            if (r2 == 0) goto L_0x0172
            int r0 = android.support.v7.appcompat.R.id.text2
            r12.setTextViewText(r0, r2)
            int r0 = android.support.v7.appcompat.R.id.text2
            r1 = 0
            r12.setViewVisibility(r0, r1)
            r15 = 1
            r23 = r15
        L_0x016f:
            r9 = 8
            goto L_0x0179
        L_0x0172:
            int r0 = android.support.v7.appcompat.R.id.text2
            r9 = 8
            r12.setViewVisibility(r0, r9)
        L_0x0179:
            if (r23 == 0) goto L_0x019e
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 16
            if (r0 < r1) goto L_0x019e
            if (r39 == 0) goto L_0x0190
            int r1 = android.support.v7.appcompat.R.dimen.notification_subtext_size
            int r1 = r11.getDimensionPixelSize(r1)
            float r1 = (float) r1
            int r13 = android.support.v7.appcompat.R.id.text
            r14 = 0
            r12.setTextViewTextSize(r13, r14, r1)
        L_0x0190:
            int r16 = android.support.v7.appcompat.R.id.line1
            r17 = 0
            r18 = 0
            r19 = 0
            r20 = 0
            r15 = r12
            r15.setViewPadding(r16, r17, r18, r19, r20)
        L_0x019e:
            r13 = 0
            r9 = r34
            int r1 = (r9 > r13 ? 1 : (r9 == r13 ? 0 : -1))
            if (r1 == 0) goto L_0x01e0
            if (r33 == 0) goto L_0x01d1
            int r13 = android.os.Build.VERSION.SDK_INT
            r14 = 16
            if (r13 < r14) goto L_0x01d1
            int r13 = android.support.v7.appcompat.R.id.chronometer
            r14 = 0
            r12.setViewVisibility(r13, r14)
            int r13 = android.support.v7.appcompat.R.id.chronometer
            java.lang.String r14 = "setBase"
            long r15 = android.os.SystemClock.elapsedRealtime()
            long r17 = java.lang.System.currentTimeMillis()
            long r19 = r15 - r17
            long r0 = r9 + r19
            r12.setLong(r13, r14, r0)
            int r0 = android.support.v7.appcompat.R.id.chronometer
            java.lang.String r1 = "setStarted"
            r13 = 1
            r12.setBoolean(r0, r1, r13)
            r14 = 0
            goto L_0x01de
        L_0x01d1:
            int r0 = android.support.v7.appcompat.R.id.time
            r14 = 0
            r12.setViewVisibility(r0, r14)
            int r0 = android.support.v7.appcompat.R.id.time
            java.lang.String r1 = "setTime"
            r12.setLong(r0, r1, r9)
        L_0x01de:
            r8 = 1
            goto L_0x01e1
        L_0x01e0:
            r14 = 0
        L_0x01e1:
            int r0 = android.support.v7.appcompat.R.id.right_side
            if (r8 == 0) goto L_0x01e7
            r1 = r14
            goto L_0x01e9
        L_0x01e7:
            r1 = 8
        L_0x01e9:
            r12.setViewVisibility(r0, r1)
            int r0 = android.support.v7.appcompat.R.id.line3
            if (r21 == 0) goto L_0x01f1
            goto L_0x01f3
        L_0x01f1:
            r14 = 8
        L_0x01f3:
            r12.setViewVisibility(r0, r14)
            return r12
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v7.app.NotificationCompatImplBase.applyStandardTemplate(android.content.Context, java.lang.CharSequence, java.lang.CharSequence, java.lang.CharSequence, int, int, android.graphics.Bitmap, java.lang.CharSequence, boolean, long, int, int, int, boolean):android.widget.RemoteViews");
    }

    public static Bitmap createIconWithBackground(Context ctx, int iconId, int size, int iconSize, int color) {
        Bitmap coloredBitmap = createColoredBitmap(ctx, R.drawable.notification_icon_background, color == 0 ? 0 : color, size);
        Canvas canvas = new Canvas(coloredBitmap);
        Drawable icon = ctx.getResources().getDrawable(iconId).mutate();
        icon.setFilterBitmap(true);
        int inset = (size - iconSize) / 2;
        icon.setBounds(inset, inset, iconSize + inset, iconSize + inset);
        icon.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.SRC_ATOP));
        icon.draw(canvas);
        return coloredBitmap;
    }

    public static void buildIntoRemoteViews(Context ctx, RemoteViews outerView, RemoteViews innerView) {
        hideNormalContent(outerView);
        outerView.removeAllViews(R.id.notification_main_column);
        outerView.addView(R.id.notification_main_column, innerView.clone());
        outerView.setViewVisibility(R.id.notification_main_column, 0);
        if (Build.VERSION.SDK_INT >= 21) {
            outerView.setViewPadding(R.id.notification_main_column_container, 0, calculateTopPadding(ctx), 0, 0);
        }
    }

    private static void hideNormalContent(RemoteViews outerView) {
        outerView.setViewVisibility(R.id.title, 8);
        outerView.setViewVisibility(R.id.text2, 8);
        outerView.setViewVisibility(R.id.text, 8);
    }

    public static int calculateTopPadding(Context ctx) {
        int padding = ctx.getResources().getDimensionPixelSize(R.dimen.notification_top_pad);
        int largePadding = ctx.getResources().getDimensionPixelSize(R.dimen.notification_top_pad_large_text);
        float largeFactor = (constrain(ctx.getResources().getConfiguration().fontScale, 1.0f, 1.3f) - 1.0f) / 0.29999995f;
        return Math.round(((1.0f - largeFactor) * ((float) padding)) + (((float) largePadding) * largeFactor));
    }

    public static float constrain(float amount, float low, float high) {
        if (amount < low) {
            return low;
        }
        return amount > high ? high : amount;
    }
}
