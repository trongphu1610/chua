package androidx.navigation.fragment;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.NavigationRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.navigation.Navigation;
import androidx.navigation.Navigator;
import androidx.navigation.fragment.FragmentNavigator;

public class NavHostFragment extends Fragment implements NavHost {
    private static final String KEY_DEFAULT_NAV_HOST = "android-support-nav:fragment:defaultHost";
    private static final String KEY_GRAPH_ID = "android-support-nav:fragment:graphId";
    private static final String KEY_NAV_CONTROLLER_STATE = "android-support-nav:fragment:navControllerState";
    private boolean mDefaultNavHost;
    private NavController mNavController;

    @NonNull
    public static NavController findNavController(@NonNull Fragment fragment) {
        for (Fragment findFragment = fragment; findFragment != null; findFragment = findFragment.getParentFragment()) {
            if (findFragment instanceof NavHostFragment) {
                return ((NavHostFragment) findFragment).getNavController();
            }
            Fragment primaryNavFragment = findFragment.requireFragmentManager().getPrimaryNavigationFragment();
            if (primaryNavFragment instanceof NavHostFragment) {
                return ((NavHostFragment) primaryNavFragment).getNavController();
            }
        }
        View view = fragment.getView();
        if (view != null) {
            return Navigation.findNavController(view);
        }
        throw new IllegalStateException("Fragment " + fragment + " does not have a NavController set");
    }

    @NonNull
    public static NavHostFragment create(@NavigationRes int graphResId) {
        Bundle b = null;
        if (graphResId != 0) {
            b = new Bundle();
            b.putInt(KEY_GRAPH_ID, graphResId);
        }
        NavHostFragment result = new NavHostFragment();
        if (b != null) {
            result.setArguments(b);
        }
        return result;
    }

    @NonNull
    public NavController getNavController() {
        if (this.mNavController != null) {
            return this.mNavController;
        }
        throw new IllegalStateException("NavController is not available before onCreate()");
    }

    public void setGraph(@NavigationRes int graphResId) {
        if (this.mNavController == null) {
            Bundle args = getArguments();
            if (args == null) {
                args = new Bundle();
            }
            args.putInt(KEY_GRAPH_ID, graphResId);
            setArguments(args);
            return;
        }
        this.mNavController.setGraph(graphResId);
    }

    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (this.mDefaultNavHost) {
            requireFragmentManager().beginTransaction().setPrimaryNavigationFragment(this).commit();
        }
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mNavController = new NavController(requireContext());
        this.mNavController.getNavigatorProvider().addNavigator(createFragmentNavigator());
        Bundle navState = null;
        int graphId = 0;
        if (savedInstanceState != null) {
            navState = savedInstanceState.getBundle(KEY_NAV_CONTROLLER_STATE);
            if (savedInstanceState.getBoolean(KEY_DEFAULT_NAV_HOST, false)) {
                this.mDefaultNavHost = true;
                requireFragmentManager().beginTransaction().setPrimaryNavigationFragment(this).commit();
            }
        }
        if (navState != null) {
            this.mNavController.restoreState(navState);
            return;
        }
        Bundle args = getArguments();
        if (args != null) {
            graphId = args.getInt(KEY_GRAPH_ID);
        }
        if (graphId != 0) {
            this.mNavController.setGraph(graphId);
        } else {
            this.mNavController.setMetadataGraph();
        }
    }

    /* access modifiers changed from: protected */
    @NonNull
    public Navigator<? extends FragmentNavigator.Destination> createFragmentNavigator() {
        return new FragmentNavigator(requireContext(), getChildFragmentManager(), getId());
    }

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FrameLayout frameLayout = new FrameLayout(inflater.getContext());
        frameLayout.setId(getId());
        return frameLayout;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!(view instanceof ViewGroup)) {
            throw new IllegalStateException("created host view " + view + " is not a ViewGroup");
        }
        Navigation.setViewNavController(view.getParent() != null ? (View) view.getParent() : view, this.mNavController);
    }

    public void onInflate(@NonNull Context context, @NonNull AttributeSet attrs, @Nullable Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NavHostFragment);
        int graphId = a.getResourceId(R.styleable.NavHostFragment_navGraph, 0);
        boolean defaultHost = a.getBoolean(R.styleable.NavHostFragment_defaultNavHost, false);
        if (graphId != 0) {
            setGraph(graphId);
        }
        if (defaultHost) {
            this.mDefaultNavHost = true;
        }
        a.recycle();
    }

    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle navState = this.mNavController.saveState();
        if (navState != null) {
            outState.putBundle(KEY_NAV_CONTROLLER_STATE, navState);
        }
        if (this.mDefaultNavHost) {
            outState.putBoolean(KEY_DEFAULT_NAV_HOST, true);
        }
    }
}
