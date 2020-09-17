package androidx.navigation;

import android.os.Bundle;
import android.support.annotation.IdRes;
import androidx.navigation.NavDestination;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000Z\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010%\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\r\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0017\u0018\u0000*\n\b\u0000\u0010\u0001 \u0001*\u00020\u00022\u00020\u0003B\u001f\u0012\u000e\u0010\u0004\u001a\n\u0012\u0006\b\u0001\u0012\u00028\u00000\u0005\u0012\b\b\u0001\u0010\u0006\u001a\u00020\u0007¢\u0006\u0002\u0010\bJ'\u0010\u001f\u001a\u00020 2\u0006\u0010!\u001a\u00020\u00072\u0017\u0010\"\u001a\u0013\u0012\u0004\u0012\u00020$\u0012\u0004\u0012\u00020 0#¢\u0006\u0002\b%J\r\u0010&\u001a\u00028\u0000H\u0016¢\u0006\u0002\u0010'J\u000e\u0010(\u001a\u00020 2\u0006\u0010)\u001a\u00020\u000eR\u001a\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u000b0\nX\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\rX\u000e¢\u0006\u0002\n\u0000R\u001c\u0010\u000f\u001a\u0004\u0018\u00010\u0010X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0011\u0010\u0012\"\u0004\b\u0013\u0010\u0014R\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u001c\u0010\u0017\u001a\u0004\u0018\u00010\u0018X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0019\u0010\u001a\"\u0004\b\u001b\u0010\u001cR\u001c\u0010\u0004\u001a\n\u0012\u0006\b\u0001\u0012\u00028\u00000\u0005X\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001e¨\u0006*"}, d2 = {"Landroidx/navigation/NavDestinationBuilder;", "D", "Landroidx/navigation/NavDestination;", "", "navigator", "Landroidx/navigation/Navigator;", "id", "", "(Landroidx/navigation/Navigator;I)V", "actions", "", "Landroidx/navigation/NavAction;", "deepLinks", "", "", "defaultArguments", "Landroid/os/Bundle;", "getDefaultArguments", "()Landroid/os/Bundle;", "setDefaultArguments", "(Landroid/os/Bundle;)V", "getId", "()I", "label", "", "getLabel", "()Ljava/lang/CharSequence;", "setLabel", "(Ljava/lang/CharSequence;)V", "getNavigator", "()Landroidx/navigation/Navigator;", "action", "", "actionId", "block", "Lkotlin/Function1;", "Landroidx/navigation/NavActionBuilder;", "Lkotlin/ExtensionFunctionType;", "build", "()Landroidx/navigation/NavDestination;", "deepLink", "uriPattern", "navigation-common-ktx_release"}, k = 1, mv = {1, 1, 10})
@NavDestinationDsl
/* compiled from: NavDestinationBuilder.kt */
public class NavDestinationBuilder<D extends NavDestination> {
    private Map<Integer, NavAction> actions = new LinkedHashMap();
    private List<String> deepLinks = new ArrayList();
    @Nullable
    private Bundle defaultArguments;
    private final int id;
    @Nullable
    private CharSequence label;
    @NotNull
    private final Navigator<? extends D> navigator;

    public NavDestinationBuilder(@NotNull Navigator<? extends D> navigator2, @IdRes int id2) {
        Intrinsics.checkParameterIsNotNull(navigator2, "navigator");
        this.navigator = navigator2;
        this.id = id2;
    }

    /* access modifiers changed from: protected */
    @NotNull
    public final Navigator<? extends D> getNavigator() {
        return this.navigator;
    }

    public final int getId() {
        return this.id;
    }

    @Nullable
    public final CharSequence getLabel() {
        return this.label;
    }

    public final void setLabel(@Nullable CharSequence charSequence) {
        this.label = charSequence;
    }

    @Nullable
    public final Bundle getDefaultArguments() {
        return this.defaultArguments;
    }

    public final void setDefaultArguments(@Nullable Bundle bundle) {
        this.defaultArguments = bundle;
    }

    public final void deepLink(@NotNull String uriPattern) {
        Intrinsics.checkParameterIsNotNull(uriPattern, "uriPattern");
        this.deepLinks.add(uriPattern);
    }

    public final void action(int actionId, @NotNull Function1<? super NavActionBuilder, Unit> block) {
        Intrinsics.checkParameterIsNotNull(block, "block");
        Map<Integer, NavAction> map = this.actions;
        Integer valueOf = Integer.valueOf(actionId);
        NavActionBuilder navActionBuilder = new NavActionBuilder();
        block.invoke(navActionBuilder);
        map.put(valueOf, navActionBuilder.build$navigation_common_ktx_release());
    }

    @NotNull
    public D build() {
        D createDestination = this.navigator.createDestination();
        NavDestination destination = createDestination;
        destination.setId(this.id);
        destination.setLabel(this.label);
        destination.setDefaultArguments(this.defaultArguments);
        for (String deepLink : this.deepLinks) {
            destination.addDeepLink(deepLink);
        }
        for (Map.Entry $actionId_action : this.actions.entrySet()) {
            destination.putAction(((Number) $actionId_action.getKey()).intValue(), (NavAction) $actionId_action.getValue());
        }
        return createDestination;
    }
}
