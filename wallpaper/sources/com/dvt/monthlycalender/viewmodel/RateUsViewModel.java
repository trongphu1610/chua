package com.dvt.monthlycalender.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003\u0018\u0000 \u000b2\u00020\u0001:\u0001\u000bB\u0005¢\u0006\u0002\u0010\u0002J\u0006\u0010\b\u001a\u00020\tJ\u0006\u0010\n\u001a\u00020\tR\u0017\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\f"}, d2 = {"Lcom/dvt/monthlycalender/viewmodel/RateUsViewModel;", "Landroid/arch/lifecycle/ViewModel;", "()V", "navigation", "Landroid/arch/lifecycle/MutableLiveData;", "", "getNavigation", "()Landroid/arch/lifecycle/MutableLiveData;", "onLaterClick", "", "onRateClick", "Companion", "app_debug"}, k = 1, mv = {1, 1, 11})
/* compiled from: RateUsViewModel.kt */
public final class RateUsViewModel extends ViewModel {
    public static final int ACTION_LATER = 2;
    public static final int ACTION_RATE = 1;
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    @NotNull
    private final MutableLiveData<Integer> navigation = new MutableLiveData<>();

    @Metadata(bv = {1, 0, 2}, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000¨\u0006\u0006"}, d2 = {"Lcom/dvt/monthlycalender/viewmodel/RateUsViewModel$Companion;", "", "()V", "ACTION_LATER", "", "ACTION_RATE", "app_debug"}, k = 1, mv = {1, 1, 11})
    /* compiled from: RateUsViewModel.kt */
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }

    @NotNull
    public final MutableLiveData<Integer> getNavigation() {
        return this.navigation;
    }

    public final void onRateClick() {
        this.navigation.setValue(1);
    }

    public final void onLaterClick() {
        this.navigation.setValue(2);
    }
}
