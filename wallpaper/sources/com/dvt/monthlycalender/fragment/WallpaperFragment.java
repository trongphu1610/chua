package com.dvt.monthlycalender.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.dvt.monthlycalender.R;
import com.dvt.monthlycalender.adapter.WallpapersAdapter;
import com.dvt.monthlycalender.base.BaseFragment;
import com.dvt.monthlycalender.databinding.FragmentWallpaperBinding;
import com.dvt.monthlycalender.listener.WallpaperItemClick;
import com.dvt.monthlycalender.viewmodel.WallpapersViewModel;
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

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u00012\u00020\u0002B\u0005¢\u0006\u0002\u0010\u0003J&\u0010\f\u001a\u0004\u0018\u00010\r2\u0006\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u00112\b\u0010\u0012\u001a\u0004\u0018\u00010\u0013H\u0016J\u0010\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u0017H\u0016J\u0010\u0010\u0018\u001a\u00020\u00152\u0006\u0010\u0019\u001a\u00020\u001aH\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X.¢\u0006\u0002\n\u0000R\u001b\u0010\u0006\u001a\u00020\u00078BX\u0002¢\u0006\f\n\u0004\b\n\u0010\u000b\u001a\u0004\b\b\u0010\t¨\u0006\u001b"}, d2 = {"Lcom/dvt/monthlycalender/fragment/WallpaperFragment;", "Lcom/dvt/monthlycalender/base/BaseFragment;", "Lcom/dvt/monthlycalender/listener/WallpaperItemClick;", "()V", "binding", "Lcom/dvt/monthlycalender/databinding/FragmentWallpaperBinding;", "viewModel", "Lcom/dvt/monthlycalender/viewmodel/WallpapersViewModel;", "getViewModel", "()Lcom/dvt/monthlycalender/viewmodel/WallpapersViewModel;", "viewModel$delegate", "Lkotlin/Lazy;", "onCreateView", "Landroid/view/View;", "inflater", "Landroid/view/LayoutInflater;", "container", "Landroid/view/ViewGroup;", "savedInstanceState", "Landroid/os/Bundle;", "onItemClickListener", "", "position", "", "setupRecyclerView", "recyclerView", "Landroid/support/v7/widget/RecyclerView;", "app_debug"}, k = 1, mv = {1, 1, 11})
/* compiled from: WallpaperFragment.kt */
public final class WallpaperFragment extends BaseFragment implements WallpaperItemClick {
    static final /* synthetic */ KProperty[] $$delegatedProperties = {Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(WallpaperFragment.class), "viewModel", "getViewModel()Lcom/dvt/monthlycalender/viewmodel/WallpapersViewModel;"))};
    private HashMap _$_findViewCache;
    private FragmentWallpaperBinding binding;
    private final Lazy viewModel$delegate = LazyKt.lazy(new WallpaperFragment$viewModel$2(this));

    private final WallpapersViewModel getViewModel() {
        Lazy lazy = this.viewModel$delegate;
        KProperty kProperty = $$delegatedProperties[0];
        return (WallpapersViewModel) lazy.getValue();
    }

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

    public /* synthetic */ void onDestroyView() {
        super.onDestroyView();
        _$_clearFindViewByIdCache();
    }

    @Nullable
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Intrinsics.checkParameterIsNotNull(inflater, "inflater");
        FragmentWallpaperBinding inflate = FragmentWallpaperBinding.inflate(inflater, container, false);
        Intrinsics.checkExpressionValueIsNotNull(inflate, "FragmentWallpaperBinding…flater, container, false)");
        this.binding = inflate;
        FragmentWallpaperBinding fragmentWallpaperBinding = this.binding;
        if (fragmentWallpaperBinding == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
        }
        RecyclerView recyclerView = fragmentWallpaperBinding.rcWallpaper;
        Intrinsics.checkExpressionValueIsNotNull(recyclerView, "binding.rcWallpaper");
        setupRecyclerView(recyclerView);
        FragmentWallpaperBinding fragmentWallpaperBinding2 = this.binding;
        if (fragmentWallpaperBinding2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
        }
        return fragmentWallpaperBinding2.getRoot();
    }

    private final void setupRecyclerView(RecyclerView recyclerView) {
        Context context = getContext();
        FragmentActivity activity = getActivity();
        if (activity == null) {
            Intrinsics.throwNpe();
        }
        Intrinsics.checkExpressionValueIsNotNull(activity, "activity!!");
        Resources resources = activity.getResources();
        Intrinsics.checkExpressionValueIsNotNull(resources, "activity!!.resources");
        int i = 2;
        if (resources.getConfiguration().orientation == 2) {
            i = 4;
        }
        recyclerView.setLayoutManager(new GridLayoutManager(context, i));
        recyclerView.setAdapter(new WallpapersAdapter(getViewModel(), this));
    }

    public void onItemClickListener(int position) {
        WallpaperDetailFragment fragment = WallpaperDetailFragment.Companion.newInstance(getViewModel().getItemAt(position));
        Transition inflateTransition = TransitionInflater.from(getContext()).inflateTransition(17760259);
        FragmentWallpaperBinding fragmentWallpaperBinding = this.binding;
        if (fragmentWallpaperBinding == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
        }
        setExitTransition(inflateTransition.removeTarget(fragmentWallpaperBinding.actionBar));
        fragment.setEnterTransition(TransitionInflater.from(getContext()).inflateTransition(17760258));
        FragmentActivity activity = getActivity();
        if (activity == null) {
            Intrinsics.throwNpe();
        }
        Intrinsics.checkExpressionValueIsNotNull(activity, "activity!!");
        activity.getSupportFragmentManager().beginTransaction().addToBackStack("he").replace(R.id.content, fragment).commit();
    }
}
