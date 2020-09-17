package androidx.navigation;

import android.support.annotation.IdRes;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000&\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\u001a:\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\b\b\u0003\u0010\u0003\u001a\u00020\u00042\b\b\u0001\u0010\u0005\u001a\u00020\u00042\u0017\u0010\u0006\u001a\u0013\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\t0\u0007¢\u0006\u0002\b\nH\b¨\u0006\u000b"}, d2 = {"createGraph", "Landroidx/navigation/NavGraph;", "Landroidx/navigation/NavController;", "id", "", "startDestination", "block", "Lkotlin/Function1;", "Landroidx/navigation/NavGraphBuilder;", "", "Lkotlin/ExtensionFunctionType;", "navigation-runtime-ktx_release"}, k = 2, mv = {1, 1, 10})
/* compiled from: NavController.kt */
public final class NavControllerKt {
    @NotNull
    public static /* bridge */ /* synthetic */ NavGraph createGraph$default(NavController $receiver, int id, int startDestination, Function1 block, int i, Object obj) {
        if ((i & 1) != 0) {
            id = 0;
        }
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(block, "block");
        NavigatorProvider $receiver$iv = $receiver.getNavigatorProvider();
        Intrinsics.checkExpressionValueIsNotNull($receiver$iv, "navigatorProvider");
        NavGraphBuilder navGraphBuilder = new NavGraphBuilder($receiver$iv, id, startDestination);
        block.invoke(navGraphBuilder);
        return navGraphBuilder.build();
    }

    @NotNull
    public static final NavGraph createGraph(@NotNull NavController $receiver, @IdRes int id, @IdRes int startDestination, @NotNull Function1<? super NavGraphBuilder, Unit> block) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(block, "block");
        NavigatorProvider $receiver$iv = $receiver.getNavigatorProvider();
        Intrinsics.checkExpressionValueIsNotNull($receiver$iv, "navigatorProvider");
        NavGraphBuilder navGraphBuilder = new NavGraphBuilder($receiver$iv, id, startDestination);
        block.invoke(navGraphBuilder);
        return navGraphBuilder.build();
    }
}
