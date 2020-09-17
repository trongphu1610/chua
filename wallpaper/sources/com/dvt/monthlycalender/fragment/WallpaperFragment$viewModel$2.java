package com.dvt.monthlycalender.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.app.Fragment;
import com.dvt.monthlycalender.viewmodel.WallpapersViewModel;
import kotlin.Metadata;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000\b\n\u0000\n\u0002\u0018\u0002\n\u0000\u0010\u0000\u001a\u00020\u0001H\n¢\u0006\u0002\b\u0002"}, d2 = {"<anonymous>", "Lcom/dvt/monthlycalender/viewmodel/WallpapersViewModel;", "invoke"}, k = 3, mv = {1, 1, 11})
/* compiled from: WallpaperFragment.kt */
final class WallpaperFragment$viewModel$2 extends Lambda implements Function0<WallpapersViewModel> {
    final /* synthetic */ WallpaperFragment this$0;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    WallpaperFragment$viewModel$2(WallpaperFragment wallpaperFragment) {
        super(0);
        this.this$0 = wallpaperFragment;
    }

    @NotNull
    public final WallpapersViewModel invoke() {
        return (WallpapersViewModel) ViewModelProviders.of((Fragment) this.this$0).get(WallpapersViewModel.class);
    }
}
