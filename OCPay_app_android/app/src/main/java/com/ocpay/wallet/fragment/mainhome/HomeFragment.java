package com.ocpay.wallet.fragment.mainhome;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.ocpay.wallet.AnalyticsEvent;
import com.ocpay.wallet.Constans;
import com.ocpay.wallet.MyApp;
import com.ocpay.wallet.OCPWallet;
import com.ocpay.wallet.R;
import com.ocpay.wallet.activities.FirstLauncherActivity;
import com.ocpay.wallet.activities.GatheringActivity;
import com.ocpay.wallet.activities.MainActivity;
import com.ocpay.wallet.activities.QRReaderActivity;
import com.ocpay.wallet.activities.SendActivity;
import com.ocpay.wallet.activities.TokenTransactionsActivity;
import com.ocpay.wallet.activities.TransactionCenterActivity;
import com.ocpay.wallet.activities.WalletDetailActivity;
import com.ocpay.wallet.adapter.HomePageAdapter;
import com.ocpay.wallet.adapter.TokenBalanceAdapter;
import com.ocpay.wallet.bean.home.TokenBalanceBean;
import com.ocpay.wallet.databinding.FragmentHomeBinding;
import com.ocpay.wallet.fragment.BaseFragment;
import com.ocpay.wallet.greendao.WalletInfo;
import com.ocpay.wallet.greendao.manager.TokenBalanceDaoUtils;
import com.ocpay.wallet.http.client.DataBlockClientIml;
import com.ocpay.wallet.http.client.EthScanHttpClientIml;
import com.ocpay.wallet.http.client.OCPayHttpClientIml;
import com.ocpay.wallet.http.rx.RxBus;
import com.ocpay.wallet.manager.AnalyticsManager;
import com.ocpay.wallet.utils.BalanceUtils;
import com.ocpay.wallet.utils.OCPPrefUtils;
import com.ocpay.wallet.utils.RateUtils;
import com.ocpay.wallet.utils.eth.OCPWalletUtils;
import com.ocpay.wallet.bean.response.HomePageResponse;
import com.ocpay.wallet.bean.response.TokenBalanceResponse;
import com.snow.commonlibrary.recycleview.BaseAdapter;
import com.snow.commonlibrary.utils.CheckNetwork;
import com.snow.commonlibrary.utils.PrefUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static com.ocpay.wallet.Constans.CONFIG.HIDE_ASSET;
import static com.ocpay.wallet.activities.QRReaderActivity.QR_CODE_MODE_PARSE;
import static com.ocpay.wallet.utils.OCPPrefUtils.getLocalHomeBean;
import static com.ocpay.wallet.utils.OCPPrefUtils.setLocalHomeBean;
import static com.ocpay.wallet.utils.TokenUtils.ETH;
import static com.ocpay.wallet.utils.TokenUtils.OCN;
import static com.ocpay.wallet.utils.eth.OCPWalletUtils.foldWalletAddress;

public class HomeFragment extends BaseFragment<FragmentHomeBinding> implements View.OnClickListener {


    private WalletInfo walletInfo;
    private HomePageAdapter mainAdapter;
    private TokenBalanceAdapter tokenBalanceAdapter;
    private boolean isShowToken;
    private BigDecimal ocnBalance;
    private long lastUpdateTime = 0;

    @Override
    public int setContentView() {
        return R.layout.fragment_home;
    }

    @Override
    public void loadData() {
        walletInfo = OCPWallet.getCurrentWallet();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initRxBus();
        initListener();
        initToken();
        updateInfo(walletInfo);
        initHomePage();
        lastUpdateTime = System.currentTimeMillis();
    }

    private void initHomePage() {

        HomePageResponse localHomeBean = getLocalHomeBean();
        if (localHomeBean != null && localHomeBean.getData() != null) {
            mainAdapter.setData(localHomeBean.getData().homePageVos);
        }
        OCPayHttpClientIml.getHomePage(Constans.RXBUS.ACTION_HOME_PAGE_UPDATE);


    }

