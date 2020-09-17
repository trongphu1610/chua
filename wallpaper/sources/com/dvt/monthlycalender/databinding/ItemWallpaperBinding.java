package com.dvt.monthlycalender.databinding;

import android.databinding.DataBindingComponent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.databinding.adapters.TextViewBindingAdapter;
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
import com.dvt.monthlycalender.extension.UtilKt;
import com.dvt.monthlycalender.model.WallPaper;

public class ItemWallpaperBinding extends ViewDataBinding {
    @Nullable
    private static final ViewDataBinding.IncludedLayouts sIncludes = null;
    @Nullable
    private static final SparseIntArray sViewsWithIds = null;
    @NonNull
    public final ImageView imgWallpaper;
    private long mDirtyFlags = -1;
    @Nullable
    private WallPaper mViewModel;
    @NonNull
    private final ConstraintLayout mboundView0;
    @NonNull
    public final TextView tvImageAuthor;
    @NonNull
    public final TextView tvImageName;

    public ItemWallpaperBinding(@NonNull DataBindingComponent bindingComponent, @NonNull View root) {
        super(bindingComponent, root, 0);
        Object[] bindings = mapBindings(bindingComponent, root, 4, sIncludes, sViewsWithIds);
        this.imgWallpaper = (ImageView) bindings[1];
        this.imgWallpaper.setTag((Object) null);
        this.mboundView0 = (ConstraintLayout) bindings[0];
        this.mboundView0.setTag((Object) null);
        this.tvImageAuthor = (TextView) bindings[3];
        this.tvImageAuthor.setTag((Object) null);
        this.tvImageName = (TextView) bindings[2];
        this.tvImageName.setTag((Object) null);
        setRootTag(root);
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
        setViewModel((WallPaper) variable);
        return true;
    }

    public void setViewModel(@Nullable WallPaper ViewModel) {
        this.mViewModel = ViewModel;
        synchronized (this) {
            this.mDirtyFlags |= 1;
        }
        notifyPropertyChanged(1);
        super.requestRebind();
    }

    @Nullable
    public WallPaper getViewModel() {
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
        int viewModelImageId = 0;
        String viewModelAuthor = null;
        String viewModelName = null;
        WallPaper viewModel = this.mViewModel;
        if (!((dirtyFlags & 3) == 0 || viewModel == null)) {
            viewModelImageId = viewModel.getImageId();
            viewModelAuthor = viewModel.getAuthor();
            viewModelName = viewModel.getName();
        }
        if ((dirtyFlags & 3) != 0) {
            UtilKt.setImageResource(this.imgWallpaper, viewModelImageId);
            TextViewBindingAdapter.setText(this.tvImageAuthor, viewModelAuthor);
            TextViewBindingAdapter.setText(this.tvImageName, viewModelName);
        }
    }

    @NonNull
    public static ItemWallpaperBinding inflate(@NonNull LayoutInflater inflater, @Nullable ViewGroup root, boolean attachToRoot) {
        return inflate(inflater, root, attachToRoot, DataBindingUtil.getDefaultComponent());
    }

    @NonNull
    public static ItemWallpaperBinding inflate(@NonNull LayoutInflater inflater, @Nullable ViewGroup root, boolean attachToRoot, @Nullable DataBindingComponent bindingComponent) {
        return (ItemWallpaperBinding) DataBindingUtil.inflate(inflater, R.layout.item_wallpaper, root, attachToRoot, bindingComponent);
    }

    @NonNull
    public static ItemWallpaperBinding inflate(@NonNull LayoutInflater inflater) {
        return inflate(inflater, DataBindingUtil.getDefaultComponent());
    }

    @NonNull
    public static ItemWallpaperBinding inflate(@NonNull LayoutInflater inflater, @Nullable DataBindingComponent bindingComponent) {
        return bind(inflater.inflate(R.layout.item_wallpaper, (ViewGroup) null, false), bindingComponent);
    }

    @NonNull
    public static ItemWallpaperBinding bind(@NonNull View view) {
        return bind(view, DataBindingUtil.getDefaultComponent());
    }

    @NonNull
    public static ItemWallpaperBinding bind(@NonNull View view, @Nullable DataBindingComponent bindingComponent) {
        if ("layout/item_wallpaper_0".equals(view.getTag())) {
            return new ItemWallpaperBinding(bindingComponent, view);
        }
        throw new RuntimeException("view tag isn't correct on view:" + view.getTag());
    }
}
