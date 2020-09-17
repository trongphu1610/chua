package androidx.navigation;

import android.view.View;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000\f\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\u001a\n\u0010\u0000\u001a\u00020\u0001*\u00020\u0002¨\u0006\u0003"}, d2 = {"findNavController", "Landroidx/navigation/NavController;", "Landroid/view/View;", "navigation-runtime-ktx_release"}, k = 2, mv = {1, 1, 10})
/* compiled from: View.kt */
public final class ViewKt {
    @NotNull
    public static final NavController findNavController(@NotNull View $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        NavController findNavController = Navigation.findNavController($receiver);
        Intrinsics.checkExpressionValueIsNotNull(findNavController, "Navigation.findNavController(this)");
        return findNavController;
    }
}
