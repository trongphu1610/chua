package androidx.navigation;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import androidx.navigation.Navigator;
import java.util.ArrayDeque;
import java.util.Iterator;

@Navigator.Name("navigation")
public class NavGraphNavigator extends Navigator<NavGraph> {
    private static final String KEY_BACK_STACK_IDS = "androidx-nav-graph:navigator:backStackIds";
    private ArrayDeque<Integer> mBackStack = new ArrayDeque<>();
    private Context mContext;

    public NavGraphNavigator(@NonNull Context context) {
        this.mContext = context;
    }

    @NonNull
    public NavGraph createDestination() {
        return new NavGraph((Navigator<? extends NavGraph>) this);
    }

    public void navigate(@NonNull NavGraph destination, @Nullable Bundle args, @Nullable NavOptions navOptions, @Nullable Navigator.Extras navigatorExtras) {
        int startId = destination.getStartDestination();
        if (startId == 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("no start destination defined via app:startDestination for ");
            sb.append(destination.getId() != 0 ? NavDestination.getDisplayName(this.mContext, destination.getId()) : "the root navigation");
            throw new IllegalStateException(sb.toString());
        }
        NavDestination startDestination = destination.findNode(startId, false);
        if (startDestination == null) {
            String dest = NavDestination.getDisplayName(this.mContext, startId);
            throw new IllegalArgumentException("navigation destination " + dest + " is not a direct child of this NavGraph");
        }
        if (navOptions == null || !navOptions.shouldLaunchSingleTop() || !isAlreadyTop(destination)) {
            this.mBackStack.add(Integer.valueOf(destination.getId()));
            dispatchOnNavigatorNavigated(destination.getId(), 1);
        } else {
            dispatchOnNavigatorNavigated(destination.getId(), 0);
        }
        startDestination.navigate(args, navOptions, navigatorExtras);
    }

    /* JADX WARNING: type inference failed for: r3v2, types: [androidx.navigation.NavDestination] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean isAlreadyTop(androidx.navigation.NavGraph r6) {
        /*
            r5 = this;
            java.util.ArrayDeque<java.lang.Integer> r0 = r5.mBackStack
            boolean r0 = r0.isEmpty()
            r1 = 0
            if (r0 == 0) goto L_0x000a
            return r1
        L_0x000a:
            java.util.ArrayDeque<java.lang.Integer> r0 = r5.mBackStack
            java.lang.Object r0 = r0.peekLast()
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            r2 = r6
        L_0x0017:
            int r3 = r2.getId()
            if (r3 == r0) goto L_0x002e
            int r3 = r2.getStartDestination()
            androidx.navigation.NavDestination r3 = r2.findNode(r3)
            boolean r4 = r3 instanceof androidx.navigation.NavGraph
            if (r4 == 0) goto L_0x002d
            r2 = r3
            androidx.navigation.NavGraph r2 = (androidx.navigation.NavGraph) r2
            goto L_0x0017
        L_0x002d:
            return r1
        L_0x002e:
            r1 = 1
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.navigation.NavGraphNavigator.isAlreadyTop(androidx.navigation.NavGraph):boolean");
    }

    public boolean popBackStack() {
        int destId = 0;
        if (this.mBackStack.isEmpty()) {
            return false;
        }
        this.mBackStack.removeLast();
        if (!this.mBackStack.isEmpty()) {
            destId = this.mBackStack.peekLast().intValue();
        }
        dispatchOnNavigatorNavigated(destId, 2);
        return true;
    }

    @Nullable
    public Bundle onSaveState() {
        Bundle b = new Bundle();
        int[] backStack = new int[this.mBackStack.size()];
        int index = 0;
        Iterator<Integer> it = this.mBackStack.iterator();
        while (it.hasNext()) {
            backStack[index] = it.next().intValue();
            index++;
        }
        b.putIntArray(KEY_BACK_STACK_IDS, backStack);
        return b;
    }

    public void onRestoreState(@Nullable Bundle savedState) {
        int[] backStack;
        if (savedState != null && (backStack = savedState.getIntArray(KEY_BACK_STACK_IDS)) != null) {
            this.mBackStack.clear();
            for (int destId : backStack) {
                this.mBackStack.add(Integer.valueOf(destId));
            }
        }
    }
}
