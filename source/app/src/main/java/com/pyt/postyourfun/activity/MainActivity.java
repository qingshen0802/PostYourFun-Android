package com.pyt.postyourfun.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Window;

import com.pyt.postyourfun.Fragment.BuyImageFragment;
import com.pyt.postyourfun.Fragment.RatingFragment;
import com.pyt.postyourfun.Fragment.ViewImageFragment;
import com.pyt.postyourfun.Payment.PaymentController;
import com.pyt.postyourfun.R;
import com.pyt.postyourfun.Utils.PagerSlidingTabStrip;
import com.sromku.simple.fb.SimpleFacebook;

public class MainActivity extends BaseActivity {

    private ViewPager pager;
    private PagerSlidingTabStrip tabs;
    private MyPagerAdapter adapter;
    private SimpleFacebook simpleFacebook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        PaymentController.sharedInstance().startPaypalService(this);

        tabs = (PagerSlidingTabStrip) this.findViewById(R.id.tabs);
        tabs.setUnderlineColor(getResources().getColor(android.R.color.transparent));
        tabs.setBackgroundResource(android.R.color.holo_blue_dark);
        tabs.setTextColor(Color.parseColor("#ffffff"));
        tabs.setIndicatorColor(Color.parseColor("#aaffffff"));
        tabs.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, this.getResources().getDisplayMetrics()));
        tabs.setAllCaps(false);
        tabs.setShouldExpand(true);

        pager = (ViewPager) this.findViewById(R.id.pager);
        adapter = new MyPagerAdapter(getSupportFragmentManager());

        pager.setAdapter(adapter);
        tabs.setViewPager(pager);

        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                if (arg0 == 0) {
                    RatingFragment frag = (RatingFragment) adapter.getItem(arg0);
                    frag.onResume();
                } else if (arg0 == 2) {
                    ViewImageFragment fragment = (ViewImageFragment) adapter.getItem(arg0);
                    fragment.onResume();
                }
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }
        });
        pager.setCurrentItem(1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        simpleFacebook = SimpleFacebook.getInstance(this);
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        private String[] TITLES;
        BuyImageFragment buyImageFragment;
        RatingFragment ratingFragment;
        ViewImageFragment viewImageFragment;

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
            TITLES = new String[]{"RATE", "IMAGE", "VIEW"};
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                if (ratingFragment == null) {
                    ratingFragment = RatingFragment.newInstance();
                }
                return ratingFragment;
            } else if (position == 1) {
                if (buyImageFragment == null) {
                    buyImageFragment = BuyImageFragment.newInstance();
                }
                return buyImageFragment;
            } else {
                if (viewImageFragment == null) {
                    viewImageFragment = ViewImageFragment.newInstance();
                }
                return viewImageFragment;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        simpleFacebook.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        PaymentController.sharedInstance().activityResult(requestCode, resultCode, data, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PaymentController.sharedInstance().stopPaypalService(this);
    }
}
