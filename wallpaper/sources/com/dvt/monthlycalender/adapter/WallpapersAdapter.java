package com.dvt.monthlycalender.adapter;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.dvt.monthlycalender.databinding.ItemWallpaperBinding;
import com.dvt.monthlycalender.listener.WallpaperItemClick;
import com.dvt.monthlycalender.viewmodel.WallpapersViewModel;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(bv = {1, 0, 2}, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0001\u0012B\u0015\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0002\u0010\u0007J\b\u0010\b\u001a\u00020\tH\u0016J\u0018\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u00022\u0006\u0010\r\u001a\u00020\tH\u0016J\u0018\u0010\u000e\u001a\u00020\u00022\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\tH\u0016R\u000e\u0010\u0005\u001a\u00020\u0006X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0003\u001a\u00020\u0004X\u0004¢\u0006\u0002\n\u0000¨\u0006\u0013"}, d2 = {"Lcom/dvt/monthlycalender/adapter/WallpapersAdapter;", "Landroid/support/v7/widget/RecyclerView$Adapter;", "Lcom/dvt/monthlycalender/adapter/WallpapersAdapter$Holder;", "viewModel2", "Lcom/dvt/monthlycalender/viewmodel/WallpapersViewModel;", "onClick", "Lcom/dvt/monthlycalender/listener/WallpaperItemClick;", "(Lcom/dvt/monthlycalender/viewmodel/WallpapersViewModel;Lcom/dvt/monthlycalender/listener/WallpaperItemClick;)V", "getItemCount", "", "onBindViewHolder", "", "holder", "position", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "Holder", "app_debug"}, k = 1, mv = {1, 1, 11})
/* compiled from: WallpapersAdapter.kt */
public final class WallpapersAdapter extends RecyclerView.Adapter<Holder> {
    /* access modifiers changed from: private */
    public final WallpaperItemClick onClick;
    private final WallpapersViewModel viewModel2;

    public WallpapersAdapter(@NotNull WallpapersViewModel viewModel22, @NotNull WallpaperItemClick onClick2) {
        Intrinsics.checkParameterIsNotNull(viewModel22, "viewModel2");
        Intrinsics.checkParameterIsNotNull(onClick2, "onClick");
        this.viewModel2 = viewModel22;
        this.onClick = onClick2;
    }

    @NotNull
    public Holder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        Intrinsics.checkParameterIsNotNull(parent, "parent");
        ItemWallpaperBinding inflate = ItemWallpaperBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        Intrinsics.checkExpressionValueIsNotNull(inflate, "ItemWallpaperBinding.inf….context), parent, false)");
        return new Holder(inflate);
    }

    public int getItemCount() {
        return this.viewModel2.getCount();
    }

    public void onBindViewHolder(@NotNull Holder holder, int position) {
        Intrinsics.checkParameterIsNotNull(holder, "holder");
        holder.bindingHolder(position);
        holder.getBinding().setViewModel(this.viewModel2.getItemAt(position));
        holder.getBinding().imgWallpaper.setOnClickListener(new WallpapersAdapter$onBindViewHolder$1(this, position));
    }

    @Metadata(bv = {1, 0, 2}, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0000\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\u000e\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006¨\u0006\u000b"}, d2 = {"Lcom/dvt/monthlycalender/adapter/WallpapersAdapter$Holder;", "Landroid/support/v7/widget/RecyclerView$ViewHolder;", "binding", "Lcom/dvt/monthlycalender/databinding/ItemWallpaperBinding;", "(Lcom/dvt/monthlycalender/databinding/ItemWallpaperBinding;)V", "getBinding", "()Lcom/dvt/monthlycalender/databinding/ItemWallpaperBinding;", "bindingHolder", "", "position", "", "app_debug"}, k = 1, mv = {1, 1, 11})
    /* compiled from: WallpapersAdapter.kt */
    public static final class Holder extends RecyclerView.ViewHolder {
        @NotNull
        private final ItemWallpaperBinding binding;

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        public Holder(@NotNull ItemWallpaperBinding binding2) {
            super(binding2.getRoot());
            Intrinsics.checkParameterIsNotNull(binding2, "binding");
            this.binding = binding2;
        }

        @NotNull
        public final ItemWallpaperBinding getBinding() {
            return this.binding;
        }

        public final void bindingHolder(int position) {
            ViewCompat.setTransitionName(this.binding.imgWallpaper, String.valueOf(position));
        }
    }
}
