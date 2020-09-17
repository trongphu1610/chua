package androidx.navigation;

import android.app.Activity;
import android.support.annotation.IdRes;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000\u0012\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\u001a\u0014\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\b\b\u0001\u0010\u0003\u001a\u00020\u0004¨\u0006\u0005"}, d2 = {"findNavController", "Landroidx/navigation/NavController;", "Landroid/app/Activity;", "viewId", "", "navigation-runtime-ktx_release"}, k = 2, mv = {1, 1, 10})
/* compiled from: Activity.kt */
public final class ActivityKt {
    @NotNull
    public static final NavController findNavController(@NotNull Activity $receiver, @IdRes int viewId) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        NavController findNavController = Navigation.findNavController($receiver, viewId);
        Intrinsics.checkExpressionValueIsNotNull(findNavController, "Navigation.findNavController(this, viewId)");
        return findNavController;
    }
}
