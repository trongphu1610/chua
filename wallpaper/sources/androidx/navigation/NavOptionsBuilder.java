package androidx.navigation;

import android.support.annotation.IdRes;
import androidx.navigation.NavOptions;
import kotlin.Deprecated;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u000e\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u001f\u0010\u001b\u001a\u00020\u001c2\u0017\u0010\u001d\u001a\u0013\u0012\u0004\u0012\u00020\u001f\u0012\u0004\u0012\u00020\u001c0\u001e¢\u0006\u0002\b J\r\u0010!\u001a\u00020\"H\u0000¢\u0006\u0002\b#J)\u0010\u0016\u001a\u00020\u001c2\b\b\u0001\u0010$\u001a\u00020\u00152\u0017\u0010\u001d\u001a\u0013\u0012\u0004\u0012\u00020%\u0012\u0004\u0012\u00020\u001c0\u001e¢\u0006\u0002\b R\u000e\u0010\u0003\u001a\u00020\u0004X\u0004¢\u0006\u0002\n\u0000R$\u0010\u0005\u001a\u00020\u00068\u0006@\u0006X\u000e¢\u0006\u0014\n\u0000\u0012\u0004\b\u0007\u0010\u0002\u001a\u0004\b\b\u0010\t\"\u0004\b\n\u0010\u000bR\u000e\u0010\f\u001a\u00020\u0006X\u000e¢\u0006\u0002\n\u0000R$\u0010\r\u001a\u00020\u00068\u0006@\u0006X\u000e¢\u0006\u0014\n\u0000\u0012\u0004\b\u000e\u0010\u0002\u001a\u0004\b\u000f\u0010\t\"\u0004\b\u0010\u0010\u000bR\u001a\u0010\u0011\u001a\u00020\u0006X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0012\u0010\t\"\u0004\b\u0013\u0010\u000bR&\u0010\u0016\u001a\u00020\u00152\u0006\u0010\u0014\u001a\u00020\u00158\u0006@FX\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0017\u0010\u0018\"\u0004\b\u0019\u0010\u001a¨\u0006&"}, d2 = {"Landroidx/navigation/NavOptionsBuilder;", "", "()V", "builder", "Landroidx/navigation/NavOptions$Builder;", "clearTask", "", "clearTask$annotations", "getClearTask", "()Z", "setClearTask", "(Z)V", "inclusive", "launchDocument", "launchDocument$annotations", "getLaunchDocument", "setLaunchDocument", "launchSingleTop", "getLaunchSingleTop", "setLaunchSingleTop", "value", "", "popUpTo", "getPopUpTo", "()I", "setPopUpTo", "(I)V", "anim", "", "block", "Lkotlin/Function1;", "Landroidx/navigation/AnimBuilder;", "Lkotlin/ExtensionFunctionType;", "build", "Landroidx/navigation/NavOptions;", "build$navigation_common_ktx_release", "id", "Landroidx/navigation/PopUpToBuilder;", "navigation-common-ktx_release"}, k = 1, mv = {1, 1, 10})
@NavOptionsDsl
/* compiled from: NavOptionsBuilder.kt */
public final class NavOptionsBuilder {
    private final NavOptions.Builder builder = new NavOptions.Builder();
    private boolean clearTask;
    private boolean inclusive;
    private boolean launchDocument;
    private boolean launchSingleTop;
    @IdRes
    private int popUpTo;

    @Deprecated(message = "Use popUpTo with the root of the graph and inclusive set to true")
    public static /* synthetic */ void clearTask$annotations() {
    }

    @Deprecated(message = "Use the documentLaunchMode flag on the Activity")
    public static /* synthetic */ void launchDocument$annotations() {
    }

    public final boolean getLaunchSingleTop() {
        return this.launchSingleTop;
    }

    public final void setLaunchSingleTop(boolean z) {
        this.launchSingleTop = z;
    }

    public final boolean getLaunchDocument() {
        return this.launchDocument;
    }

    public final void setLaunchDocument(boolean z) {
        this.launchDocument = z;
    }

    public final boolean getClearTask() {
        return this.clearTask;
    }

    public final void setClearTask(boolean z) {
        this.clearTask = z;
    }

    public final int getPopUpTo() {
        return this.popUpTo;
    }

    public final void setPopUpTo(int value) {
        this.popUpTo = value;
        this.inclusive = false;
    }

    public final void popUpTo(@IdRes int id, @NotNull Function1<? super PopUpToBuilder, Unit> block) {
        Intrinsics.checkParameterIsNotNull(block, "block");
        setPopUpTo(id);
        PopUpToBuilder popUpToBuilder = new PopUpToBuilder();
        block.invoke(popUpToBuilder);
        this.inclusive = popUpToBuilder.getInclusive();
    }

    public final void anim(@NotNull Function1<? super AnimBuilder, Unit> block) {
        Intrinsics.checkParameterIsNotNull(block, "block");
        AnimBuilder $receiver = new AnimBuilder();
        block.invoke($receiver);
        this.builder.setEnterAnim($receiver.getEnter()).setExitAnim($receiver.getExit()).setPopEnterAnim($receiver.getPopEnter()).setPopExitAnim($receiver.getPopExit());
    }

    @NotNull
    public final NavOptions build$navigation_common_ktx_release() {
        NavOptions.Builder builder2 = this.builder;
        NavOptions.Builder $receiver = builder2;
        $receiver.setLaunchSingleTop(this.launchSingleTop);
        $receiver.setLaunchDocument(this.launchDocument);
        $receiver.setClearTask(this.clearTask);
        $receiver.setPopUpTo(this.popUpTo, this.inclusive);
        return builder2.build();
    }
}
