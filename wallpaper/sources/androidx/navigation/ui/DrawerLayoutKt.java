package androidx.navigation.ui;

import android.support.v4.widget.DrawerLayout;
import androidx.navigation.NavController;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000\u0012\n\u0000\n\u0002\u0010\u000b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a\u0014\u0010\u0000\u001a\u00020\u0001*\u0004\u0018\u00010\u00022\u0006\u0010\u0003\u001a\u00020\u0004¨\u0006\u0005"}, d2 = {"navigateUp", "", "Landroid/support/v4/widget/DrawerLayout;", "navController", "Landroidx/navigation/NavController;", "navigation-ui-ktx_release"}, k = 2, mv = {1, 1, 10})
/* compiled from: DrawerLayout.kt */
public final class DrawerLayoutKt {
    public static final boolean navigateUp(@Nullable DrawerLayout $receiver, @NotNull NavController navController) {
        Intrinsics.checkParameterIsNotNull(navController, "navController");
        return NavigationUI.navigateUp($receiver, navController);
    }
}
