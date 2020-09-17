package androidx.navigation;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(bv = {1, 0, 2}, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\r\u0010\u000b\u001a\u00020\fH\u0000¢\u0006\u0002\b\rJ\u001f\u0010\t\u001a\u00020\u000e2\u0017\u0010\u000f\u001a\u0013\u0012\u0004\u0012\u00020\u0011\u0012\u0004\u0012\u00020\u000e0\u0010¢\u0006\u0002\b\u0012R\u001a\u0010\u0003\u001a\u00020\u0004X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\bR\u0010\u0010\t\u001a\u0004\u0018\u00010\nX\u000e¢\u0006\u0002\n\u0000¨\u0006\u0013"}, d2 = {"Landroidx/navigation/NavActionBuilder;", "", "()V", "destinationId", "", "getDestinationId", "()I", "setDestinationId", "(I)V", "navOptions", "Landroidx/navigation/NavOptions;", "build", "Landroidx/navigation/NavAction;", "build$navigation_common_ktx_release", "", "block", "Lkotlin/Function1;", "Landroidx/navigation/NavOptionsBuilder;", "Lkotlin/ExtensionFunctionType;", "navigation-common-ktx_release"}, k = 1, mv = {1, 1, 10})
@NavDestinationDsl
/* compiled from: NavDestinationBuilder.kt */
public final class NavActionBuilder {
    private int destinationId;
    private NavOptions navOptions;

    public final int getDestinationId() {
        return this.destinationId;
    }

    public final void setDestinationId(int i) {
        this.destinationId = i;
    }

    public final void navOptions(@NotNull Function1<? super NavOptionsBuilder, Unit> block) {
        Intrinsics.checkParameterIsNotNull(block, "block");
        NavOptionsBuilder navOptionsBuilder = new NavOptionsBuilder();
        block.invoke(navOptionsBuilder);
        this.navOptions = navOptionsBuilder.build$navigation_common_ktx_release();
    }

    @NotNull
    public final NavAction build$navigation_common_ktx_release() {
        return new NavAction(this.destinationId, this.navOptions);
    }
}
