package com.ocpay.wallet.activities;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.ocpay.wallet.Constans;
import com.ocpay.wallet.MyApp;
import com.ocpay.wallet.R;
import com.ocpay.wallet.databinding.ActivityTransactionDetailBinding;
import com.ocpay.wallet.greendao.NotificationBean;
import com.ocpay.wallet.greendao.TransactionRecord;
import com.ocpay.wallet.greendao.manager.TransactionRecordDaoUtils;
import com.ocpay.wallet.http.client.EthScanHttpClientIml;
import com.ocpay.wallet.http.rx.RxBus;
import com.ocpay.wallet.utils.CommonUtils;
import com.ocpay.wallet.utils.eth.OCPWalletUtils;
import com.ocpay.wallet.utils.qr.QRCodeUtils;
import com.ocpay.wallet.bean.response.BaseTransaction;
import com.ocpay.wallet.bean.response.CustomTransaction;
import com.ocpay.wallet.bean.response.EthTransaction;
import com.ocpay.wallet.bean.response.EtherScanTxListResponse;
import com.ocpay.wallet.bean.response.EventTransaction;
import com.ocpay.wallet.widget.dialog.WarmDialog;
import com.snow.commonlibrary.utils.DateUtils;
import com.snow.commonlibrary.utils.ShareUtils;
import com.snow.commonlibrary.utils.StringUtil;
import com.snow.commonlibrary.widget.webview.WebViewActivity;

import java.text.ParseException;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static com.ocpay.wallet.Constans.CONFIG.NOTIFICATION;
import static com.ocpay.wallet.Constans.WALLET.TX_CUSTOM;
import static com.ocpay.wallet.Constans.WALLET.TX_ETH;
import static com.ocpay.wallet.Constans.WALLET.TX_EVENT_LOG;
import static com.ocpay.wallet.utils.TokenUtils.ETH;
import static com.snow.commonlibrary.utils.DateUtils.dmyhmsDatePattern;
import static com.snow.commonlibrary.utils.ShareUtils.toClipboardData;

/**
 * Created by y on 2018/4/20.
 */

public class TransactionDetailActivity extends BaseActivity implements View.OnClickListener, View.OnLongClickListener {

    private ActivityTransactionDetailBinding binding;
    private EventTransaction eventTransaction;
    private EthTransaction ethTransaction;
    private String txHash;
    private String txUrl;
    private CustomTransaction customTransaction;
    private String transceiver;
    private NotificationBean notificationBean;


