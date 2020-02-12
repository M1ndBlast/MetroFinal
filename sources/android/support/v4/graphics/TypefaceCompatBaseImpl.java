package android.support.v4.graphics;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.GuardedBy;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.res.FontResourcesParserCompat;
import android.support.v4.graphics.TypefaceCompat;
import android.support.v4.graphics.fonts.FontResult;
import android.support.v4.os.ResultReceiver;
import android.support.v4.provider.FontRequest;
import android.support.v4.provider.FontsContractCompat;
import android.support.v4.provider.FontsContractInternal;
import android.support.v4.util.LruCache;
import android.util.Log;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@RequiresApi(14)
@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
class TypefaceCompatBaseImpl implements TypefaceCompat.TypefaceCompatImpl {
    private static final String CACHE_FILE_PREFIX = "cached_font_";
    private static final int SYNC_FETCH_TIMEOUT_MS = 500;
    private static final String TAG = "TypefaceCompatBaseImpl";
    private static final boolean VERBOSE_TRACING = false;
    private static final LruCache<String, TypefaceCompat.TypefaceHolder> sDynamicTypefaceCache = new LruCache<>(16);
    @GuardedBy("sLock")
    private static FontsContractInternal sFontsContract;
    private static final Object sLock = new Object();
    private final Context mApplicationContext;

    TypefaceCompatBaseImpl(Context context) {
        this.mApplicationContext = context.getApplicationContext();
    }

    public void create(@NonNull final FontRequest request, @NonNull final TypefaceCompat.FontRequestCallback callback) {
        TypefaceCompat.TypefaceHolder cachedTypeface = findFromCache(request.getProviderAuthority(), request.getQuery());
        if (cachedTypeface != null) {
            callback.onTypefaceRetrieved(cachedTypeface.getTypeface());
        }
        synchronized (sLock) {
            if (sFontsContract == null) {
                sFontsContract = new FontsContractInternal(this.mApplicationContext);
            }
            sFontsContract.getFont(request, new ResultReceiver((Handler) null) {
                public void onReceiveResult(int resultCode, Bundle resultData) {
                    TypefaceCompatBaseImpl.this.receiveResult(request, callback, resultCode, resultData);
                }
            });
        }
    }

    private static TypefaceCompat.TypefaceHolder findFromCache(String providerAuthority, String query) {
        synchronized (sDynamicTypefaceCache) {
            TypefaceCompat.TypefaceHolder typeface = sDynamicTypefaceCache.get(createProviderUid(providerAuthority, query));
            if (typeface != null) {
                return typeface;
            }
            return null;
        }
    }

    static void putInCache(String providerAuthority, String query, TypefaceCompat.TypefaceHolder typeface) {
        String key = createProviderUid(providerAuthority, query);
        synchronized (sDynamicTypefaceCache) {
            sDynamicTypefaceCache.put(key, typeface);
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void receiveResult(FontRequest request, TypefaceCompat.FontRequestCallback callback, int resultCode, Bundle resultData) {
        TypefaceCompat.TypefaceHolder cachedTypeface = findFromCache(request.getProviderAuthority(), request.getQuery());
        if (cachedTypeface != null) {
            callback.onTypefaceRetrieved(cachedTypeface.getTypeface());
        } else if (resultCode != 0) {
            callback.onTypefaceRequestFailed(resultCode);
        } else if (resultData == null) {
            callback.onTypefaceRequestFailed(1);
        } else {
            List<FontResult> resultList = resultData.getParcelableArrayList("font_results");
            if (resultList == null || resultList.isEmpty()) {
                callback.onTypefaceRequestFailed(1);
                return;
            }
            TypefaceCompat.TypefaceHolder typeface = createTypeface(resultList);
            if (typeface == null) {
                Log.e(TAG, "Error creating font " + request.getQuery());
                callback.onTypefaceRequestFailed(-3);
                return;
            }
            putInCache(request.getProviderAuthority(), request.getQuery(), typeface);
            callback.onTypefaceRetrieved(typeface.getTypeface());
        }
    }

    public TypefaceCompat.TypefaceHolder createTypeface(List<FontResult> resultList) {
        Typeface typeface = null;
        FontResult result = resultList.get(0);
        File tmpFile = copyToCacheFile((InputStream) new FileInputStream(result.getFileDescriptor().getFileDescriptor()));
        if (tmpFile != null) {
            try {
                typeface = Typeface.createFromFile(tmpFile.getPath());
            } catch (RuntimeException e) {
                return null;
            } finally {
                tmpFile.delete();
            }
        }
        if (typeface == null) {
            return null;
        }
        return new TypefaceCompat.TypefaceHolder(typeface, result.getWeight(), result.getItalic());
    }

    public TypefaceCompat.TypefaceHolder createTypeface(@NonNull FontsContractCompat.FontInfo[] fonts, Map<Uri, ByteBuffer> uriBuffer) {
        if (fonts.length < 1) {
            return null;
        }
        Typeface typeface = null;
        FontsContractCompat.FontInfo font = fonts[0];
        File tmpFile = copyToCacheFile(uriBuffer.get(font.getUri()));
        if (tmpFile != null) {
            try {
                typeface = Typeface.createFromFile(tmpFile.getPath());
            } catch (RuntimeException e) {
                return null;
            } finally {
                tmpFile.delete();
            }
        }
        if (typeface == null) {
            return null;
        }
        return new TypefaceCompat.TypefaceHolder(typeface, font.getWeight(), font.isItalic());
    }

    private File copyToCacheFile(InputStream is) {
        FileOutputStream fos = null;
        try {
            File cacheFile = new File(this.mApplicationContext.getCacheDir(), CACHE_FILE_PREFIX + Thread.currentThread().getId());
            fos = new FileOutputStream(cacheFile, false);
            byte[] buffer = new byte[1024];
            while (true) {
                int read = is.read(buffer);
                int readLen = read;
                if (read == -1) {
                    return cacheFile;
                }
                fos.write(buffer, 0, readLen);
            }
        } catch (IOException e) {
            Log.e(TAG, "Error copying font file descriptor to temp local file.", e);
            return null;
        } finally {
            closeQuietly(is);
            closeQuietly(fos);
        }
    }

    private File copyToCacheFile(ByteBuffer is) {
        try {
            File cacheDir = this.mApplicationContext.getCacheDir();
            File cacheFile = new File(cacheDir, CACHE_FILE_PREFIX + Thread.currentThread().getId());
            FileOutputStream fos = new FileOutputStream(cacheFile, false);
            byte[] buffer = new byte[1024];
            while (is.hasRemaining()) {
                int len = Math.min(1024, is.remaining());
                is.get(buffer, 0, len);
                fos.write(buffer, 0, len);
            }
            closeQuietly((Closeable) fos);
            return cacheFile;
        } catch (IOException e) {
            Log.e(TAG, "Error copying font file descriptor to temp local file.", e);
            closeQuietly((Closeable) null);
            return null;
        } catch (Throwable th) {
            closeQuietly((Closeable) null);
            throw th;
        }
    }

    static void closeQuietly(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException io) {
                Log.e(TAG, "Error closing input stream", io);
            }
        }
    }

