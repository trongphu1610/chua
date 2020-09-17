package com.hblong.btday13.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.hblong.btday13.R;
import com.hblong.btday13.adapter.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private NavigationView navigationView;
    private ViewPager viewPager;
    private DrawerLayout drawerLayout;
    private TabLayout tabLayout;

    private ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigationView = findViewById(R.id.navigation_view);
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        drawerLayout = findViewById(R.id.drawer_layout);


        navigationView.setItemIconTintList(null);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);

        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(navigationView);
        switch (item.getItemId()) {
            case R.id.nav_music_item:
                viewPager.setCurrentItem(1);
                break;
            case R.id.nav_video_item:
                viewPager.setCurrentItem(2);
                break;
            case R.id.nav_news_item:
                viewPager.setCurrentItem(0);
                break;
        }
        return false;
    }
}