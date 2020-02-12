package android.support.v4.content.res;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.FontRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.content.res.FontResourcesParserCompat;
import android.support.v4.graphics.TypefaceCompat;
import android.support.v4.os.BuildCompat;
import android.util.Log;
import android.util.TypedValue;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;

public final class ResourcesCompat {
    private static final String TAG = "ResourcesCompat";

    @Nullable
    public static Drawable getDrawable(@NonNull Resources res, @DrawableRes int id, @Nullable Resources.Theme theme) throws Resources.NotFoundException {
        if (Build.VERSION.SDK_INT >= 21) {
            return res.getDrawable(id, theme);
        }
        return res.getDrawable(id);
    }

    @Nullable
    public static Drawable getDrawableForDensity(@NonNull Resources res, @DrawableRes int id, int density, @Nullable Resources.Theme theme) throws Resources.NotFoundException {
        if (Build.VERSION.SDK_INT >= 21) {
            return res.getDrawableForDensity(id, density, theme);
        }
        if (Build.VERSION.SDK_INT >= 15) {
            return res.getDrawableForDensity(id, density);
        }
        return res.getDrawable(id);
    }

    @ColorInt
    public static int getColor(@NonNull Resources res, @ColorRes int id, @Nullable Resources.Theme theme) throws Resources.NotFoundException {
        if (Build.VERSION.SDK_INT >= 23) {
            return res.getColor(id, theme);
        }
        return res.getColor(id);
    }

    @Nullable
    public static ColorStateList getColorStateList(@NonNull Resources res, @ColorRes int id, @Nullable Resources.Theme theme) throws Resources.NotFoundException {
        if (Build.VERSION.SDK_INT >= 23) {
            return res.getColorStateList(id, theme);
        }
        return res.getColorStateList(id);
    }

    @Nullable
    public static Typeface getFont(@NonNull Context context, @FontRes int id) throws Resources.NotFoundException {
        if (context.isRestricted()) {
            return null;
        }
        if (BuildCompat.isAtLeastO()) {
            return context.getResources().getFont(id);
        }
        return loadFont(context, id, 0).getTypeface();
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public static TypefaceCompat.TypefaceHolder getFont(@NonNull Context context, @FontRes int id, int style) throws Resources.NotFoundException {
        if (context.isRestricted()) {
            return null;
        }
        if (!BuildCompat.isAtLeastO()) {
            return loadFont(context, id, style);
        }
        return new TypefaceCompat.TypefaceHolder(Typeface.create(context.getResources().getFont(id), style), (style & 1) == 0 ? 400 : 700, (style & 2) != 0);
    }

    private static TypefaceCompat.TypefaceHolder loadFont(@NonNull Context context, int id, int style) {
        TypedValue value = new TypedValue();
        Resources resources = context.getResources();
        resources.getValue(id, value, true);
        TypefaceCompat.TypefaceHolder typeface = loadFont(context, resources, value, id, style);
        if (typeface != null) {
            return typeface;
        }
        throw new Resources.NotFoundException("Font resource ID #0x" + Integer.toHexString(id));
    }

    private static TypefaceCompat.TypefaceHolder loadFont(@NonNull Context context, Resources wrapper, TypedValue value, int id, int style) {
        if (value.string == null) {
            throw new Resources.NotFoundException("Resource \"" + wrapper.getResourceName(id) + "\" (" + Integer.toHexString(id) + ") is not a Font: " + value);
        }
        TypefaceCompat.TypefaceHolder cached = TypefaceCompat.findFromCache(wrapper, id, style);
        if (cached != null) {
            return cached;
        }
        String file = value.string.toString();
        try {
            if (!file.toLowerCase().endsWith(".xml")) {
                return TypefaceCompat.createFromResourcesFontFile(context, wrapper, id, style);
            }
            FontResourcesParserCompat.FamilyResourceEntry familyEntry = FontResourcesParserCompat.parse(wrapper.getXml(id), wrapper);
            if (familyEntry != null) {
                return TypefaceCompat.createFromResourcesFamilyXml(context, familyEntry, wrapper, id, style);
            }
            Log.e(TAG, "Failed to find font-family tag");
            return null;
        } catch (XmlPullParserException e) {
            Log.e(TAG, "Failed to parse xml resource " + file, e);
            return null;
        } catch (IOException e2) {
            Log.e(TAG, "Failed to read xml resource " + file, e2);
            return null;
        }
    }

    private ResourcesCompat() {
    }
}
