package androidx.navigation;

import kotlin.Metadata;
import kotlin.jvm.JvmClassMappingKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.KClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000*\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0004\u001a4\u0010\u0000\u001a\u0002H\u0001\"\b\b\u0000\u0010\u0002*\u00020\u0003\"\u000e\b\u0001\u0010\u0001*\b\u0012\u0004\u0012\u0002H\u00020\u0004*\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\n¢\u0006\u0002\u0010\b\u001a:\u0010\u0000\u001a\u0002H\u0001\"\b\b\u0000\u0010\u0002*\u00020\u0003\"\u000e\b\u0001\u0010\u0001*\b\u0012\u0004\u0012\u0002H\u00020\u0004*\u00020\u00052\f\u0010\t\u001a\b\u0012\u0004\u0012\u0002H\u00010\nH\n¢\u0006\u0002\u0010\u000b\u001a%\u0010\f\u001a\u00020\r\"\b\b\u0000\u0010\u0002*\u00020\u0003*\u00020\u00052\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0004H\n\u001a?\u0010\u000f\u001a\u0014\u0012\u000e\b\u0001\u0012\n \u0010*\u0004\u0018\u00010\u00030\u0003\u0018\u00010\u0004\"\b\b\u0000\u0010\u0002*\u00020\u0003*\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0004H\n¨\u0006\u0011"}, d2 = {"get", "T", "D", "Landroidx/navigation/NavDestination;", "Landroidx/navigation/Navigator;", "Landroidx/navigation/NavigatorProvider;", "name", "", "(Landroidx/navigation/NavigatorProvider;Ljava/lang/String;)Landroidx/navigation/Navigator;", "clazz", "Lkotlin/reflect/KClass;", "(Landroidx/navigation/NavigatorProvider;Lkotlin/reflect/KClass;)Landroidx/navigation/Navigator;", "plusAssign", "", "navigator", "set", "kotlin.jvm.PlatformType", "navigation-common-ktx_release"}, k = 2, mv = {1, 1, 10})
/* compiled from: NavigatorProvider.kt */
public final class NavigatorProviderKt {
    @NotNull
    public static final <D extends NavDestination, T extends Navigator<D>> T get(@NotNull NavigatorProvider $receiver, @NotNull String name) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(name, "name");
        T navigator = $receiver.getNavigator(name);
        Intrinsics.checkExpressionValueIsNotNull(navigator, "getNavigator(name)");
        return navigator;
    }

    @NotNull
    public static final <D extends NavDestination, T extends Navigator<D>> T get(@NotNull NavigatorProvider $receiver, @NotNull KClass<T> clazz) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(clazz, "clazz");
        T navigator = $receiver.getNavigator(JvmClassMappingKt.getJavaClass(clazz));
        Intrinsics.checkExpressionValueIsNotNull(navigator, "getNavigator(clazz.java)");
        return navigator;
    }

    @Nullable
    public static final <D extends NavDestination> Navigator<? extends NavDestination> set(@NotNull NavigatorProvider $receiver, @NotNull String name, @NotNull Navigator<D> navigator) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(name, "name");
        Intrinsics.checkParameterIsNotNull(navigator, "navigator");
        return $receiver.addNavigator(name, navigator);
    }

    public static final <D extends NavDestination> void plusAssign(@NotNull NavigatorProvider $receiver, @NotNull Navigator<D> navigator) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(navigator, "navigator");
        $receiver.addNavigator(navigator);
    }
}
