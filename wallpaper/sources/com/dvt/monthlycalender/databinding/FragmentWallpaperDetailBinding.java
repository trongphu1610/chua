package com.dvt.monthlycalender.databinding;

import android.arch.lifecycle.MutableLiveData;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.dvt.monthlycalender.R;
import com.dvt.monthlycalender.viewmodel.WallpaperDetailViewModel;

public class FragmentWallpaperDetailBinding extends ViewDataBinding implements OnClickListener.Listener {
    @Nullable
    private static final ViewDataBinding.IncludedLayouts sIncludes = null;
    @Nullable
    private static final SparseIntArray sViewsWithIds = null;
    @NonNull
    public final Button btnSetAsWallpaper;
    @NonNull
    public final ImageView imgWallpaper;
    @Nullable
    private final View.OnClickListener mCallback1;
    private long mDirtyFlags = -1;
    @Nullable
    private WallpaperDetailViewModel mViewModel;
    @NonNull
    private final ConstraintLayout mboundView0;
    @NonNull
    public final TextView tvImageAuthor;
    @NonNull
    public final TextView tvImageName;

    public FragmentWallpaperDetailBinding(@NonNull DataBindingComponent bindingComponent, @NonNull View root) {
        super(bindingComponent, root, 1);
        Object[] bindings = mapBindings(bindingComponent, root, 5, sIncludes, sViewsWithIds);
        this.btnSetAsWallpaper = (Button) bindings[2];
        this.btnSetAsWallpaper.setTag((Object) null);
        this.imgWallpaper = (ImageView) bindings[1];
        this.imgWallpaper.setTag((Object) null);
        this.mboundView0 = (ConstraintLayout) bindings[0];
        this.mboundView0.setTag((Object) null);
        this.tvImageAuthor = (TextView) bindings[4];
        this.tvImageAuthor.setTag((Object) null);
        this.tvImageName = (TextView) bindings[3];
        this.tvImageName.setTag((Object) null);
        setRootTag(root);
        this.mCallback1 = new OnClickListener(this, 1);
        invalidateAll();
    }

    public void invalidateAll() {
        synchronized (this) {
            this.mDirtyFlags = 4;
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
        setViewModel((WallpaperDetailViewModel) variable);
        return true;
    }

    public void setViewModel(@Nullable WallpaperDetailViewModel ViewModel) {
        this.mViewModel = ViewModel;
        synchronized (this) {
            this.mDirtyFlags |= 2;
        }
        notifyPropertyChanged(1);
        super.requestRebind();
    }

    @Nullable
    public WallpaperDetailViewModel getViewModel() {
        return this.mViewModel;
    }

    /* access modifiers changed from: protected */
    public boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        if (localFieldId != 0) {
            return false;
        }
        return onChangeViewModelIsWallpaper((MutableLiveData) object, fieldId);
    }

    private boolean onChangeViewModelIsWallpaper(MutableLiveData<Boolean> mutableLiveData, int fieldId) {
        if (fieldId != 0) {
            return false;
        }
        synchronized (this) {
            this.mDirtyFlags |= 1;
        }
        return true;
    }

