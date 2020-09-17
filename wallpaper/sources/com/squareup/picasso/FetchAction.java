package com.squareup.picasso;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import com.squareup.picasso.Picasso;

class FetchAction extends Action<Object> {
    private Callback callback;
    private final Object target = new Object();

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    FetchAction(Picasso picasso, Request data, int memoryPolicy, int networkPolicy, Object tag, String key, Callback callback2) {
        super(picasso, null, data, memoryPolicy, networkPolicy, 0, (Drawable) null, key, tag, false);
        this.callback = callback2;
    }

    /* access modifiers changed from: package-private */
    public void complete(Bitmap result, Picasso.LoadedFrom from) {
        if (this.callback != null) {
            this.callback.onSuccess();
        }
    }

    /* access modifiers changed from: package-private */
    public void error(Exception e) {
        if (this.callback != null) {
            this.callback.onError(e);
        }
    }

    /* access modifiers changed from: package-private */
    public void cancel() {
        super.cancel();
        this.callback = null;
    }

    /* access modifiers changed from: package-private */
    public Object getTarget() {
        return this.target;
    }
}
