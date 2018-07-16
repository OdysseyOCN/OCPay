package com.ocpay.wallet.activities;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.ocpay.wallet.Constans;
import com.ocpay.wallet.OCPWallet;
import com.ocpay.wallet.R;
import com.ocpay.wallet.bean.QRCodeBean;
import com.ocpay.wallet.bean.QRCodeTransaction;
import com.ocpay.wallet.bean.StatusResponse;
import com.ocpay.wallet.databinding.ActivityPreviewTransactionBinding;
import com.ocpay.wallet.utils.BalanceUtils;
import com.ocpay.wallet.utils.CommonUtils;
import com.ocpay.wallet.utils.WalletVerifyHelp;
import com.ocpay.wallet.utils.eth.OCPWalletUtils;
import com.ocpay.wallet.widget.dialog.PasswordConfirmDialog;
import com.ocpay.wallet.widget.dialog.QRCodeSignDialog;
import com.ocpay.wallet.widget.dialog.WarmDialog;
import com.snow.commonlibrary.utils.StringUtil;

import org.web3j.crypto.ECKeyPair;

import java.math.BigDecimal;
import java.math.BigInteger;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;

import static com.ocpay.wallet.Constans.QRCODE.QRCODE_BEAN;
import static com.ocpay.wallet.widget.dialog.QRCodeSignDialog.MODE_SIGN_SUCCESSFUL;

/**
 * Created by y on 2018/4/20.
 */

public class TransactionPreviewActivity extends BaseActivity implements View.OnClickListener {

    private ActivityPreviewTransactionBinding binding;


    private QRCodeBean qrCodeBean;
    private QRCodeTransaction qrCodeTransaction;


    public static void startTransactionPreviewActivity(Activity activity, QRCodeBean qrCodeBean) {
        Intent intent = new Intent(activity, TransactionPreviewActivity.class);
        intent.putExtra(QRCODE_BEAN, qrCodeBean);
        activity.startActivity(intent);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_preview_transaction);

        initView();
        initViewData();


    }

    private void initViewData() {
        if (qrCodeTransaction == null) return;
        binding.tvTokenName.setText(qrCodeTransaction.getTokenName());
        binding.tvTxTo.setText(qrCodeTransaction.getTransactionTo());
        binding.tvTxFrom.setText(qrCodeTransaction.getTransactionFrom());
        binding.tvOrderAct.setText(qrCodeTransaction.getTxAction());
        binding.tvTransferAmount.setText(BalanceUtils.decimalFormat(new BigDecimal(qrCodeTransaction.getAmount())));
        BigDecimal eth = OCPWalletUtils.getTransactionFee_W(new BigDecimal(qrCodeTransaction.getGasPrice()), new BigDecimal(qrCodeTransaction.getGasLimit()));
        binding.tvTxFee.setText(eth + " ETH");
    }


    private void initView() {
        boolean parse = false;
        try {
            qrCodeBean = (QRCodeBean) getIntent().getSerializableExtra(QRCODE_BEAN);
            if (qrCodeBean == null) {
                WarmDialog.showTip(this, "Parse Transaction fail");
                return;
            }
            qrCodeTransaction = qrCodeBean.getTransaction();

            if (qrCodeTransaction == null) {
                WarmDialog.showTip(this, "Parse Transaction fail");
            }

        } catch (Exception e) {
            e.printStackTrace();
            WarmDialog.showTip(this, "Parse Transaction fail");

        }
        binding.includeActionBar.ivBack.setOnClickListener(this);
        binding.includeActionBar.actionBarTitle.setText(R.string.activity_title_preview_transfer);
        binding.includeActionBar.toolbarMenuIcon.setVisibility(View.GONE);
        binding.tvActionSign.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_action_sign:
                showPasswordDialog();

                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

    private void showPasswordDialog() {
        PasswordConfirmDialog passwordConfirmDialog = PasswordConfirmDialog.getInstance(this);
        passwordConfirmDialog.setListener(new PasswordConfirmDialog.ConfirmListener() {
            @Override
            public void onConfirm(String pwd) {
                sign(pwd);
            }
        });
        passwordConfirmDialog.show();
    }

    private void sign(String pwd) {
        WalletVerifyHelp walletVerifyHelp = new WalletVerifyHelp() {
            @Override
            public void showLoading() {
                TransactionPreviewActivity.this.showLoading(false);
            }

            @Override
            public void getPrivateKey(ECKeyPair keyPair) {
                Observable.just(keyPair)
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(new Function<ECKeyPair, Object>() {
                            @Override
                            public Object apply(ECKeyPair ecKeyPair) throws Exception {
                                try {
                                    String signHex = OCPWalletUtils.signTransaction(ecKeyPair, qrCodeTransaction.getAmount(),
                                            qrCodeTransaction.getTransactionTo(),
                                            OCPWallet.gwei2Wei(qrCodeTransaction.getGasPrice()).toString(),
                                            qrCodeTransaction.getGasLimit(),
                                            qrCodeTransaction.getData(),
                                            qrCodeTransaction.getContractAddress(),
                                            new BigInteger(qrCodeTransaction.getNonce())
                                    );
                                    if (!signHex.startsWith("0x")) {
                                        WarmDialog.showTip(TransactionPreviewActivity.this, "Parse Transaction fail");
                                        return "";
                                    }
                                    qrCodeBean.setTransaction(null);
                                    qrCodeBean.setData(signHex);
                                    String qrCode = CommonUtils.Object2Json(qrCodeBean);

                                    showQRCode(qrCode);


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return "";
                            }
                        })
                        .subscribe();


            }

            @Override
            public void onError(StatusResponse statusResponse) {
                TransactionPreviewActivity.this.dismissLoading();
                WarmDialog.showTip(TransactionPreviewActivity.this, statusResponse.getResponseTip());
            }

            @Override
            public void onSuccess(StatusResponse statusResponse) {
                TransactionPreviewActivity.this.dismissLoading();


            }
        };
        walletVerifyHelp.validWalletPassword(qrCodeTransaction.getTransactionFrom(), pwd);


    }

    private void showQRCode(String signHex) {
        QRCodeSignDialog qrCodeSignDialog = QRCodeSignDialog.getInstance(TransactionPreviewActivity.this);
        qrCodeSignDialog.show();
        qrCodeSignDialog.changeType(MODE_SIGN_SUCCESSFUL);
        qrCodeSignDialog.setData(signHex);
    }


}
