package com.yang.weixin;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewConfiguration;

import com.yang.weixin.fragment.TabFragment;
import com.yang.weixin.view.ColorChangeable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {
    private ViewPager mViewPager;
    private List<Fragment> mTabs = new ArrayList<>();
    private String[] mTitles = new String[]{"第一页", "第二页", "第三页", "第四页"};
    private PagerAdapter mAdapter;
    private List<ColorChangeable> mTabIndicators = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //使overFlowButton一直出现
        setOverFlowButtonAlways();
        initViewAndEvent();
        initData();
    }

    private void initData() {
        for (String title : mTitles) {
            TabFragment fragment = new TabFragment();
            Bundle bundle = new Bundle();
            bundle.putString(TabFragment.TITLE, title);
            fragment.setArguments(bundle);
            mTabs.add(fragment);
        }
        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mTabs.get(position);
            }

            @Override
            public int getCount() {
                return mTabs.size();
            }
        };
        mViewPager.setAdapter(mAdapter);
    }

    private void initViewAndEvent() {
        mViewPager = (ViewPager) findViewById(R.id.id_viewPager);
        ColorChangeable one = (ColorChangeable) findViewById(R.id.id_indicator_one);
        ColorChangeable two = (ColorChangeable) findViewById(R.id.id_indicator_two);
        ColorChangeable three = (ColorChangeable) findViewById(R.id.id_indicator_three);
        ColorChangeable four = (ColorChangeable) findViewById(R.id.id_indicator_four);
        mTabIndicators.add(one);
        mTabIndicators.add(two);
        mTabIndicators.add(three);
        mTabIndicators.add(four);
        one.setOnClickListener(this);
        two.setOnClickListener(this);
        three.setOnClickListener(this);
        four.setOnClickListener(this);
        one.setIconAlpha(1);
        mViewPager.addOnPageChangeListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void setOverFlowButtonAlways() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field field = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            field.setAccessible(true);
            field.setBoolean(config, false);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    //设置menu显示图片
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //Menu是接口 MenuBuilder为该接口的具体实现类
        if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
            try {
                Method method = menu.getClass().getDeclaredMethod("setOptionalIconVisible", Boolean.TYPE);
                method.setAccessible(true);
                method.invoke(menu, true);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onClick(View v) {
        resetOtherTabsAlpha();
        switch (v.getId()) {
            case R.id.id_indicator_one:
                mViewPager.setCurrentItem(0, false);//false不让其有动画效果
                mTabIndicators.get(0).setIconAlpha(1);
                break;
            case R.id.id_indicator_two:
                mViewPager.setCurrentItem(1, false);
                mTabIndicators.get(1).setIconAlpha(1);
                break;
            case R.id.id_indicator_three:
                mViewPager.setCurrentItem(2, false);
                mTabIndicators.get(2).setIconAlpha(1);
                break;
            case R.id.id_indicator_four:
                mViewPager.setCurrentItem(3, false);
                mTabIndicators.get(3).setIconAlpha(1);
                break;
        }
    }

    private void resetOtherTabsAlpha() {
        for (int i = 0; i < mTabIndicators.size(); i++) {
            mTabIndicators.get(i).setIconAlpha(0);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (positionOffset > 0) {
            ColorChangeable left = mTabIndicators.get(position);
            ColorChangeable right = mTabIndicators.get(position + 1);
            left.setIconAlpha(1 - positionOffset);
            right.setIconAlpha(positionOffset);
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
