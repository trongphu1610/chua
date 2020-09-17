package com.example.bt_tablayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.bt_tablayout.adapter.NewsPagerAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private NavigationView navigationView;
    private ViewPager viewPager;
    private DrawerLayout drawerLayout;
    private TabLayout tabLayout;

    private NewsPagerAdapter newsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigationView = findViewById(R.id.nav_option);
        viewPager = findViewById(R.id.vp_page);
        tabLayout = findViewById(R.id.tab_layout);
        drawerLayout = findViewById(R.id.drawer);


        navigationView.setItemIconTintList(null);

        newsPagerAdapter = new NewsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(newsPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);

        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(navigationView);
        switch (item.getItemId()) {
            case R.id.tab_news:
                viewPager.setCurrentItem(0);
                break;
            case R.id.tab_music:
                viewPager.setCurrentItem(1);
                break;
            case R.id.tab_video:
                viewPager.setCurrentItem(2);
                break;
            case R.id.tab_covid:
                viewPager.setCurrentItem(3);
                break;
        }
        return false;
    }
}