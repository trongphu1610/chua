package com.dvt.monthlycalender.databinding;

import android.databinding.DataBindingComponent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.databinding.generated.callback.OnClickListener;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.dvt.monthlycalender.R;
import com.dvt.monthlycalender.viewmodel.RateUsViewModel;

public class FragmentRateUsBinding extends ViewDataBinding implements OnClickListener.Listener {
    @Nullable
    private static final ViewDataBinding.IncludedLayouts sIncludes = null;
    @Nullable
    private static final SparseIntArray sViewsWithIds = new SparseIntArray();
    @NonNull
    public final ImageView imgRateApp;
    @Nullable
    private final View.OnClickListener mCallback2;
    @Nullable
    private final View.OnClickListener mCallback3;
    private long mDirtyFlags = -1;
    @Nullable
    private RateUsViewModel mViewModel;
    @NonNull
    private final ConstraintLayout mboundView0;
    @NonNull
    public final TextView textView2;
    @NonNull
    public final TextView textView3;
    @NonNull
    public final TextView tvRatePleaseRate;
    @NonNull
    public final TextView tvRateThank;

    static {
        sViewsWithIds.put(R.id.img_rate_app, 3);
        sViewsWithIds.put(R.id.tv_rate_thank, 4);
        sViewsWithIds.put(R.id.tv_rate_please_rate, 5);
    }

    public FragmentRateUsBinding(@NonNull DataBindingComponent bindingComponent, @NonNull View root) {
        super(bindingComponent, root, 0);
        Object[] bindings = mapBindings(bindingComponent, root, 6, sIncludes, sViewsWithIds);
        this.imgRateApp = (ImageView) bindings[3];
        this.mboundView0 = (ConstraintLayout) bindings[0];
        this.mboundView0.setTag((Object) null);
        this.textView2 = (TextView) bindings[2];
        this.textView2.setTag((Object) null);
        this.textView3 = (TextView) bindings[1];
        this.textView3.setTag((Object) null);
        this.tvRatePleaseRate = (TextView) bindings[5];
        this.tvRateThank = (TextView) bindings[4];
        setRootTag(root);
        this.mCallback2 = new OnClickListener(this, 1);
        this.mCallback3 = new OnClickListener(this, 2);
        invalidateAll();
    }

    public void invalidateAll() {
        synchronized (this) {
            this.mDirtyFlags = 2;
        }
        requestRebind();
    }

    public boolean hasPendingBindings() {
        synchronized (this) {
            if (this.mDirtyFlags != 0) {
                return true;
            }
            return false;
        }
    }

    public boolean setVariable(int variableId, @Nullable Object variable) {
        if (1 != variableId) {
            return false;
        }
        setViewModel((RateUsViewModel) variable);
        return true;
    }

    public void setViewModel(@Nullable RateUsViewModel ViewModel) {
        this.mViewModel = ViewModel;
        synchronized (this) {
            this.mDirtyFlags |= 1;
        }
        notifyPropertyChanged(1);
        super.requestRebind();
    }

    @Nullable
    public RateUsViewModel getViewModel() {
        return this.mViewModel;
    }

    /* access modifiers changed from: protected */
    public boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        return false;
    }

    /* access modifiers changed from: protected */
    public void executeBindings() {
        long dirtyFlags;
        synchronized (this) {
            dirtyFlags = this.mDirtyFlags;
            this.mDirtyFlags = 0;
        }
        RateUsViewModel rateUsViewModel = this.mViewModel;
        if ((dirtyFlags & 2) != 0) {
            this.textView2.setOnClickListener(this.mCallback3);
            this.textView3.setOnClickListener(this.mCallback2);
        }
    }

    public final void _internalCallbackOnClick(int sourceId, View callbackArg_0) {
        boolean viewModelJavaLangObjectNull = false;
        switch (sourceId) {
            case 1:
                RateUsViewModel viewModel = this.mViewModel;
                if (viewModel != null) {
                    viewModelJavaLangObjectNull = true;
                }
                if (viewModelJavaLangObjectNull) {
                    viewModel.onLaterClick();
                    return;
                }
                return;
            case 2:
                RateUsViewModel viewModel2 = this.mViewModel;
                if (viewModel2 != null) {
                    viewModelJavaLangObjectNull = true;
                }
                if (viewModelJavaLangObjectNull) {
                    viewModel2.onRateClick();
                    return;
                }
                return;
            default:
                return;
        }
    }

    @NonNull
    public static FragmentRateUsBinding inflate(@NonNull LayoutInflater inflater, @Nullable ViewGroup root, boolean attachToRoot) {
        return inflate(inflater, root, attachToRoot, DataBindingUtil.getDefaultComponent());
    }

    @NonNull
    public static FragmentRateUsBinding inflate(@NonNull LayoutInflater inflater, @Nullable ViewGroup root, boolean attachToRoot, @Nullable DataBindingComponent bindingComponent) {
        return (FragmentRateUsBinding) DataBindingUtil.inflate(inflater, R.layout.fragment_rate_us, root, attachToRoot, bindingComponent);
    }

    @NonNull
    public static FragmentRateUsBinding inflate(@NonNull LayoutInflater inflater) {
        return inflate(inflater, DataBindingUtil.getDefaultComponent());
    }

    @NonNull
    public static FragmentRateUsBinding inflate(@NonNull LayoutInflater inflater, @Nullable DataBindingComponent bindingComponent) {
        return bind(inflater.inflate(R.layout.fragment_rate_us, (ViewGroup) null, false), bindingComponent);
    }

    @NonNull
    public static FragmentRateUsBinding bind(@NonNull View view) {
        return bind(view, DataBindingUtil.getDefaultComponent());
    }

    @NonNull
    public static FragmentRateUsBinding bind(@NonNull View view, @Nullable DataBindingComponent bindingComponent) {
        if ("layout/fragment_rate_us_0".equals(view.getTag())) {
            return new FragmentRateUsBinding(bindingComponent, view);
        }
        throw new RuntimeException("view tag isn't correct on view:" + view.getTag());
    }
}
