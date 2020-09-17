package com.dvt.monthlycalender.fragment;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.dvt.monthlycalender.R;
import com.dvt.monthlycalender.base.BaseFragment;
import com.dvt.monthlycalender.databinding.FragmentWallpaperDetailBinding;
import com.dvt.monthlycalender.listener.WallpaperDetailClick;
import com.dvt.monthlycalender.model.WallPaper;
import com.dvt.monthlycalender.viewmodel.WallpaperDetailViewModel;
import java.util.HashMap;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u0000 \u00192\u00020\u00012\u00020\u0002:\u0001\u0019B\u0005¢\u0006\u0002\u0010\u0003J\u0012\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0011H\u0016J&\u0010\u0012\u001a\u0004\u0018\u00010\u00132\u0006\u0010\u0014\u001a\u00020\u00152\b\u0010\u0016\u001a\u0004\u0018\u00010\u00172\b\u0010\u0010\u001a\u0004\u0018\u00010\u0011H\u0016J\b\u0010\u0018\u001a\u00020\u000fH\u0016R\u000e\u0010\u0004\u001a\u00020\u0005X.¢\u0006\u0002\n\u0000R\u001b\u0010\u0006\u001a\u00020\u00078FX\u0002¢\u0006\f\n\u0004\b\n\u0010\u000b\u001a\u0004\b\b\u0010\tR\u000e\u0010\f\u001a\u00020\rX.¢\u0006\u0002\n\u0000¨\u0006\u001a"}, d2 = {"Lcom/dvt/monthlycalender/fragment/WallpaperDetailFragment;", "Lcom/dvt/monthlycalender/base/BaseFragment;", "Lcom/dvt/monthlycalender/listener/WallpaperDetailClick;", "()V", "binding", "Lcom/dvt/monthlycalender/databinding/FragmentWallpaperDetailBinding;", "viewModel", "Lcom/dvt/monthlycalender/viewmodel/WallpaperDetailViewModel;", "getViewModel", "()Lcom/dvt/monthlycalender/viewmodel/WallpaperDetailViewModel;", "viewModel$delegate", "Lkotlin/Lazy;", "wallpaper", "Lcom/dvt/monthlycalender/model/WallPaper;", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "onCreateView", "Landroid/view/View;", "inflater", "Landroid/view/LayoutInflater;", "container", "Landroid/view/ViewGroup;", "setDefaultWallpaper", "Companion", "app_debug"}, k = 1, mv = {1, 1, 11})
/* compiled from: WallpaperDetailFragment.kt */
public final class WallpaperDetailFragment extends BaseFragment implements WallpaperDetailClick {
    static final /* synthetic */ KProperty[] $$delegatedProperties = {Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(WallpaperDetailFragment.class), "viewModel", "getViewModel()Lcom/dvt/monthlycalender/viewmodel/WallpaperDetailViewModel;"))};
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    private HashMap _$_findViewCache;
    private FragmentWallpaperDetailBinding binding;
    @NotNull
    private final Lazy viewModel$delegate = LazyKt.lazy(new WallpaperDetailFragment$viewModel$2(this));
    /* access modifiers changed from: private */
    public WallPaper wallpaper;

    public void _$_clearFindViewByIdCache() {
        if (this._$_findViewCache != null) {
            this._$_findViewCache.clear();
        }
    }

    public View _$_findCachedViewById(int i) {
        if (this._$_findViewCache == null) {
            this._$_findViewCache = new HashMap();
        }
        View view = (View) this._$_findViewCache.get(Integer.valueOf(i));
        if (view != null) {
            return view;
        }
        View view2 = getView();
        if (view2 == null) {
            return null;
        }
        View findViewById = view2.findViewById(i);
        this._$_findViewCache.put(Integer.valueOf(i), findViewById);
        return findViewById;
    }

    @NotNull
    public final WallpaperDetailViewModel getViewModel() {
        Lazy lazy = this.viewModel$delegate;
        KProperty kProperty = $$delegatedProperties[0];
        return (WallpaperDetailViewModel) lazy.getValue();
    }

    public /* synthetic */ void onDestroyView() {
        super.onDestroyView();
        _$_clearFindViewByIdCache();
    }

    @NotNull
    public static final /* synthetic */ WallPaper access$getWallpaper$p(WallpaperDetailFragment $this) {
        WallPaper wallPaper = $this.wallpaper;
        if (wallPaper == null) {
            Intrinsics.throwUninitializedPropertyAccessException("wallpaper");
        }
        return wallPaper;
    }

    @Metadata(bv = {1, 0, 2}, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u000e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006¨\u0006\u0007"}, d2 = {"Lcom/dvt/monthlycalender/fragment/WallpaperDetailFragment$Companion;", "", "()V", "newInstance", "Lcom/dvt/monthlycalender/fragment/WallpaperDetailFragment;", "wallpaper", "Lcom/dvt/monthlycalender/model/WallPaper;", "app_debug"}, k = 1, mv = {1, 1, 11})
    /* compiled from: WallpaperDetailFragment.kt */
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }

        @NotNull
        public final WallpaperDetailFragment newInstance(@NotNull WallPaper wallpaper) {
            Intrinsics.checkParameterIsNotNull(wallpaper, "wallpaper");
            WallpaperDetailFragment result = new WallpaperDetailFragment();
            result.wallpaper = wallpaper;
            return result;
        }
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getViewModel().getWallpaper() == null) {
            WallpaperDetailViewModel viewModel = getViewModel();
            WallPaper wallPaper = this.wallpaper;
            if (wallPaper == null) {
                Intrinsics.throwUninitializedPropertyAccessException("wallpaper");
            }
            viewModel.setWallpaper(wallPaper);
            getViewModel().setSetWallpaperClick(this);
        }
    }

    @Nullable
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Intrinsics.checkParameterIsNotNull(inflater, "inflater");
        FragmentWallpaperDetailBinding inflate = FragmentWallpaperDetailBinding.inflate(inflater, container, false);
        Intrinsics.checkExpressionValueIsNotNull(inflate, "FragmentWallpaperDetailB…flater, container, false)");
        this.binding = inflate;
        FragmentWallpaperDetailBinding fragmentWallpaperDetailBinding = this.binding;
        if (fragmentWallpaperDetailBinding == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
        }
        fragmentWallpaperDetailBinding.setViewModel(getViewModel());
        FragmentWallpaperDetailBinding fragmentWallpaperDetailBinding2 = this.binding;
        if (fragmentWallpaperDetailBinding2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
        }
        fragmentWallpaperDetailBinding2.setLifecycleOwner(this);
        FragmentWallpaperDetailBinding fragmentWallpaperDetailBinding3 = this.binding;
        if (fragmentWallpaperDetailBinding3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
        }
        return fragmentWallpaperDetailBinding3.getRoot();
    }

    public void setDefaultWallpaper() {
        new Thread(new WallpaperDetailFragment$setDefaultWallpaper$1(this)).start();
        getViewModel().isWallpaper().setValue(true);
        FragmentWallpaperDetailBinding fragmentWallpaperDetailBinding = this.binding;
        if (fragmentWallpaperDetailBinding == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
        }
        Snackbar.make(fragmentWallpaperDetailBinding.getRoot(), (int) R.string.success, 0).show();
    }
}
