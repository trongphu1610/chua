package androidx.navigation;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import androidx.navigation.Navigator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Navigator.Name("activity")
public class ActivityNavigator extends Navigator<Destination> {
    private static final String EXTRA_NAV_CURRENT = "android-support-navigation:ActivityNavigator:current";
    private static final String EXTRA_NAV_SOURCE = "android-support-navigation:ActivityNavigator:source";
    private Context mContext;
    private Activity mHostActivity;

    public ActivityNavigator(@NonNull Context context) {
        this.mContext = context;
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                this.mHostActivity = (Activity) context;
                return;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
    }

    /* access modifiers changed from: package-private */
    @NonNull
    public Context getContext() {
        return this.mContext;
    }

    @NonNull
    public Destination createDestination() {
        return new Destination((Navigator<? extends Destination>) this);
    }

    public boolean popBackStack() {
        if (this.mHostActivity == null) {
            return false;
        }
        int destId = 0;
        Intent intent = this.mHostActivity.getIntent();
        if (intent != null) {
            destId = intent.getIntExtra(EXTRA_NAV_SOURCE, 0);
        }
        this.mHostActivity.finish();
        dispatchOnNavigatorNavigated(destId, 2);
        return true;
    }

    public void navigate(@NonNull Destination destination, @Nullable Bundle args, @Nullable NavOptions navOptions, @Nullable Navigator.Extras navigatorExtras) {
        Intent hostIntent;
        int hostCurrentId;
        if (destination.getIntent() == null) {
            throw new IllegalStateException("Destination " + destination.getId() + " does not have an Intent set.");
        }
        Intent intent = new Intent(destination.getIntent());
        if (args != null) {
            intent.putExtras(args);
            String dataPattern = destination.getDataPattern();
            if (!TextUtils.isEmpty(dataPattern)) {
                StringBuffer data = new StringBuffer();
                Matcher matcher = Pattern.compile("\\{(.+?)\\}").matcher(dataPattern);
                while (matcher.find()) {
                    String argName = matcher.group(1);
                    if (args.containsKey(argName)) {
                        matcher.appendReplacement(data, "");
                        data.append(Uri.encode(args.getString(argName)));
                    } else {
                        throw new IllegalArgumentException("Could not find " + argName + " in " + args + " to fill data pattern " + dataPattern);
                    }
                }
                matcher.appendTail(data);
                intent.setData(Uri.parse(data.toString()));
            }
        }
        if (navOptions != null && navOptions.shouldClearTask()) {
            intent.addFlags(32768);
        }
        if (navOptions != null && navOptions.shouldLaunchDocument() && Build.VERSION.SDK_INT >= 21) {
            intent.addFlags(524288);
        } else if (!(this.mContext instanceof Activity)) {
            intent.addFlags(268435456);
        }
        if (navOptions != null && navOptions.shouldLaunchSingleTop()) {
            intent.addFlags(536870912);
        }
        if (!(this.mHostActivity == null || (hostIntent = this.mHostActivity.getIntent()) == null || (hostCurrentId = hostIntent.getIntExtra(EXTRA_NAV_CURRENT, 0)) == 0)) {
            intent.putExtra(EXTRA_NAV_SOURCE, hostCurrentId);
        }
        int destId = destination.getId();
        intent.putExtra(EXTRA_NAV_CURRENT, destId);
        NavOptions.addPopAnimationsToIntent(intent, navOptions);
        if (!(navOptions == null || this.mHostActivity == null)) {
            int enterAnim = navOptions.getEnterAnim();
            int exitAnim = navOptions.getExitAnim();
            if (!(enterAnim == -1 && exitAnim == -1)) {
                this.mHostActivity.overridePendingTransition(enterAnim != -1 ? enterAnim : 0, exitAnim != -1 ? exitAnim : 0);
            }
        }
        if ((navigatorExtras instanceof Extras) != 0) {
            ActivityCompat.startActivity(this.mContext, intent, ((Extras) navigatorExtras).getActivityOptions().toBundle());
        } else {
            this.mContext.startActivity(intent);
        }
        dispatchOnNavigatorNavigated(destId, 0);
    }

    public static class Destination extends NavDestination {
        private String mDataPattern;
        private Intent mIntent;

        public Destination(@NonNull NavigatorProvider navigatorProvider) {
            this((Navigator<? extends Destination>) navigatorProvider.getNavigator(ActivityNavigator.class));
        }

        public Destination(@NonNull Navigator<? extends Destination> activityNavigator) {
            super(activityNavigator);
        }

        public void onInflate(@NonNull Context context, @NonNull AttributeSet attrs) {
            super.onInflate(context, attrs);
            TypedArray a = context.getResources().obtainAttributes(attrs, R.styleable.ActivityNavigator);
            String className = a.getString(R.styleable.ActivityNavigator_android_name);
            if (className != null) {
                setComponentName(new ComponentName(context, parseClassFromName(context, className, Activity.class)));
            }
            setAction(a.getString(R.styleable.ActivityNavigator_action));
            String data = a.getString(R.styleable.ActivityNavigator_data);
            if (data != null) {
                setData(Uri.parse(data));
            }
            setDataPattern(a.getString(R.styleable.ActivityNavigator_dataPattern));
            a.recycle();
        }

        @NonNull
        public Destination setIntent(@Nullable Intent intent) {
            this.mIntent = intent;
            return this;
        }

        @Nullable
        public Intent getIntent() {
            return this.mIntent;
        }

        @NonNull
        public Destination setComponentName(@Nullable ComponentName name) {
            if (this.mIntent == null) {
                this.mIntent = new Intent();
            }
            this.mIntent.setComponent(name);
            return this;
        }

        @Nullable
        public ComponentName getComponent() {
            if (this.mIntent == null) {
                return null;
            }
            return this.mIntent.getComponent();
        }

        @NonNull
        public Destination setAction(@Nullable String action) {
            if (this.mIntent == null) {
                this.mIntent = new Intent();
            }
            this.mIntent.setAction(action);
            return this;
        }

        @Nullable
        public String getAction() {
            if (this.mIntent == null) {
                return null;
            }
            return this.mIntent.getAction();
        }

        @NonNull
        public Destination setData(@Nullable Uri data) {
            if (this.mIntent == null) {
                this.mIntent = new Intent();
            }
            this.mIntent.setData(data);
            return this;
        }

        @Nullable
        public Uri getData() {
            if (this.mIntent == null) {
                return null;
            }
            return this.mIntent.getData();
        }

        @NonNull
        public Destination setDataPattern(@Nullable String dataPattern) {
            this.mDataPattern = dataPattern;
            return this;
        }

        @Nullable
        public String getDataPattern() {
            return this.mDataPattern;
        }
    }

    public static class Extras implements Navigator.Extras {
        @NonNull
        private final ActivityOptionsCompat mActivityOptions;

        public Extras(@NonNull ActivityOptionsCompat activityOptions) {
            this.mActivityOptions = activityOptions;
        }

        /* access modifiers changed from: package-private */
        @NonNull
        public ActivityOptionsCompat getActivityOptions() {
            return this.mActivityOptions;
        }
    }
}
