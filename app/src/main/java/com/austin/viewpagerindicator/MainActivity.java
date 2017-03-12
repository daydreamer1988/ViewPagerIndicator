package com.austin.viewpagerindicator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.austin.viewpagerindicator.widget.ViewPagerIndicator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ViewPagerIndicator mIndicator;
    private ViewPager mViewPager;
    private List<String> mTitles = Arrays.asList("Java1", "Android2", "Php3","Java4", "Android5", "Php6");
    private ArrayList<PagerFragment> mFragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        findById();

        initDatas();

        //动态添加TAB
        mIndicator.setTabTitles(mTitles, 4);

        mIndicator.setViewPager(mViewPager, 0);

        mIndicator.setOnPageChangeListener(new ViewPagerIndicator.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }

            @Override
            public int getCount() {
                return mFragments.size();
            }
        });

    }

    private void initDatas() {
        for (String mTitle : mTitles) {
            PagerFragment instance = PagerFragment.getInstance(mTitle);
            mFragments.add(instance);
        }
    }

    private void findById() {
        mIndicator = (ViewPagerIndicator) findViewById(R.id.indicator);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
    }
}
