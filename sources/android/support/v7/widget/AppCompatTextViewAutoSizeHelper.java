package android.support.v7.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.v7.appcompat.R;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextDirectionHeuristic;
import android.text.TextDirectionHeuristics;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.TextView;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

class AppCompatTextViewAutoSizeHelper {
    private static final int DEFAULT_AUTO_SIZE_GRANULARITY_IN_PX = 1;
    private static final int DEFAULT_AUTO_SIZE_MAX_TEXT_SIZE_IN_SP = 112;
    private static final int DEFAULT_AUTO_SIZE_MIN_TEXT_SIZE_IN_SP = 12;
    private static final RectF TEMP_RECTF = new RectF();
    static final int UNSET_AUTO_SIZE_UNIFORM_CONFIGURATION_VALUE = -1;
    private static final int VERY_WIDE = 1048576;
    private int mAutoSizeMaxTextSizeInPx = -1;
    private int mAutoSizeMinTextSizeInPx = -1;
    private int mAutoSizeStepGranularityInPx = -1;
    private int[] mAutoSizeTextSizesInPx = new int[0];
    private int mAutoSizeTextType = 0;
    private final Context mContext;
    private boolean mHasPresetAutoSizeValues = false;
    private Hashtable<String, Method> mMethodByNameCache = new Hashtable<>();
    private boolean mNeedsAutoSizeText = false;
    private TextPaint mTempTextPaint;
    private final TextView mTextView;

    AppCompatTextViewAutoSizeHelper(TextView textView) {
        this.mTextView = textView;
        this.mContext = this.mTextView.getContext();
    }

    /* access modifiers changed from: package-private */
    public void loadFromAttributes(AttributeSet attrs, int defStyleAttr) {
        int autoSizeStepSizeArrayResId;
        int autoSizeMinTextSizeInPx = -1;
        int autoSizeMaxTextSizeInPx = -1;
        int autoSizeStepGranularityInPx = -1;
        TypedArray a = this.mContext.obtainStyledAttributes(attrs, R.styleable.AppCompatTextView, defStyleAttr, 0);
        if (a.hasValue(R.styleable.AppCompatTextView_autoSizeTextType)) {
            this.mAutoSizeTextType = a.getInt(R.styleable.AppCompatTextView_autoSizeTextType, 0);
        }
        if (a.hasValue(R.styleable.AppCompatTextView_autoSizeStepGranularity)) {
            autoSizeStepGranularityInPx = a.getDimensionPixelSize(R.styleable.AppCompatTextView_autoSizeStepGranularity, -1);
        }
        if (a.hasValue(R.styleable.AppCompatTextView_autoSizeMinTextSize)) {
            autoSizeMinTextSizeInPx = a.getDimensionPixelSize(R.styleable.AppCompatTextView_autoSizeMinTextSize, -1);
        }
        if (a.hasValue(R.styleable.AppCompatTextView_autoSizeMaxTextSize)) {
            autoSizeMaxTextSizeInPx = a.getDimensionPixelSize(R.styleable.AppCompatTextView_autoSizeMaxTextSize, -1);
        }
        if (a.hasValue(R.styleable.AppCompatTextView_autoSizePresetSizes) && (autoSizeStepSizeArrayResId = a.getResourceId(R.styleable.AppCompatTextView_autoSizePresetSizes, 0)) > 0) {
            TypedArray autoSizePreDefTextSizes = a.getResources().obtainTypedArray(autoSizeStepSizeArrayResId);
            setupAutoSizeUniformPresetSizes(autoSizePreDefTextSizes);
            autoSizePreDefTextSizes.recycle();
        }
        a.recycle();
        if (!supportsAutoSizeText()) {
            this.mAutoSizeTextType = 0;
        } else if (this.mAutoSizeTextType == 1) {
            if (!this.mHasPresetAutoSizeValues) {
                DisplayMetrics displayMetrics = this.mContext.getResources().getDisplayMetrics();
                if (autoSizeMinTextSizeInPx == -1) {
                    autoSizeMinTextSizeInPx = (int) TypedValue.applyDimension(2, 12.0f, displayMetrics);
                }
                if (autoSizeMaxTextSizeInPx == -1) {
                    autoSizeMaxTextSizeInPx = (int) TypedValue.applyDimension(2, 112.0f, displayMetrics);
                }
                if (autoSizeStepGranularityInPx == -1) {
                    autoSizeStepGranularityInPx = 1;
                }
                validateAndSetAutoSizeTextTypeUniformConfiguration(autoSizeMinTextSizeInPx, autoSizeMaxTextSizeInPx, autoSizeStepGranularityInPx);
            }
            setupAutoSizeText();
        }
    }

