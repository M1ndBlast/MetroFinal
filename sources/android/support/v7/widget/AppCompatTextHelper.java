package android.support.v7.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.v7.appcompat.R;
import android.support.v7.text.AllCapsTransformationMethod;
import android.widget.TextView;

@RequiresApi(9)
class AppCompatTextHelper {
    @NonNull
    private final AppCompatTextViewAutoSizeHelper mAutoSizeTextHelper = new AppCompatTextViewAutoSizeHelper(this.mView);
    private TintInfo mDrawableBottomTint;
    private TintInfo mDrawableLeftTint;
    private TintInfo mDrawableRightTint;
    private TintInfo mDrawableTopTint;
    final TextView mView;

    static AppCompatTextHelper create(TextView textView) {
        if (Build.VERSION.SDK_INT >= 17) {
            return new AppCompatTextHelperV17(textView);
        }
        return new AppCompatTextHelper(textView);
    }

    AppCompatTextHelper(TextView view) {
        this.mView = view;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x01c9  */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x01d0  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void loadFromAttributes(android.util.AttributeSet r26, int r27) {
        /*
            r25 = this;
            r1 = r25
            r2 = r26
            r3 = r27
            android.widget.TextView r4 = r1.mView
            android.content.Context r4 = r4.getContext()
            android.support.v7.widget.AppCompatDrawableManager r5 = android.support.v7.widget.AppCompatDrawableManager.get()
            boolean r6 = r1.shouldLoadFontResources(r4)
            int[] r7 = android.support.v7.appcompat.R.styleable.AppCompatTextHelper
            r8 = 0
            android.support.v7.widget.TintTypedArray r7 = android.support.v7.widget.TintTypedArray.obtainStyledAttributes(r4, r2, r7, r3, r8)
            int r9 = android.support.v7.appcompat.R.styleable.AppCompatTextHelper_android_textAppearance
            r10 = -1
            int r9 = r7.getResourceId(r9, r10)
            int r11 = android.support.v7.appcompat.R.styleable.AppCompatTextHelper_android_drawableLeft
            boolean r11 = r7.hasValue(r11)
            if (r11 == 0) goto L_0x0036
            int r11 = android.support.v7.appcompat.R.styleable.AppCompatTextHelper_android_drawableLeft
            int r11 = r7.getResourceId(r11, r8)
            android.support.v7.widget.TintInfo r11 = createTintInfo(r4, r5, r11)
            r1.mDrawableLeftTint = r11
        L_0x0036:
            int r11 = android.support.v7.appcompat.R.styleable.AppCompatTextHelper_android_drawableTop
            boolean r11 = r7.hasValue(r11)
            if (r11 == 0) goto L_0x004a
            int r11 = android.support.v7.appcompat.R.styleable.AppCompatTextHelper_android_drawableTop
            int r11 = r7.getResourceId(r11, r8)
            android.support.v7.widget.TintInfo r11 = createTintInfo(r4, r5, r11)
            r1.mDrawableTopTint = r11
        L_0x004a:
            int r11 = android.support.v7.appcompat.R.styleable.AppCompatTextHelper_android_drawableRight
            boolean r11 = r7.hasValue(r11)
            if (r11 == 0) goto L_0x005e
            int r11 = android.support.v7.appcompat.R.styleable.AppCompatTextHelper_android_drawableRight
            int r11 = r7.getResourceId(r11, r8)
            android.support.v7.widget.TintInfo r11 = createTintInfo(r4, r5, r11)
            r1.mDrawableRightTint = r11
        L_0x005e:
            int r11 = android.support.v7.appcompat.R.styleable.AppCompatTextHelper_android_drawableBottom
            boolean r11 = r7.hasValue(r11)
            if (r11 == 0) goto L_0x0072
            int r11 = android.support.v7.appcompat.R.styleable.AppCompatTextHelper_android_drawableBottom
            int r11 = r7.getResourceId(r11, r8)
            android.support.v7.widget.TintInfo r11 = createTintInfo(r4, r5, r11)
            r1.mDrawableBottomTint = r11
        L_0x0072:
            r7.recycle()
            android.widget.TextView r11 = r1.mView
            android.text.method.TransformationMethod r11 = r11.getTransformationMethod()
            boolean r11 = r11 instanceof android.text.method.PasswordTransformationMethod
            r12 = 0
            r13 = 0
            r14 = 0
            r15 = 0
            r16 = 0
            r17 = 0
            r18 = 0
            if (r9 == r10) goto L_0x00f6
            int[] r10 = android.support.v7.appcompat.R.styleable.TextAppearance
            android.support.v7.widget.TintTypedArray r7 = android.support.v7.widget.TintTypedArray.obtainStyledAttributes((android.content.Context) r4, (int) r9, (int[]) r10)
            if (r11 != 0) goto L_0x00a3
            int r10 = android.support.v7.appcompat.R.styleable.TextAppearance_textAllCaps
            boolean r10 = r7.hasValue(r10)
            if (r10 == 0) goto L_0x00a3
            r10 = 1
            int r13 = android.support.v7.appcompat.R.styleable.TextAppearance_textAllCaps
            r8 = 0
            boolean r12 = r7.getBoolean(r13, r8)
            r13 = r10
            goto L_0x00a4
        L_0x00a3:
            r8 = 0
        L_0x00a4:
            if (r6 == 0) goto L_0x00c1
            int r10 = android.support.v7.appcompat.R.styleable.TextAppearance_android_textStyle
            int r10 = r7.getInt(r10, r8)
            int r8 = android.support.v7.appcompat.R.styleable.TextAppearance_android_fontFamily
            boolean r8 = r7.hasValue(r8)
            if (r8 == 0) goto L_0x00bf
            int r8 = android.support.v7.appcompat.R.styleable.TextAppearance_android_fontFamily     // Catch:{ NotFoundException | UnsupportedOperationException -> 0x00be }
            android.support.v4.graphics.TypefaceCompat$TypefaceHolder r8 = r7.getFont(r8, r10)     // Catch:{ NotFoundException | UnsupportedOperationException -> 0x00be }
            r17 = r8
            goto L_0x00bf
        L_0x00be:
            r0 = move-exception
        L_0x00bf:
            r18 = r10
        L_0x00c1:
            int r8 = android.os.Build.VERSION.SDK_INT
            r10 = 23
            if (r8 >= r10) goto L_0x00f3
            int r8 = android.support.v7.appcompat.R.styleable.TextAppearance_android_textColor
            boolean r8 = r7.hasValue(r8)
            if (r8 == 0) goto L_0x00d5
            int r8 = android.support.v7.appcompat.R.styleable.TextAppearance_android_textColor
            android.content.res.ColorStateList r14 = r7.getColorStateList(r8)
        L_0x00d5:
            int r8 = android.support.v7.appcompat.R.styleable.TextAppearance_android_textColorHint
            boolean r8 = r7.hasValue(r8)
            if (r8 == 0) goto L_0x00e3
            int r8 = android.support.v7.appcompat.R.styleable.TextAppearance_android_textColorHint
            android.content.res.ColorStateList r15 = r7.getColorStateList(r8)
        L_0x00e3:
            int r8 = android.support.v7.appcompat.R.styleable.TextAppearance_android_textColorLink
            boolean r8 = r7.hasValue(r8)
            if (r8 == 0) goto L_0x00f3
            int r8 = android.support.v7.appcompat.R.styleable.TextAppearance_android_textColorLink
            android.content.res.ColorStateList r8 = r7.getColorStateList(r8)
            r16 = r8
        L_0x00f3:
            r7.recycle()
        L_0x00f6:
            int[] r8 = android.support.v7.appcompat.R.styleable.TextAppearance
            r10 = 0
            android.support.v7.widget.TintTypedArray r7 = android.support.v7.widget.TintTypedArray.obtainStyledAttributes(r4, r2, r8, r3, r10)
            if (r11 != 0) goto L_0x010e
            int r8 = android.support.v7.appcompat.R.styleable.TextAppearance_textAllCaps
            boolean r8 = r7.hasValue(r8)
            if (r8 == 0) goto L_0x010e
            r13 = 1
            int r8 = android.support.v7.appcompat.R.styleable.TextAppearance_textAllCaps
            boolean r12 = r7.getBoolean(r8, r10)
        L_0x010e:
            int r8 = android.os.Build.VERSION.SDK_INT
            r10 = 23
            if (r8 >= r10) goto L_0x0140
            int r8 = android.support.v7.appcompat.R.styleable.TextAppearance_android_textColor
            boolean r8 = r7.hasValue(r8)
            if (r8 == 0) goto L_0x0123
            int r8 = android.support.v7.appcompat.R.styleable.TextAppearance_android_textColor
            android.content.res.ColorStateList r8 = r7.getColorStateList(r8)
            r14 = r8
        L_0x0123:
            int r8 = android.support.v7.appcompat.R.styleable.TextAppearance_android_textColorHint
            boolean r8 = r7.hasValue(r8)
            if (r8 == 0) goto L_0x0132
            int r8 = android.support.v7.appcompat.R.styleable.TextAppearance_android_textColorHint
            android.content.res.ColorStateList r8 = r7.getColorStateList(r8)
            r15 = r8
        L_0x0132:
            int r8 = android.support.v7.appcompat.R.styleable.TextAppearance_android_textColorLink
            boolean r8 = r7.hasValue(r8)
            if (r8 == 0) goto L_0x0140
            int r8 = android.support.v7.appcompat.R.styleable.TextAppearance_android_textColorLink
            android.content.res.ColorStateList r16 = r7.getColorStateList(r8)
        L_0x0140:
            r8 = r16
            if (r6 == 0) goto L_0x0162
            int r10 = android.support.v7.appcompat.R.styleable.TextAppearance_android_fontFamily
            boolean r10 = r7.hasValue(r10)
            if (r10 == 0) goto L_0x0162
            int r10 = android.support.v7.appcompat.R.styleable.TextAppearance_android_textStyle
            r19 = r4
            r4 = 0
            int r10 = r7.getInt(r10, r4)
            int r4 = android.support.v7.appcompat.R.styleable.TextAppearance_android_fontFamily     // Catch:{ NotFoundException | UnsupportedOperationException -> 0x015e }
            android.support.v4.graphics.TypefaceCompat$TypefaceHolder r4 = r7.getFont(r4, r10)     // Catch:{ NotFoundException | UnsupportedOperationException -> 0x015e }
            r17 = r4
            goto L_0x0168
        L_0x015e:
            r0 = move-exception
            r4 = r17
            goto L_0x0168
        L_0x0162:
            r19 = r4
            r4 = r17
            r10 = r18
        L_0x0168:
            r7.recycle()
            if (r14 == 0) goto L_0x0175
            r20 = r5
            android.widget.TextView r5 = r1.mView
            r5.setTextColor(r14)
            goto L_0x0177
        L_0x0175:
            r20 = r5
        L_0x0177:
            if (r15 == 0) goto L_0x017e
            android.widget.TextView r5 = r1.mView
            r5.setHintTextColor(r15)
        L_0x017e:
            if (r8 == 0) goto L_0x0185
            android.widget.TextView r5 = r1.mView
            r5.setLinkTextColor(r8)
        L_0x0185:
            if (r11 != 0) goto L_0x018c
            if (r13 == 0) goto L_0x018c
            r1.setAllCaps(r12)
        L_0x018c:
            if (r4 == 0) goto L_0x01d7
            android.widget.TextView r5 = r1.mView
            r21 = r6
            android.graphics.Typeface r6 = r4.getTypeface()
            r5.setTypeface(r6)
            android.widget.TextView r5 = r1.mView
            android.text.TextPaint r5 = r5.getPaint()
            r6 = r10 & 1
            r16 = 1
            if (r6 == 0) goto L_0x01b2
            int r6 = r4.getWeight()
            r22 = r7
            r7 = 600(0x258, float:8.41E-43)
            if (r6 >= r7) goto L_0x01b4
            r6 = r16
            goto L_0x01b5
        L_0x01b2:
            r22 = r7
        L_0x01b4:
            r6 = 0
        L_0x01b5:
            r5.setFakeBoldText(r6)
            r7 = r10 & 2
            if (r7 == 0) goto L_0x01c3
            boolean r7 = r4.isItalic()
            if (r7 != 0) goto L_0x01c3
            goto L_0x01c5
        L_0x01c3:
            r16 = 0
        L_0x01c5:
            r7 = r16
            if (r7 == 0) goto L_0x01d0
            r16 = -1098907648(0xffffffffbe800000, float:-0.25)
        L_0x01cb:
            r23 = r4
            r4 = r16
            goto L_0x01d3
        L_0x01d0:
            r16 = 0
            goto L_0x01cb
        L_0x01d3:
            r5.setTextSkewX(r4)
            goto L_0x01dd
        L_0x01d7:
            r23 = r4
            r21 = r6
            r22 = r7
        L_0x01dd:
            android.support.v7.widget.AppCompatTextViewAutoSizeHelper r4 = r1.mAutoSizeTextHelper
            r4.loadFromAttributes(r2, r3)
            int r4 = android.os.Build.VERSION.SDK_INT
            r5 = 26
            if (r4 < r5) goto L_0x0221
            android.support.v7.widget.AppCompatTextViewAutoSizeHelper r4 = r1.mAutoSizeTextHelper
            int r4 = r4.getAutoSizeTextType()
            if (r4 == 0) goto L_0x0221
            android.support.v7.widget.AppCompatTextViewAutoSizeHelper r4 = r1.mAutoSizeTextHelper
            int[] r4 = r4.getAutoSizeTextAvailableSizes()
            int r5 = r4.length
            if (r5 <= 0) goto L_0x0221
            android.widget.TextView r5 = r1.mView
            int r5 = r5.getAutoSizeStepGranularity()
            r6 = -1
            if (r5 == r6) goto L_0x021b
            android.widget.TextView r5 = r1.mView
            android.support.v7.widget.AppCompatTextViewAutoSizeHelper r6 = r1.mAutoSizeTextHelper
            int r6 = r6.getAutoSizeMinTextSize()
            android.support.v7.widget.AppCompatTextViewAutoSizeHelper r7 = r1.mAutoSizeTextHelper
            int r7 = r7.getAutoSizeMaxTextSize()
            android.support.v7.widget.AppCompatTextViewAutoSizeHelper r2 = r1.mAutoSizeTextHelper
            int r2 = r2.getAutoSizeStepGranularity()
            r3 = 0
            r5.setAutoSizeTextTypeUniformWithConfiguration(r6, r7, r2, r3)
            goto L_0x0221
        L_0x021b:
            r3 = 0
            android.widget.TextView r2 = r1.mView
            r2.setAutoSizeTextTypeUniformWithPresetSizes(r4, r3)
        L_0x0221:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v7.widget.AppCompatTextHelper.loadFromAttributes(android.util.AttributeSet, int):void");
    }

    private boolean shouldLoadFontResources(Context context) {
        return !context.isRestricted();
    }

    /* access modifiers changed from: package-private */
    public void onSetTextAppearance(Context context, int resId) {
        ColorStateList textColor;
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(context, resId, R.styleable.TextAppearance);
        if (a.hasValue(R.styleable.TextAppearance_textAllCaps)) {
            setAllCaps(a.getBoolean(R.styleable.TextAppearance_textAllCaps, false));
        }
        if (Build.VERSION.SDK_INT < 23 && a.hasValue(R.styleable.TextAppearance_android_textColor) && (textColor = a.getColorStateList(R.styleable.TextAppearance_android_textColor)) != null) {
            this.mView.setTextColor(textColor);
        }
        a.recycle();
    }

    /* access modifiers changed from: package-private */
    public void setAllCaps(boolean allCaps) {
        this.mView.setTransformationMethod(allCaps ? new AllCapsTransformationMethod(this.mView.getContext()) : null);
    }

    /* access modifiers changed from: package-private */
    public void applyCompoundDrawablesTints() {
        if (this.mDrawableLeftTint != null || this.mDrawableTopTint != null || this.mDrawableRightTint != null || this.mDrawableBottomTint != null) {
            Drawable[] compoundDrawables = this.mView.getCompoundDrawables();
            applyCompoundDrawableTint(compoundDrawables[0], this.mDrawableLeftTint);
            applyCompoundDrawableTint(compoundDrawables[1], this.mDrawableTopTint);
            applyCompoundDrawableTint(compoundDrawables[2], this.mDrawableRightTint);
            applyCompoundDrawableTint(compoundDrawables[3], this.mDrawableBottomTint);
        }
    }

    /* access modifiers changed from: package-private */
    public final void applyCompoundDrawableTint(Drawable drawable, TintInfo info) {
        if (drawable != null && info != null) {
            AppCompatDrawableManager.tintDrawable(drawable, info, this.mView.getDrawableState());
        }
    }

    protected static TintInfo createTintInfo(Context context, AppCompatDrawableManager drawableManager, int drawableId) {
        ColorStateList tintList = drawableManager.getTintList(context, drawableId);
        if (tintList == null) {
            return null;
        }
        TintInfo tintInfo = new TintInfo();
        tintInfo.mHasTintList = true;
        tintInfo.mTintList = tintList;
        return tintInfo;
    }

    /* access modifiers changed from: package-private */
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (isAutoSizeEnabled()) {
            if (getNeedsAutoSizeText()) {
                autoSizeText();
            }
            setNeedsAutoSizeText(true);
        }
    }

    /* access modifiers changed from: package-private */
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public void setTextSize(int unit, float size) {
        if (!isAutoSizeEnabled()) {
            setTextSizeInternal(unit, size);
        }
    }

    private boolean isAutoSizeEnabled() {
        return this.mAutoSizeTextHelper.isAutoSizeEnabled();
    }

    private boolean getNeedsAutoSizeText() {
        return this.mAutoSizeTextHelper.getNeedsAutoSizeText();
    }

    private void setNeedsAutoSizeText(boolean needsAutoSizeText) {
        this.mAutoSizeTextHelper.setNeedsAutoSizeText(needsAutoSizeText);
    }

    private void autoSizeText() {
        this.mAutoSizeTextHelper.autoSizeText();
    }

    private void setTextSizeInternal(int unit, float size) {
        this.mAutoSizeTextHelper.setTextSizeInternal(unit, size);
    }

    /* access modifiers changed from: package-private */
    public void setAutoSizeTextTypeWithDefaults(int autoSizeTextType) {
        this.mAutoSizeTextHelper.setAutoSizeTextTypeWithDefaults(autoSizeTextType);
    }

    /* access modifiers changed from: package-private */
    public void setAutoSizeTextTypeUniformWithConfiguration(int autoSizeMinTextSize, int autoSizeMaxTextSize, int autoSizeStepGranularity, int unit) throws IllegalArgumentException {
        this.mAutoSizeTextHelper.setAutoSizeTextTypeUniformWithConfiguration(autoSizeMinTextSize, autoSizeMaxTextSize, autoSizeStepGranularity, unit);
    }

    /* access modifiers changed from: package-private */
    public void setAutoSizeTextTypeUniformWithPresetSizes(@NonNull int[] presetSizes, int unit) throws IllegalArgumentException {
        this.mAutoSizeTextHelper.setAutoSizeTextTypeUniformWithPresetSizes(presetSizes, unit);
    }

    /* access modifiers changed from: package-private */
    public int getAutoSizeTextType() {
        return this.mAutoSizeTextHelper.getAutoSizeTextType();
    }

    /* access modifiers changed from: package-private */
    public int getAutoSizeStepGranularity() {
        return this.mAutoSizeTextHelper.getAutoSizeStepGranularity();
    }

    /* access modifiers changed from: package-private */
    public int getAutoSizeMinTextSize() {
        return this.mAutoSizeTextHelper.getAutoSizeMinTextSize();
    }

    /* access modifiers changed from: package-private */
    public int getAutoSizeMaxTextSize() {
        return this.mAutoSizeTextHelper.getAutoSizeMaxTextSize();
    }

    /* access modifiers changed from: package-private */
    public int[] getAutoSizeTextAvailableSizes() {
        return this.mAutoSizeTextHelper.getAutoSizeTextAvailableSizes();
    }
}
