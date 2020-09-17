package com.dvt.monthlycalender.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.View;
import com.dvt.monthlycalender.R;
import com.dvt.monthlycalender.base.BaseActivity;
import com.dvt.monthlycalender.fragment.RateUsFragment;
import com.dvt.monthlycalender.fragment.WallpaperFragment;
import com.dvt.monthlycalender.util.Network;
import java.util.HashMap;
import kotlin.Metadata;
import org.jetbrains.annotations.Nullable;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\b\u0010\u0007\u001a\u00020\bH\u0016J\u0012\u0010\t\u001a\u00020\b2\b\u0010\n\u001a\u0004\u0018\u00010\u000bH\u0014J\u0006\u0010\f\u001a\u00020\rR\u0016\u0010\u0003\u001a\u0004\u0018\u00010\u00048BX\u0004¢\u0006\u0006\u001a\u0004\b\u0005\u0010\u0006¨\u0006\u000e"}, d2 = {"Lcom/dvt/monthlycalender/activity/MainActivity;", "Lcom/dvt/monthlycalender/base/BaseActivity;", "()V", "currentFragment", "Landroid/support/v4/app/Fragment;", "getCurrentFragment", "()Landroid/support/v4/app/Fragment;", "onBackPressed", "", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "showRateFragment", "", "app_debug"}, k = 1, mv = {1, 1, 11})
/* compiled from: MainActivity.kt */
public final class MainActivity extends BaseActivity {
    private HashMap _$_findViewCache;

    public void _$_clearFindViewByIdCache() {
        if (this._$_findViewCache != null) {
            this._$_findViewCache.clear();
        }
    }

    public View _$_findCachedViewById(int i) {
        if (this._$_findViewCache == null) {
            this._$_findViewCache = new HashMap();
        }
        View view = (View) this._$_findViewCache.get(Integer.valueOf(i));
        if (view != null) {
            return view;
        }
        View findViewById = findViewById(i);
        this._$_findViewCache.put(Integer.valueOf(i), findViewById);
        return findViewById;
    }

    private final Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.content);
    }

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_main);
        if (getCurrentFragment() == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content, new WallpaperFragment()).commit();
        }
    }

    public void onBackPressed() {
        if (!(getCurrentFragment() instanceof WallpaperFragment) || !showRateFragment()) {
            super.onBackPressed();
        }
    }

    public final boolean showRateFragment() {
        SharedPreferences sharePre = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = sharePre.edit();
        if (sharePre.getBoolean("is_rated", false) || !Network.Companion.hasBeenConnected(this)) {
            return false;
        }
        getSupportFragmentManager().beginTransaction().replace(16908290, new RateUsFragment()).commit();
        return true;
    }
}
