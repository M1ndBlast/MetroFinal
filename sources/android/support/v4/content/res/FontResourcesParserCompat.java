package android.support.v4.content.res;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.annotation.ArrayRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.compat.R;
import android.util.Base64;
import android.util.Xml;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class FontResourcesParserCompat {
    private static final int ITALIC = 1;
    private static final int NORMAL_WEIGHT = 400;

    public interface FamilyResourceEntry {
    }

    public static final class ProviderResourceEntry implements FamilyResourceEntry {
        @Nullable
        private final List<List<byte[]>> mCerts;
        @NonNull
        private final String mProviderAuthority;
        @NonNull
        private final String mProviderPackage;
        @NonNull
        private final String mQuery;

        public ProviderResourceEntry(@NonNull String authority, @NonNull String pkg, @NonNull String query, @Nullable List<List<byte[]>> certs) {
            this.mProviderAuthority = authority;
            this.mProviderPackage = pkg;
            this.mQuery = query;
            this.mCerts = certs;
        }

        @NonNull
        public String getAuthority() {
            return this.mProviderAuthority;
        }

        @NonNull
        public String getPackage() {
            return this.mProviderPackage;
        }

        @NonNull
        public String getQuery() {
            return this.mQuery;
        }

        @Nullable
        public List<List<byte[]>> getCerts() {
            return this.mCerts;
        }
    }

    public static final class FontFileResourceEntry {
        @NonNull
        private final String mFileName;
        private boolean mItalic;
        private int mResourceId;
        private int mWeight;

        public FontFileResourceEntry(@NonNull String fileName, int weight, boolean italic, int resourceId) {
            this.mFileName = fileName;
            this.mWeight = weight;
            this.mItalic = italic;
            this.mResourceId = resourceId;
        }

        @NonNull
        public String getFileName() {
            return this.mFileName;
        }

        public int getWeight() {
            return this.mWeight;
        }

        public boolean isItalic() {
            return this.mItalic;
        }

        public int getResourceId() {
            return this.mResourceId;
        }
    }

    public static final class FontFamilyFilesResourceEntry implements FamilyResourceEntry {
        @NonNull
        private final FontFileResourceEntry[] mEntries;

        public FontFamilyFilesResourceEntry(@NonNull FontFileResourceEntry[] entries) {
            this.mEntries = entries;
        }

        @NonNull
        public FontFileResourceEntry[] getEntries() {
            return this.mEntries;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:0:0x0000 A[LOOP_START, MTH_ENTER_BLOCK] */
    /* JADX WARNING: Removed duplicated region for block: B:5:0x000e  */
    /* JADX WARNING: Removed duplicated region for block: B:7:0x0016  */
    @android.support.annotation.Nullable
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.support.v4.content.res.FontResourcesParserCompat.FamilyResourceEntry parse(org.xmlpull.v1.XmlPullParser r3, android.content.res.Resources r4) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
        L_0x0000:
            int r0 = r3.next()
            r1 = r0
            r2 = 2
            if (r0 == r2) goto L_0x000c
            r0 = 1
            if (r1 == r0) goto L_0x000c
            goto L_0x0000
        L_0x000c:
            if (r1 == r2) goto L_0x0016
            org.xmlpull.v1.XmlPullParserException r0 = new org.xmlpull.v1.XmlPullParserException
            java.lang.String r2 = "No start tag found"
            r0.<init>(r2)
            throw r0
        L_0x0016:
            android.support.v4.content.res.FontResourcesParserCompat$FamilyResourceEntry r0 = readFamilies(r3, r4)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.content.res.FontResourcesParserCompat.parse(org.xmlpull.v1.XmlPullParser, android.content.res.Resources):android.support.v4.content.res.FontResourcesParserCompat$FamilyResourceEntry");
    }

    @Nullable
    private static FamilyResourceEntry readFamilies(XmlPullParser parser, Resources resources) throws XmlPullParserException, IOException {
        parser.require(2, (String) null, "font-family");
        if (parser.getName().equals("font-family")) {
            return readFamily(parser, resources);
        }
        skip(parser);
        return null;
    }

    @Nullable
    private static FamilyResourceEntry readFamily(XmlPullParser parser, Resources resources) throws XmlPullParserException, IOException {
        TypedArray array = resources.obtainAttributes(Xml.asAttributeSet(parser), R.styleable.FontFamily);
        String authority = array.getString(R.styleable.FontFamily_fontProviderAuthority);
        String providerPackage = array.getString(R.styleable.FontFamily_fontProviderPackage);
        String query = array.getString(R.styleable.FontFamily_fontProviderQuery);
        int certsId = array.getResourceId(R.styleable.FontFamily_fontProviderCerts, 0);
        array.recycle();
        if (authority == null || providerPackage == null || query == null) {
            List<FontFileResourceEntry> fonts = new ArrayList<>();
            while (parser.next() != 3) {
                if (parser.getEventType() == 2) {
                    if (parser.getName().equals("font")) {
                        fonts.add(readFont(parser, resources));
                    } else {
                        skip(parser);
                    }
                }
            }
            if (fonts.isEmpty()) {
                return null;
            }
            return new FontFamilyFilesResourceEntry((FontFileResourceEntry[]) fonts.toArray(new FontFileResourceEntry[fonts.size()]));
        }
        while (parser.next() != 3) {
            skip(parser);
        }
        return new ProviderResourceEntry(authority, providerPackage, query, readCerts(resources, certsId));
    }

    public static List<List<byte[]>> readCerts(Resources resources, @ArrayRes int certsId) {
        List<List<byte[]>> certs = null;
        if (certsId != 0) {
            TypedArray typedArray = resources.obtainTypedArray(certsId);
            if (typedArray.length() > 0) {
                certs = new ArrayList<>();
                if (typedArray.getResourceId(0, 0) != 0) {
                    for (int i = 0; i < typedArray.length(); i++) {
                        certs.add(toByteArrayList(resources.getStringArray(typedArray.getResourceId(i, 0))));
                    }
                } else {
                    certs.add(toByteArrayList(resources.getStringArray(certsId)));
                }
            }
        }
        return certs;
    }

    private static List<byte[]> toByteArrayList(String[] stringArray) {
        List<byte[]> result = new ArrayList<>();
        for (String item : stringArray) {
            result.add(Base64.decode(item, 0));
        }
        return result;
    }

    private static FontFileResourceEntry readFont(XmlPullParser parser, Resources resources) throws XmlPullParserException, IOException {
        TypedArray array = resources.obtainAttributes(Xml.asAttributeSet(parser), R.styleable.FontFamilyFont);
        int weight = array.getInt(R.styleable.FontFamilyFont_fontWeight, NORMAL_WEIGHT);
        boolean z = true;
        if (1 != array.getInt(R.styleable.FontFamilyFont_fontStyle, 0)) {
            z = false;
        }
        boolean isItalic = z;
        String filename = array.getString(R.styleable.FontFamilyFont_font);
        int resourceId = array.getResourceId(R.styleable.FontFamilyFont_font, 0);
        array.recycle();
        while (parser.next() != 3) {
            skip(parser);
        }
        return new FontFileResourceEntry(filename, weight, isItalic, resourceId);
    }

    private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        int depth = 1;
        while (depth > 0) {
            switch (parser.next()) {
                case 2:
                    depth++;
                    break;
                case 3:
                    depth--;
                    break;
            }
        }
    }
}
