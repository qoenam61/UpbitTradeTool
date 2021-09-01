package com.example.upbitautotrade.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.upbitautotrade.R;
import com.example.upbitautotrade.appinterface.UpBitTradeActivity;
import com.example.upbitautotrade.model.DayCandle;
import com.example.upbitautotrade.model.MarketInfo;
import com.example.upbitautotrade.viewmodel.CoinEvaluationViewModel;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import static com.example.upbitautotrade.utils.BackgroundProcessor.UPDATE_DAY_CANDLE_INFO;
import static com.example.upbitautotrade.utils.BackgroundProcessor.UPDATE_MARKETS_INFO;

public class CoinCorrelationGroup extends Fragment {
    public static final String TAG = "CoinCorrelationGroup";

    public final String MARKET_NAME = "KRW";
    public final String MARKET_WARNING = "CAUTION";

    private final int MONITOR_DAY_CANDLE_COUNT = 30;

    private UpBitTradeActivity mActivity;
    private CoinEvaluationViewModel mViewModel;
    private View mView;

    boolean mIsActive = false;

    private CoinListAdapter mCoinListAdapter;
    private ArrayAdapter<String> mSpinnerAdapter;

    private Map<String, MarketInfo> mMarketsMapInfo;
    private List<String> mMarketListInfo;
    private ArrayList mDeadMarketList;

    private Map<String, List<DayCandle>> mDayCandleMapInfo;

    private Map<String, CorrelationCoin> mCorrelationResultSet;

    private String[] deadMarket = {
            "KRW-GLM", "KRW-WAX", "KRW-STR", "KRW-STM", "KRW-STE", "KRW-ARD", "KRW-MVL", "KRW-ORB", "KRW-HIV", "KRW-STR",
            "KRW-POL", "KRW-IQ ", "KRW-ELF", "KRW-DKA", "KRW-JST", "KRW-MTL", "KRW-QKC", "KRW-BOR", "KRW-SSX", "KRW-POW",
            "KRW-CRE", "KRW-TT ", "KRW-SBD", "KRW-GRS", "KRW-STP", "KRW-RFR", "KRW-HUM", "KRW-AER", "KRW-MBL", "KRW-MOC",
            "KRW-HUN", "KRW-AHT", "KRW-FCT", "KRW-TON", "KRW-CBK", "KRW-PLA", "KRW-BTG", "KRW-SC ", "KRW-ICX", "KRW-ANK",
            "KRW-IOS", "KRW-LSK", "KRW-KNC", "KRW-PUN", "KRW-STO"
    };
    private String mBaseMarketId;


    public CoinCorrelationGroup() {
        mMarketsMapInfo = new HashMap<>();
        mDayCandleMapInfo = new HashMap<>();
        mDeadMarketList = new ArrayList(Arrays.asList(deadMarket));
        mCorrelationResultSet = new HashMap<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (UpBitTradeActivity)getActivity();
        mViewModel =  mActivity.getCoinEvaluationViewModel();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_coin_correlation_group, container, false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        RecyclerView coinList = mView.findViewById(R.id.coin_evaluation_list);
        coinList.setLayoutManager(layoutManager);
        mCoinListAdapter = new CoinListAdapter();
        coinList.setAdapter(mCoinListAdapter);

        TextView title = mView.findViewById(R.id.fragment_title);
        title.setText(getTitleName());
        return mView;
    }


    @Override
    public void onStart() {
        super.onStart();
        if (mViewModel != null) {
            mViewModel.getMarketsInfo().observe(
                    getViewLifecycleOwner(),
                    marketsInfo -> {
                        if (!mIsActive) {
                            return;
                        }
                        mMarketsMapInfo.clear();
                        Iterator<MarketInfo> iterator = marketsInfo.iterator();
                        while (iterator.hasNext()) {
                            MarketInfo marketInfo = iterator.next();
                            if (marketInfo.getMarketId().contains(MARKET_NAME+"-")
                                    && !marketInfo.getMarket_warning().contains(MARKET_WARNING)) {
                                if (mDeadMarketList.contains(marketInfo.getMarketId())) {
                                    continue;
                                }
                                mMarketsMapInfo.put(marketInfo.getMarketId(), marketInfo);
                            }
                        }
                        List<String> marketKoranNameList = new ArrayList<>();
                        mMarketListInfo = new ArrayList<>();

                        Set<String> ketSet = mMarketsMapInfo.keySet();
                        Iterator<String> koreanNameIterator = ketSet.iterator();
                        while (koreanNameIterator.hasNext()) {
                            String marketId = koreanNameIterator.next();
                            marketKoranNameList.add(mMarketsMapInfo.get(marketId).getKorean_name());
                            mMarketListInfo.add(marketId);
                        }


                        Spinner spinner = mView.findViewById(R.id.coin_select_group);
                        if (mSpinnerAdapter == null) {
                            mSpinnerAdapter = new ArrayAdapter<>(
                                    getContext(), android.R.layout.simple_spinner_item, marketKoranNameList);

                            spinner.setAdapter(mSpinnerAdapter);
                            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    String marketId = mMarketListInfo.get(position);;
                                    if (mBaseMarketId == null || !mBaseMarketId.equals(marketId)) {
                                        mCorrelationResultSet.clear();
                                        mBaseMarketId = marketId;
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                }
                            });
                        }
                        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mSpinnerAdapter.notifyDataSetChanged();
                        registerPeriodicUpdate(mMarketsMapInfo.keySet());
                    }
            );