    public static void startTxDetailActivity(Activity activity, BaseTransaction transaction) {

        Intent intent = new Intent(activity, TransactionDetailActivity.class);
        if (transaction instanceof EthTransaction) {
            intent.putExtra(TX_ETH, (EthTransaction) transaction);

        }
        if (transaction instanceof CustomTransaction) {
            intent.putExtra(TX_CUSTOM, (CustomTransaction) transaction);

        }
        if (transaction instanceof EventTransaction) {
            intent.putExtra(TX_EVENT_LOG, (EventTransaction) transaction);

        }

        activity.startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_transaction_detail);
        initData();
        initView();
        initListener();
        initQRCode();
        initRxBus();

    }

    private void initRxBus() {
        Disposable disposable = RxBus.getInstance().toObservable(Constans.RXBUS.ACTION_UPDATE_TX_DETAIL, EtherScanTxListResponse.class)
                .subscribe(new Consumer<EtherScanTxListResponse>() {
                    @Override
                    public void accept(EtherScanTxListResponse response) throws Exception {
                        dismissLoading();
                        if (response == null || response.getResult() == null || response.getResult().size() <= 0) {
                            WarmDialog.showTip(TransactionDetailActivity.this, "Get Transaction failed!");
                            return;
                        }

                        for (CustomTransaction transaction : response.getResult()) {
                            if (transaction.getHash().equals(notificationBean.getTxHash())) {
                                customTransaction = transaction;
                                initView();
                                initQRCode();
                                return;
                            }
                        }
                        WarmDialog.showTip(TransactionDetailActivity.this, "Get Transaction failed!");

                    }
                });

        addDisposable(disposable);


    }

    private void initQRCode() {
        if (StringUtil.isEmpty(txHash)) return;
        txUrl = Constans.HTTP.API_TXHAH + txHash;
        QRCodeUtils.updateQRCode(binding.ivQrCodeTxUrl, txUrl);
    }


    private void initData() {
        if (initNotificationBean()) return;
        eventTransaction = (EventTransaction) getIntent().getSerializableExtra(Constans.WALLET.TX_EVENT_LOG);
        ethTransaction = (EthTransaction) getIntent().getSerializableExtra(Constans.WALLET.TX_ETH);
        customTransaction = (CustomTransaction) getIntent().getSerializableExtra(Constans.WALLET.TX_CUSTOM);
    }

    private boolean initNotificationBean() {
        notificationBean = (NotificationBean) getIntent().getSerializableExtra(NOTIFICATION);
        if (notificationBean != null && !StringUtil.isEmpty(notificationBean.getTxHash()) && !StringUtil.isEmpty(notificationBean.getBlockNumber())) {
            //cancel notification
            NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(Integer.valueOf(notificationBean.getId() + ""));


            String transceiver = notificationBean.getTransceiver();
            if (!StringUtil.isEmpty(transceiver) && OCPWalletUtils.isEthAddress(transceiver)) {
                this.transceiver = transceiver;
            }
            //  load from green dao
            TransactionRecord transactionRecord = TransactionRecordDaoUtils.sqlByTxHash(MyApp.getContext(), notificationBean.getTxHash());
            if (transactionRecord == null) {
                pullTransaction();
                return true;
            }

            CustomTransaction customTransaction = CommonUtils.modelA2B(transactionRecord, CustomTransaction.class);
            if (customTransaction == null) {
                pullTransaction();
                return true;
            }
            customTransaction.setQuerier(transceiver);
            this.customTransaction = customTransaction;

            return true;
        }
        return false;
    }


    //  pull from network
    private void pullTransaction() {
        if (notificationBean == null)
            return;
        EthScanHttpClientIml.getTransactionList(Constans.RXBUS.ACTION_UPDATE_TX_DETAIL, transceiver, notificationBean.getBlockNumber(), notificationBean.getBlockNumber());
        showLoading(false);

    }

    private void initView() {
        initEventTransaction(eventTransaction);
        initEthTransaction(ethTransaction);
        initCustomTransaction(customTransaction);

    }


    private void initListener() {
        binding.tvClipTxUrl.setOnClickListener(this);
        binding.ivBack.setOnClickListener(this);
        binding.tvTransferTxhash.setOnClickListener(this);
        binding.tvTransferFrom.setOnLongClickListener(this);
        binding.tvTransferTo.setOnLongClickListener(this);

    }

    private void initEthTransaction(EthTransaction ethTransaction) {
        if (ethTransaction == null) return;
        txHash = ethTransaction.getHash();
    }

    private void initEventTransaction(EventTransaction eventTransaction) {
        if (eventTransaction == null) return;
        txHash = eventTransaction.getTransactionHash();
        binding.tvTransferAmount.setText(eventTransaction.getTransferAmount());
        binding.tvTransferFee.setText(eventTransaction.getFee().toString()+" "+ETH);
        binding.tvTransferFrom.setText(eventTransaction.getTransferFrom());
        binding.tvTransferTo.setText(eventTransaction.getTransferTo());
        binding.tvTransferTxhash.setText(eventTransaction.getTransactionHash());
        binding.tvTransferBlockHigh.setText(eventTransaction.getBlockNumber().toString());
        try {
            binding.tvTimestamp.setText(DateUtils.TimeStamp2Custom(Long.valueOf(eventTransaction.getTimeStamp()), dmyhmsDatePattern));
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private void initCustomTransaction(CustomTransaction customTransaction) {
        if (customTransaction == null) return;
        txHash = customTransaction.getHash();
        binding.tvTransferAmount.setText(customTransaction.getTransactionAmountString());
        binding.tvTransferFee.setText(customTransaction.getTransactionFee().toString());
        binding.tvTransferFrom.setText(customTransaction.getFrom());
        binding.tvTransferTo.setText(customTransaction.getTransferTo());
        binding.tvTransferTxhash.setText(customTransaction.getHash());
        binding.tvTransferBlockHigh.setText(customTransaction.getBlockNumber().toString());
        binding.civWalletIcon.setBackgroundResource(OCPWalletUtils.getWalletProfilePicture(customTransaction.getQuerier()));
        try {
            binding.tvTimestamp.setText(DateUtils.TimeStamp2Custom(Long.valueOf(customTransaction.getTimeStamp()), dmyhmsDatePattern));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_clip_tx_url:
                String tip = getResources().getString(R.string.address_copied_to_clipboard);
                ShareUtils.toClipboardData(this, "", txUrl, tip);
                break;
            case R.id.tv_transfer_txhash:
                WebViewActivity.loadUrl(this, txUrl, null);
                break;


        }

    }


    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.tv_transfer_from:
                toClipboardData(MyApp.getContext(), "", binding.tvTransferFrom.getText().toString(), getResources().getString(R.string.address_copied_to_clipboard));
                break;

            case R.id.tv_transfer_to:
                toClipboardData(MyApp.getContext(), "", binding.tvTransferTo.getText().toString(), getResources().getString(R.string.address_copied_to_clipboard));
                break;
        }

        return false;
    }
}