    /* JADX INFO: finally extract failed */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00a0, code lost:
        r0 = th;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void executeBindings() {
        /*
            r25 = this;
            r1 = r25
            r2 = 0
            monitor-enter(r25)
            long r4 = r1.mDirtyFlags     // Catch:{ all -> 0x009b }
            r2 = r4
            r4 = 0
            r1.mDirtyFlags = r4     // Catch:{ all -> 0x009b }
            monitor-exit(r25)     // Catch:{ all -> 0x009b }
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            com.dvt.monthlycalender.viewmodel.WallpaperDetailViewModel r13 = r1.mViewModel
            r14 = 7
            long r16 = r2 & r14
            int r18 = (r16 > r4 ? 1 : (r16 == r4 ? 0 : -1))
            r16 = 6
            if (r18 == 0) goto L_0x0067
            long r18 = r2 & r16
            int r20 = (r18 > r4 ? 1 : (r18 == r4 ? 0 : -1))
            if (r20 == 0) goto L_0x0034
            if (r13 == 0) goto L_0x0034
            java.lang.String r6 = r13.getImageAuthor()
            java.lang.String r8 = r13.getImageName()
            int r9 = r13.getImageId()
        L_0x0034:
            if (r13 == 0) goto L_0x003a
            android.arch.lifecycle.MutableLiveData r7 = r13.isWallpaper()
        L_0x003a:
            r4 = 0
            r1.updateLiveDataRegistration(r4, r7)
            if (r7 == 0) goto L_0x0047
            java.lang.Object r5 = r7.getValue()
            r10 = r5
            java.lang.Boolean r10 = (java.lang.Boolean) r10
        L_0x0047:
            boolean r12 = android.databinding.ViewDataBinding.safeUnbox((java.lang.Boolean) r10)
            long r18 = r2 & r14
            r20 = 0
            int r5 = (r18 > r20 ? 1 : (r18 == r20 ? 0 : -1))
            if (r5 == 0) goto L_0x0061
            if (r12 == 0) goto L_0x005c
            r18 = 16
            long r22 = r2 | r18
        L_0x0059:
            r2 = r22
            goto L_0x0061
        L_0x005c:
            r18 = 8
            long r22 = r2 | r18
            goto L_0x0059
        L_0x0061:
            if (r12 == 0) goto L_0x0066
            r4 = 8
        L_0x0066:
            r11 = r4
        L_0x0067:
            r4 = 4
            long r18 = r2 & r4
            r4 = 0
            int r20 = (r18 > r4 ? 1 : (r18 == r4 ? 0 : -1))
            if (r20 == 0) goto L_0x0078
            android.widget.Button r4 = r1.btnSetAsWallpaper
            android.view.View$OnClickListener r5 = r1.mCallback1
            r4.setOnClickListener(r5)
        L_0x0078:
            long r4 = r2 & r14
            r14 = 0
            int r18 = (r4 > r14 ? 1 : (r4 == r14 ? 0 : -1))
            if (r18 == 0) goto L_0x0085
            android.widget.Button r4 = r1.btnSetAsWallpaper
            r4.setVisibility(r11)
        L_0x0085:
            long r4 = r2 & r16
            int r16 = (r4 > r14 ? 1 : (r4 == r14 ? 0 : -1))
            if (r16 == 0) goto L_0x009a
            android.widget.ImageView r4 = r1.imgWallpaper
            com.dvt.monthlycalender.extension.UtilKt.setImageResource(r4, r9)
            android.widget.TextView r4 = r1.tvImageAuthor
            android.databinding.adapters.TextViewBindingAdapter.setText(r4, r6)
            android.widget.TextView r4 = r1.tvImageName
            android.databinding.adapters.TextViewBindingAdapter.setText(r4, r8)
        L_0x009a:
            return
        L_0x009b:
            r0 = move-exception
            r3 = r2
        L_0x009d:
            r2 = r0
            monitor-exit(r25)     // Catch:{ all -> 0x00a0 }
            throw r2
        L_0x00a0:
            r0 = move-exception
            goto L_0x009d
        */
        throw new UnsupportedOperationException("Method not decompiled: com.dvt.monthlycalender.databinding.FragmentWallpaperDetailBinding.executeBindings():void");
    }

    public final void _internalCallbackOnClick(int sourceId, View callbackArg_0) {
        WallpaperDetailViewModel viewModel = this.mViewModel;
        if (viewModel != null) {
            viewModel.invokeClick();
        }
    }

    @NonNull
    public static FragmentWallpaperDetailBinding inflate(@NonNull LayoutInflater inflater, @Nullable ViewGroup root, boolean attachToRoot) {
        return inflate(inflater, root, attachToRoot, DataBindingUtil.getDefaultComponent());
    }

    @NonNull
    public static FragmentWallpaperDetailBinding inflate(@NonNull LayoutInflater inflater, @Nullable ViewGroup root, boolean attachToRoot, @Nullable DataBindingComponent bindingComponent) {
        return (FragmentWallpaperDetailBinding) DataBindingUtil.inflate(inflater, R.layout.fragment_wallpaper_detail, root, attachToRoot, bindingComponent);
    }

    @NonNull
    public static FragmentWallpaperDetailBinding inflate(@NonNull LayoutInflater inflater) {
        return inflate(inflater, DataBindingUtil.getDefaultComponent());
    }

    @NonNull
    public static FragmentWallpaperDetailBinding inflate(@NonNull LayoutInflater inflater, @Nullable DataBindingComponent bindingComponent) {
        return bind(inflater.inflate(R.layout.fragment_wallpaper_detail, (ViewGroup) null, false), bindingComponent);
    }

    @NonNull
    public static FragmentWallpaperDetailBinding bind(@NonNull View view) {
        return bind(view, DataBindingUtil.getDefaultComponent());
    }

    @NonNull
    public static FragmentWallpaperDetailBinding bind(@NonNull View view, @Nullable DataBindingComponent bindingComponent) {
        if ("layout/fragment_wallpaper_detail_0".equals(view.getTag())) {
            return new FragmentWallpaperDetailBinding(bindingComponent, view);
        }
        throw new RuntimeException("view tag isn't correct on view:" + view.getTag());
    }
}
