package android.support.v4.graphics.fonts;

import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.util.Preconditions;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public final class FontResult implements Parcelable {
    public static final Parcelable.Creator<FontResult> CREATOR = new Parcelable.Creator<FontResult>() {
        public FontResult createFromParcel(Parcel in) {
            return new FontResult(in);
        }

        public FontResult[] newArray(int size) {
            return new FontResult[size];
        }
    };
    private final ParcelFileDescriptor mFileDescriptor;
    private final String mFontVariationSettings;
    private final boolean mItalic;
    private final int mTtcIndex;
    private final int mWeight;

    public FontResult(@NonNull ParcelFileDescriptor fileDescriptor, int ttcIndex, @Nullable String fontVariationSettings, int weight, boolean italic) {
        this.mFileDescriptor = (ParcelFileDescriptor) Preconditions.checkNotNull(fileDescriptor);
        this.mTtcIndex = ttcIndex;
        this.mFontVariationSettings = fontVariationSettings;
        this.mWeight = weight;
        this.mItalic = italic;
    }

    public ParcelFileDescriptor getFileDescriptor() {
        return this.mFileDescriptor;
    }

    public int getTtcIndex() {
        return this.mTtcIndex;
    }

    public String getFontVariationSettings() {
        return this.mFontVariationSettings;
    }

    public int getWeight() {
        return this.mWeight;
    }

    public boolean getItalic() {
        return this.mItalic;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mFileDescriptor, flags);
        dest.writeInt(this.mTtcIndex);
        dest.writeInt(this.mFontVariationSettings != null ? 1 : 0);
        if (this.mFontVariationSettings != null) {
            dest.writeString(this.mFontVariationSettings);
        }
        dest.writeInt(this.mWeight);
        dest.writeInt(this.mItalic ? 1 : 0);
    }

    private FontResult(Parcel in) {
        this.mFileDescriptor = (ParcelFileDescriptor) in.readParcelable((ClassLoader) null);
        this.mTtcIndex = in.readInt();
        boolean z = true;
        if (in.readInt() == 1) {
            this.mFontVariationSettings = in.readString();
        } else {
            this.mFontVariationSettings = null;
        }
        this.mWeight = in.readInt();
        this.mItalic = in.readInt() != 1 ? false : z;
    }
}
