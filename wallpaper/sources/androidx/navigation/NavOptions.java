package androidx.navigation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.AnimRes;
import android.support.annotation.AnimatorRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class NavOptions {
    private static final String KEY_ENTER_ANIM = "enterAnim";
    private static final String KEY_EXIT_ANIM = "exitAnim";
    private static final String KEY_LAUNCH_MODE = "launchMode";
    private static final String KEY_NAV_OPTIONS = "android-support-nav:navOptions";
    private static final String KEY_POP_ENTER_ANIM = "popEnterAnim";
    private static final String KEY_POP_EXIT_ANIM = "popExitAnim";
    private static final String KEY_POP_UP_TO = "popUpTo";
    private static final String KEY_POP_UP_TO_INCLUSIVE = "popUpToInclusive";
    static final int LAUNCH_CLEAR_TASK = 4;
    static final int LAUNCH_DOCUMENT = 2;
    static final int LAUNCH_SINGLE_TOP = 1;
    @AnimRes
    @AnimatorRes
    private int mEnterAnim;
    @AnimRes
    @AnimatorRes
    private int mExitAnim;
    private int mLaunchMode;
    @AnimRes
    @AnimatorRes
    private int mPopEnterAnim;
    @AnimRes
    @AnimatorRes
    private int mPopExitAnim;
    @IdRes
    private int mPopUpTo;
    private boolean mPopUpToInclusive;

    public static void addPopAnimationsToIntent(@NonNull Intent intent, @Nullable NavOptions navOptions) {
        if (navOptions != null) {
            intent.putExtra(KEY_NAV_OPTIONS, navOptions.toBundle());
        }
    }

    public static void applyPopAnimationsToPendingTransition(@NonNull Activity activity) {
        Bundle bundle;
        Intent intent = activity.getIntent();
        if (intent != null && (bundle = intent.getBundleExtra(KEY_NAV_OPTIONS)) != null) {
            NavOptions navOptions = fromBundle(bundle);
            int popEnterAnim = navOptions.getPopEnterAnim();
            int popExitAnim = navOptions.getPopExitAnim();
            if (popEnterAnim != -1 || popExitAnim != -1) {
                int popExitAnim2 = 0;
                int popEnterAnim2 = popEnterAnim != -1 ? popEnterAnim : 0;
                if (popExitAnim != -1) {
                    popExitAnim2 = popExitAnim;
                }
                activity.overridePendingTransition(popEnterAnim2, popExitAnim2);
            }
        }
    }

    NavOptions(int launchMode, @IdRes int popUpTo, boolean popUpToInclusive, @AnimRes @AnimatorRes int enterAnim, @AnimRes @AnimatorRes int exitAnim, @AnimRes @AnimatorRes int popEnterAnim, @AnimRes @AnimatorRes int popExitAnim) {
        this.mLaunchMode = launchMode;
        this.mPopUpTo = popUpTo;
        this.mPopUpToInclusive = popUpToInclusive;
        this.mEnterAnim = enterAnim;
        this.mExitAnim = exitAnim;
        this.mPopEnterAnim = popEnterAnim;
        this.mPopExitAnim = popExitAnim;
    }

    public boolean shouldLaunchSingleTop() {
        return (this.mLaunchMode & 1) != 0;
    }

    @Deprecated
    public boolean shouldLaunchDocument() {
        return (this.mLaunchMode & 2) != 0;
    }

    @Deprecated
    public boolean shouldClearTask() {
        return (this.mLaunchMode & 4) != 0;
    }

    @IdRes
    public int getPopUpTo() {
        return this.mPopUpTo;
    }

    public boolean isPopUpToInclusive() {
        return this.mPopUpToInclusive;
    }

    @AnimRes
    @AnimatorRes
    public int getEnterAnim() {
        return this.mEnterAnim;
    }

    @AnimRes
    @AnimatorRes
    public int getExitAnim() {
        return this.mExitAnim;
    }

    @AnimRes
    @AnimatorRes
    public int getPopEnterAnim() {
        return this.mPopEnterAnim;
    }

    @AnimRes
    @AnimatorRes
    public int getPopExitAnim() {
        return this.mPopExitAnim;
    }

    @NonNull
    private Bundle toBundle() {
        Bundle b = new Bundle();
        b.putInt(KEY_LAUNCH_MODE, this.mLaunchMode);
        b.putInt(KEY_POP_UP_TO, this.mPopUpTo);
        b.putBoolean(KEY_POP_UP_TO_INCLUSIVE, this.mPopUpToInclusive);
        b.putInt(KEY_ENTER_ANIM, this.mEnterAnim);
        b.putInt(KEY_EXIT_ANIM, this.mExitAnim);
        b.putInt(KEY_POP_ENTER_ANIM, this.mPopEnterAnim);
        b.putInt(KEY_POP_EXIT_ANIM, this.mPopExitAnim);
        return b;
    }

    @NonNull
    private static NavOptions fromBundle(@NonNull Bundle b) {
        return new NavOptions(b.getInt(KEY_LAUNCH_MODE, 0), b.getInt(KEY_POP_UP_TO, 0), b.getBoolean(KEY_POP_UP_TO_INCLUSIVE, false), b.getInt(KEY_ENTER_ANIM, -1), b.getInt(KEY_EXIT_ANIM, -1), b.getInt(KEY_POP_ENTER_ANIM, -1), b.getInt(KEY_POP_EXIT_ANIM, -1));
    }

    public static class Builder {
        @AnimRes
        @AnimatorRes
        int mEnterAnim = -1;
        @AnimRes
        @AnimatorRes
        int mExitAnim = -1;
        int mLaunchMode;
        @AnimRes
        @AnimatorRes
        int mPopEnterAnim = -1;
        @AnimRes
        @AnimatorRes
        int mPopExitAnim = -1;
        @IdRes
        int mPopUpTo;
        boolean mPopUpToInclusive;

        @NonNull
        public Builder setLaunchSingleTop(boolean singleTop) {
            if (singleTop) {
                this.mLaunchMode |= 1;
            } else {
                this.mLaunchMode &= -2;
            }
            return this;
        }

        @Deprecated
        @NonNull
        public Builder setLaunchDocument(boolean launchDocument) {
            if (launchDocument) {
                this.mLaunchMode |= 2;
            } else {
                this.mLaunchMode &= -3;
            }
            return this;
        }

        @Deprecated
        @NonNull
        public Builder setClearTask(boolean clearTask) {
            if (clearTask) {
                this.mLaunchMode |= 4;
            } else {
                this.mLaunchMode &= -5;
            }
            return this;
        }

        @NonNull
        public Builder setPopUpTo(@IdRes int destinationId, boolean inclusive) {
            this.mPopUpTo = destinationId;
            this.mPopUpToInclusive = inclusive;
            return this;
        }

        @NonNull
        public Builder setEnterAnim(@AnimRes @AnimatorRes int enterAnim) {
            this.mEnterAnim = enterAnim;
            return this;
        }

        @NonNull
        public Builder setExitAnim(@AnimRes @AnimatorRes int exitAnim) {
            this.mExitAnim = exitAnim;
            return this;
        }

        @NonNull
        public Builder setPopEnterAnim(@AnimRes @AnimatorRes int popEnterAnim) {
            this.mPopEnterAnim = popEnterAnim;
            return this;
        }

        @NonNull
        public Builder setPopExitAnim(@AnimRes @AnimatorRes int popExitAnim) {
            this.mPopExitAnim = popExitAnim;
            return this;
        }

        @NonNull
        public NavOptions build() {
            return new NavOptions(this.mLaunchMode, this.mPopUpTo, this.mPopUpToInclusive, this.mEnterAnim, this.mExitAnim, this.mPopEnterAnim, this.mPopExitAnim);
        }
    }
}
