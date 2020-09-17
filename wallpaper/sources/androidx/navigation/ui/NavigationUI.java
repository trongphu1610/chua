package androidx.navigation.ui;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavGraph;
import androidx.navigation.NavOptions;
import java.lang.ref.WeakReference;

public class NavigationUI {
    private NavigationUI() {
    }

    public static boolean onNavDestinationSelected(@NonNull MenuItem item, @NonNull NavController navController) {
        return onNavDestinationSelected(item, navController, false);
    }

    static boolean onNavDestinationSelected(@NonNull MenuItem item, @NonNull NavController navController, boolean popUp) {
        NavOptions.Builder builder = new NavOptions.Builder().setLaunchSingleTop(true).setEnterAnim(R.anim.nav_default_enter_anim).setExitAnim(R.anim.nav_default_exit_anim).setPopEnterAnim(R.anim.nav_default_pop_enter_anim).setPopExitAnim(R.anim.nav_default_pop_exit_anim);
        if (popUp) {
            builder.setPopUpTo(findStartDestination(navController.getGraph()).getId(), false);
        }
        try {
            navController.navigate(item.getItemId(), (Bundle) null, builder.build());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean navigateUp(@Nullable DrawerLayout drawerLayout, @NonNull NavController navController) {
        if (drawerLayout == null || navController.getCurrentDestination() != findStartDestination(navController.getGraph())) {
            return navController.navigateUp();
        }
        drawerLayout.openDrawer((int) GravityCompat.START);
        return true;
    }

    public static void setupActionBarWithNavController(@NonNull AppCompatActivity activity, @NonNull NavController navController) {
        setupActionBarWithNavController(activity, navController, (DrawerLayout) null);
    }

    public static void setupActionBarWithNavController(@NonNull AppCompatActivity activity, @NonNull NavController navController, @Nullable DrawerLayout drawerLayout) {
        navController.addOnNavigatedListener(new ActionBarOnNavigatedListener(activity, drawerLayout));
    }

    public static void setupWithNavController(@NonNull Toolbar toolbar, @NonNull NavController navController) {
        setupWithNavController(toolbar, navController, (DrawerLayout) null);
    }

    public static void setupWithNavController(@NonNull Toolbar toolbar, @NonNull final NavController navController, @Nullable final DrawerLayout drawerLayout) {
        navController.addOnNavigatedListener(new ToolbarOnNavigatedListener(toolbar, drawerLayout));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                NavigationUI.navigateUp(drawerLayout, navController);
            }
        });
    }

    public static void setupWithNavController(@NonNull CollapsingToolbarLayout collapsingToolbarLayout, @NonNull Toolbar toolbar, @NonNull NavController navController) {
        setupWithNavController(collapsingToolbarLayout, toolbar, navController, (DrawerLayout) null);
    }

    public static void setupWithNavController(@NonNull CollapsingToolbarLayout collapsingToolbarLayout, @NonNull Toolbar toolbar, @NonNull final NavController navController, @Nullable final DrawerLayout drawerLayout) {
        navController.addOnNavigatedListener(new CollapsingToolbarOnNavigatedListener(collapsingToolbarLayout, toolbar, drawerLayout));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                NavigationUI.navigateUp(drawerLayout, navController);
            }
        });
    }

    public static void setupWithNavController(@NonNull final NavigationView navigationView, @NonNull final NavController navController) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                boolean handled = NavigationUI.onNavDestinationSelected(item, navController, true);
                if (handled) {
                    ViewParent parent = navigationView.getParent();
                    if (parent instanceof DrawerLayout) {
                        ((DrawerLayout) parent).closeDrawer((View) navigationView);
                    } else {
                        BottomSheetBehavior bottomSheetBehavior = NavigationUI.findBottomSheetBehavior(navigationView);
                        if (bottomSheetBehavior != null) {
                            bottomSheetBehavior.setState(5);
                        }
                    }
                }
                return handled;
            }
        });
        final WeakReference<NavigationView> weakReference = new WeakReference<>(navigationView);
        navController.addOnNavigatedListener(new NavController.OnNavigatedListener() {
            public void onNavigated(@NonNull NavController controller, @NonNull NavDestination destination) {
                NavigationView view = (NavigationView) weakReference.get();
                if (view == null) {
                    controller.removeOnNavigatedListener(this);
                    return;
                }
                Menu menu = view.getMenu();
                int size = menu.size();
                for (int h = 0; h < size; h++) {
                    MenuItem item = menu.getItem(h);
                    item.setChecked(NavigationUI.matchDestination(destination, item.getItemId()));
                }
            }
        });
    }

    static BottomSheetBehavior findBottomSheetBehavior(@NonNull View view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (!(params instanceof CoordinatorLayout.LayoutParams)) {
            ViewParent parent = view.getParent();
            if (parent instanceof View) {
                return findBottomSheetBehavior((View) parent);
            }
            return null;
        }
        CoordinatorLayout.Behavior behavior = ((CoordinatorLayout.LayoutParams) params).getBehavior();
        if (!(behavior instanceof BottomSheetBehavior)) {
            return null;
        }
        return (BottomSheetBehavior) behavior;
    }

    public static void setupWithNavController(@NonNull BottomNavigationView bottomNavigationView, @NonNull final NavController navController) {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return NavigationUI.onNavDestinationSelected(item, navController, true);
            }
        });
        final WeakReference<BottomNavigationView> weakReference = new WeakReference<>(bottomNavigationView);
        navController.addOnNavigatedListener(new NavController.OnNavigatedListener() {
            public void onNavigated(@NonNull NavController controller, @NonNull NavDestination destination) {
                BottomNavigationView view = (BottomNavigationView) weakReference.get();
                if (view == null) {
                    controller.removeOnNavigatedListener(this);
                    return;
                }
                Menu menu = view.getMenu();
                int size = menu.size();
                for (int h = 0; h < size; h++) {
                    MenuItem item = menu.getItem(h);
                    if (NavigationUI.matchDestination(destination, item.getItemId())) {
                        item.setChecked(true);
                    }
                }
            }
        });
    }

    static boolean matchDestination(@NonNull NavDestination destination, @IdRes int destId) {
        NavDestination currentDestination = destination;
        while (currentDestination.getId() != destId && currentDestination.getParent() != null) {
            currentDestination = currentDestination.getParent();
        }
        return currentDestination.getId() == destId;
    }

    static NavDestination findStartDestination(@NonNull NavGraph graph) {
        NavDestination startDestination = graph;
        while (startDestination instanceof NavGraph) {
            NavGraph parent = (NavGraph) startDestination;
            startDestination = parent.findNode(parent.getStartDestination());
        }
        return startDestination;
    }

    private static class ActionBarOnNavigatedListener extends AbstractAppBarOnNavigatedListener {
        private final AppCompatActivity mActivity;

        ActionBarOnNavigatedListener(@NonNull AppCompatActivity activity, @Nullable DrawerLayout drawerLayout) {
            super(activity.getDrawerToggleDelegate().getActionBarThemedContext(), drawerLayout);
            this.mActivity = activity;
        }

        /* access modifiers changed from: protected */
        public void setTitle(CharSequence title) {
            this.mActivity.getSupportActionBar().setTitle(title);
        }

        /* access modifiers changed from: protected */
        public void setNavigationIcon(Drawable icon) {
            ActionBar actionBar = this.mActivity.getSupportActionBar();
            if (icon == null) {
                actionBar.setDisplayHomeAsUpEnabled(false);
                return;
            }
            actionBar.setDisplayHomeAsUpEnabled(true);
            this.mActivity.getDrawerToggleDelegate().setActionBarUpIndicator(icon, 0);
        }
    }

    private static class ToolbarOnNavigatedListener extends AbstractAppBarOnNavigatedListener {
        private final WeakReference<Toolbar> mToolbarWeakReference;

        ToolbarOnNavigatedListener(@NonNull Toolbar toolbar, @Nullable DrawerLayout drawerLayout) {
            super(toolbar.getContext(), drawerLayout);
            this.mToolbarWeakReference = new WeakReference<>(toolbar);
        }

        public void onNavigated(@NonNull NavController controller, @NonNull NavDestination destination) {
            if (((Toolbar) this.mToolbarWeakReference.get()) == null) {
                controller.removeOnNavigatedListener(this);
            } else {
                super.onNavigated(controller, destination);
            }
        }

        /* access modifiers changed from: protected */
        public void setTitle(CharSequence title) {
            ((Toolbar) this.mToolbarWeakReference.get()).setTitle(title);
        }

        /* access modifiers changed from: protected */
        public void setNavigationIcon(Drawable icon) {
            Toolbar toolbar = (Toolbar) this.mToolbarWeakReference.get();
            if (toolbar != null) {
                toolbar.setNavigationIcon(icon);
            }
        }
    }

    private static class CollapsingToolbarOnNavigatedListener extends AbstractAppBarOnNavigatedListener {
        private final WeakReference<CollapsingToolbarLayout> mCollapsingToolbarLayoutWeakReference;
        private final WeakReference<Toolbar> mToolbarWeakReference;

        CollapsingToolbarOnNavigatedListener(@NonNull CollapsingToolbarLayout collapsingToolbarLayout, @NonNull Toolbar toolbar, @Nullable DrawerLayout drawerLayout) {
            super(collapsingToolbarLayout.getContext(), drawerLayout);
            this.mCollapsingToolbarLayoutWeakReference = new WeakReference<>(collapsingToolbarLayout);
            this.mToolbarWeakReference = new WeakReference<>(toolbar);
        }

        public void onNavigated(@NonNull NavController controller, @NonNull NavDestination destination) {
            CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) this.mCollapsingToolbarLayoutWeakReference.get();
            Toolbar toolbar = (Toolbar) this.mToolbarWeakReference.get();
            if (collapsingToolbarLayout == null || toolbar == null) {
                controller.removeOnNavigatedListener(this);
            } else {
                super.onNavigated(controller, destination);
            }
        }

        /* access modifiers changed from: protected */
        public void setTitle(CharSequence title) {
            CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) this.mCollapsingToolbarLayoutWeakReference.get();
            if (collapsingToolbarLayout != null) {
                collapsingToolbarLayout.setTitle(title);
            }
        }

        /* access modifiers changed from: protected */
        public void setNavigationIcon(Drawable icon) {
            Toolbar toolbar = (Toolbar) this.mToolbarWeakReference.get();
            if (toolbar != null) {
                toolbar.setNavigationIcon(icon);
            }
        }
    }

    private static abstract class AbstractAppBarOnNavigatedListener implements NavController.OnNavigatedListener {
        private ValueAnimator mAnimator;
        private DrawerArrowDrawable mArrowDrawable;
        private final Context mContext;
        @Nullable
        private final WeakReference<DrawerLayout> mDrawerLayoutWeakReference;

        /* access modifiers changed from: protected */
        public abstract void setNavigationIcon(Drawable drawable);

        /* access modifiers changed from: protected */
        public abstract void setTitle(CharSequence charSequence);

        AbstractAppBarOnNavigatedListener(@NonNull Context context, @Nullable DrawerLayout drawerLayout) {
            this.mContext = context;
            if (drawerLayout != null) {
                this.mDrawerLayoutWeakReference = new WeakReference<>(drawerLayout);
            } else {
                this.mDrawerLayoutWeakReference = null;
            }
        }

        public void onNavigated(@NonNull NavController controller, @NonNull NavDestination destination) {
            DrawerLayout drawerLayout = this.mDrawerLayoutWeakReference != null ? (DrawerLayout) this.mDrawerLayoutWeakReference.get() : null;
            if (this.mDrawerLayoutWeakReference == null || drawerLayout != null) {
                CharSequence title = destination.getLabel();
                if (!TextUtils.isEmpty(title)) {
                    setTitle(title);
                }
                boolean z = false;
                boolean isStartDestination = NavigationUI.findStartDestination(controller.getGraph()) == destination;
                if (drawerLayout != null || !isStartDestination) {
                    if (drawerLayout != null && isStartDestination) {
                        z = true;
                    }
                    setActionBarUpIndicator(z);
                    return;
                }
                setNavigationIcon((Drawable) null);
                return;
            }
            controller.removeOnNavigatedListener(this);
        }

        /* access modifiers changed from: package-private */
        public void setActionBarUpIndicator(boolean showAsDrawerIndicator) {
            boolean animate = true;
            if (this.mArrowDrawable == null) {
                this.mArrowDrawable = new DrawerArrowDrawable(this.mContext);
                animate = false;
            }
            setNavigationIcon(this.mArrowDrawable);
            float endValue = showAsDrawerIndicator ? 0.0f : 1.0f;
            if (animate) {
                float startValue = this.mArrowDrawable.getProgress();
                if (this.mAnimator != null) {
                    this.mAnimator.cancel();
                }
                this.mAnimator = ObjectAnimator.ofFloat(this.mArrowDrawable, NotificationCompat.CATEGORY_PROGRESS, new float[]{startValue, endValue});
                this.mAnimator.start();
                return;
            }
            this.mArrowDrawable.setProgress(endValue);
        }
    }
}
