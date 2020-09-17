package com.dvt.monthlycalender.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.app.Fragment;
import com.dvt.monthlycalender.viewmodel.RateUsViewModel;
import kotlin.Metadata;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000\b\n\u0000\n\u0002\u0018\u0002\n\u0000\u0010\u0000\u001a\u00020\u0001H\n¢\u0006\u0002\b\u0002"}, d2 = {"<anonymous>", "Lcom/dvt/monthlycalender/viewmodel/RateUsViewModel;", "invoke"}, k = 3, mv = {1, 1, 11})
/* compiled from: RateUsFragment.kt */
final class RateUsFragment$viewModel$2 extends Lambda implements Function0<RateUsViewModel> {
    final /* synthetic */ RateUsFragment this$0;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    RateUsFragment$viewModel$2(RateUsFragment rateUsFragment) {
        super(0);
        this.this$0 = rateUsFragment;
    }

    @NotNull
    public final RateUsViewModel invoke() {
        return (RateUsViewModel) ViewModelProviders.of((Fragment) this.this$0).get(RateUsViewModel.class);
    }
}
