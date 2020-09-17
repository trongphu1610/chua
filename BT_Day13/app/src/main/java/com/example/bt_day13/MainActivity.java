package com.example.bt_day13;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.bt_day13.adapter.MusicPagerAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ViewPager vpPage;
    private MusicPagerAdapter adapter;
    private TabLayout tlMucsic;
    private NavigationView navMusic;
    private DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vpPage = findViewById(R.id.vp_page);
        drawerLayout = findViewById(R.id.drawer);
        tlMucsic = findViewById(R.id.tab_music);
        navMusic = findViewById(R.id.nav_option);

        adapter = new MusicPagerAdapter(getSupportFragmentManager());
        vpPage.setAdapter(adapter);
        tlMucsic.setupWithViewPager(vpPage);
        navMusic.setNavigationItemSelectedListener(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_option);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            if (drawerLayout.isDrawerOpen(GravityCompat.END)){
                drawerLayout.openDrawer(GravityCompat.END);
            }else {
                drawerLayout.openDrawer(GravityCompat.END);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tab_new:
                vpPage.setCurrentItem(0);
                return true;
            case R.id.tab_video:
                vpPage.setCurrentItem(1);
                return true;
            case R.id.tab_music:
                vpPage.setCurrentItem(2);
                return true;
            case R.id.tab_covid:
                Toast.makeText(this, "I'm pro", Toast.LENGTH_SHORT).show();
                return true;
        }
        return false;
    }
}