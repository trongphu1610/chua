package com.dvt.monthlycalender.extension;

import android.databinding.BindingAdapter;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000\u0014\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\u001a\u0018\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0007¨\u0006\u0006"}, d2 = {"setImageResource", "", "imageView", "Landroid/widget/ImageView;", "resource", "", "app_debug"}, k = 2, mv = {1, 1, 11})
/* compiled from: Util.kt */
public final class UtilKt {
    @BindingAdapter({"glide_res"})
    public static final void setImageResource(@NotNull ImageView imageView, int resource) {
        Intrinsics.checkParameterIsNotNull(imageView, "imageView");
        Glide.with(imageView.getContext()).load(Integer.valueOf(resource)).into(imageView);
    }
}
