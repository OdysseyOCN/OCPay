package com.ocpay.wallet.fragment.mainhome;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.ocpay.wallet.R;
import com.ocpay.wallet.activities.MarketSearchActivity;
import com.ocpay.wallet.databinding.FragmentMarketsBinding;
import com.ocpay.wallet.fragment.BaseFragment;
import com.ocpay.wallet.fragment.fragmentadapter.MyFragmentAdapter;
import com.ocpay.wallet.fragment.market.MarketsPageFragment;
import com.ocpay.wallet.http.client.BlockAsiaClientIml;
import com.ocpay.wallet.http.rx.RxBus;
import com.snow.commonlibrary.log.MyLog;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static com.ocpay.wallet.Constans.RXBUS.RXBUS_MARKET_EXCHANGE;
import static com.ocpay.wallet.Constans.RXBUS.RXBUS_MARKET_FAVORITE;
import static com.ocpay.wallet.Constans.RXBUS.RXBUS_MARKET_SEARCH_EXCHANGE;
import static com.ocpay.wallet.Constans.RXBUS.RXBUS_MARKET_SEARCH_TOKEN;
import static com.ocpay.wallet.Constans.RXBUS.RXBUS_MARKET_TOKEN;
import static com.ocpay.wallet.Constans.RXBUS.RXBUS_SORT_BAR_UPDATE;
import static com.ocpay.wallet.bean.MarketToken.TYPE_TOKEN;
import static com.ocpay.wallet.bean.MarketToken.TYPE_TOKEN_EXCHANGE;
import static com.ocpay.wallet.bean.MarketToken.TYPE_TOKEN_FAVORITE;

public class MarketsFragment extends BaseFragment<FragmentMarketsBinding> implements View.OnClickListener {

    private List<BaseFragment> fragments;
    private List<String> titles;
    private int currentPosition = 0;
    private static final String SEARCH_TAG = "search";
    private boolean isSearch;

    @Override
    public int setContentView() {
        return R.layout.fragment_markets;
    }

    @Override
    public void loadData() {

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MyLog.i("TAG ==>" + getTag());
        isSearch = SEARCH_TAG.equals(getTag());
        initView();
        initNews();
        initFragment();
        initListener();
        initRxBus();
    }

    private void initView() {
        if (isSearch) bindingView.clActionBa.setVisibility(View.GONE);

    }

