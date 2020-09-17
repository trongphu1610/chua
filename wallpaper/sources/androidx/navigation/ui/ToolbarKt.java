package androidx.navigation.ui;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import androidx.navigation.NavController;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000\u0018\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a\u001e\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u00042\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0006¨\u0006\u0007"}, d2 = {"setupWithNavController", "", "Landroid/support/v7/widget/Toolbar;", "navController", "Landroidx/navigation/NavController;", "drawerLayout", "Landroid/support/v4/widget/DrawerLayout;", "navigation-ui-ktx_release"}, k = 2, mv = {1, 1, 10})
/* compiled from: Toolbar.kt */
public final class ToolbarKt {
    public static /* bridge */ /* synthetic */ void setupWithNavController$default(Toolbar toolbar, NavController navController, DrawerLayout drawerLayout, int i, Object obj) {
        if ((i & 2) != 0) {
            drawerLayout = null;
        }
        setupWithNavController(toolbar, navController, drawerLayout);
    }

    public static final void setupWithNavController(@NotNull Toolbar $receiver, @NotNull NavController navController, @Nullable DrawerLayout drawerLayout) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(navController, "navController");
        NavigationUI.setupWithNavController($receiver, navController, drawerLayout);
    }
}
