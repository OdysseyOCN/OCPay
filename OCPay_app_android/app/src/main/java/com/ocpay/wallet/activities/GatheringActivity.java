package com.ocpay.wallet.activities;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.ocpay.wallet.Constans;
import com.ocpay.wallet.MyApp;
import com.ocpay.wallet.R;
import com.ocpay.wallet.bean.QRCodeBean;
import com.ocpay.wallet.bean.QRCodeTransaction;
import com.ocpay.wallet.databinding.ActivityGatheringBinding;
import com.ocpay.wallet.utils.CommonUtils;
import com.ocpay.wallet.utils.eth.OCPWalletUtils;
import com.ocpay.wallet.utils.eth.util.bip44.Base64;
import com.ocpay.wallet.utils.qr.QRCodeUtils;
import com.ocpay.wallet.widget.BalanceEditText;
import com.snow.commonlibrary.log.MyLog;
import com.snow.commonlibrary.utils.RegularExpressionUtils;

import static com.ocpay.wallet.Constans.REGULAR.REGULAR_FLOAT;
import static com.ocpay.wallet.Constans.WALLET.TOKEN_NAME;
import static com.ocpay.wallet.Constans.WALLET.WALLET_ADDRESS;
import static com.snow.commonlibrary.utils.ShareUtils.toClipboardData;
import static com.snow.commonlibrary.utils.ShareUtils.toShare;

/**
 * Created by y on 2018/4/20.
 */

public class GatheringActivity extends BaseActivity implements View.OnClickListener {

    private ActivityGatheringBinding shareBinding;

    private String tokenName;
    private String walletAddress;
    private Handler handler;


    public static void startGatheringActivity(Activity activity, String tokenName, String address) {
        Intent intent = new Intent(activity, GatheringActivity.class);
        intent.putExtra(TOKEN_NAME, tokenName);
        intent.putExtra(WALLET_ADDRESS, address);
        activity.startActivity(intent);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shareBinding = DataBindingUtil.setContentView(this, R.layout.activity_gathering);

        initView();


        initQRCode();
    }

    private void initQRCode() {
        handler = new Handler();
        shareBinding.etAmount.setAfterEditListen(new BalanceEditText.OnAfterEditListen() {
            @Override
            public void afterEdit() {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        String amount = shareBinding.etAmount.getExactAmount().toString();
                        if (TextUtils.isEmpty(amount)) return;
                        MyLog.i("amount:" + amount);
                        boolean valid = RegularExpressionUtils.valid(amount, REGULAR_FLOAT);
                        if (!valid) {
                            shareBinding.etAmount.setText("");
                            Toast.makeText(MyApp.getContext(), "Input ErrorInfo", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String jsonQRCode = getQRCodeAboutReceive(amount);
                        updateQRCode(jsonQRCode);
                    }
                };
                handler.postDelayed(runnable, 1000);
            }
        });
        updateQRCode(getQRCodeAboutReceive("0"));
    }

    private String getQRCodeAboutReceive(String amount) {
        QRCodeBean qrCodeBean = new QRCodeBean();
        qrCodeBean.setEthereum(walletAddress);
        qrCodeBean.setMode(Constans.QRCODE.MODE_TRANSACTION_RECEIVE);
        QRCodeTransaction tx = new QRCodeTransaction();
        tx.setAmount(amount);
        tx.setTransactionTo(walletAddress);
        tx.setTokenName(tokenName);
        String jsonData = CommonUtils.Object2Json(tx);
        qrCodeBean.setData(jsonData);
        return CommonUtils.Object2Json(qrCodeBean);
    }

    private void initView() {
        tokenName = getIntent().getStringExtra(TOKEN_NAME);
        walletAddress = getIntent().getStringExtra(WALLET_ADDRESS);
        shareBinding.tvWalletAddress.setText(OCPWalletUtils.foldWalletAddress(walletAddress));
        shareBinding.includeActionBar.ivBack.setOnClickListener(this);
        shareBinding.includeActionBar.actionBarTitle.setText(R.string.activity_qr_code);
        shareBinding.includeActionBar.toolbarMenuIcon.setImageResource(R.mipmap.ic_share);
        shareBinding.includeActionBar.toolbarMenuIcon.setOnClickListener(this);
        shareBinding.btnAddressToClipboard.setOnClickListener(this);
        shareBinding.ivHead.setBackgroundResource(OCPWalletUtils.getWalletProfilePicture(walletAddress));
    }

    private void updateQRCode(String code) {
        QRCodeUtils.updateQRCode(shareBinding.qrCode, 260, code);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_address_to_clipboard:
                addressToClipboard();
                break;
            case R.id.iv_back:
                finish();
                break;

            case R.id.toolbar_menu_icon:
                String title = getResources().getString(R.string.activity_qr_code);
                toShare(GatheringActivity.this, walletAddress, title);
                break;
        }
    }


    private void addressToClipboard() {
        String tip = getResources().getString(R.string.address_copied_to_clipboard);
        toClipboardData(MyApp.getContext(), "", walletAddress, tip);
    }
}