    private static String createProviderUid(String authority, String query) {
        return "provider:" + authority + "-" + query;
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    @Nullable
    public TypefaceCompat.TypefaceHolder createFromResourcesFontFile(Resources resources, int id, int style) {
        InputStream is = null;
        try {
            is = resources.openRawResource(id);
            Typeface baseTypeface = createTypeface(resources, is);
            if (baseTypeface != null) {
                Typeface typeface = Typeface.create(baseTypeface, style);
                if (typeface != null) {
                    TypefaceCompat.TypefaceHolder result = new TypefaceCompat.TypefaceHolder(typeface, 400, false);
                    sDynamicTypefaceCache.put(createAssetUid(resources, id, style), result);
                    closeQuietly(is);
                    return result;
                }
            }
            return null;
        } catch (IOException e) {
            return null;
        } finally {
            closeQuietly(is);
        }
    }

    @Nullable
    public TypefaceCompat.TypefaceHolder createFromResourcesFamilyXml(FontResourcesParserCompat.FamilyResourceEntry entry, Resources resources, int id, int style) {
        if (entry instanceof FontResourcesParserCompat.ProviderResourceEntry) {
            return createFromResources((FontResourcesParserCompat.ProviderResourceEntry) entry);
        }
        TypefaceCompat.TypefaceHolder typeface = createFromResources((FontResourcesParserCompat.FontFamilyFilesResourceEntry) entry, resources, id, style);
        if (typeface != null) {
            sDynamicTypefaceCache.put(createAssetUid(resources, id, style), typeface);
        }
        return typeface;
    }

    private FontResourcesParserCompat.FontFileResourceEntry findBestEntry(FontResourcesParserCompat.FontFamilyFilesResourceEntry entry, int targetWeight, boolean isTargetItalic) {
        int bestScore = Integer.MAX_VALUE;
        FontResourcesParserCompat.FontFileResourceEntry bestEntry = null;
        for (FontResourcesParserCompat.FontFileResourceEntry e : entry.getEntries()) {
            int score = (Math.abs(e.getWeight() - targetWeight) * 2) + (isTargetItalic == e.isItalic() ? 0 : 1);
            if (bestEntry == null || bestScore > score) {
                bestEntry = e;
                bestScore = score;
            }
        }
        return bestEntry;
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    /* access modifiers changed from: package-private */
    @Nullable
    public TypefaceCompat.TypefaceHolder createFromResources(FontResourcesParserCompat.FontFamilyFilesResourceEntry entry, Resources resources, int id, int style) {
        FontResourcesParserCompat.FontFileResourceEntry best = findBestEntry(entry, (style & 1) == 0 ? 400 : 700, (style & 2) != 0);
        if (best == null) {
            return null;
        }
        InputStream is = null;
        try {
            is = resources.openRawResource(best.getResourceId());
            Typeface baseTypeface = createTypeface(resources, is);
            if (baseTypeface != null) {
                Typeface typeface = Typeface.create(baseTypeface, style);
                if (typeface != null) {
                    TypefaceCompat.TypefaceHolder typefaceHolder = new TypefaceCompat.TypefaceHolder(typeface, best.getWeight(), best.isItalic());
                    closeQuietly(is);
                    return typefaceHolder;
                }
            }
            return null;
        } catch (IOException e) {
            return null;
        } finally {
            closeQuietly(is);
        }
    }

    @Nullable
    private TypefaceCompat.TypefaceHolder createFromResources(FontResourcesParserCompat.ProviderResourceEntry entry) {
        TypefaceCompat.TypefaceHolder cached = findFromCache(entry.getAuthority(), entry.getQuery());
        if (cached != null) {
            return cached;
        }
        FontRequest request = new FontRequest(entry.getAuthority(), entry.getPackage(), entry.getQuery(), entry.getCerts());
        WaitableCallback callback = new WaitableCallback(entry.getAuthority() + "/" + entry.getQuery());
        create(request, callback);
        return new TypefaceCompat.TypefaceHolder(callback.waitWithTimeout(500), 400, false);
    }

    private static final class WaitableCallback extends TypefaceCompat.FontRequestCallback {
        private static final int FINISHED = 2;
        private static final int NOT_STARTED = 0;
        private static final int WAITING = 1;
        private final Condition mCond = this.mLock.newCondition();
        private final String mFontTitle;
        private final ReentrantLock mLock = new ReentrantLock();
        @GuardedBy("mLock")
        private int mState = 0;
        @GuardedBy("mLock")
        private Typeface mTypeface;

        WaitableCallback(String fontTitle) {
            this.mFontTitle = fontTitle;
        }

        public void onTypefaceRetrieved(Typeface typeface) {
            this.mLock.lock();
            try {
                if (this.mState == 1) {
                    this.mTypeface = typeface;
                    this.mState = 2;
                }
                this.mCond.signal();
            } finally {
                this.mLock.unlock();
            }
        }

        public void onTypefaceRequestFailed(int reason) {
            Log.w(TypefaceCompatBaseImpl.TAG, "Remote font fetch failed(" + reason + "): " + this.mFontTitle);
            this.mLock.lock();
            try {
                if (this.mState == 1) {
                    this.mTypeface = null;
                    this.mState = 2;
                }
                this.mCond.signal();
            } finally {
                this.mLock.unlock();
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 8 */
        public Typeface waitWithTimeout(long timeoutMillis) {
            Typeface typeface;
            this.mLock.lock();
            try {
                if (this.mState == 2) {
                    typeface = this.mTypeface;
                } else {
                    if (this.mState == 0) {
                        this.mState = 1;
                        long remainingNanos = TimeUnit.MILLISECONDS.toNanos(timeoutMillis);
                        while (true) {
                            if (this.mState != 1) {
                                break;
                            }
                            try {
                                remainingNanos = this.mCond.awaitNanos(remainingNanos);
                            } catch (InterruptedException e) {
                            }
                            if (this.mState != 2) {
                                if (remainingNanos < 0) {
                                    Log.w(TypefaceCompatBaseImpl.TAG, "Remote font fetch timed out: " + this.mFontTitle);
                                    this.mState = 2;
                                    break;
                                }
                            } else {
                                Log.w(TypefaceCompatBaseImpl.TAG, "Remote font fetched in " + (timeoutMillis - TimeUnit.NANOSECONDS.toMillis(remainingNanos)) + "ms :" + this.mFontTitle);
                                typeface = this.mTypeface;
                                break;
                            }
                        }
                    }
                    this.mLock.unlock();
                    return null;
                }
                return typeface;
            } finally {
                this.mLock.unlock();
            }
        }
    }

    public TypefaceCompat.TypefaceHolder findFromCache(Resources resources, int id, int style) {
        TypefaceCompat.TypefaceHolder typefaceHolder;
        String key = createAssetUid(resources, id, style);
        synchronized (sDynamicTypefaceCache) {
            typefaceHolder = sDynamicTypefaceCache.get(key);
        }
        return typefaceHolder;
    }

    private static String createAssetUid(Resources resources, int id, int style) {
        return resources.getResourcePackageName(id) + "-" + id + "-" + style;
    }

    /* access modifiers changed from: package-private */
    public Typeface createTypeface(Resources resources, InputStream is) throws IOException {
        File tmpFile = copyToCacheFile(is);
        if (tmpFile == null) {
            return null;
        }
        try {
            return Typeface.createFromFile(tmpFile.getPath());
        } catch (RuntimeException e) {
            Log.e(TAG, "Failed to create font", e);
            return null;
        } finally {
            tmpFile.delete();
        }
    }

    static void closeQuietly(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException io) {
                Log.e(TAG, "Error closing stream", io);
            }
        }
    }
}