    /* access modifiers changed from: package-private */
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public void setAutoSizeTextTypeWithDefaults(int autoSizeTextType) {
        if (supportsAutoSizeText()) {
            switch (autoSizeTextType) {
                case 0:
                    clearAutoSizeConfiguration();
                    return;
                case 1:
                    DisplayMetrics displayMetrics = this.mContext.getResources().getDisplayMetrics();
                    validateAndSetAutoSizeTextTypeUniformConfiguration((int) TypedValue.applyDimension(2, 12.0f, displayMetrics), (int) TypedValue.applyDimension(2, 112.0f, displayMetrics), 1);
                    setupAutoSizeText();
                    return;
                default:
                    throw new IllegalArgumentException("Unknown auto-size text type: " + autoSizeTextType);
            }
        }
    }

    /* access modifiers changed from: package-private */
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public void setAutoSizeTextTypeUniformWithConfiguration(int autoSizeMinTextSize, int autoSizeMaxTextSize, int autoSizeStepGranularity, int unit) throws IllegalArgumentException {
        if (supportsAutoSizeText()) {
            DisplayMetrics displayMetrics = this.mContext.getResources().getDisplayMetrics();
            validateAndSetAutoSizeTextTypeUniformConfiguration((int) TypedValue.applyDimension(unit, (float) autoSizeMinTextSize, displayMetrics), (int) TypedValue.applyDimension(unit, (float) autoSizeMaxTextSize, displayMetrics), (int) TypedValue.applyDimension(unit, (float) autoSizeStepGranularity, displayMetrics));
            setupAutoSizeText();
        }
    }

    /* access modifiers changed from: package-private */
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public void setAutoSizeTextTypeUniformWithPresetSizes(@NonNull int[] presetSizes, int unit) throws IllegalArgumentException {
        if (supportsAutoSizeText()) {
            int presetSizesLength = presetSizes.length;
            if (presetSizesLength > 0) {
                int[] presetSizesInPx = new int[presetSizesLength];
                if (unit == 0) {
                    presetSizesInPx = Arrays.copyOf(presetSizes, presetSizesLength);
                } else {
                    DisplayMetrics displayMetrics = this.mContext.getResources().getDisplayMetrics();
                    for (int i = 0; i < presetSizesLength; i++) {
                        presetSizesInPx[i] = (int) TypedValue.applyDimension(unit, (float) presetSizes[i], displayMetrics);
                    }
                }
                this.mAutoSizeTextSizesInPx = cleanupAutoSizePresetSizes(presetSizesInPx);
                if (!setupAutoSizeUniformPresetSizesConfiguration()) {
                    throw new IllegalArgumentException("None of the preset sizes is valid: " + Arrays.toString(presetSizes));
                }
            } else {
                this.mHasPresetAutoSizeValues = false;
            }
            setupAutoSizeText();
        }
    }

    /* access modifiers changed from: package-private */
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public int getAutoSizeTextType() {
        return this.mAutoSizeTextType;
    }

    /* access modifiers changed from: package-private */
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public int getAutoSizeStepGranularity() {
        return this.mAutoSizeStepGranularityInPx;
    }

    /* access modifiers changed from: package-private */
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public int getAutoSizeMinTextSize() {
        return this.mAutoSizeMinTextSizeInPx;
    }

    /* access modifiers changed from: package-private */
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public int getAutoSizeMaxTextSize() {
        return this.mAutoSizeMaxTextSizeInPx;
    }

    /* access modifiers changed from: package-private */
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public int[] getAutoSizeTextAvailableSizes() {
        return this.mAutoSizeTextSizesInPx;
    }

    private void setupAutoSizeUniformPresetSizes(TypedArray textSizes) {
        int textSizesLength = textSizes.length();
        int[] parsedSizes = new int[textSizesLength];
        if (textSizesLength > 0) {
            for (int i = 0; i < textSizesLength; i++) {
                parsedSizes[i] = textSizes.getDimensionPixelSize(i, -1);
            }
            this.mAutoSizeTextSizesInPx = cleanupAutoSizePresetSizes(parsedSizes);
            setupAutoSizeUniformPresetSizesConfiguration();
        }
    }

