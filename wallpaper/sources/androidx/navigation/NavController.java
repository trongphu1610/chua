package androidx.navigation;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NavigationRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.util.Pair;
import android.util.Log;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigator;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class NavController {
    private static final String KEY_BACK_STACK_IDS = "android-support-nav:controller:backStackIds";
    static final String KEY_DEEP_LINK_EXTRAS = "android-support-nav:controller:deepLinkExtras";
    static final String KEY_DEEP_LINK_IDS = "android-support-nav:controller:deepLinkIds";
    @NonNull
    public static final String KEY_DEEP_LINK_INTENT = "android-support-nav:controller:deepLinkIntent";
    private static final String KEY_GRAPH_ID = "android-support-nav:controller:graphId";
    private static final String KEY_NAVIGATOR_STATE = "android-support-nav:controller:navigatorState";
    private static final String KEY_NAVIGATOR_STATE_NAMES = "android-support-nav:controller:navigatorState:names";
    private static final String TAG = "NavController";
    private Activity mActivity;
    final Deque<NavDestination> mBackStack = new ArrayDeque();
    private int[] mBackStackToRestore;
    final Context mContext;
    private NavGraph mGraph;
    private int mGraphId;
    private NavInflater mInflater;
    private final SimpleNavigatorProvider mNavigatorProvider = new SimpleNavigatorProvider() {
        @Nullable
        public Navigator<? extends NavDestination> addNavigator(@NonNull String name, @NonNull Navigator<? extends NavDestination> navigator) {
            Navigator<? extends NavDestination> previousNavigator = super.addNavigator(name, navigator);
            if (previousNavigator != navigator) {
                if (previousNavigator != null) {
                    previousNavigator.removeOnNavigatorNavigatedListener(NavController.this.mOnNavigatedListener);
                }
                navigator.addOnNavigatorNavigatedListener(NavController.this.mOnNavigatedListener);
            }
            return previousNavigator;
        }
    };
    private Bundle mNavigatorStateToRestore;
    final Navigator.OnNavigatorNavigatedListener mOnNavigatedListener = new Navigator.OnNavigatorNavigatedListener() {
        public void onNavigatorNavigated(@NonNull Navigator navigator, @IdRes int destId, int backStackEffect) {
            switch (backStackEffect) {
                case 1:
                    NavDestination newDest = NavController.this.findDestination(destId);
                    if (newDest == null) {
                        throw new IllegalArgumentException("Navigator " + navigator + " reported navigation to unknown destination id " + NavDestination.getDisplayName(NavController.this.mContext, destId));
                    }
                    NavController.this.mBackStack.add(newDest);
                    NavController.this.dispatchOnNavigated(newDest);
                    return;
                case 2:
                    NavDestination lastFromNavigator = null;
                    Iterator<NavDestination> iterator = NavController.this.mBackStack.descendingIterator();
                    while (true) {
                        if (iterator.hasNext()) {
                            NavDestination destination = iterator.next();
                            if (destination.getNavigator() == navigator) {
                                lastFromNavigator = destination;
                            }
                        }
                    }
                    if (lastFromNavigator == null) {
                        throw new IllegalArgumentException("Navigator " + navigator + " reported pop but did not have any destinations" + " on the NavController back stack");
                    }
                    NavController.this.popBackStack(lastFromNavigator.getId(), false);
                    if (!NavController.this.mBackStack.isEmpty()) {
                        NavController.this.mBackStack.removeLast();
                    }
                    while (!NavController.this.mBackStack.isEmpty() && (NavController.this.mBackStack.peekLast() instanceof NavGraph)) {
                        NavController.this.popBackStack();
                    }
                    if (!NavController.this.mBackStack.isEmpty()) {
                        NavController.this.dispatchOnNavigated(NavController.this.mBackStack.peekLast());
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    };
    private final CopyOnWriteArrayList<OnNavigatedListener> mOnNavigatedListeners = new CopyOnWriteArrayList<>();

    public interface OnNavigatedListener {
        void onNavigated(@NonNull NavController navController, @NonNull NavDestination navDestination);
    }

    public NavController(@NonNull Context context) {
        this.mContext = context;
        while (true) {
            if (!(context instanceof ContextWrapper)) {
                break;
            } else if (context instanceof Activity) {
                this.mActivity = (Activity) context;
                break;
            } else {
                context = ((ContextWrapper) context).getBaseContext();
            }
        }
        this.mNavigatorProvider.addNavigator(new NavGraphNavigator(this.mContext));
        this.mNavigatorProvider.addNavigator(new ActivityNavigator(this.mContext));
    }

    /* access modifiers changed from: package-private */
    @NonNull
    public Context getContext() {
        return this.mContext;
    }

    @NonNull
    public NavigatorProvider getNavigatorProvider() {
        return this.mNavigatorProvider;
    }

    public void addOnNavigatedListener(@NonNull OnNavigatedListener listener) {
        if (!this.mBackStack.isEmpty()) {
            listener.onNavigated(this, this.mBackStack.peekLast());
        }
        this.mOnNavigatedListeners.add(listener);
    }

    public void removeOnNavigatedListener(@NonNull OnNavigatedListener listener) {
        this.mOnNavigatedListeners.remove(listener);
    }

    public boolean popBackStack() {
        if (this.mBackStack.isEmpty()) {
            return false;
        }
        return popBackStack(getCurrentDestination().getId(), true);
    }

    public boolean popBackStack(@IdRes int destinationId, boolean inclusive) {
        NavDestination destination;
        if (this.mBackStack.isEmpty()) {
            return false;
        }
        ArrayList arrayList = new ArrayList();
        Iterator<NavDestination> iterator = this.mBackStack.descendingIterator();
        boolean foundDestination = false;
        while (true) {
            if (!iterator.hasNext()) {
                break;
            }
            NavDestination destination2 = iterator.next();
            if (inclusive || destination2.getId() != destinationId) {
                arrayList.add(destination2);
            }
            if (destination2.getId() == destinationId) {
                foundDestination = true;
                break;
            }
        }
        if (!foundDestination) {
            String destinationName = NavDestination.getDisplayName(this.mContext, destinationId);
            Log.i(TAG, "Ignoring popBackStack to destination " + destinationName + " as it was not found on the current back stack");
            return false;
        }
        boolean popped = false;
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            Object next = it.next();
            while (true) {
                destination = (NavDestination) next;
                if (!this.mBackStack.isEmpty() && this.mBackStack.peekLast().getId() != destination.getId()) {
                    if (!it.hasNext()) {
                        destination = null;
                        break;
                    }
                    next = it.next();
                } else {
                    break;
                }
            }
            if (destination != null) {
                popped = destination.getNavigator().popBackStack() || popped;
            }
        }
        return popped;
    }

    public boolean navigateUp() {
        if (this.mBackStack.size() != 1) {
            return popBackStack();
        }
        NavDestination currentDestination = getCurrentDestination();
        int destId = currentDestination.getId();
        for (NavGraph parent = currentDestination.getParent(); parent != null; parent = parent.getParent()) {
            if (parent.getStartDestination() != destId) {
                new NavDeepLinkBuilder(this).setDestination(parent.getId()).createTaskStackBuilder().startActivities();
                if (this.mActivity != null) {
                    this.mActivity.finish();
                }
                return true;
            }
            destId = parent.getId();
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public void dispatchOnNavigated(NavDestination destination) {
        Iterator<OnNavigatedListener> it = this.mOnNavigatedListeners.iterator();
        while (it.hasNext()) {
            it.next().onNavigated(this, destination);
        }
    }

    public void setMetadataGraph() {
        NavGraph metadataGraph = getNavInflater().inflateMetadataGraph();
        if (metadataGraph != null) {
            setGraph(metadataGraph);
        }
    }

    @NonNull
    public NavInflater getNavInflater() {
        if (this.mInflater == null) {
            this.mInflater = new NavInflater(this.mContext, this.mNavigatorProvider);
        }
        return this.mInflater;
    }

    public void setGraph(@NavigationRes int graphResId) {
        this.mGraph = getNavInflater().inflate(graphResId);
        this.mGraphId = graphResId;
        onGraphCreated();
    }

    public void setGraph(@NonNull NavGraph graph) {
        this.mGraph = graph;
        this.mGraphId = 0;
        onGraphCreated();
    }

    private void onGraphCreated() {
        ArrayList<String> navigatorNames;
        if (!(this.mNavigatorStateToRestore == null || (navigatorNames = this.mNavigatorStateToRestore.getStringArrayList(KEY_NAVIGATOR_STATE_NAMES)) == null)) {
            Iterator<String> it = navigatorNames.iterator();
            while (it.hasNext()) {
                String name = it.next();
                Navigator navigator = this.mNavigatorProvider.getNavigator(name);
                Bundle bundle = this.mNavigatorStateToRestore.getBundle(name);
                if (bundle != null) {
                    navigator.onRestoreState(bundle);
                }
            }
        }
        boolean deepLinked = false;
        if (this.mBackStackToRestore != null) {
            for (int destinationId : this.mBackStackToRestore) {
                NavDestination node = findDestination(destinationId);
                if (node == null) {
                    throw new IllegalStateException("unknown destination during restore: " + this.mContext.getResources().getResourceName(destinationId));
                }
                this.mBackStack.add(node);
            }
            this.mBackStackToRestore = null;
        }
        if (this.mGraph != null && this.mBackStack.isEmpty()) {
            if (this.mActivity != null && onHandleDeepLink(this.mActivity.getIntent())) {
                deepLinked = true;
            }
            if (!deepLinked) {
                this.mGraph.navigate((Bundle) null, (NavOptions) null, (Navigator.Extras) null);
            }
        }
    }

    public boolean onHandleDeepLink(@Nullable Intent intent) {
        Pair<NavDestination, Bundle> matchingDeepLink;
        if (intent == null) {
            return false;
        }
        Bundle extras = intent.getExtras();
        int[] deepLink = extras != null ? extras.getIntArray(KEY_DEEP_LINK_IDS) : null;
        Bundle bundle = new Bundle();
        Bundle deepLinkExtras = extras != null ? extras.getBundle(KEY_DEEP_LINK_EXTRAS) : null;
        if (deepLinkExtras != null) {
            bundle.putAll(deepLinkExtras);
        }
        if (!((deepLink != null && deepLink.length != 0) || intent.getData() == null || (matchingDeepLink = this.mGraph.matchDeepLink(intent.getData())) == null)) {
            deepLink = ((NavDestination) matchingDeepLink.first).buildDeepLinkIds();
            bundle.putAll((Bundle) matchingDeepLink.second);
        }
        if (deepLink == null || deepLink.length == 0) {
            return false;
        }
        bundle.putParcelable(KEY_DEEP_LINK_INTENT, intent);
        int flags = intent.getFlags();
        if ((flags & 268435456) != 0 && (flags & 32768) == 0) {
            intent.addFlags(32768);
            TaskStackBuilder.create(this.mContext).addNextIntentWithParentStack(intent).startActivities();
            if (this.mActivity != null) {
                this.mActivity.finish();
            }
            return true;
        } else if ((268435456 & flags) != 0) {
            if (!this.mBackStack.isEmpty()) {
                navigate(this.mGraph.getStartDestination(), bundle, new NavOptions.Builder().setPopUpTo(this.mGraph.getId(), true).setEnterAnim(0).setExitAnim(0).build());
            }
            int index = 0;
            while (index < deepLink.length) {
                int index2 = index + 1;
                int destinationId = deepLink[index];
                NavDestination node = findDestination(destinationId);
                if (node == null) {
                    throw new IllegalStateException("unknown destination during deep link: " + NavDestination.getDisplayName(this.mContext, destinationId));
                }
                node.navigate(bundle, new NavOptions.Builder().setEnterAnim(0).setExitAnim(0).build(), (Navigator.Extras) null);
                index = index2;
            }
            return true;
        } else {
            NavGraph graph = this.mGraph;
            int i = 0;
            while (i < deepLink.length) {
                int destinationId2 = deepLink[i];
                NavDestination node2 = i == 0 ? this.mGraph : graph.findNode(destinationId2);
                if (node2 == null) {
                    throw new IllegalStateException("unknown destination during deep link: " + NavDestination.getDisplayName(this.mContext, destinationId2));
                }
                if (i != deepLink.length - 1) {
                    graph = (NavGraph) node2;
                } else {
                    node2.navigate(bundle, new NavOptions.Builder().setPopUpTo(this.mGraph.getId(), true).setEnterAnim(0).setExitAnim(0).build(), (Navigator.Extras) null);
                }
                i++;
            }
            return true;
        }
    }

    @NonNull
    public NavGraph getGraph() {
        if (this.mGraph != null) {
            return this.mGraph;
        }
        throw new IllegalStateException("You must call setGraph() before calling getGraph()");
    }

    @Nullable
    public NavDestination getCurrentDestination() {
        return this.mBackStack.peekLast();
    }

    /* access modifiers changed from: package-private */
    public NavDestination findDestination(@IdRes int destinationId) {
        NavGraph currentGraph;
        if (this.mGraph == null) {
            return null;
        }
        if (this.mGraph.getId() == destinationId) {
            return this.mGraph;
        }
        NavDestination currentNode = this.mBackStack.isEmpty() ? this.mGraph : this.mBackStack.peekLast();
        if (currentNode instanceof NavGraph) {
            currentGraph = (NavGraph) currentNode;
        } else {
            currentGraph = currentNode.getParent();
        }
        return currentGraph.findNode(destinationId);
    }

    public final void navigate(@IdRes int resId) {
        navigate(resId, (Bundle) null);
    }

    public final void navigate(@IdRes int resId, @Nullable Bundle args) {
        navigate(resId, args, (NavOptions) null);
    }

    public void navigate(@IdRes int resId, @Nullable Bundle args, @Nullable NavOptions navOptions) {
        navigate(resId, args, navOptions, (Navigator.Extras) null);
    }

    public void navigate(@IdRes int resId, @Nullable Bundle args, @Nullable NavOptions navOptions, @Nullable Navigator.Extras navigatorExtras) {
        String str;
        NavDestination currentNode = this.mBackStack.isEmpty() ? this.mGraph : this.mBackStack.peekLast();
        if (currentNode == null) {
            throw new IllegalStateException("no current navigation node");
        }
        int destId = resId;
        NavAction navAction = currentNode.getAction(resId);
        if (navAction != null) {
            if (navOptions == null) {
                navOptions = navAction.getNavOptions();
            }
            destId = navAction.getDestinationId();
        }
        if (destId == 0 && navOptions != null && navOptions.getPopUpTo() != 0) {
            popBackStack(navOptions.getPopUpTo(), navOptions.isPopUpToInclusive());
        } else if (destId == 0) {
            throw new IllegalArgumentException("Destination id == 0 can only be used in conjunction with navOptions.popUpTo != 0");
        } else {
            NavDestination node = findDestination(destId);
            if (node == null) {
                String dest = NavDestination.getDisplayName(this.mContext, destId);
                StringBuilder sb = new StringBuilder();
                sb.append("navigation destination ");
                sb.append(dest);
                if (navAction != null) {
                    str = " referenced from action " + NavDestination.getDisplayName(this.mContext, resId);
                } else {
                    str = "";
                }
                sb.append(str);
                sb.append(" is unknown to this NavController");
                throw new IllegalArgumentException(sb.toString());
            }
            if (navOptions != null) {
                if (navOptions.shouldClearTask()) {
                    popBackStack(this.mGraph.getId(), true);
                } else if (navOptions.getPopUpTo() != 0) {
                    popBackStack(navOptions.getPopUpTo(), navOptions.isPopUpToInclusive());
                }
            }
            node.navigate(args, navOptions, navigatorExtras);
        }
    }

    public void navigate(@NonNull NavDirections directions) {
        navigate(directions.getActionId(), directions.getArguments());
    }

    public void navigate(@NonNull NavDirections directions, @Nullable NavOptions navOptions) {
        navigate(directions.getActionId(), directions.getArguments(), navOptions);
    }

    public void navigate(@NonNull NavDirections directions, @NonNull Navigator.Extras navigatorExtras) {
        navigate(directions.getActionId(), directions.getArguments(), (NavOptions) null, navigatorExtras);
    }

    @NonNull
    public NavDeepLinkBuilder createDeepLink() {
        return new NavDeepLinkBuilder(this);
    }

    @Nullable
    public Bundle saveState() {
        Bundle b = null;
        if (this.mGraphId != 0) {
            b = new Bundle();
            b.putInt(KEY_GRAPH_ID, this.mGraphId);
        }
        ArrayList<String> navigatorNames = new ArrayList<>();
        Bundle navigatorState = new Bundle();
        for (Map.Entry<String, Navigator<? extends NavDestination>> entry : this.mNavigatorProvider.getNavigators().entrySet()) {
            String name = entry.getKey();
            Bundle savedState = entry.getValue().onSaveState();
            if (savedState != null) {
                navigatorNames.add(name);
                navigatorState.putBundle(name, savedState);
            }
        }
        if (!navigatorNames.isEmpty()) {
            if (b == null) {
                b = new Bundle();
            }
            navigatorState.putStringArrayList(KEY_NAVIGATOR_STATE_NAMES, navigatorNames);
            b.putBundle(KEY_NAVIGATOR_STATE, navigatorState);
        }
        if (!this.mBackStack.isEmpty()) {
            if (b == null) {
                b = new Bundle();
            }
            int[] backStack = new int[this.mBackStack.size()];
            int index = 0;
            for (NavDestination destination : this.mBackStack) {
                backStack[index] = destination.getId();
                index++;
            }
            b.putIntArray(KEY_BACK_STACK_IDS, backStack);
        }
        return b;
    }

    public void restoreState(@Nullable Bundle navState) {
        if (navState != null) {
            this.mGraphId = navState.getInt(KEY_GRAPH_ID);
            this.mNavigatorStateToRestore = navState.getBundle(KEY_NAVIGATOR_STATE);
            this.mBackStackToRestore = navState.getIntArray(KEY_BACK_STACK_IDS);
            if (this.mGraphId != 0) {
                setGraph(this.mGraphId);
            }
        }
    }
}
