package com.ocpay.wallet.activities;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ocpay.wallet.AnalyticsEvent;
import com.ocpay.wallet.Constans;
import com.ocpay.wallet.MyApp;
import com.ocpay.wallet.OCPWallet;
import com.ocpay.wallet.R;
import com.ocpay.wallet.adapter.TokenTransferAdapter;
import com.ocpay.wallet.adapter.WalletManageListsAdapter;
import com.ocpay.wallet.databinding.ActivityTokenDetailsBinding;
import com.ocpay.wallet.greendao.WalletInfo;
import com.ocpay.wallet.greendao.manager.WalletInfoDaoUtils;
import com.ocpay.wallet.http.client.EthScanHttpClientIml;
import com.ocpay.wallet.http.client.HttpClient;
import com.ocpay.wallet.http.rx.RxBus;
import com.ocpay.wallet.manager.AnalyticsManager;
import com.ocpay.wallet.utils.BalanceUtils;
import com.ocpay.wallet.utils.CharDataHelper;
import com.ocpay.wallet.utils.OCPPrefUtils;
import com.ocpay.wallet.utils.RateUtils;
import com.ocpay.wallet.utils.TokenUtils;
import com.ocpay.wallet.bean.response.BaseTransaction;
import com.ocpay.wallet.bean.response.CustomTransaction;
import com.ocpay.wallet.bean.response.EtherScanTxListResponse;
import com.ocpay.wallet.bean.response.EventLogTransactionResponse;
import com.ocpay.wallet.utils.TransactionUtils;
import com.snow.commonlibrary.log.MyLog;
import com.snow.commonlibrary.recycleview.BaseAdapter;
import com.snow.commonlibrary.utils.StringUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

import static com.ocpay.wallet.Constans.RXBUS.ACTION_TRANSACTION_SINGLE_TOKEN;
import static com.ocpay.wallet.Constans.ETH_ADDRESS.OCN_TOKEN_ADDRESS;
import static com.ocpay.wallet.Constans.WALLET.TOKEN_BALANCE;
import static com.ocpay.wallet.Constans.WALLET.TOKEN_NAME;
import static com.ocpay.wallet.Constans.WALLET.WALLET_ADDRESS;
import static com.ocpay.wallet.utils.TokenUtils.ETH;

public class TokenTransactionsActivity extends BaseActivity implements View.OnClickListener, BaseAdapter.OnItemClickListener {


    private ActivityTokenDetailsBinding binding;
    private WalletManageListsAdapter manageListsAdapter;
    private String tokenName;
    private TokenTransferAdapter transferAdapter;
    private BigDecimal tokenBalance = new BigDecimal(0);
    private LineChartData data;
    private LineChartView chart;
    private double top;
    private WalletInfo walletInfo;
    private String walletAddress;

    public static void startTokenTransactionActivity(Activity activity, String tokenName, String walletAddress, String tokenBalance) {
        Intent intent = new Intent(activity, TokenTransactionsActivity.class);
        intent.putExtra(TOKEN_NAME, tokenName);
        intent.putExtra(TOKEN_BALANCE, tokenBalance);
        intent.putExtra(WALLET_ADDRESS, walletAddress);
        activity.startActivity(intent);

    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(TokenTransactionsActivity.this, R.layout.activity_token_details);
        initActionBar();
        showLoading(false);
        initData();
        initView();
        initRxBus();
        initListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        pullData();

    }

