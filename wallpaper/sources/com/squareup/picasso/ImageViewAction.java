package com.squareup.picasso;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;

class ImageViewAction extends Action<ImageView> {
    Callback callback;

    ImageViewAction(Picasso picasso, ImageView imageView, Request data, int memoryPolicy, int networkPolicy, int errorResId, Drawable errorDrawable, String key, Object tag, Callback callback2, boolean noFade) {
        super(picasso, imageView, data, memoryPolicy, networkPolicy, errorResId, errorDrawable, key, tag, noFade);
        this.callback = callback2;
    }

    public void complete(Bitmap result, Picasso.LoadedFrom from) {
        if (result == null) {
            throw new AssertionError(String.format("Attempted to complete action with no result!\n%s", new Object[]{this}));
        }
        ImageView target = (ImageView) this.target.get();
        if (target != null) {
            Context context = this.picasso.context;
            ImageView imageView = target;
            Context context2 = context;
            Bitmap bitmap = result;
            Picasso.LoadedFrom loadedFrom = from;
            PicassoDrawable.setBitmap(imageView, context2, bitmap, loadedFrom, this.noFade, this.picasso.indicatorsEnabled);
            if (this.callback != null) {
                this.callback.onSuccess();
            }
        }
    }

    public void error(Exception e) {
        ImageView target = (ImageView) this.target.get();
        if (target != null) {
            Drawable placeholder = target.getDrawable();
            if (placeholder instanceof Animatable) {
                ((Animatable) placeholder).stop();
            }
            if (this.errorResId != 0) {
                target.setImageResource(this.errorResId);
            } else if (this.errorDrawable != null) {
                target.setImageDrawable(this.errorDrawable);
            }
            if (this.callback != null) {
                this.callback.onError(e);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void cancel() {
        super.cancel();
        if (this.callback != null) {
            this.callback = null;
        }
    }
}
