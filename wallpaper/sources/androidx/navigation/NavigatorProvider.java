package androidx.navigation;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

@SuppressLint({"TypeParameterUnusedInFormals"})
public interface NavigatorProvider {
    @Nullable
    Navigator<? extends NavDestination> addNavigator(@NonNull Navigator<? extends NavDestination> navigator);

    @Nullable
    Navigator<? extends NavDestination> addNavigator(@NonNull String str, @NonNull Navigator<? extends NavDestination> navigator);

    @NonNull
    <D extends NavDestination, T extends Navigator<? extends D>> T getNavigator(@NonNull Class<T> cls);

    @NonNull
    <D extends NavDestination, T extends Navigator<? extends D>> T getNavigator(@NonNull String str);
}
