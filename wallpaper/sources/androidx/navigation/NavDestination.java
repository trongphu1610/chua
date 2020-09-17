package androidx.navigation;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.support.v4.util.SparseArrayCompat;
import android.util.AttributeSet;
import androidx.navigation.Navigator;
import androidx.navigation.common.R;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class NavDestination {
    private static final HashMap<String, Class> sClasses = new HashMap<>();
    private SparseArrayCompat<NavAction> mActions;
    private ArrayList<NavDeepLink> mDeepLinks;
    private Bundle mDefaultArgs;
    private int mId;
    private CharSequence mLabel;
    private final Navigator mNavigator;
    private NavGraph mParent;

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ClassType {
        Class value();
    }

    @NonNull
    protected static <C> Class<? extends C> parseClassFromName(@NonNull Context context, @NonNull String name, @NonNull Class<? extends C> expectedClassType) {
        if (name.charAt(0) == '.') {
            name = context.getPackageName() + name;
        }
        Class clazz = sClasses.get(name);
        if (clazz == null) {
            try {
                clazz = Class.forName(name, true, context.getClassLoader());
                sClasses.put(name, clazz);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(e);
            }
        }
        if (expectedClassType.isAssignableFrom(clazz)) {
            return clazz;
        }
        throw new IllegalArgumentException(name + " must be a subclass of " + expectedClassType);
    }

    @NonNull
    static String getDisplayName(@NonNull Context context, int id) {
        try {
            return context.getResources().getResourceName(id);
        } catch (Resources.NotFoundException e) {
            return Integer.toString(id);
        }
    }

    public NavDestination(@NonNull Navigator<? extends NavDestination> navigator) {
        this.mNavigator = navigator;
    }

    @CallSuper
    public void onInflate(@NonNull Context context, @NonNull AttributeSet attrs) {
        TypedArray a = context.getResources().obtainAttributes(attrs, R.styleable.Navigator);
        setId(a.getResourceId(R.styleable.Navigator_android_id, 0));
        setLabel(a.getText(R.styleable.Navigator_android_label));
        a.recycle();
    }

    /* access modifiers changed from: package-private */
    public void setParent(NavGraph parent) {
        this.mParent = parent;
    }

    @Nullable
    public NavGraph getParent() {
        return this.mParent;
    }

    @IdRes
    public int getId() {
        return this.mId;
    }

    public void setId(@IdRes int id) {
        this.mId = id;
    }

    public void setLabel(@Nullable CharSequence label) {
        this.mLabel = label;
    }

    @Nullable
    public CharSequence getLabel() {
        return this.mLabel;
    }

    @NonNull
    public Navigator getNavigator() {
        return this.mNavigator;
    }

    @NonNull
    public Bundle getDefaultArguments() {
        if (this.mDefaultArgs == null) {
            this.mDefaultArgs = new Bundle();
        }
        return this.mDefaultArgs;
    }

    public void setDefaultArguments(@Nullable Bundle args) {
        this.mDefaultArgs = args;
    }

    public void addDefaultArguments(@NonNull Bundle args) {
        getDefaultArguments().putAll(args);
    }

    public void addDeepLink(@NonNull String uriPattern) {
        if (this.mDeepLinks == null) {
            this.mDeepLinks = new ArrayList<>();
        }
        this.mDeepLinks.add(new NavDeepLink(uriPattern));
    }

    /* access modifiers changed from: package-private */
    @Nullable
    public Pair<NavDestination, Bundle> matchDeepLink(@NonNull Uri uri) {
        if (this.mDeepLinks == null) {
            return null;
        }
        Iterator<NavDeepLink> it = this.mDeepLinks.iterator();
        while (it.hasNext()) {
            Bundle matchingArguments = it.next().getMatchingArguments(uri);
            if (matchingArguments != null) {
                return Pair.create(this, matchingArguments);
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    @NonNull
    public int[] buildDeepLinkIds() {
        ArrayDeque<NavDestination> hierarchy = new ArrayDeque<>();
        NavDestination current = this;
        do {
            NavGraph parent = current.getParent();
            if (parent == null || parent.getStartDestination() != current.getId()) {
                hierarchy.addFirst(current);
            }
            current = parent;
        } while (current != null);
        int[] deepLinkIds = new int[hierarchy.size()];
        int index = 0;
        Iterator<NavDestination> it = hierarchy.iterator();
        while (it.hasNext()) {
            deepLinkIds[index] = it.next().getId();
            index++;
        }
        return deepLinkIds;
    }

    @Nullable
    public NavAction getAction(@IdRes int id) {
        NavAction destination = this.mActions == null ? null : this.mActions.get(id);
        if (destination != null) {
            return destination;
        }
        if (getParent() != null) {
            return getParent().getAction(id);
        }
        return null;
    }

    public void putAction(@IdRes int actionId, @IdRes int destId) {
        putAction(actionId, new NavAction(destId));
    }

    public void putAction(@IdRes int actionId, @NonNull NavAction action) {
        if (actionId == 0) {
            throw new IllegalArgumentException("Cannot have an action with actionId 0");
        }
        if (this.mActions == null) {
            this.mActions = new SparseArrayCompat<>();
        }
        this.mActions.put(actionId, action);
    }

    public void removeAction(@IdRes int actionId) {
        if (this.mActions != null) {
            this.mActions.delete(actionId);
        }
    }

    public void navigate(@Nullable Bundle args, @Nullable NavOptions navOptions, @Nullable Navigator.Extras navigatorExtras) {
        Bundle defaultArgs = getDefaultArguments();
        Bundle finalArgs = new Bundle();
        finalArgs.putAll(defaultArgs);
        if (args != null) {
            finalArgs.putAll(args);
        }
        this.mNavigator.navigate(this, finalArgs, navOptions, navigatorExtras);
    }
}
