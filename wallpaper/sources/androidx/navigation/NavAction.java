package androidx.navigation;

import android.support.annotation.IdRes;
import android.support.annotation.Nullable;

public class NavAction {
    @IdRes
    private final int mDestinationId;
    private NavOptions mNavOptions;

    public NavAction(@IdRes int destinationId) {
        this(destinationId, (NavOptions) null);
    }

    public NavAction(@IdRes int destinationId, @Nullable NavOptions navOptions) {
        this.mDestinationId = destinationId;
        this.mNavOptions = navOptions;
    }

    public int getDestinationId() {
        return this.mDestinationId;
    }

    public void setNavOptions(@Nullable NavOptions navOptions) {
        this.mNavOptions = navOptions;
    }

    @Nullable
    public NavOptions getNavOptions() {
        return this.mNavOptions;
    }
}
