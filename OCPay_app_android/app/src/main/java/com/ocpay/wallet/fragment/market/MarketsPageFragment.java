package com.ocpay.wallet.fragment.market;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import com.ocpay.wallet.R;
import com.ocpay.wallet.activities.AccountSignInActivity;
import com.ocpay.wallet.activities.MainActivity;
import com.ocpay.wallet.activities.MarketSearchActivity;
import com.ocpay.wallet.adapter.MarketPageAdapter;
import com.ocpay.wallet.bean.MarketToken;
import com.ocpay.wallet.bean.response.MarketTokenResponse;
import com.ocpay.wallet.databinding.FragmentMarketsPageBinding;
import com.ocpay.wallet.fragment.BaseFragment;
import com.ocpay.wallet.http.ResponseUtils;
import com.ocpay.wallet.http.client.BlockAsiaClientIml;
import com.ocpay.wallet.http.rx.RxBus;
import com.snow.commonlibrary.recycleview.xrecyclerview.XRecyclerView;
import com.snow.commonlibrary.utils.StringUtil;
import com.snow.commonlibrary.utils.ViewUtils;

import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static com.ocpay.wallet.Constans.RXBUS.ACTION_SIGN_IN_UPDATE;
import static com.ocpay.wallet.Constans.RXBUS.RXBUS_MARKET_EXCHANGE;
import static com.ocpay.wallet.Constans.RXBUS.RXBUS_MARKET_FAVORITE;
import static com.ocpay.wallet.Constans.RXBUS.RXBUS_MARKET_FAVORITE_UPDATE;
import static com.ocpay.wallet.Constans.RXBUS.RXBUS_MARKET_SEARCH_EXCHANGE;
import static com.ocpay.wallet.Constans.RXBUS.RXBUS_MARKET_SEARCH_TOKEN;
import static com.ocpay.wallet.Constans.RXBUS.RXBUS_MARKET_TOKEN;
import static com.ocpay.wallet.Constans.RXBUS.RXBUS_MARKET_TOKEN_UPDATE;
import static com.ocpay.wallet.Constans.RXBUS.RXBUS_SORT_BAR_UPDATE;
import static com.ocpay.wallet.Constans.RXBUS.RXBUS_TOKEN_COLLECT;
import static com.ocpay.wallet.Constans.RXBUS.RXBUS_TOKEN_COLLECT_UPDATE;
import static com.ocpay.wallet.bean.MarketToken.TYPE_TOKEN;
import static com.ocpay.wallet.bean.MarketToken.TYPE_TOKEN_EXCHANGE;
import static com.ocpay.wallet.bean.MarketToken.TYPE_TOKEN_FAVORITE;
import static com.ocpay.wallet.bean.MarketToken.TYPE_TOKEN_LIST;
import static com.ocpay.wallet.http.ResponseUtils.needLogin;


/**
 * Created by y on 2018/8/4.
 */

public class MarketsPageFragment extends BaseFragment<FragmentMarketsPageBinding> implements View.OnClickListener {

    int keyMode;
    private MarketPageAdapter adapter;
    private MarketToken lastMarketToken;

    @Override
    public int setContentView() {
        return R.layout.fragment_markets_page;
    }

    @Override
    public void loadData() {

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initData();

        initView();

        initListener();

        initSearchMode();

        initMarketMode();

        initRxBus();

    }

    //market
    private void initMarketMode() {
        if (getActivity() instanceof MarketSearchActivity) return;
        getPullData();

    }

    private void initSearchMode() {
        if (!(getActivity() instanceof MarketSearchActivity)) return;


    }