    private void initRxBus() {
        Disposable sortBarDisposable = RxBus.getInstance().toObservable(RXBUS_SORT_BAR_UPDATE, String.class)
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        if (getActivity() == null) return;
                        updateSortBar(currentPosition);
                    }
                });
        addDisposable(sortBarDisposable);
    }

    private void initListener() {

        bindingView.vpHome.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                updateSortBar(position);
                if (getActivity() instanceof MarketSearchActivity) {
                    String lastKey = ((MarketSearchActivity) getActivity()).getLastSearchKey();
                    if (currentPosition == 0) {
                        BlockAsiaClientIml.getMarketToken(RXBUS_MARKET_SEARCH_TOKEN, lastKey);
                    } else {
                        BlockAsiaClientIml.getMarketToken(RXBUS_MARKET_SEARCH_EXCHANGE, lastKey);
                    }

                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        bindingView.xtvChange.setOnClickListener(this);
        bindingView.xtvLastPrice.setOnClickListener(this);
        bindingView.xtvTokenValue.setOnClickListener(this);
        bindingView.ivSearch.setOnClickListener(this);

        bindingView.ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MarketSearchActivity.startArticleDetail(getContext());

            }
        });

        bindingView.tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MarketSearchActivity.startArticleDetail(getContext());

            }
        });


    }

    private void updateSortBar(int position) {
        Fragment item = myAdapter.getItem(position);
        if (item instanceof MarketsPageFragment) {
            boolean isSearch = getActivity() instanceof MarketSearchActivity;
            int itemType;
            if (isSearch) {
                itemType = position == 0 ? TYPE_TOKEN : TYPE_TOKEN_EXCHANGE;
            } else {
                itemType = position == 0 ? TYPE_TOKEN_FAVORITE : position == 1 ? TYPE_TOKEN : TYPE_TOKEN_EXCHANGE;
            }
            changeSortBarStatus(itemType, ((MarketsPageFragment) item).getCurrentOrder());
        }
    }


    /**
     * Home fragment
     */
    private void initNews() {


        fragments = new ArrayList<>();
        titles = new ArrayList<>();
        if (!isSearch) titles.add(getString(R.string.fg_market_favorite));
        titles.add(getString(R.string.fg_market_token));
        titles.add(getString(R.string.fg_market_exchange));
        if (!isSearch)
            fragments.add(MarketsPageFragment.instance(getContext(), RXBUS_MARKET_FAVORITE, MarketsPageFragment.class));
        fragments.add(MarketsPageFragment.instance(getContext(), RXBUS_MARKET_TOKEN, MarketsPageFragment.class));
        fragments.add(MarketsPageFragment.instance(getContext(), RXBUS_MARKET_EXCHANGE, MarketsPageFragment.class));
    }

    MyFragmentAdapter myAdapter;

    private void initFragment() {
        myAdapter = new MyFragmentAdapter(getChildFragmentManager(), fragments, titles);
        bindingView.vpHome.setCanScroll(true);

        bindingView.vpHome.setScrollAni(false);

        bindingView.vpHome.setAdapter(myAdapter);
        // 左右预加载页面的个数
        bindingView.vpHome.setOffscreenPageLimit(2);
        myAdapter.notifyDataSetChanged();
//        bindingView.tabHome.setTabMode(XTabLayout.MODE_SCROLLABLE);
        bindingView.tabHome.setupWithViewPager(bindingView.vpHome);
    }


    private void changeSortBarStatus(int tabType, int sortType) {
        bindingView.xtvTokenValue.setClickable(tabType != TYPE_TOKEN_EXCHANGE);
        int tab1 = R.string.title_token_value;
        int tab2 = R.string.title_last_price_volume;
        int tab3 = R.string.market_property_change_24h;

        switch (tabType) {
            case TYPE_TOKEN_EXCHANGE:
                tab1 = R.string.title_name;
                tab2 = R.string.title_token_pair;
                tab3 = R.string.market_property_volume;
                break;
            case TYPE_TOKEN_FAVORITE:
            case TYPE_TOKEN:
                tab1 = R.string.title_token_value;
                tab2 = R.string.title_last_price_volume;
                tab3 = R.string.market_property_change_24h;
                break;
        }

        bindingView.xtvTokenValue.setText(getString(tab1));
        bindingView.xtvLastPrice.setText(getString(tab2));
        bindingView.xtvChange.setText(getString(tab3));

        bindingView.xtvTokenValue.setRightDrawable(tabType == TYPE_TOKEN_EXCHANGE ? null : getResources().getDrawable(R.drawable.ic_sort_default));
        bindingView.xtvLastPrice.setRightDrawable(getResources().getDrawable(R.drawable.ic_sort_default));
        bindingView.xtvChange.setRightDrawable(getResources().getDrawable(R.drawable.ic_sort_default));

        //默认值1 最新价降序 2最新价升序 3 量降序 4量升序 5涨跌降序 6涨跌升序
        //1 交易量降序 2 交易量升序  3 交易对降序 4 交易所升序
        switch (sortType) {
            case 1:
            case 2:
                int re1 = sortType == 1 ? R.drawable.ic_sort_desc : R.drawable.ic_sort_asc;
                bindingView.xtvTokenValue.setRightDrawable(getResources().getDrawable(re1));
                break;

            case 4:
            case 3:
                int re2 = sortType == 3 ? R.drawable.ic_sort_desc : R.drawable.ic_sort_asc;
                bindingView.xtvLastPrice.setRightDrawable(getResources().getDrawable(re2));
                break;
            case 6:
            case 5:
                int re3 = sortType == 5 ? R.drawable.ic_sort_desc : R.drawable.ic_sort_asc;
                bindingView.xtvChange.setRightDrawable(getResources().getDrawable(re3));
                break;

        }

        if (tabType == TYPE_TOKEN_EXCHANGE) {
            bindingView.xtvTokenValue.setRightDrawable(null);
        }


        bindingView.xtvTokenValue.postInvalidate();
        bindingView.xtvLastPrice.postInvalidate();
        bindingView.xtvChange.postInvalidate();


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.xtv_token_value:
                updateSort(1);
                break;
            case R.id.xtv_last_price:
                updateSort(2);
                break;
            case R.id.xtv_change:
                updateSort(3);
                break;
            case R.id.iv_search:
                MarketSearchActivity.startArticleDetail(getContext());
                break;

        }
    }

    private void updateSort(int sortType) {
        Fragment item = myAdapter.getItem(currentPosition);
        if (item != null && item instanceof MarketsPageFragment) {
            ((MarketsPageFragment) item).actionSort(sortType);
        }
    }


    public int getCurrentPosition() {
        return currentPosition;
    }
}
