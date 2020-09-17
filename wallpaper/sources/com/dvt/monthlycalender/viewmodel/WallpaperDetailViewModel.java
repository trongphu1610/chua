package com.dvt.monthlycalender.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import com.dvt.monthlycalender.listener.WallpaperDetailClick;
import com.dvt.monthlycalender.model.WallPaper;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u0006\u0010\u0014\u001a\u00020\u0015J\u0006\u0010\u0016\u001a\u00020\u0017J\u0006\u0010\u0018\u001a\u00020\u0015J\u0006\u0010\u0019\u001a\u00020\u001aR \u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0003\u0010\u0006\"\u0004\b\u0007\u0010\bR\u001a\u0010\t\u001a\u00020\nX.¢\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000eR\u001c\u0010\u000f\u001a\u0004\u0018\u00010\u0010X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0011\u0010\u0012\"\u0004\b\u0007\u0010\u0013¨\u0006\u001b"}, d2 = {"Lcom/dvt/monthlycalender/viewmodel/WallpaperDetailViewModel;", "Landroid/arch/lifecycle/ViewModel;", "()V", "isWallpaper", "Landroid/arch/lifecycle/MutableLiveData;", "", "()Landroid/arch/lifecycle/MutableLiveData;", "setWallpaper", "(Landroid/arch/lifecycle/MutableLiveData;)V", "setWallpaperClick", "Lcom/dvt/monthlycalender/listener/WallpaperDetailClick;", "getSetWallpaperClick", "()Lcom/dvt/monthlycalender/listener/WallpaperDetailClick;", "setSetWallpaperClick", "(Lcom/dvt/monthlycalender/listener/WallpaperDetailClick;)V", "wallpaper", "Lcom/dvt/monthlycalender/model/WallPaper;", "getWallpaper", "()Lcom/dvt/monthlycalender/model/WallPaper;", "(Lcom/dvt/monthlycalender/model/WallPaper;)V", "getImageAuthor", "", "getImageId", "", "getImageName", "invokeClick", "", "app_debug"}, k = 1, mv = {1, 1, 11})
/* compiled from: WallpaperDetailViewModel.kt */
public final class WallpaperDetailViewModel extends ViewModel {
    @NotNull
    private MutableLiveData<Boolean> isWallpaper = new MutableLiveData<>();
    @NotNull
    public WallpaperDetailClick setWallpaperClick;
    @Nullable
    private WallPaper wallpaper;

    public WallpaperDetailViewModel() {
        this.isWallpaper.setValue(false);
    }

    @NotNull
    public final WallpaperDetailClick getSetWallpaperClick() {
        WallpaperDetailClick wallpaperDetailClick = this.setWallpaperClick;
        if (wallpaperDetailClick == null) {
            Intrinsics.throwUninitializedPropertyAccessException("setWallpaperClick");
        }
        return wallpaperDetailClick;
    }

    public final void setSetWallpaperClick(@NotNull WallpaperDetailClick wallpaperDetailClick) {
        Intrinsics.checkParameterIsNotNull(wallpaperDetailClick, "<set-?>");
        this.setWallpaperClick = wallpaperDetailClick;
    }

    @Nullable
    public final WallPaper getWallpaper() {
        return this.wallpaper;
    }

    public final void setWallpaper(@Nullable WallPaper wallPaper) {
        this.wallpaper = wallPaper;
    }

    @NotNull
    public final MutableLiveData<Boolean> isWallpaper() {
        return this.isWallpaper;
    }

    public final void setWallpaper(@NotNull MutableLiveData<Boolean> mutableLiveData) {
        Intrinsics.checkParameterIsNotNull(mutableLiveData, "<set-?>");
        this.isWallpaper = mutableLiveData;
    }

    public final int getImageId() {
        WallPaper wallPaper = this.wallpaper;
        if (wallPaper == null) {
            Intrinsics.throwNpe();
        }
        return wallPaper.getImageId();
    }

    @NotNull
    public final String getImageName() {
        WallPaper wallPaper = this.wallpaper;
        if (wallPaper == null) {
            Intrinsics.throwNpe();
        }
        return wallPaper.getName();
    }

    @NotNull
    public final String getImageAuthor() {
        WallPaper wallPaper = this.wallpaper;
        if (wallPaper == null) {
            Intrinsics.throwNpe();
        }
        return wallPaper.getAuthor();
    }

    public final void invokeClick() {
        WallpaperDetailClick wallpaperDetailClick = this.setWallpaperClick;
        if (wallpaperDetailClick == null) {
            Intrinsics.throwUninitializedPropertyAccessException("setWallpaperClick");
        }
        wallpaperDetailClick.setDefaultWallpaper();
    }
}
