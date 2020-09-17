package androidx.navigation;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NavigationRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.TaskStackBuilder;
import androidx.navigation.Navigator;
import java.util.ArrayDeque;
import java.util.Iterator;

public class NavDeepLinkBuilder {
    private final Context mContext;
    private int mDestId;
    private NavGraph mGraph;
    private final Intent mIntent;

    public NavDeepLinkBuilder(@NonNull Context context) {
        this.mContext = context;
        if (this.mContext instanceof Activity) {
            this.mIntent = new Intent(this.mContext, this.mContext.getClass());
        } else {
            Intent launchIntent = this.mContext.getPackageManager().getLaunchIntentForPackage(this.mContext.getPackageName());
            this.mIntent = launchIntent != null ? launchIntent : new Intent();
        }
        this.mIntent.addFlags(268468224);
    }

    NavDeepLinkBuilder(@NonNull NavController navController) {
        this(navController.getContext());
        this.mGraph = navController.getGraph();
    }

    @NonNull
    public NavDeepLinkBuilder setComponentName(@NonNull Class<? extends Activity> activityClass) {
        return setComponentName(new ComponentName(this.mContext, activityClass));
    }

    @NonNull
    public NavDeepLinkBuilder setComponentName(@NonNull ComponentName componentName) {
        this.mIntent.setComponent(componentName);
        return this;
    }

    @NonNull
    public NavDeepLinkBuilder setGraph(@NavigationRes int navGraphId) {
        return setGraph(new NavInflater(this.mContext, new PermissiveNavigatorProvider(this.mContext)).inflate(navGraphId));
    }

    @NonNull
    public NavDeepLinkBuilder setGraph(@NonNull NavGraph navGraph) {
        this.mGraph = navGraph;
        if (this.mDestId != 0) {
            fillInIntent();
        }
        return this;
    }

    @NonNull
    public NavDeepLinkBuilder setDestination(@IdRes int destId) {
        this.mDestId = destId;
        if (this.mGraph != null) {
            fillInIntent();
        }
        return this;
    }

    private void fillInIntent() {
        NavDestination node = null;
        ArrayDeque<NavDestination> possibleDestinations = new ArrayDeque<>();
        possibleDestinations.add(this.mGraph);
        while (!possibleDestinations.isEmpty() && node == null) {
            NavDestination destination = possibleDestinations.poll();
            if (destination.getId() == this.mDestId) {
                node = destination;
            } else if (destination instanceof NavGraph) {
                Iterator<NavDestination> it = ((NavGraph) destination).iterator();
                while (it.hasNext()) {
                    possibleDestinations.add(it.next());
                }
            }
        }
        if (node == null) {
            String dest = NavDestination.getDisplayName(this.mContext, this.mDestId);
            throw new IllegalArgumentException("navigation destination " + dest + " is unknown to this NavController");
        }
        this.mIntent.putExtra("android-support-nav:controller:deepLinkIds", node.buildDeepLinkIds());
    }

    @NonNull
    public NavDeepLinkBuilder setArguments(@Nullable Bundle args) {
        this.mIntent.putExtra("android-support-nav:controller:deepLinkExtras", args);
        return this;
    }

    @NonNull
    public TaskStackBuilder createTaskStackBuilder() {
        if (this.mIntent.getIntArrayExtra("android-support-nav:controller:deepLinkIds") != null) {
            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this.mContext).addNextIntentWithParentStack(new Intent(this.mIntent));
            for (int index = 0; index < taskStackBuilder.getIntentCount(); index++) {
                taskStackBuilder.editIntentAt(index).putExtra(NavController.KEY_DEEP_LINK_INTENT, this.mIntent);
            }
            return taskStackBuilder;
        } else if (this.mGraph == null) {
            throw new IllegalStateException("You must call setGraph() before constructing the deep link");
        } else {
            throw new IllegalStateException("You must call setDestination() before constructing the deep link");
        }
    }

    @NonNull
    public PendingIntent createPendingIntent() {
        return createTaskStackBuilder().getPendingIntent(this.mDestId, 134217728);
    }

    private static class PermissiveNavigatorProvider extends SimpleNavigatorProvider {
        private final Navigator<NavDestination> mDestNavigator = new Navigator<NavDestination>() {
            @NonNull
            public NavDestination createDestination() {
                return new NavDestination(this);
            }

            public void navigate(@NonNull NavDestination destination, @Nullable Bundle args, @Nullable NavOptions navOptions, @Nullable Navigator.Extras navigatorExtras) {
                throw new IllegalStateException("navigate is not supported");
            }

            public boolean popBackStack() {
                throw new IllegalStateException("popBackStack is not supported");
            }
        };

        PermissiveNavigatorProvider(Context context) {
            addNavigator(new NavGraphNavigator(context));
        }

        @NonNull
        public Navigator<? extends NavDestination> getNavigator(@NonNull String name) {
            try {
                return super.getNavigator(name);
            } catch (IllegalStateException e) {
                return this.mDestNavigator;
            }
        }
    }
}
