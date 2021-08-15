package com.example.upbitautotrade.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.upbitautotrade.R;
import com.example.upbitautotrade.appinterface.UpBitTradeActivity;
import com.example.upbitautotrade.fragment.CoinCorrelationGroup;
import com.example.upbitautotrade.fragment.CoinEvaluationAdvanceFragment;
import com.example.upbitautotrade.fragment.CoinEvaluationFragment;
import com.example.upbitautotrade.fragment.CoinRateCorrelationGroup;
import com.example.upbitautotrade.fragment.MarketBTCDelta;
import com.example.upbitautotrade.fragment.MarketUSDTDelta;
import com.example.upbitautotrade.fragment.MyCoinsAssetsFragment;
import com.example.upbitautotrade.utils.BackgroundProcessor;
import com.example.upbitautotrade.viewmodel.AccountsViewModel;
import com.example.upbitautotrade.viewmodel.CoinEvaluationViewModel;
import com.example.upbitautotrade.viewmodel.UpBitViewModel;

public class TradePagerActivity extends FragmentActivity implements UpBitTradeActivity {
    private final String TAG = "TradePagerActivity";
    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 6;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager2 mViewPager;
    private BackgroundProcessor mBackgroundProcessor;
    private AccountsViewModel mAccountsViewModel;
    private CoinEvaluationViewModel mCoinEvaluationViewModel;

    private String mAccessKey;
    private String mSecretKey;
    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private FragmentStateAdapter pagerAdapter;
    private Fragment mMyCoinsAssetsFragment;
    private Fragment mCoinEvaluationFragment;
    private Fragment mMarketUSDTDelta;
    private Fragment mMarketBTCDelta;
    private Fragment mMarketCorrleation;
    private Fragment mMarketCorrleationRate;

    public TradePagerActivity() {
        mBackgroundProcessor = new BackgroundProcessor();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_pager);

        mAccessKey = getIntent().getStringExtra("ACCESS_KEY");
        mSecretKey = getIntent().getStringExtra("SECRET_KEY");

        // Instantiate a ViewPager2 and a PagerAdapter.
        mViewPager = findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(this);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager = findViewById(R.id.pager);
        mViewPager.setPageTransformer(new ZoomOutPageTransformer());
        mAccountsViewModel = new ViewModelProvider(this).get(AccountsViewModel.class);
        mCoinEvaluationViewModel = new ViewModelProvider(this).get(CoinEvaluationViewModel.class);

        mMyCoinsAssetsFragment = new MyCoinsAssetsFragment();
//        mCoinEvaluationFragment = new CoinEvaluationFragment();
        mCoinEvaluationFragment = new CoinEvaluationAdvanceFragment();
        mMarketUSDTDelta = new MarketUSDTDelta();
        mMarketBTCDelta = new MarketBTCDelta();
        mMarketCorrleation = new CoinCorrelationGroup();
        mMarketCorrleationRate = new CoinRateCorrelationGroup();
    }

    @Override
    public void onBackPressed() {
        if (mViewPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
        }
        mBackgroundProcessor.stopBackgroundProcessor();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBackgroundProcessor.stopBackgroundProcessor();
    }

    @Override
    public Activity getActivity() {
        return getActivity();
    }

    @Override
    public UpBitViewModel getViewModel() {
        return null;
    }

    @Override
    public AccountsViewModel getAccountsViewModel() {
        return mAccountsViewModel;
    }

    @Override
    public CoinEvaluationViewModel getCoinEvaluationViewModel() {
        return mCoinEvaluationViewModel;
    }

    @Override
    public BackgroundProcessor getProcessor() {
        return mBackgroundProcessor;
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        public ScreenSlidePagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @Override
        public Fragment createFragment(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = mMyCoinsAssetsFragment;
                    mBackgroundProcessor.setViewModel(mAccountsViewModel, mAccessKey, mSecretKey);
                    break;
                case 1:
                    fragment = mCoinEvaluationFragment;
                    mBackgroundProcessor.setViewModel(mCoinEvaluationViewModel, mAccessKey, mSecretKey);
                    break;
                case 2:
                    fragment = mMarketUSDTDelta;
                    mBackgroundProcessor.setViewModel(mCoinEvaluationViewModel, mAccessKey, mSecretKey);
                    break;
                case 3:
                    fragment = mMarketBTCDelta;
                    mBackgroundProcessor.setViewModel(mCoinEvaluationViewModel, mAccessKey, mSecretKey);
                    break;
                case 4:
                    fragment = mMarketCorrleation;
                    mBackgroundProcessor.setViewModel(mCoinEvaluationViewModel, mAccessKey, mSecretKey);
                    break;
                case 5:
                    fragment = mMarketCorrleationRate;
                    mBackgroundProcessor.setViewModel(mCoinEvaluationViewModel, mAccessKey, mSecretKey);
                    break;
                default:
                    fragment = null;
                    break;
            }
            return fragment;
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }

    public class ZoomOutPageTransformer implements ViewPager2.PageTransformer {
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0f);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0f);
            }
        }
    }

    public String getAccessKey() {
        return mAccessKey;
    }

    public String getSecretKey() {
        return mSecretKey;
    }
}
