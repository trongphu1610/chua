package androidx.navigation;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000\u001a\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\u001a\u001f\u0010\u0000\u001a\u00020\u00012\u0017\u0010\u0002\u001a\u0013\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00050\u0003¢\u0006\u0002\b\u0006¨\u0006\u0007"}, d2 = {"navOptions", "Landroidx/navigation/NavOptions;", "block", "Lkotlin/Function1;", "Landroidx/navigation/NavOptionsBuilder;", "", "Lkotlin/ExtensionFunctionType;", "navigation-common-ktx_release"}, k = 2, mv = {1, 1, 10})
/* compiled from: NavOptionsBuilder.kt */
public final class NavOptionsBuilderKt {
    @NotNull
    public static final NavOptions navOptions(@NotNull Function1<? super NavOptionsBuilder, Unit> block) {
        Intrinsics.checkParameterIsNotNull(block, "block");
        NavOptionsBuilder navOptionsBuilder = new NavOptionsBuilder();
        block.invoke(navOptionsBuilder);
        NavOptions build$navigation_common_ktx_release = navOptionsBuilder.build$navigation_common_ktx_release();
        Intrinsics.checkExpressionValueIsNotNull(build$navigation_common_ktx_release, "NavOptionsBuilder().apply(block).build()");
        return build$navigation_common_ktx_release;
    }
}
