package androidx.navigation;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;

public interface NavDirections {
    @IdRes
    int getActionId();

    @Nullable
    Bundle getArguments();
}
