package android.support.v7.app;

import android.content.Context;
import android.support.annotation.RequiresApi;
import android.view.KeyEvent;
import android.view.Window;

@RequiresApi(26)
class AppCompatDelegateImplO extends AppCompatDelegateImplN {
    AppCompatDelegateImplO(Context context, Window window, AppCompatCallback callback) {
        super(context, window, callback);
    }

    public boolean checkActionBarFocusKey(KeyEvent event) {
        return false;
    }
}
