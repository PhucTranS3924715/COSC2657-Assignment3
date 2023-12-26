package com.example.assignment3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CustomerMainActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main);

        viewPager = findViewById(R.id.viewPager);
        bottomNavigation = findViewById(R.id.bottom_navigation);

        // Set up the ViewPager and adapter
        FragmentStateAdapter adapter = new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0:
                        return new HomeFragment();
                    case 1:
                        return new ActivityFragment();
                    case 2:
                        return new VoucherFragment();
                    case 3:
                        return new ProfileFragment();
                    default:
                        return new HomeFragment();
                }
            }

            @Override
            public int getItemCount() {
                return 4;
            }
        };
        viewPager.setAdapter(adapter);

        // Set up the BottomNavigationView
        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.home) {
                viewPager.setCurrentItem(0);
                return true;
            } else if (id == R.id.activity) {
                viewPager.setCurrentItem(1);
                return true;
            } else if (id == R.id.voucher) {
                viewPager.setCurrentItem(2);
                return true;
            } else if (id == R.id.profile) {
                viewPager.setCurrentItem(3);
                return true;
            }
            return false;
        });

        // Update bottom navigation view when swiping the view pager
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                bottomNavigation.getMenu().getItem(position).setChecked(true);
            }
        });

    }
}