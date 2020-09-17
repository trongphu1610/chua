package androidx.navigation;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import androidx.navigation.NavDestination;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class Navigator<D extends NavDestination> {
    public static final int BACK_STACK_DESTINATION_ADDED = 1;
    public static final int BACK_STACK_DESTINATION_POPPED = 2;
    public static final int BACK_STACK_UNCHANGED = 0;
    private final CopyOnWriteArrayList<OnNavigatorNavigatedListener> mOnNavigatedListeners = new CopyOnWriteArrayList<>();

    public interface Extras {
    }

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Name {
        String value();
    }

    public interface OnNavigatorNavigatedListener {
        void onNavigatorNavigated(@NonNull Navigator navigator, @IdRes int i, int i2);
    }

    @NonNull
    public abstract D createDestination();

    public abstract void navigate(@NonNull D d, @Nullable Bundle bundle, @Nullable NavOptions navOptions, @Nullable Extras extras);

    public abstract boolean popBackStack();

    @Nullable
    public Bundle onSaveState() {
        return null;
    }

    public void onRestoreState(@NonNull Bundle savedState) {
    }

    public void onActive() {
    }

    public void onInactive() {
    }

    public final void addOnNavigatorNavigatedListener(@NonNull OnNavigatorNavigatedListener listener) {
        if (this.mOnNavigatedListeners.add(listener) && this.mOnNavigatedListeners.size() == 1) {
            onActive();
        }
    }

    public final void removeOnNavigatorNavigatedListener(@NonNull OnNavigatorNavigatedListener listener) {
        if (this.mOnNavigatedListeners.remove(listener) && this.mOnNavigatedListeners.isEmpty()) {
            onInactive();
        }
    }

    public final void dispatchOnNavigatorNavigated(@IdRes int destId, int backStackEffect) {
        Iterator<OnNavigatorNavigatedListener> it = this.mOnNavigatedListeners.iterator();
        while (it.hasNext()) {
            it.next().onNavigatorNavigated(this, destId, backStackEffect);
        }
    }
}
