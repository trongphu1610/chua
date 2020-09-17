package com.trongphu.musichome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.trongphu.musichome.adapter.MusicPagerAdapter;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private TabLayout tabLayout;
    private DrawerLayout drawer;
    private ViewPager vpPage;
    private NavigationView navView;
    private MusicPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tabLayout = findViewById(R.id.tab_layout);
        drawer = findViewById(R.id.drawer);
        vpPage = findViewById(R.id.vp_pager);
        navView = findViewById(R.id.nav_music);

        adapter = new MusicPagerAdapter(getSupportFragmentManager());
        vpPage.setAdapter(adapter);
        tabLayout.setupWithViewPager(vpPage);
        navView.setNavigationItemSelectedListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_option);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_album:
                vpPage.setCurrentItem(0);
                return true;
            case R.id.item_faverite:
                vpPage.setCurrentItem(1);
        }
        return false;
    }
}