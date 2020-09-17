package com.bumptech.glide.request.target;

import android.support.annotation.NonNull;
import com.bumptech.glide.util.Util;

@Deprecated
public abstract class SimpleTarget<Z> extends BaseTarget<Z> {
    private final int height;
    private final int width;

    public SimpleTarget() {
        this(Integer.MIN_VALUE, Integer.MIN_VALUE);
    }

    public SimpleTarget(int width2, int height2) {
        this.width = width2;
        this.height = height2;
    }

    public final void getSize(@NonNull SizeReadyCallback cb) {
        if (!Util.isValidDimensions(this.width, this.height)) {
            throw new IllegalArgumentException("Width and height must both be > 0 or Target#SIZE_ORIGINAL, but given width: " + this.width + " and height: " + this.height + ", either provide dimensions in the constructor or call override()");
        }
        cb.onSizeReady(this.width, this.height);
    }

    public void removeCallback(@NonNull SizeReadyCallback cb) {
    }
}