            mViewModel.getDayCandleInfo().observe(
                    getViewLifecycleOwner(),
                    dayCandles -> {
                        if (!mIsActive) {
                            return;
                        }
                        mDayCandleMapInfo.put(dayCandles.get(0).getMarketId(), dayCandles);
                        correlationCheck(mBaseMarketId);
                    }
            );
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mIsActive = false;
        mActivity.getProcessor().stopBackgroundProcessor();
    }

    @Override
    public void onResume() {
        super.onResume();
        mActivity.getProcessor().setViewModel(mActivity.getCoinEvaluationViewModel(), mActivity.getAccessKey(), mActivity.getSecretKey());
        mViewModel =  mActivity.getCoinEvaluationViewModel();
        mIsActive = true;
        mActivity.getProcessor().startBackgroundProcessor();
        mActivity.getProcessor().registerProcess(UPDATE_MARKETS_INFO, null);
    }

    private void registerPeriodicUpdate(Set<String> keySet) {
        Iterator<String> regIterator = keySet.iterator();
        while (regIterator.hasNext()) {
            String key = regIterator.next();
            if (!key.equals("KRW-KRW")) {
                mActivity.getProcessor().registerPeriodicUpdate(UPDATE_DAY_CANDLE_INFO, key, MONITOR_DAY_CANDLE_COUNT);
            }
        }
    }

    private void correlationCheck(String marketId) {
        List<DayCandle> baseCandle = mDayCandleMapInfo.get(marketId);
        if (baseCandle == null) {
            return;
        }

        List<Double> basePriceList = new ArrayList<>();

        Iterator<DayCandle> baseCandleIterator = baseCandle.iterator();
        while (baseCandleIterator.hasNext()) {
            DayCandle candle = baseCandleIterator.next();
            basePriceList.add(candle.getTradePrice().doubleValue());
        }

        String compareMarketId = null;
        Iterator<List<DayCandle>> iterator = mDayCandleMapInfo.values().iterator();
        while (iterator.hasNext()) {
            List<DayCandle> candles = iterator.next();
            compareMarketId = candles.get(0).getMarketId();
            if (!marketId.equals(compareMarketId)) {
                List<Double> comparePriceList = new ArrayList<>();

                setCompareItem(candles, comparePriceList);

                double colValue = correlation(basePriceList.stream().mapToDouble(Double::doubleValue).toArray(),
                        comparePriceList.stream().mapToDouble(Double::doubleValue).toArray());
                Log.d(TAG, "correlationCheck -marketId: "+compareMarketId + " value: "+colValue);
                if (colValue >= 0.95) {
                    mCorrelationResultSet.put(compareMarketId, new CorrelationCoin(compareMarketId, colValue));
                } else {
                    mCorrelationResultSet.remove(compareMarketId);
                }
            }
        }
        List<String> correlationResultList = new ArrayList<>(mCorrelationResultSet.keySet());
        Collections.sort(correlationResultList, (value1, value2) -> mCorrelationResultSet.get(value1).compareTo(mCorrelationResultSet.get(value2)));
        mCoinListAdapter.setMonitoringItems(correlationResultList);
        mCoinListAdapter.notifyDataSetChanged();
    }

    protected void setCompareItem(List<DayCandle> candles, List<Double> comparePriceList) {
        Iterator<DayCandle> compareCandleIterator = candles.iterator();
        while (compareCandleIterator.hasNext()) {
            DayCandle candle = compareCandleIterator.next();
            comparePriceList.add(candle.getTradePrice().doubleValue());
        }
    }

    protected String getTitleName() {
        return "가격 유사 그룹 선택";
    }

    private double correlation(double[] xs, double[] ys) {
        //TODO: check here that arrays are not null, of the same length etc

        double sx = 0.0;
        double sy = 0.0;
        double sxx = 0.0;
        double syy = 0.0;
        double sxy = 0.0;

        int n = 0;
        if (xs.length > ys.length) {
            n = ys.length;
            for(int i = 0; i < n; ++i) {
                double x = xs[i + 1];
                double y = ys[i];
                sx += x;
                sy += y;
                sxx += x * x;
                syy += y * y;
                sxy += x * y;
            }
        } else {
            n = xs.length;
            for(int i = 0; i < n; ++i) {
                double x = xs[i];
                double y = ys[i];
                sx += x;
                sy += y;
                sxx += x * x;
                syy += y * y;
                sxy += x * y;
            }
        }

/*        int n = xs.length > ys.length ? ys.length : xs.length;

        for(int i = 0; i < n; ++i) {
            double x = xs[i];
            double y = ys[i];
            sx += x;
            sy += y;
            sxx += x * x;
            syy += y * y;
            sxy += x * y;
        }*/

        // covariation
        double cov = sxy / n - sx * sy / n / n;
        // standard error of x
        double sigmax = Math.sqrt(sxx / n -  sx * sx / n / n);
        // standard error of y
        double sigmay = Math.sqrt(syy / n -  sy * sy / n / n);

        // correlation is just a normalized covariation
        return cov / sigmax / sigmay;
    }

    private class CoinHolder extends RecyclerView.ViewHolder {
        public TextView mCoinName;
        public TextView mCurrentPrice;
        public TextView mChangedRate;
        public TextView mChangedPrice;
        public TextView mCorValue;

        public CoinHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mCoinName = itemView.findViewById(R.id.coin_name);
            mCurrentPrice = itemView.findViewById(R.id.coin_current_price);
            mChangedRate = itemView.findViewById(R.id.coin_change_rate);
            mChangedPrice = itemView.findViewById(R.id.coin_changed_price);
            mCorValue = itemView.findViewById(R.id.coin_corrValue);
        }
    }

    private class CoinListAdapter extends RecyclerView.Adapter<CoinHolder> {
        private DecimalFormat mFormat;
        private DecimalFormat mNonZeroFormat;
        private DecimalFormat mPercentFormat;
        private SimpleDateFormat mTimeFormat;
        private List<String> mCoinListInfo;

        public CoinListAdapter() {
            mFormat = new DecimalFormat("###,###,###,###.#");
            mNonZeroFormat = new DecimalFormat("###,###,###,###");
            mPercentFormat = new DecimalFormat("###.##" + "%");
            mTimeFormat = new SimpleDateFormat("HH:mm:ss", Locale.KOREA);
            mTimeFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        }

        public void setMonitoringItems(List<String> coinList) {
            mCoinListInfo = coinList;
            notifyDataSetChanged();
        }

        @Override
        public CoinHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.correlation_coin_item, parent, false);
            return new CoinHolder(view);
        }

        @Override
        public void onBindViewHolder(CoinHolder holder, int position) {
            String key = mCoinListInfo.get(position);
            MarketInfo marketInfo = mMarketsMapInfo.get(key);
            if (marketInfo != null) {
                holder.mCoinName.setText(marketInfo.getKorean_name());
            }

            DayCandle candle = mDayCandleMapInfo.get(key).get(0);
            DayCandle dayAgo = mDayCandleMapInfo.get(key).get(1);
            if (candle != null && dayAgo != null) {
                double changedPrice = candle.getTradePrice().doubleValue() - dayAgo.getTradePrice().doubleValue();
                double changedRate = changedPrice / dayAgo.getTradePrice().doubleValue();

                holder.mCurrentPrice.setText(mNonZeroFormat.format(candle.getTradePrice().doubleValue()));
                holder.mChangedRate.setText(mPercentFormat.format(changedRate));
                holder.mChangedPrice.setText(mFormat.format(changedPrice));
            }

            if (mCorrelationResultSet.get(key) != null) {
                holder.mCorValue.setText(mPercentFormat.format(mCorrelationResultSet.get(key).getColValue()));
            }
        }

        @Override
        public int getItemCount() {
            return mCoinListInfo != null ? mCoinListInfo.size() : 0;
        }
    }

    private class CorrelationCoin implements Comparable<CorrelationCoin> {
        String marketId;
        Double colValue;

        public CorrelationCoin(String marketId, Double colValue) {
            this.marketId = marketId;
            this.colValue = colValue;
        }

        public String getMarketId() {
            return marketId;
        }

        public Double getColValue() {
            return colValue;
        }

        @Override
        public int compareTo(CorrelationCoin o) {
            if (this.colValue < o.getColValue()) {
                return 1;
            } else if (this.colValue > o.getColValue()) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}