    private void initRxBus() {
        initFavoriteRxBus();
        initMarketTokenRxBus();
        initMarketTokenSearchBus();
        initExchangeRxBus();
        initExchangeSearchBus();

        Disposable tokenCollect = RxBus.getInstance().toObservable(RXBUS_TOKEN_COLLECT, MarketTokenResponse.class)
                .subscribe(new Consumer<MarketTokenResponse>() {
                    @Override
                    public void accept(MarketTokenResponse marketTokenResponse) throws Exception {
                        dismissLoading();
                        boolean b = ResponseUtils.parseResponseCode(marketTokenResponse);
                        if (b) {
                            if (tempMarketToken == null) return;
                            //token  update
                            if (keyMode == RXBUS_MARKET_TOKEN) {
                                //change status
                                tempMarketToken.collect_status = changeStatus;
                                // update favorite
                                RxBus.getInstance().post(RXBUS_MARKET_FAVORITE_UPDATE, "");

                                if (getActivity() instanceof MarketSearchActivity) {
                                    RxBus.getInstance().post(RXBUS_MARKET_TOKEN_UPDATE, "");
                                }
                            }
                            // favorite
                            //add
                            if (keyMode == RXBUS_MARKET_FAVORITE) {
                                if (tempMarketToken.ID != null) {
                                    RxBus.getInstance().post(RXBUS_TOKEN_COLLECT_UPDATE, tempMarketToken.ID);
                                }
                                tempMarketToken.collect_status = "1".equals(tempMarketToken.collect_status) ? "0" : "1";
                                adapter.notifyDataSetChanged();
                                showMarketToken(adapter.getmData());
                                RxBus.getInstance().post(RXBUS_MARKET_TOKEN_UPDATE, "");
                            }

                            adapter.notifyDataSetChanged();
                            tempMarketToken = null;
                        }
                    }
                });


        addDisposable(tokenCollect);


    }

    private void initExchangeSearchBus() {

        if (keyMode != RXBUS_MARKET_EXCHANGE || (!(getActivity() instanceof MarketSearchActivity)))
            return;
        Disposable exchangeSearch = RxBus.getInstance().toObservable(RXBUS_MARKET_SEARCH_EXCHANGE, MarketTokenResponse.class)
                .subscribe(new Consumer<MarketTokenResponse>() {
                    @Override
                    public void accept(MarketTokenResponse marketTokenResponse) throws Exception {
                        if ((getActivity() instanceof MarketSearchActivity)) {
                            updateExchangeResult(marketTokenResponse);
                            showSearchResult(marketTokenResponse);
                        }
                    }
                });

        addDisposable(exchangeSearch);
    }

    private void initMarketTokenSearchBus() {

        if (keyMode != RXBUS_MARKET_TOKEN || (!(getActivity() instanceof MarketSearchActivity)))
            return;

        Disposable marketSearchToken = RxBus.getInstance().toObservable(RXBUS_MARKET_SEARCH_TOKEN, MarketTokenResponse.class)
                .subscribe(new Consumer<MarketTokenResponse>() {
                    @Override
                    public void accept(MarketTokenResponse marketTokenResponse) throws Exception {
                        showMarketTokenResult(marketTokenResponse);
                        showSearchResult(marketTokenResponse);

                    }
                });

        addDisposable(marketSearchToken);

    }

    private void initExchangeRxBus() {
        if (keyMode != RXBUS_MARKET_EXCHANGE || (!(getActivity() instanceof MainActivity)))
            return;
        Disposable tokenFavorite = RxBus.getInstance().toObservable(RXBUS_MARKET_EXCHANGE, MarketTokenResponse.class)
                .subscribe(new Consumer<MarketTokenResponse>() {
                    @Override
                    public void accept(MarketTokenResponse marketTokenResponse) throws Exception {
                        if ((getActivity() instanceof MainActivity)) {
                            updateExchangeResult(marketTokenResponse);
                        }
                    }
                });

        addDisposable(tokenFavorite);


    }

    private void updateExchangeResult(MarketTokenResponse marketTokenResponse) {
        showContentView();
        dismissLoading();
        bindingView.xrlContent.refreshComplete();
        boolean b = ResponseUtils.parseResponseCode(marketTokenResponse);
        if (b) {
            updateMarketToken(marketTokenResponse, TYPE_TOKEN_EXCHANGE, -1);
        } else {
            showErrorView();
        }
    }


    private void updateMarketToken(MarketTokenResponse marketTokenResponse, int typeTokenExchange, int childType) {
        if (marketTokenResponse != null && marketTokenResponse.data != null) {
            currentOrder = tempOrder;
            setType(marketTokenResponse.data, typeTokenExchange, childType);
            adapter.setData(marketTokenResponse.data);
            RxBus.getInstance().post(RXBUS_SORT_BAR_UPDATE, "");
        }
    }