    private void initListener() {
        bindingView.includeHead.ivSlideMenu.setOnClickListener(this);
        bindingView.include.ivAddressQr.setOnClickListener(this);
        bindingView.include.llSend.setOnClickListener(this);
        bindingView.include.llScan.setOnClickListener(this);
        bindingView.include.llRecord.setOnClickListener(this);
        bindingView.include.ivReChange.setOnClickListener(this);
        getSwipeRefreshLayout().setEnabled(true);
        getSwipeRefreshLayout().setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                boolean networkConnected = CheckNetwork.isNetworkConnected(MyApp.getContext());
                boolean wifiConnected = CheckNetwork.isWifiConnected(MyApp.getContext());
                if (!networkConnected || !wifiConnected) {
                    getSwipeRefreshLayout().setRefreshing(false);
                    return;
                }
                updateInfo(OCPWallet.getCurrentWallet());
            }
        });
        bindingView.nvResultSafety.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (getSwipeRefreshLayout() != null) {
                    if (!getSwipeRefreshLayout().isRefreshing()) {
                        getSwipeRefreshLayout().setEnabled(bindingView.nvResultSafety.getScrollY() == 0);
                    }
                } else {
                    getSwipeRefreshLayout().setEnabled(true);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (System.currentTimeMillis() - lastUpdateTime < 1000 * 30) return;
        lastUpdateTime = System.currentTimeMillis();
        updateInfo(OCPWallet.getCurrentWallet());

    }

    private void initRxBus() {

        /** nav update **/
        Disposable disposable = RxBus.getInstance()
                .toObservable(Constans.RXBUS.ACTION_SELECT_WALLET, String.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String walletInfo) throws Exception {
                        if (OCPWallet.getCurrentWallet() == null) {
                            if (getActivity() != null) {
                                FirstLauncherActivity.startFirstLauncherActivity(getActivity());
                                getActivity().finish();
                            }
                            return;
                        }

                        updateInfo(OCPWallet.getCurrentWallet());
                        if (getActivity() instanceof MainActivity) {
                            ((MainActivity) getActivity()).updateNavList();
                        }
                    }
                });
        addDisposable(disposable);
        /** balance update **/
        Disposable tokenUpdate = RxBus.getInstance()
                .toObservable(Constans.RXBUS.ACTION_TOKEN_BALANCE_UPDATE, TokenBalanceResponse.class)
                .subscribe(new Consumer<TokenBalanceResponse>() {
                    @Override
                    public void accept(TokenBalanceResponse tokenBalanceResponse) throws Exception {
                        if (OCN.equals(tokenBalanceResponse.getTokenName())) {
                            ocnBalance = tokenBalanceResponse.getTokenBalance();
                        }
                        updateTokenAdapter(tokenBalanceResponse);
                    }
                });
        addDisposable(tokenUpdate);

        /** token price update **/
        Disposable tokenPrice = RxBus.getInstance()
                .toObservable(Constans.RXBUS.ACTION_TOKEN_PRICE_UPDATE, String.class)
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String tokenPriceResponse) throws Exception {
                        tokenBalanceAdapter.notifyDataSetChanged();

                    }
                });
        addDisposable(tokenPrice);

        /** home page update **/
        Disposable updateHomePage = RxBus.getInstance()
                .toObservable(Constans.RXBUS.ACTION_HOME_PAGE_UPDATE, HomePageResponse.class)
                .subscribe(new Consumer<HomePageResponse>() {
                    @Override
                    public void accept(HomePageResponse tokenPriceResponse) throws Exception {
                        if (tokenPriceResponse == null || tokenPriceResponse.getData() == null) {
                            Toast.makeText(getContext(), "get homepage error", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (tokenPriceResponse.getData().homePageVos != null) {
                            mainAdapter.setData(tokenPriceResponse.getData().homePageVos);
                            setLocalHomeBean(tokenPriceResponse);
                        }
                    }
                });
        addDisposable(updateHomePage);


    }


    private void updateTokenAdapter(TokenBalanceResponse tokenBalanceResponse) {
        getSwipeRefreshLayout().setRefreshing(false);
        if (!tokenBalanceResponse.getWalletAddress().equals(OCPWallet.getCurrentWallet().getWalletAddress()))
            return;
        if (tokenBalanceAdapter.getmData() == null || tokenBalanceAdapter.getmData().size() <= 0)
            return;
        for (int i = 0; i < tokenBalanceAdapter.getmData().size(); i++) {
            if (tokenBalanceResponse.getTokenName().equals(tokenBalanceAdapter.getmData().get(i).getTokenName())) {
                tokenBalanceAdapter.getmData().get(i).setTokenBalance(tokenBalanceResponse.getTokenBalance().toString());
                tokenBalanceAdapter.notifyDataSetChanged();
            }
        }
        updateAmount();
    }

    private void updateAmount() {
        BigDecimal totalToken = RateUtils.getTotalToken(tokenBalanceAdapter.getmData(), ETH);
        String estimateToken = RateUtils.estimateToken(ETH, totalToken);
        bindingView.include.tvTotalOcn.setText(BalanceUtils.decimalFormat(totalToken));
        bindingView.include.tvEstimateValue.setText(estimateToken);
        bindingView.include.tvTotalOcn.invalidate();
        bindingView.include.tvEstimateValue.invalidate();

        updateSize();
    }

    private void updateSize() {
        bindingView.include.tvTotalOcn.post(new Runnable() {
            @Override
            public void run() {
                if (bindingView.include.tvTotalOcn.getLineCount() > 1) {
                    float textSize = bindingView.include.tvTotalOcn.getTextSize();
                    bindingView.include.tvTotalOcn.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize / 1.18f);
                    bindingView.include.tvTotalOcn.invalidate();
                    updateSize();
                }
            }
        });

    }


    public void updateInfo(WalletInfo walletInfo) {
        if (!isAdded()) return;
        if (getSwipeRefreshLayout() != null && getSwipeRefreshLayout().isRefreshing()) {
            getSwipeRefreshLayout().setRefreshing(false);
        }
        getSwipeRefreshLayout().setRefreshing(true);
        if (walletInfo == null) return;
        //  update wallet info
        this.walletInfo = walletInfo;
        int visibleBackup = !this.walletInfo.isBackup() || walletInfo.getWalletType() == Constans.WALLET.WALLET_TYPE_WATCHING ? View.VISIBLE : View.GONE;
        int walletTip = walletInfo.getWalletType() == Constans.WALLET.WALLET_TYPE_WATCHING ? R.string.tip_watching : R.string.tip_please_backup;

        int tipBackground = walletInfo.getWalletType() == Constans.WALLET.WALLET_TYPE_WATCHING ? R.drawable.shape_stroke_corner_btn_white_r45 : R.drawable.shape_stroke_corner_btn_blue_r45;

        int tipColor = walletInfo.getWalletType() == Constans.WALLET.WALLET_TYPE_WATCHING ? R.color.white : R.color.blue_43addc;


        bindingView.include.tvTapBackUp.setText(walletTip);
        bindingView.include.tvTapBackUp.setBackgroundResource(tipBackground);
        bindingView.include.tvTapBackUp.setTextColor(getResources().getColor(tipColor));

        bindingView.include.tvTapBackUp.setVisibility(visibleBackup);
        bindingView.include.tvTapBackUp.setOnClickListener(this);
        bindingView.includeHead.tvWalletName.setText(walletInfo.getWalletName());
        bindingView.includeHead.rivHeadPortrait.setBackgroundResource(OCPWalletUtils.getWalletProfilePicture(walletInfo.getWalletAddress()));
        bindingView.include.tvWalletAddress.setText(foldWalletAddress(walletInfo.getWalletAddress()));
        //update  info
        getTokenBalance();
    }

    private void initView() {

        mainAdapter = new HomePageAdapter(getContext());
        RecyclerView.LayoutManager manager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        bindingView.rlView.setLayoutManager(manager);
        bindingView.rlView.setAdapter(mainAdapter);


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.iv_address_qr:
                AnalyticsManager.getInstance().sendEvent(AnalyticsEvent.home_button_QRcode);
                if (walletInfo.isBackup()) {
                    GatheringActivity.startGatheringActivity(getActivity(), ETH, walletInfo.getWalletAddress());
                } else {
                    WalletDetailActivity.startWalletDetailActivity(getActivity(), walletInfo.getWalletAddress(), true);
                }
                break;
            case R.id.iv_hide_asset:

                hideAsset();
                break;
            case R.id.iv_re_change:
                AnalyticsManager.getInstance().sendEvent(AnalyticsEvent.home_button_Switch);
                isShowToken = !isShowToken;
                int tbVisible = isShowToken ? View.GONE : View.VISIBLE;
                int tokenBoardVisible = isShowToken ? View.VISIBLE : View.GONE;
                bindingView.rlToken.setVisibility(tokenBoardVisible);
                bindingView.rlView.setVisibility(tbVisible);
                break;
            case R.id.ll_scan:
                AnalyticsManager.getInstance().sendEvent(AnalyticsEvent.home_button_Scan);
                QRReaderActivity.startQRReaderActivity(getActivity(), -1, QR_CODE_MODE_PARSE);
                break;
            case R.id.ll_send:
                AnalyticsManager.getInstance().sendEvent(AnalyticsEvent.home_button_Send);
                SendActivity.startSendActivity(getActivity(), ocnBalance, ETH);
                break;
            case R.id.ll_record:
                AnalyticsManager.getInstance().sendEvent(AnalyticsEvent.home_button_Record);
                TransactionCenterActivity.startContactsActivity(getActivity());
                break;
            case R.id.iv_slide_menu:
                AnalyticsManager.getInstance().sendEvent(AnalyticsEvent.home_button_Walletmanagement);
                RxBus.getInstance().post(Constans.RXBUS.ACTION_OPEN_DRAWER, 1);
                break;

            case R.id.tv_tap_back_up:
                WalletDetailActivity.startWalletDetailActivity(getActivity(), walletInfo.getWalletAddress(), true);
                break;

        }


    }

    private void hideAsset() {
        boolean isHide = PrefUtils.getBoolean(MyApp.getContext(), HIDE_ASSET, false);
        PrefUtils.putBoolean(MyApp.getContext(), HIDE_ASSET, !isHide);
    }


    public void initToken() {
        if (walletInfo == null) {
            walletInfo = OCPWallet.getCurrentWallet();
        }
        if (getContext() == null || walletInfo == null) return;

        tokenBalanceAdapter = new TokenBalanceAdapter(getContext());
        bindingView.rlToken.setLayoutManager(new LinearLayoutManager(getContext()));
        bindingView.rlToken.setAdapter(tokenBalanceAdapter);
        List<TokenBalanceBean> list = new ArrayList<>();
        BigDecimal ethBalance = TokenBalanceDaoUtils.getTokenBalance(MyApp.getContext(), walletInfo.getWalletAddress(), ETH);
        BigDecimal ocnBalance = TokenBalanceDaoUtils.getTokenBalance(MyApp.getContext(), walletInfo.getWalletAddress(), OCN);
        list.add(new TokenBalanceBean("https://resource.jinse.com/phenix/img/coin/ETH.png", ETH, ethBalance.toString()));
        list.add(new TokenBalanceBean("https://resource.jinse.com/phenix/img/coin/EOS.png", OCN, ocnBalance.toString()));
        tokenBalanceAdapter.setData(list);
        tokenBalanceAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView.Adapter adapter, Object data, int position) {
                if (data instanceof TokenBalanceBean) {
                    TokenTransactionsActivity.startTokenTransactionActivity(getActivity(), ((TokenBalanceBean) data).getTokenName(), walletInfo.getWalletAddress(), ((TokenBalanceBean) data).getTokenBalance());
                }

            }
        });
    }


    public void getTokenBalance() {


        for (TokenBalanceBean tb : tokenBalanceAdapter.getmData()) {
            BigDecimal tokenBalance = TokenBalanceDaoUtils.getTokenBalance(MyApp.getContext(), walletInfo.getWalletAddress(), tb.getTokenName());
            tb.setTokenBalance(tokenBalance.toString());

            EthScanHttpClientIml.getTokenBalanceOf(OCPWallet.getCurrentWallet().getWalletAddress(), tb.getTokenName());
            DataBlockClientIml.getTokenPrice(Constans.RXBUS.ACTION_TOKEN_PRICE_UPDATE, tb.getTokenName());
        }
        OCPPrefUtils.setLastTImeOfTokenPrice(System.currentTimeMillis());
    }

}
