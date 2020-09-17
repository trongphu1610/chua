package androidx.navigation;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import androidx.navigation.Navigator;
import java.util.HashMap;
import java.util.Map;

@SuppressLint({"TypeParameterUnusedInFormals"})
@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class SimpleNavigatorProvider implements NavigatorProvider {
    private static final HashMap<Class, String> sAnnotationNames = new HashMap<>();
    private final HashMap<String, Navigator<? extends NavDestination>> mNavigators = new HashMap<>();

    @NonNull
    private String getNameForNavigator(@NonNull Class<? extends Navigator> navigatorClass) {
        String name = sAnnotationNames.get(navigatorClass);
        if (name == null) {
            Navigator.Name annotation = (Navigator.Name) navigatorClass.getAnnotation(Navigator.Name.class);
            name = annotation != null ? annotation.value() : null;
            if (!validateName(name)) {
                throw new IllegalArgumentException("No @Navigator.Name annotation found for " + navigatorClass.getSimpleName());
            }
            sAnnotationNames.put(navigatorClass, name);
        }
        return name;
    }

    @NonNull
    public <D extends NavDestination, T extends Navigator<? extends D>> T getNavigator(@NonNull Class<T> navigatorClass) {
        return getNavigator(getNameForNavigator(navigatorClass));
    }

    @NonNull
    public <D extends NavDestination, T extends Navigator<? extends D>> T getNavigator(@NonNull String name) {
        if (!validateName(name)) {
            throw new IllegalArgumentException("navigator name cannot be an empty string");
        }
        Navigator<? extends NavDestination> navigator = this.mNavigators.get(name);
        if (navigator != null) {
            return navigator;
        }
        throw new IllegalStateException("Could not find Navigator with name \"" + name + "\". You must call NavController.addNavigator() for each navigation type.");
    }

    @Nullable
    public Navigator<? extends NavDestination> addNavigator(@NonNull Navigator<? extends NavDestination> navigator) {
        return addNavigator(getNameForNavigator(navigator.getClass()), navigator);
    }

    @Nullable
    public Navigator<? extends NavDestination> addNavigator(@NonNull String name, @NonNull Navigator<? extends NavDestination> navigator) {
        if (validateName(name)) {
            return this.mNavigators.put(name, navigator);
        }
        throw new IllegalArgumentException("navigator name cannot be an empty string");
    }

    /* access modifiers changed from: package-private */
    public Map<String, Navigator<? extends NavDestination>> getNavigators() {
        return this.mNavigators;
    }

    private boolean validateName(String name) {
        return name != null && !name.isEmpty();
    }
}