    private void initFavoriteRxBus() {
        if (keyMode != RXBUS_MARKET_FAVORITE) return;
        Disposable tokenFavorite = RxBus.getInstance().toObservable(RXBUS_MARKET_FAVORITE, MarketTokenResponse.class)
                .subscribe(new Consumer<MarketTokenResponse>() {
                    @Override
                    public void accept(MarketTokenResponse marketTokenResponse) throws Exception {
                        showContentView();
                        dismissLoading();
                        bindingView.xrlContent.refreshComplete();
                        boolean b = ResponseUtils.parseResponseCode(marketTokenResponse);
                        if (b) {
                            if (showMarketToken(marketTokenResponse.data))
                                updateMarketToken(marketTokenResponse, TYPE_TOKEN_FAVORITE, -1);
                        } else {
                            if (needLogin(marketTokenResponse)) {
                                bindingView.rlLogin.setVisibility(View.VISIBLE);
                            } else {
                                showErrorView();
                            }
                        }
                    }
                });

        addDisposable(tokenFavorite);


        Disposable updateFavorite = RxBus.getInstance().toObservable(RXBUS_MARKET_FAVORITE_UPDATE, String.class)
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String marketTokenResponse) throws Exception {
                        getPullData();
                    }
                });

        addDisposable(updateFavorite);


        Disposable updateLocal = RxBus.getInstance().toObservable(ACTION_SIGN_IN_UPDATE, String.class)
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String loginResponse) throws Exception {
                        getPullData();
                        RxBus.getInstance().post(RXBUS_TOKEN_COLLECT_UPDATE, "");
                    }
                });

        addDisposable(updateLocal);


    }

    private boolean showMarketToken(List<MarketToken> data) {
        boolean hasData = (data == null || data.size() <= 0) ? false : true;
        int visibleNone = hasData ? View.GONE : View.VISIBLE;
        bindingView.rlLogin.setVisibility(View.GONE);
        bindingView.clSearchNone.setVisibility(visibleNone);

        if (getActivity() instanceof MainActivity) {
            ViewGroup.LayoutParams layoutParams = bindingView.ivSearchPic.getLayoutParams();
            layoutParams.width = ViewUtils.dp2px(getContext(), 70);
            layoutParams.height = ViewUtils.dp2px(getContext(), 70);
            bindingView.ivSearchPic.setLayoutParams(layoutParams);
            bindingView.ivSearchPic.setImageResource(R.mipmap.ic_no_record);
            bindingView.tvSearchTip.setText(getString(R.string.tip_no_data));
        }

        return hasData;
    }


    private void showMarketTokenResult(MarketTokenResponse marketTokenResponse) {
        showContentView();
        dismissLoading();
        bindingView.xrlContent.refreshComplete();
        boolean b = ResponseUtils.parseResponseCode(marketTokenResponse);
        if (b) {
            updateMarketToken(marketTokenResponse, TYPE_TOKEN, TYPE_TOKEN_LIST);
        } else {
            showErrorView();
        }
    }

    private void initMarketTokenRxBus() {
        if (keyMode != RXBUS_MARKET_TOKEN || (!(getActivity() instanceof MainActivity)))
            return;
        Disposable marketToken = RxBus.getInstance().toObservable(RXBUS_MARKET_TOKEN, MarketTokenResponse.class)
                .subscribe(new Consumer<MarketTokenResponse>() {
                    @Override
                    public void accept(MarketTokenResponse marketTokenResponse) throws Exception {
                        if ((getActivity() instanceof MainActivity)) {
                            showMarketTokenResult(marketTokenResponse);
                        }
                    }
                });

        addDisposable(marketToken);


        Disposable updateTokenCollect = RxBus.getInstance().toObservable(RXBUS_TOKEN_COLLECT_UPDATE, String.class)
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String tokenId) throws Exception {
                        if (StringUtil.isEmpty(tokenId) || adapter == null || adapter.getmData() == null)
                            return;
                        for (MarketToken marketToken : adapter.getmData()) {
                            if (tokenId.equals(marketToken.ID)) {
                                marketToken.collect_status = "0";
                                adapter.notifyDataSetChanged();
                                return;
                            }
                        }


                    }
                });

        addDisposable(updateTokenCollect);


        Disposable updateFavorite = RxBus.getInstance().toObservable(RXBUS_MARKET_TOKEN_UPDATE, String.class)
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String marketTokenResponse) throws Exception {
                        getPullData();
                    }
                });

        addDisposable(updateFavorite);

    }

    /**
     * 0 没结果      1 显示结果
     *
     * @param marketTokenResponse
     */
    private void showSearchResult(MarketTokenResponse marketTokenResponse) {

        int resultStatus = (marketTokenResponse == null || marketTokenResponse.data == null || marketTokenResponse.data.size() <= 0) ? 0 : 1;

//        RxBus.getInstance().post(RXBUS_MARKET_SEARCH_RESULT_STATUS, resultStatus);

        if (bindingView == null) return;

        int visibleNone = resultStatus == 0 ? View.VISIBLE : View.GONE;

        bindingView.clSearchNone.setVisibility(visibleNone);

    }


    private MarketToken tempMarketToken;
    private String changeStatus;

    private void initListener() {
        adapter.setOnMarketActionListen(new MarketPageAdapter.OnMarketActionListen() {

            @Override
            public void onCollect(int position, MarketToken token) {
                showLoading();
                tempMarketToken = token;
                changeStatus = "0".equals(token.collect_status) ? "1" : "0";
                String exchangeName = null;
                if (keyMode == RXBUS_MARKET_TOKEN) {
                    exchangeName = token.itemType != TYPE_TOKEN ? token.exchange_name : "";
                } else if (keyMode == RXBUS_MARKET_FAVORITE) {
                    exchangeName = token.type == 1 ? "" : token.exchange_name;
                }
                BlockAsiaClientIml.getTokenCollect(RXBUS_TOKEN_COLLECT, token.token, exchangeName);
            }

            @Override
            public void onExpand(int position, MarketToken token) {
                if (lastMarketToken != null && lastMarketToken.expand) {
                    lastMarketToken.expand = false;
                    adapter.notifyDataSetChanged();
                    return;
                }
                if (lastMarketToken != null) lastMarketToken.expand = false;
                lastMarketToken = token;
                token.expand = true;
                adapter.notifyDataSetChanged();
            }
        });

        bindingView.btnLogin.setOnClickListener(this);

    }

    private void initView() {
        bindingView.xrlContent.setPullRefreshEnabled((getActivity() instanceof MainActivity));
        bindingView.xrlContent.setLoadingMoreEnabled(false);
        bindingView.xrlContent.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MarketPageAdapter(getContext());
        bindingView.xrlContent.setAdapter(adapter);

        bindingView.xrlContent.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                getPullData(keyMode, currentOrder);
            }

            @Override
            public void onLoadMore() {
                bindingView.xrlContent.refreshComplete();

            }
        });

    }


    private void setType(List<MarketToken> lists, int parentType, int childType) {
        if (lists == null || lists.size() <= 0) return;
        for (int i = 0; i < lists.size(); i++) {
            lists.get(i).itemType = parentType;
            if (lists.get(i) == null || lists.get(i).child == null || lists.get(i).child.size() <= 0)
                continue;
            for (int j = 0; j < lists.get(i).child.size(); j++) {
                lists.get(i).child.get(j).itemType = childType;
            }
        }
    }


    private void initData() {
        keyMode = getArguments().getInt(KEY_MODE);
    }

    int currentOrder = 1;
    int tempOrder = 1;

    public void getPullData() {
        showAnimalLoading();
        getPullData(keyMode, currentOrder);
    }

    public void getPullData(int keyMode, int currentOrder) {
        BlockAsiaClientIml.getMarketToken(keyMode, currentOrder);
    }


    //1 - 2 - 3
    public void actionSort(int sortType) {
        if (getContext() == null) return;
        showLoading();
        switch (keyMode) {
            case RXBUS_MARKET_EXCHANGE:
            case RXBUS_MARKET_FAVORITE:
            case RXBUS_MARKET_TOKEN:
                if (sortType == 1) {
                    tempOrder = currentOrder == 1 ? 2 : 1;
                }
                if (sortType == 2) {
                    tempOrder = currentOrder == 3 ? 4 : 3;
                }
                if (sortType == 3) {
                    tempOrder = currentOrder == 5 ? 6 : 5;
                }
                break;
        }
        String lastSearchKey = null;
        if (getActivity() instanceof MarketSearchActivity) {
            lastSearchKey = ((MarketSearchActivity) getActivity()).getLastSearchKey();
        }
        int searchMode;
        boolean isSearch = getActivity() instanceof MarketSearchActivity;
        searchMode = isSearch && keyMode == RXBUS_MARKET_EXCHANGE ? RXBUS_MARKET_SEARCH_EXCHANGE : isSearch && keyMode == RXBUS_MARKET_TOKEN ? RXBUS_MARKET_SEARCH_TOKEN : keyMode;

        BlockAsiaClientIml.getMarketToken(searchMode, tempOrder, lastSearchKey);

    }


    public int getCurrentOrder() {
        return currentOrder;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                AccountSignInActivity.startAccountSignInActivity(getActivity());
                break;

        }
    }


}
