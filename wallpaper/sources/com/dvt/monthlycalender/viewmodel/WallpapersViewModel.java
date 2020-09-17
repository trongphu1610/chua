package com.dvt.monthlycalender.viewmodel;

import android.arch.lifecycle.ViewModel;
import com.dvt.monthlycalender.R;
import com.dvt.monthlycalender.model.WallPaper;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\u0010\u000e\n\u0002\b\u0005\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u0006\u0010\u000f\u001a\u00020\fJ\u000e\u0010\u0010\u001a\u00020\u00052\u0006\u0010\u0011\u001a\u00020\fR!\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u00048BX\u0002¢\u0006\f\n\u0004\b\b\u0010\t\u001a\u0004\b\u0006\u0010\u0007R\"\u0010\n\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\r0\u000b0\u0004X\u0004¢\u0006\u0004\n\u0002\u0010\u000e¨\u0006\u0012"}, d2 = {"Lcom/dvt/monthlycalender/viewmodel/WallpapersViewModel;", "Landroid/arch/lifecycle/ViewModel;", "()V", "arrWallpapers", "", "Lcom/dvt/monthlycalender/model/WallPaper;", "getArrWallpapers", "()[Lcom/dvt/monthlycalender/model/WallPaper;", "arrWallpapers$delegate", "Lkotlin/Lazy;", "groupOfImage", "Lkotlin/Pair;", "", "", "[Lkotlin/Pair;", "getCount", "getItemAt", "position", "app_debug"}, k = 1, mv = {1, 1, 11})
/* compiled from: WallpapersViewModel.kt */
public final class WallpapersViewModel extends ViewModel {
    static final /* synthetic */ KProperty[] $$delegatedProperties = {Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(WallpapersViewModel.class), "arrWallpapers", "getArrWallpapers()[Lcom/dvt/monthlycalender/model/WallPaper;"))};
    private final Lazy arrWallpapers$delegate = LazyKt.lazy(new WallpapersViewModel$arrWallpapers$2(this));
    /* access modifiers changed from: private */
    public final Pair<Integer, String>[] groupOfImage = {new Pair<>(Integer.valueOf(R.drawable.january), "January"), new Pair<>(Integer.valueOf(R.drawable.february), "February"), new Pair<>(Integer.valueOf(R.drawable.march), "March"), new Pair<>(Integer.valueOf(R.drawable.april), "April"), new Pair<>(Integer.valueOf(R.drawable.may), "May"), new Pair<>(Integer.valueOf(R.drawable.june), "June"), new Pair<>(Integer.valueOf(R.drawable.july), "July"), new Pair<>(Integer.valueOf(R.drawable.august), "August"), new Pair<>(Integer.valueOf(R.drawable.september), "September"), new Pair<>(Integer.valueOf(R.drawable.october), "October"), new Pair<>(Integer.valueOf(R.drawable.november), "November"), new Pair<>(Integer.valueOf(R.drawable.december), "December")};

    private final WallPaper[] getArrWallpapers() {
        Lazy lazy = this.arrWallpapers$delegate;
        KProperty kProperty = $$delegatedProperties[0];
        return (WallPaper[]) lazy.getValue();
    }

    @NotNull
    public final WallPaper getItemAt(int position) {
        return getArrWallpapers()[position];
    }

    public final int getCount() {
        return getArrWallpapers().length;
    }
}
