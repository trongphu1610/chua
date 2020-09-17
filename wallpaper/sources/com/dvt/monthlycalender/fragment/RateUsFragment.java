package com.dvt.monthlycalender.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.dvt.monthlycalender.base.BaseFragment;
import com.dvt.monthlycalender.databinding.FragmentRateUsBinding;
import com.dvt.monthlycalender.viewmodel.RateUsViewModel;
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

@Metadata(bv = {1, 0, 2}, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J&\u0010\t\u001a\u0004\u0018\u00010\n2\u0006\u0010\u000b\u001a\u00020\f2\b\u0010\r\u001a\u0004\u0018\u00010\u000e2\b\u0010\u000f\u001a\u0004\u0018\u00010\u0010H\u0016J\b\u0010\u0011\u001a\u00020\u0012H\u0016R\u001b\u0010\u0003\u001a\u00020\u00048FX\u0002¢\u0006\f\n\u0004\b\u0007\u0010\b\u001a\u0004\b\u0005\u0010\u0006¨\u0006\u0013"}, d2 = {"Lcom/dvt/monthlycalender/fragment/RateUsFragment;", "Lcom/dvt/monthlycalender/base/BaseFragment;", "()V", "viewModel", "Lcom/dvt/monthlycalender/viewmodel/RateUsViewModel;", "getViewModel", "()Lcom/dvt/monthlycalender/viewmodel/RateUsViewModel;", "viewModel$delegate", "Lkotlin/Lazy;", "onCreateView", "Landroid/view/View;", "inflater", "Landroid/view/LayoutInflater;", "container", "Landroid/view/ViewGroup;", "savedInstanceState", "Landroid/os/Bundle;", "registerObserver", "", "app_debug"}, k = 1, mv = {1, 1, 11})
/* compiled from: RateUsFragment.kt */
public final class RateUsFragment extends BaseFragment {
    static final /* synthetic */ KProperty[] $$delegatedProperties = {Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(RateUsFragment.class), "viewModel", "getViewModel()Lcom/dvt/monthlycalender/viewmodel/RateUsViewModel;"))};
    private HashMap _$_findViewCache;
    @NotNull
    private final Lazy viewModel$delegate = LazyKt.lazy(new RateUsFragment$viewModel$2(this));

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
    public final RateUsViewModel getViewModel() {
        Lazy lazy = this.viewModel$delegate;
        KProperty kProperty = $$delegatedProperties[0];
        return (RateUsViewModel) lazy.getValue();
    }

    public /* synthetic */ void onDestroyView() {
        super.onDestroyView();
        _$_clearFindViewByIdCache();
    }

    @Nullable
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Intrinsics.checkParameterIsNotNull(inflater, "inflater");
        FragmentRateUsBinding binding = FragmentRateUsBinding.inflate(inflater, container, false);
        Intrinsics.checkExpressionValueIsNotNull(binding, "binding");
        binding.setViewModel(getViewModel());
        return binding.getRoot();
    }

    public void registerObserver() {
        getViewModel().getNavigation().observe(this, new RateUsFragment$registerObserver$1(this));
    }
}