    private boolean setupAutoSizeUniformPresetSizesConfiguration() {
        int sizesLength = this.mAutoSizeTextSizesInPx.length;
        this.mHasPresetAutoSizeValues = sizesLength > 0;
        if (this.mHasPresetAutoSizeValues) {
            this.mAutoSizeTextType = 1;
            this.mAutoSizeMinTextSizeInPx = this.mAutoSizeTextSizesInPx[0];
            this.mAutoSizeMaxTextSizeInPx = this.mAutoSizeTextSizesInPx[sizesLength - 1];
            this.mAutoSizeStepGranularityInPx = -1;
        }
        return this.mHasPresetAutoSizeValues;
    }

    private int[] cleanupAutoSizePresetSizes(int[] presetValues) {
        if (presetValuesLength == 0) {
            return presetValues;
        }
        Arrays.sort(presetValues);
        List<Integer> uniqueValidSizes = new ArrayList<>();
        for (int currentPresetValue : presetValues) {
            if (currentPresetValue > 0 && Collections.binarySearch(uniqueValidSizes, Integer.valueOf(currentPresetValue)) < 0) {
                uniqueValidSizes.add(Integer.valueOf(currentPresetValue));
            }
        }
        if (presetValuesLength == uniqueValidSizes.size()) {
            return presetValues;
        }
        int uniqueValidSizesLength = uniqueValidSizes.size();
        int[] cleanedUpSizes = new int[uniqueValidSizesLength];
        for (int i = 0; i < uniqueValidSizesLength; i++) {
            cleanedUpSizes[i] = uniqueValidSizes.get(i).intValue();
        }
        return cleanedUpSizes;
    }

    private void validateAndSetAutoSizeTextTypeUniformConfiguration(int autoSizeMinTextSizeInPx, int autoSizeMaxTextSizeInPx, int autoSizeStepGranularityInPx) throws IllegalArgumentException {
        if (autoSizeMinTextSizeInPx <= 0) {
            throw new IllegalArgumentException("Minimum auto-size text size (" + autoSizeMinTextSizeInPx + "px) is less or equal to (0px)");
        } else if (autoSizeMaxTextSizeInPx <= autoSizeMinTextSizeInPx) {
            throw new IllegalArgumentException("Maximum auto-size text size (" + autoSizeMaxTextSizeInPx + "px) is less or equal to minimum auto-size " + "text size (" + autoSizeMinTextSizeInPx + "px)");
        } else if (autoSizeStepGranularityInPx <= 0) {
            throw new IllegalArgumentException("The auto-size step granularity (" + autoSizeStepGranularityInPx + "px) is less or equal to (0px)");
        } else {
            this.mAutoSizeTextType = 1;
            this.mAutoSizeMinTextSizeInPx = autoSizeMinTextSizeInPx;
            this.mAutoSizeMaxTextSizeInPx = autoSizeMaxTextSizeInPx;
            this.mAutoSizeStepGranularityInPx = autoSizeStepGranularityInPx;
            this.mHasPresetAutoSizeValues = false;
        }
    }

    private void setupAutoSizeText() {
        if (supportsAutoSizeText() && this.mAutoSizeTextType == 1) {
            if (!this.mHasPresetAutoSizeValues || this.mAutoSizeTextSizesInPx.length == 0) {
                int autoSizeValuesLength = (int) Math.ceil((double) (((float) (this.mAutoSizeMaxTextSizeInPx - this.mAutoSizeMinTextSizeInPx)) / ((float) this.mAutoSizeStepGranularityInPx)));
                if ((this.mAutoSizeMaxTextSizeInPx - this.mAutoSizeMinTextSizeInPx) % this.mAutoSizeStepGranularityInPx == 0) {
                    autoSizeValuesLength++;
                }
                this.mAutoSizeTextSizesInPx = new int[autoSizeValuesLength];
                int sizeToAdd = this.mAutoSizeMinTextSizeInPx;
                for (int i = 0; i < autoSizeValuesLength; i++) {
                    this.mAutoSizeTextSizesInPx[i] = sizeToAdd;
                    sizeToAdd += this.mAutoSizeStepGranularityInPx;
                }
            }
            this.mNeedsAutoSizeText = true;
            autoSizeText();
        }
    }

