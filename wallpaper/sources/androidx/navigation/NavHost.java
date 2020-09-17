package androidx.navigation;

import android.support.annotation.NonNull;

public interface NavHost {
    @NonNull
    NavController getNavController();
}
