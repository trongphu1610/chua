package androidx.navigation.fragment;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import androidx.navigation.NavDestination;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigator;
import androidx.navigation.NavigatorProvider;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

@Navigator.Name("fragment")
public class FragmentNavigator extends Navigator<Destination> {
    private static final String KEY_BACK_STACK_IDS = "androidx-nav-fragment:navigator:backStackIds";
    private static final String TAG = "FragmentNavigator";
    ArrayDeque<Integer> mBackStack = new ArrayDeque<>();
    private int mContainerId;
    FragmentManager mFragmentManager;
    boolean mIsPendingBackStackOperation = false;
    private final FragmentManager.OnBackStackChangedListener mOnBackStackChangedListener = new FragmentManager.OnBackStackChangedListener() {
        public void onBackStackChanged() {
            if (FragmentNavigator.this.mIsPendingBackStackOperation) {
                FragmentNavigator.this.mIsPendingBackStackOperation = !FragmentNavigator.this.isBackStackEqual();
                return;
            }
            int newCount = FragmentNavigator.this.mFragmentManager.getBackStackEntryCount() + 1;
            if (newCount < FragmentNavigator.this.mBackStack.size()) {
                while (FragmentNavigator.this.mBackStack.size() > newCount) {
                    FragmentNavigator.this.mBackStack.removeLast();
                }
                FragmentNavigator.this.dispatchOnNavigatorNavigated(FragmentNavigator.this.mBackStack.isEmpty() ? 0 : FragmentNavigator.this.mBackStack.peekLast().intValue(), 2);
            }
        }
    };

    public FragmentNavigator(@NonNull Context context, @NonNull FragmentManager manager, int containerId) {
        this.mFragmentManager = manager;
        this.mContainerId = containerId;
    }

    public void onActive() {
        this.mFragmentManager.addOnBackStackChangedListener(this.mOnBackStackChangedListener);
    }

    public void onInactive() {
        this.mFragmentManager.removeOnBackStackChangedListener(this.mOnBackStackChangedListener);
    }

    public boolean popBackStack() {
        int destId = 0;
        if (this.mBackStack.isEmpty()) {
            return false;
        }
        if (this.mFragmentManager.isStateSaved()) {
            Log.i(TAG, "Ignoring popBackStack() call: FragmentManager has already saved its state");
            return false;
        }
        boolean popped = false;
        if (this.mFragmentManager.getBackStackEntryCount() > 0) {
            this.mFragmentManager.popBackStack();
            this.mIsPendingBackStackOperation = true;
            popped = true;
        }
        this.mBackStack.removeLast();
        if (!this.mBackStack.isEmpty()) {
            destId = this.mBackStack.peekLast().intValue();
        }
        dispatchOnNavigatorNavigated(destId, 2);
        return popped;
    }

    @NonNull
    public Destination createDestination() {
        return new Destination((Navigator<? extends Destination>) this);
    }