    /* access modifiers changed from: package-private */
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public void autoSizeText() {
        int maxHeight;
        int maxWidth = (this.mTextView.getWidth() - this.mTextView.getTotalPaddingLeft()) - this.mTextView.getTotalPaddingRight();
        if (Build.VERSION.SDK_INT >= 21) {
            maxHeight = (this.mTextView.getHeight() - this.mTextView.getExtendedPaddingBottom()) - this.mTextView.getExtendedPaddingBottom();
        } else {
            maxHeight = (this.mTextView.getHeight() - this.mTextView.getCompoundPaddingBottom()) - this.mTextView.getCompoundPaddingTop();
        }
        if (maxWidth > 0 && maxHeight > 0) {
            synchronized (TEMP_RECTF) {
                TEMP_RECTF.setEmpty();
                TEMP_RECTF.right = (float) maxWidth;
                TEMP_RECTF.bottom = (float) maxHeight;
                float optimalTextSize = (float) findLargestTextSizeWhichFits(TEMP_RECTF);
                if (optimalTextSize != this.mTextView.getTextSize()) {
                    setTextSizeInternal(0, optimalTextSize);
                }
            }
        }
    }

    private void clearAutoSizeConfiguration() {
        this.mAutoSizeTextType = 0;
        this.mAutoSizeMinTextSizeInPx = -1;
        this.mAutoSizeMaxTextSizeInPx = -1;
        this.mAutoSizeStepGranularityInPx = -1;
        this.mAutoSizeTextSizesInPx = new int[0];
        this.mNeedsAutoSizeText = false;
    }

    /* access modifiers changed from: package-private */
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public void setTextSizeInternal(int unit, float size) {
        Resources res;
        if (this.mContext == null) {
            res = Resources.getSystem();
        } else {
            res = this.mContext.getResources();
        }
        setRawTextSize(TypedValue.applyDimension(unit, size, res.getDisplayMetrics()));
    }

    private void setRawTextSize(float size) {
        if (size != this.mTextView.getPaint().getTextSize()) {
            this.mTextView.getPaint().setTextSize(size);
            if (this.mTextView.getLayout() != null) {
                this.mNeedsAutoSizeText = false;
                try {
                    Method method = this.mMethodByNameCache.get("nullLayouts");
                    if (method == null && (method = TextView.class.getDeclaredMethod("nullLayouts", new Class[0])) != null) {
                        method.setAccessible(true);
                        this.mMethodByNameCache.put("nullLayouts", method);
                    }
                    if (method != null) {
                        method.invoke(this.mTextView, new Object[0]);
                    }
                } catch (Exception e) {
                }
                this.mTextView.requestLayout();
                this.mTextView.invalidate();
            }
        }
    }

    private int findLargestTextSizeWhichFits(RectF availableSpace) {
        int sizesCount = this.mAutoSizeTextSizesInPx.length;
        if (sizesCount == 0) {
            throw new IllegalStateException("No available text sizes to choose from.");
        }
        int bestSizeIndex = 0;
        int lowIndex = 0 + 1;
        int highIndex = sizesCount - 1;
        while (lowIndex <= highIndex) {
            int sizeToTryIndex = (lowIndex + highIndex) / 2;
            if (suggestedSizeFitsInSpace(this.mAutoSizeTextSizesInPx[sizeToTryIndex], availableSpace)) {
                bestSizeIndex = lowIndex;
                lowIndex = sizeToTryIndex + 1;
            } else {
                highIndex = sizeToTryIndex - 1;
                bestSizeIndex = highIndex;
            }
        }
        return this.mAutoSizeTextSizesInPx[bestSizeIndex];
    }

    private boolean suggestedSizeFitsInSpace(int suggestedSizeInPx, RectF availableSpace) {
        int availableWidth;
        StaticLayout layout;
        CharSequence text = this.mTextView.getText();
        int maxLines = Build.VERSION.SDK_INT >= 16 ? this.mTextView.getMaxLines() : -1;
        if (((Boolean) invokeAndReturnWithDefault(this.mTextView, "getHorizontallyScrolling", false)).booleanValue()) {
            availableWidth = 1048576;
        } else {
            availableWidth = (this.mTextView.getMeasuredWidth() - this.mTextView.getTotalPaddingLeft()) - this.mTextView.getTotalPaddingRight();
        }
        if (this.mTempTextPaint == null) {
            this.mTempTextPaint = new TextPaint();
        } else {
            this.mTempTextPaint.reset();
        }
        this.mTempTextPaint.set(this.mTextView.getPaint());
        this.mTempTextPaint.setTextSize((float) suggestedSizeInPx);
        Layout.Alignment alignment = (Layout.Alignment) invokeAndReturnWithDefault(this.mTextView, "getLayoutAlignment", Layout.Alignment.ALIGN_NORMAL);
        if (Build.VERSION.SDK_INT >= 23) {
            layout = createStaticLayoutForMeasuring(text, alignment, availableWidth, maxLines);
        } else {
            layout = createStaticLayoutForMeasuringPre23(text, alignment, availableWidth);
        }
        if ((maxLines == -1 || layout.getLineCount() <= maxLines) && ((float) layout.getHeight()) <= availableSpace.bottom) {
            return true;
        }
        return false;
    }

