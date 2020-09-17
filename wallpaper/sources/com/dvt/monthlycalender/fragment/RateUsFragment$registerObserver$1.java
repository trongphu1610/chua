package com.dvt.monthlycalender.fragment;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import com.dvt.monthlycalender.util.AppUtils;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.Nullable;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000\u0010\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\u0010\u0000\u001a\u00020\u00012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003H\n¢\u0006\u0004\b\u0004\u0010\u0005"}, d2 = {"<anonymous>", "", "it", "", "onChanged", "(Ljava/lang/Integer;)V"}, k = 3, mv = {1, 1, 11})
/* compiled from: RateUsFragment.kt */
final class RateUsFragment$registerObserver$1<T> implements Observer<Integer> {
    final /* synthetic */ RateUsFragment this$0;

    RateUsFragment$registerObserver$1(RateUsFragment rateUsFragment) {
        this.this$0 = rateUsFragment;
    }

    public final void onChanged(@Nullable Integer it) {
        if (it != null && it.intValue() == 1) {
            PreferenceManager.getDefaultSharedPreferences(this.this$0.getContext()).edit().putBoolean("is_rated", true);
            AppUtils.Companion companion = AppUtils.Companion;
            Context context = this.this$0.getContext();
            if (context == null) {
                Intrinsics.throwNpe();
            }
            Intrinsics.checkExpressionValueIsNotNull(context, "context!!");
            companion.openChPlay(context);
        }
        FragmentActivity activity = this.this$0.getActivity();
        if (activity != null) {
            activity.finish();
        }
    }
}
