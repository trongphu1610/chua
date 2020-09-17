package androidx.navigation;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.support.v4.util.SparseArrayCompat;
import android.util.AttributeSet;
import androidx.navigation.common.R;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class NavGraph extends NavDestination implements Iterable<NavDestination> {
    final SparseArrayCompat<NavDestination> mNodes;
    private int mStartDestId;

    public NavGraph(@NonNull NavigatorProvider navigatorProvider) {
        this((Navigator<? extends NavGraph>) navigatorProvider.getNavigator(NavGraphNavigator.class));
    }

    public NavGraph(@NonNull Navigator<? extends NavGraph> navGraphNavigator) {
        super(navGraphNavigator);
        this.mNodes = new SparseArrayCompat<>();
    }

    public void onInflate(@NonNull Context context, @NonNull AttributeSet attrs) {
        super.onInflate(context, attrs);
        TypedArray a = context.getResources().obtainAttributes(attrs, R.styleable.NavGraphNavigator);
        setStartDestination(a.getResourceId(R.styleable.NavGraphNavigator_startDestination, 0));
        a.recycle();
    }

    /* access modifiers changed from: package-private */
    @Nullable
    public Pair<NavDestination, Bundle> matchDeepLink(@NonNull Uri uri) {
        Pair<NavDestination, Bundle> result = super.matchDeepLink(uri);
        if (result != null) {
            return result;
        }
        Iterator<NavDestination> it = iterator();
        while (it.hasNext()) {
            Pair<NavDestination, Bundle> childResult = it.next().matchDeepLink(uri);
            if (childResult != null) {
                return childResult;
            }
        }
        return null;
    }

    public void addDestination(@NonNull NavDestination node) {
        if (node.getId() == 0) {
            throw new IllegalArgumentException("Destinations must have an id. Call setId() or include an android:id in your navigation XML.");
        }
        NavDestination existingDestination = this.mNodes.get(node.getId());
        if (existingDestination != node) {
            if (node.getParent() != null) {
                throw new IllegalStateException("Destination already has a parent set. Call NavGraph.remove() to remove the previous parent.");
            }
            if (existingDestination != null) {
                existingDestination.setParent((NavGraph) null);
            }
            node.setParent(this);
            this.mNodes.put(node.getId(), node);
        }
    }

    public void addDestinations(@NonNull Collection<NavDestination> nodes) {
        for (NavDestination node : nodes) {
            if (node != null) {
                addDestination(node);
            }
        }
    }

    public void addDestinations(@NonNull NavDestination... nodes) {
        for (NavDestination node : nodes) {
            if (node != null) {
                addDestination(node);
            }
        }
    }

    @Nullable
    public NavDestination findNode(@IdRes int resid) {
        return findNode(resid, true);
    }

    /* access modifiers changed from: package-private */
    public NavDestination findNode(@IdRes int resid, boolean searchParents) {
        NavDestination destination = this.mNodes.get(resid);
        if (destination != null) {
            return destination;
        }
        if (!searchParents || getParent() == null) {
            return null;
        }
        return getParent().findNode(resid);
    }

    @NonNull
    public Iterator<NavDestination> iterator() {
        return new Iterator<NavDestination>() {
            private int mIndex = -1;
            private boolean mWentToNext = false;

            public boolean hasNext() {
                return this.mIndex + 1 < NavGraph.this.mNodes.size();
            }

            public NavDestination next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                this.mWentToNext = true;
                SparseArrayCompat<NavDestination> sparseArrayCompat = NavGraph.this.mNodes;
                int i = this.mIndex + 1;
                this.mIndex = i;
                return sparseArrayCompat.valueAt(i);
            }

            public void remove() {
                if (!this.mWentToNext) {
                    throw new IllegalStateException("You must call next() before you can remove an element");
                }
                NavGraph.this.mNodes.valueAt(this.mIndex).setParent((NavGraph) null);
                NavGraph.this.mNodes.removeAt(this.mIndex);
                this.mIndex--;
                this.mWentToNext = false;
            }
        };
    }

    public void addAll(@NonNull NavGraph other) {
        Iterator<NavDestination> iterator = other.iterator();
        while (iterator.hasNext()) {
            iterator.remove();
            addDestination(iterator.next());
        }
    }

    public void remove(@NonNull NavDestination node) {
        int index = this.mNodes.indexOfKey(node.getId());
        if (index >= 0) {
            this.mNodes.valueAt(index).setParent((NavGraph) null);
            this.mNodes.removeAt(index);
        }
    }

    public void clear() {
        Iterator<NavDestination> iterator = iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
    }

    @IdRes
    public int getStartDestination() {
        return this.mStartDestId;
    }

    public void setStartDestination(@IdRes int startDestId) {
        this.mStartDestId = startDestId;
    }
}
