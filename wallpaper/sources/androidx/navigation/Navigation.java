package androidx.navigation;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import java.lang.ref.WeakReference;

public class Navigation {
    private Navigation() {
    }

    @NonNull
    public static NavController findNavController(@NonNull Activity activity, @IdRes int viewId) {
        NavController navController = findViewNavController(ActivityCompat.requireViewById(activity, viewId));
        if (navController != null) {
            return navController;
        }
        throw new IllegalStateException("Activity " + activity + " does not have a NavController set on " + viewId);
    }

    @NonNull
    public static NavController findNavController(@NonNull View view) {
        NavController navController = findViewNavController(view);
        if (navController != null) {
            return navController;
        }
        throw new IllegalStateException("View " + view + " does not have a NavController set");
    }

    @NonNull
    public static View.OnClickListener createNavigateOnClickListener(@IdRes int resId) {
        return createNavigateOnClickListener(resId, (Bundle) null);
    }

    @NonNull
    public static View.OnClickListener createNavigateOnClickListener(@IdRes final int resId, @Nullable final Bundle args) {
        return new View.OnClickListener() {
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(resId, args);
            }
        };
    }

    public static void setViewNavController(@NonNull View view, @Nullable NavController controller) {
        view.setTag(R.id.nav_controller_view_tag, controller);
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.view.ViewParent] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    @android.support.annotation.Nullable
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static androidx.navigation.NavController findViewNavController(@android.support.annotation.NonNull android.view.View r4) {
        /*
        L_0x0000:
            r0 = 0
            if (r4 == 0) goto L_0x0018
            androidx.navigation.NavController r1 = getViewNavController(r4)
            if (r1 == 0) goto L_0x000a
            return r1
        L_0x000a:
            android.view.ViewParent r2 = r4.getParent()
            boolean r3 = r2 instanceof android.view.View
            if (r3 == 0) goto L_0x0016
            r0 = r2
            android.view.View r0 = (android.view.View) r0
        L_0x0016:
            r4 = r0
            goto L_0x0000
        L_0x0018:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.navigation.Navigation.findViewNavController(android.view.View):androidx.navigation.NavController");
    }

    @Nullable
    private static NavController getViewNavController(@NonNull View view) {
        Object tag = view.getTag(R.id.nav_controller_view_tag);
        if (tag instanceof WeakReference) {
            return (NavController) ((WeakReference) tag).get();
        }
        if (tag instanceof NavController) {
            return (NavController) tag;
        }
        return null;
    }
}
