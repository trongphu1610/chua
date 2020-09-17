package com.dvt.monthlycalender.databinding;

import android.databinding.DataBindingComponent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.dvt.monthlycalender.R;

public class FragmentWallpaperBinding extends ViewDataBinding {
    @Nullable
    private static final ViewDataBinding.IncludedLayouts sIncludes = null;
    @Nullable
    private static final SparseIntArray sViewsWithIds = new SparseIntArray();
    @NonNull
    public final ConstraintLayout actionBar;
    private long mDirtyFlags = -1;
    @NonNull
    private final ConstraintLayout mboundView0;
    @NonNull
    public final RecyclerView rcWallpaper;

    static {
        sViewsWithIds.put(R.id.action_bar, 1);
        sViewsWithIds.put(R.id.rc_wallpaper, 2);
    }

    public FragmentWallpaperBinding(@NonNull DataBindingComponent bindingComponent, @NonNull View root) {
        super(bindingComponent, root, 0);
        Object[] bindings = mapBindings(bindingComponent, root, 3, sIncludes, sViewsWithIds);
        this.actionBar = (ConstraintLayout) bindings[1];
        this.mboundView0 = (ConstraintLayout) bindings[0];
        this.mboundView0.setTag((Object) null);
        this.rcWallpaper = (RecyclerView) bindings[2];
        setRootTag(root);
        invalidateAll();
    }

    public void invalidateAll() {
        synchronized (this) {
            this.mDirtyFlags = 1;
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
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        return false;
    }

    /* access modifiers changed from: protected */
    public void executeBindings() {
        synchronized (this) {
            long dirtyFlags = this.mDirtyFlags;
            this.mDirtyFlags = 0;
        }
    }

    @NonNull
    public static FragmentWallpaperBinding inflate(@NonNull LayoutInflater inflater, @Nullable ViewGroup root, boolean attachToRoot) {
        return inflate(inflater, root, attachToRoot, DataBindingUtil.getDefaultComponent());
    }

    @NonNull
    public static FragmentWallpaperBinding inflate(@NonNull LayoutInflater inflater, @Nullable ViewGroup root, boolean attachToRoot, @Nullable DataBindingComponent bindingComponent) {
        return (FragmentWallpaperBinding) DataBindingUtil.inflate(inflater, R.layout.fragment_wallpaper, root, attachToRoot, bindingComponent);
    }

    @NonNull
    public static FragmentWallpaperBinding inflate(@NonNull LayoutInflater inflater) {
        return inflate(inflater, DataBindingUtil.getDefaultComponent());
    }

    @NonNull
    public static FragmentWallpaperBinding inflate(@NonNull LayoutInflater inflater, @Nullable DataBindingComponent bindingComponent) {
        return bind(inflater.inflate(R.layout.fragment_wallpaper, (ViewGroup) null, false), bindingComponent);
    }

    @NonNull
    public static FragmentWallpaperBinding bind(@NonNull View view) {
        return bind(view, DataBindingUtil.getDefaultComponent());
    }

    @NonNull
    public static FragmentWallpaperBinding bind(@NonNull View view, @Nullable DataBindingComponent bindingComponent) {
        if ("layout/fragment_wallpaper_0".equals(view.getTag())) {
            return new FragmentWallpaperBinding(bindingComponent, view);
        }
        throw new RuntimeException("view tag isn't correct on view:" + view.getTag());
    }
}