    private void initListener() {
        binding.includeBottomButton.rlImportWallet.setOnClickListener(this);
        binding.includeBottomButton.rlCreateWallet.setOnClickListener(this);

        binding.refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullData();
            }
        });

    }

    private void initData() {

        String strTokenBalance = getIntent().getStringExtra(TOKEN_BALANCE);
        if (strTokenBalance != null) {
            tokenBalance = new BigDecimal(strTokenBalance);
        }

        walletAddress = getIntent().getStringExtra(WALLET_ADDRESS);
        if (StringUtil.isEmpty(walletAddress)) return;

        WalletInfo walletInfo = WalletInfoDaoUtils.sqlByAddress(MyApp.getContext(), walletAddress);
        if (walletInfo != null) {
            this.walletInfo = walletInfo;
        }


    }


    private void initActionBar() {
        tokenName = getIntent().getStringExtra(TOKEN_NAME);
        binding.tvTitle.setText(tokenName);
        binding.ivBack.setOnClickListener(this);
    }

    private void initView() {
        transferAdapter = new TokenTransferAdapter(this, TokenTransferAdapter.TYPE.SINGLE);
        binding.rlTokenTransactions.setLayoutManager(new LinearLayoutManager(TokenTransactionsActivity.this));
        binding.rlTokenTransactions.setAdapter(transferAdapter);
        transferAdapter.setOnItemClickListener(this);
        binding.tvTokenBalance.setText(BalanceUtils.decimalFormat(tokenBalance));
        String estimateToken = RateUtils.estimateToken(tokenName, tokenBalance);
        binding.tvTokenValue.setText(estimateToken);
        chart = binding.chart;
        chart.setLineChartData(new LineChartData());

        //init bottom
        binding.includeBottomButton.tvBottomCreateWallet.setText(R.string.btn_bottom_send);
        binding.includeBottomButton.tvBottomImportWallet.setText(R.string.btn_bottom_receive);
        binding.includeBottomButton.ivBottomFirst.setImageResource(R.mipmap.ic_send_round);
        binding.includeBottomButton.ivBottomTwo.setImageResource(R.mipmap.ic_receive);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_create_wallet:
                String eventSend = ETH.equals(tokenName) ? AnalyticsEvent.ETH_button_Send : AnalyticsEvent.OCN_button_Send;
                AnalyticsManager.getInstance().sendEvent(eventSend);
                SendActivity.startSendActivity(this, tokenBalance, tokenName);
                break;
            case R.id.rl_import_wallet:
                String eventReceive = ETH.equals(tokenName) ? AnalyticsEvent.ETH_button_Receive : AnalyticsEvent.OCN_button_Receive;
                AnalyticsManager.getInstance().sendEvent(eventReceive);
                GatheringActivity.startGatheringActivity(this, tokenName, walletAddress);
                break;
        }
    }


    public void getTokenTrList() {
        String walletAddress = OCPWallet.getCurrentWallet().getWalletAddress();
        HttpClient.Builder
                .getEthScanServer()
                .getTokenTransactionList(OCN_TOKEN_ADDRESS, walletAddress, walletAddress, "1000", "last")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Observer<EventLogTransactionResponse>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                MyLog.i("onSubscribe");
                            }

                            @Override
                            public void onNext(EventLogTransactionResponse o) {
                                RxBus.getInstance().post(Constans.RXBUS.ACTION_UPDATE_TRANSACTION_LIST, o);
                                MyLog.i("onNext" + o.toString());

                            }

                            @Override
                            public void onError(Throwable e) {
                                MyLog.i("onError" + e.getMessage());

                            }

                            @Override
                            public void onComplete() {
                                MyLog.i("onComplete");

                            }
                        }
                );
    }

    @Override
    public void onItemClicked(RecyclerView.Adapter adapter, Object data, int position) {

        TransactionDetailActivity.startTxDetailActivity(this, (BaseTransaction) data);
    }


    private void pullData() {


        String startBlockNo = OCPPrefUtils.getFirstStartBlockNo();
        EthScanHttpClientIml.getTransactionList(ACTION_TRANSACTION_SINGLE_TOKEN, walletAddress, startBlockNo, Constans.ETH.DEFAULT_END_BLOCKNO);
    }

    private void initRxBus() {
        Disposable disposable = RxBus.getInstance().toObservable(ACTION_TRANSACTION_SINGLE_TOKEN, EtherScanTxListResponse.class).subscribe(new Consumer<EtherScanTxListResponse>() {

            @Override
            public void accept(EtherScanTxListResponse o) throws Exception {
                List transactions = filterTokenTransaction(tokenName, o.getResult());

                TransactionUtils.mergeTransaction(transactions, walletAddress, tokenName);

                transferAdapter.setData(transactions);


                generateData(tokenBalance, transactions);
                resetViewport();
                dismissLoading();
                if (binding.refresh.isRefreshing()) {
                    binding.refresh.setRefreshing(false);
                }
            }


        });
        addDisposable(disposable);

    }


    //filter eth or token transaction
    private List<CustomTransaction> filterTokenTransaction(String tokenName, List<CustomTransaction> result) {
        List<CustomTransaction> list = new ArrayList<>();
        String erc20Address = "";
        if (!ETH.equals(tokenName)) {
            erc20Address = TokenUtils.getTokenMap().get(tokenName);
        }

        if (result == null || result.size() <= 0) return list;
        for (CustomTransaction customTransaction : result) {
            if (ETH.equals(tokenName)) {
                if (customTransaction.isEthTransaction()) {
                    list.add(customTransaction);
                }
            } else {
                if (erc20Address.equals(customTransaction.getTo())) {
                    list.add(customTransaction);
                }

            }
        }
        return list;
    }


    private void generateData(BigDecimal currentAmount, List<CustomTransaction> transactions) {

        List<Line> lines = new ArrayList<Line>();

        List<PointValue> values = new ArrayList<PointValue>();

        List<Float> y = CharDataHelper.getY(currentAmount.setScale(3, BigDecimal.ROUND_DOWN).floatValue(), transactions);

        top = CharDataHelper.getMaxValue(y) * 1.1;
        for (int j = 0; j < 7; ++j) {
            values.add(new PointValue(j, y.get(j)));
        }

        Line line = new Line(values);
        line.setColor(Color.parseColor("#2870C3"));
        line.setShape(ValueShape.CIRCLE);
        // cubic
        line.setCubic(false);
        //area
        line.setFilled(true);
        //value labels
        line.setHasLabels(false);

        line.setHasLabelsOnlyForSelected(true);
        //Link line
        line.setHasLines(true);
        //show point
        line.setHasPoints(false);

        line.setStrokeWidth(1);


//        line.setHasGradientToTransparent(hasGradientToTransparent);
//        if (pointsHaveDifferentColor) {
//            line.setPointColor(ChartUtils.COLORS[(i + 1) % ChartUtils.COLORS.length]);
//        }

        lines.add(line);


        data = new LineChartData(lines);

        List<String> x = CharDataHelper.getXMore();

        List<AxisValue> axisValues = new ArrayList<>();
        for (int i = 0; i < x.size(); i++) {
            axisValues.add(new AxisValue(i, x.get(i).toCharArray()));
        }

        if (true) {
            Axis axisX = new Axis().setHasLines(true);
            Axis axisY = new Axis();
//            axisX.setName("Axis X");
//            axisY.setName("Axis Y");
//            axisX.setValues(new ArrayList<AxisValue>())
            axisX.setValues(axisValues);
            axisX.setMaxLabelChars(5);
            data.setAxisXBottom(axisX);
            data.setAxisYLeft(axisY);
        } else {
            data.setAxisXBottom(null);
            data.setAxisYLeft(null);
        }

//        data.setBaseValue(Float.NEGATIVE_INFINITY);
        chart.setLineChartData(data);


    }

    private void resetViewport() {
        // Reset viewport height range to (0,100)
        if (top == 0) top = 1;
        final Viewport v = new Viewport(chart.getMaximumViewport());
        v.bottom = 0;
        v.top = (float) this.top;
        v.left = 0;
        v.right = 7;
        chart.setMaximumViewport(v);
        chart.setCurrentViewport(v);
    }


}