    @TargetApi(23)
    private StaticLayout createStaticLayoutForMeasuring(CharSequence text, Layout.Alignment alignment, int availableWidth, int maxLines) {
        int i;
        TextDirectionHeuristic textDirectionHeuristic = (TextDirectionHeuristic) invokeAndReturnWithDefault(this.mTextView, "getTextDirectionHeuristic", TextDirectionHeuristics.FIRSTSTRONG_LTR);
        StaticLayout.Builder hyphenationFrequency = StaticLayout.Builder.obtain(text, 0, text.length(), this.mTempTextPaint, availableWidth).setAlignment(alignment).setLineSpacing(this.mTextView.getLineSpacingExtra(), this.mTextView.getLineSpacingMultiplier()).setIncludePad(this.mTextView.getIncludeFontPadding()).setBreakStrategy(this.mTextView.getBreakStrategy()).setHyphenationFrequency(this.mTextView.getHyphenationFrequency());
        if (maxLines == -1) {
            i = Integer.MAX_VALUE;
        } else {
            i = maxLines;
        }
        return hyphenationFrequency.setMaxLines(i).setTextDirection(textDirectionHeuristic).build();
    }

    @TargetApi(14)
    private StaticLayout createStaticLayoutForMeasuringPre23(CharSequence text, Layout.Alignment alignment, int availableWidth) {
        boolean includePad;
        float lineSpacingAdd;
        float lineSpacingMultiplier;
        if (Build.VERSION.SDK_INT >= 16) {
            lineSpacingMultiplier = this.mTextView.getLineSpacingMultiplier();
            lineSpacingAdd = this.mTextView.getLineSpacingExtra();
            includePad = this.mTextView.getIncludeFontPadding();
        } else {
            lineSpacingMultiplier = ((Float) invokeAndReturnWithDefault(this.mTextView, "getLineSpacingMultiplier", Float.valueOf(1.0f))).floatValue();
            lineSpacingAdd = ((Float) invokeAndReturnWithDefault(this.mTextView, "getLineSpacingExtra", Float.valueOf(0.0f))).floatValue();
            includePad = ((Boolean) invokeAndReturnWithDefault(this.mTextView, "getIncludeFontPadding", true)).booleanValue();
        }
        return new StaticLayout(text, this.mTempTextPaint, availableWidth, alignment, lineSpacingMultiplier, lineSpacingAdd, includePad);
    }

    private <T> T invokeAndReturnWithDefault(@NonNull Object object, @NonNull String methodName, @NonNull T defaultValue) {
        try {
            Method method = this.mMethodByNameCache.get(methodName);
            if (method == null && (method = TextView.class.getDeclaredMethod(methodName, new Class[0])) != null) {
                method.setAccessible(true);
                this.mMethodByNameCache.put(methodName, method);
            }
            T result = method.invoke(object, new Object[0]);
            if (result != null || 0 == 0) {
                return result;
            }
        } catch (Exception e) {
            if (0 != 0 || 1 == 0) {
                return null;
            }
        } catch (Throwable th) {
            if (0 == 0 && 0 != 0) {
                T result2 = defaultValue;
            }
            throw th;
        }
        return defaultValue;
    }

    /* access modifiers changed from: package-private */
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public boolean isAutoSizeEnabled() {
        return supportsAutoSizeText() && this.mAutoSizeTextType != 0;
    }

    /* access modifiers changed from: package-private */
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public boolean getNeedsAutoSizeText() {
        return this.mNeedsAutoSizeText;
    }

    /* access modifiers changed from: package-private */
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public void setNeedsAutoSizeText(boolean needsAutoSizeText) {
        this.mNeedsAutoSizeText = needsAutoSizeText;
    }

    private boolean supportsAutoSizeText() {
        return !(this.mTextView instanceof AppCompatEditText);
    }
}
