package com.dvt.monthlycalender.fragment;

import android.app.WallpaperManager;
import kotlin.Metadata;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000\b\n\u0000\n\u0002\u0010\u0002\n\u0000\u0010\u0000\u001a\u00020\u0001H\n¢\u0006\u0002\b\u0002"}, d2 = {"<anonymous>", "", "run"}, k = 3, mv = {1, 1, 11})
/* compiled from: WallpaperDetailFragment.kt */
final class WallpaperDetailFragment$setDefaultWallpaper$1 implements Runnable {
    final /* synthetic */ WallpaperDetailFragment this$0;

    WallpaperDetailFragment$setDefaultWallpaper$1(WallpaperDetailFragment wallpaperDetailFragment) {
        this.this$0 = wallpaperDetailFragment;
    }

    public final void run() {
        WallpaperManager.getInstance(this.this$0.getContext()).setResource(this.this$0.getViewModel().getImageId());
    }
}