    public void navigate(@NonNull Destination destination, @Nullable Bundle args, @Nullable NavOptions navOptions, @Nullable Navigator.Extras navigatorExtras) {
        int backStackEffect;
        Navigator.Extras extras = navigatorExtras;
        if (this.mFragmentManager.isStateSaved()) {
            Log.i(TAG, "Ignoring navigate() call: FragmentManager has already saved its state");
            return;
        }
        Fragment frag = destination.createFragment(args);
        FragmentTransaction ft = this.mFragmentManager.beginTransaction();
        int enterAnim = navOptions != null ? navOptions.getEnterAnim() : -1;
        int exitAnim = navOptions != null ? navOptions.getExitAnim() : -1;
        int popEnterAnim = navOptions != null ? navOptions.getPopEnterAnim() : -1;
        int popExitAnim = navOptions != null ? navOptions.getPopExitAnim() : -1;
        boolean isSingleTopReplacement = false;
        if (!(enterAnim == -1 && exitAnim == -1 && popEnterAnim == -1 && popExitAnim == -1)) {
            ft.setCustomAnimations(enterAnim != -1 ? enterAnim : 0, exitAnim != -1 ? exitAnim : 0, popEnterAnim != -1 ? popEnterAnim : 0, popExitAnim != -1 ? popExitAnim : 0);
        }
        ft.replace(this.mContainerId, frag);
        ft.setPrimaryNavigationFragment(frag);
        int destId = destination.getId();
        boolean initialNavigation = this.mBackStack.isEmpty();
        boolean isClearTask = navOptions != null && navOptions.shouldClearTask();
        if (navOptions != null && !initialNavigation && navOptions.shouldLaunchSingleTop() && this.mBackStack.peekLast().intValue() == destId) {
            isSingleTopReplacement = true;
        }
        if (initialNavigation || isClearTask) {
            backStackEffect = 1;
        } else if (isSingleTopReplacement) {
            if (this.mBackStack.size() > 1) {
                this.mFragmentManager.popBackStack();
                ft.addToBackStack(Integer.toString(destId));
                this.mIsPendingBackStackOperation = true;
            }
            backStackEffect = 0;
        } else {
            ft.addToBackStack(Integer.toString(destId));
            this.mIsPendingBackStackOperation = true;
            backStackEffect = 1;
        }
        if (extras instanceof Extras) {
            for (Map.Entry<View, String> sharedElement : ((Extras) extras).getSharedElements().entrySet()) {
                Map.Entry<View, String> entry = sharedElement;
                ft.addSharedElement(sharedElement.getKey(), sharedElement.getValue());
                Navigator.Extras extras2 = navigatorExtras;
            }
        }
        ft.setReorderingAllowed(true);
        ft.commit();
        if (backStackEffect == 1) {
            this.mBackStack.add(Integer.valueOf(destId));
        }
        dispatchOnNavigatorNavigated(destId, backStackEffect);
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

    /* access modifiers changed from: package-private */
    public boolean isBackStackEqual() {
        int fragmentBackStackCount = this.mFragmentManager.getBackStackEntryCount();
        if (this.mBackStack.size() != fragmentBackStackCount + 1) {
            return false;
        }
        Iterator<Integer> backStackIterator = this.mBackStack.descendingIterator();
        int fragmentBackStackIndex = fragmentBackStackCount - 1;
        while (backStackIterator.hasNext() && fragmentBackStackIndex >= 0) {
            int fragmentBackStackIndex2 = fragmentBackStackIndex - 1;
            if (backStackIterator.next().intValue() != Integer.valueOf(this.mFragmentManager.getBackStackEntryAt(fragmentBackStackIndex).getName()).intValue()) {
                return false;
            }
            fragmentBackStackIndex = fragmentBackStackIndex2;
        }
        return true;
    }

    public static class Destination extends NavDestination {
        private Class<? extends Fragment> mFragmentClass;

        public Destination(@NonNull NavigatorProvider navigatorProvider) {
            this((Navigator<? extends Destination>) navigatorProvider.getNavigator(FragmentNavigator.class));
        }

        public Destination(@NonNull Navigator<? extends Destination> fragmentNavigator) {
            super(fragmentNavigator);
        }

        public void onInflate(@NonNull Context context, @NonNull AttributeSet attrs) {
            super.onInflate(context, attrs);
            TypedArray a = context.getResources().obtainAttributes(attrs, R.styleable.FragmentNavigator);
            String className = a.getString(R.styleable.FragmentNavigator_android_name);
            if (className != null) {
                setFragmentClass(parseClassFromName(context, className, Fragment.class));
            }
            a.recycle();
        }

        @NonNull
        public Destination setFragmentClass(@NonNull Class<? extends Fragment> clazz) {
            this.mFragmentClass = clazz;
            return this;
        }

        @NonNull
        public Class<? extends Fragment> getFragmentClass() {
            if (this.mFragmentClass != null) {
                return this.mFragmentClass;
            }
            throw new IllegalStateException("fragment class not set");
        }

        @NonNull
        public Fragment createFragment(@Nullable Bundle args) {
            try {
                Fragment f = (Fragment) getFragmentClass().newInstance();
                if (args != null) {
                    f.setArguments(args);
                }
                return f;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class Extras implements Navigator.Extras {
        private final LinkedHashMap<View, String> mSharedElements = new LinkedHashMap<>();

        Extras(Map<View, String> sharedElements) {
            this.mSharedElements.putAll(sharedElements);
        }

        @NonNull
        public Map<View, String> getSharedElements() {
            return Collections.unmodifiableMap(this.mSharedElements);
        }

        public static class Builder {
            private final LinkedHashMap<View, String> mSharedElements = new LinkedHashMap<>();

            @NonNull
            public Builder addSharedElements(@NonNull Map<View, String> sharedElements) {
                for (Map.Entry<View, String> sharedElement : sharedElements.entrySet()) {
                    View view = sharedElement.getKey();
                    String name = sharedElement.getValue();
                    if (!(view == null || name == null)) {
                        addSharedElement(view, name);
                    }
                }
                return this;
            }

            @NonNull
            public Builder addSharedElement(@NonNull View sharedElement, @NonNull String name) {
                this.mSharedElements.put(sharedElement, name);
                return this;
            }

            @NonNull
            public Extras build() {
                return new Extras(this.mSharedElements);
            }
        }
    }
}
