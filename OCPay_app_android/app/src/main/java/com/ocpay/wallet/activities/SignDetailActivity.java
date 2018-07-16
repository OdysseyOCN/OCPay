package com.ocpay.wallet.activities;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.ocpay.wallet.MyApp;
import com.ocpay.wallet.R;
import com.ocpay.wallet.bean.QRCodeBean;
import com.ocpay.wallet.bean.StatusResponse;
import com.ocpay.wallet.databinding.ActivitySignDetailBinding;
import com.ocpay.wallet.greendao.WalletInfo;
import com.ocpay.wallet.greendao.manager.WalletInfoDaoUtils;
import com.ocpay.wallet.utils.CommonUtils;
import com.ocpay.wallet.utils.WalletVerifyHelp;
import com.ocpay.wallet.utils.eth.util.AES;
import com.ocpay.wallet.widget.dialog.PasswordConfirmDialog;
import com.ocpay.wallet.widget.dialog.QRCodeSignDialog;
import com.ocpay.wallet.widget.dialog.WarmDialog;
import com.snow.commonlibrary.utils.EncodeUtils;

import static com.ocpay.wallet.widget.dialog.QRCodeSignDialog.MODE_SIGN_SUCCESSFUL;

public class SignDetailActivity extends BaseActivity implements View.OnClickListener {


    private ActivitySignDetailBinding binding;
    public static final int ITEM_USE_AGREE = 0;
    public static final int ITEM_PRIVACY_POLICY = 1;
    public static final int ITEM_VERSION_LOG = 2;
    public static final int ITEM_PRODUCT_GUIDE = 3;
    public static final int ITEM_CHECK_VERSION = 4;

    public static final String qrcode = "qrcode";
    private QRCodeBean qrCodeBean = null;
    private String walletAddress;
    private String randomCode;


    public static void startSignDetailActivity(Activity activity, QRCodeBean bean) {
        Intent intent = new Intent(activity, SignDetailActivity.class);
        intent.putExtra(qrcode, bean);
        activity.startActivity(intent);

    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(SignDetailActivity.this, R.layout.activity_sign_detail);
        qrCodeBean = (QRCodeBean) getIntent().getSerializableExtra(qrcode);
        initActionBar();
        initView();
        initListener();

    }

    private void initListener() {


    }

    private void initActionBar() {
        binding.includeActionBar.actionBarTitle.setText(R.string.activity_sign_detail);
        binding.includeActionBar.llToolbar.setBackgroundColor(getResources().getColor(R.color.white));
        binding.includeActionBar.actionBarTitle.setTextColor(getResources().getColor(R.color.color_text_main));
        binding.includeActionBar.ivBack.setImageResource(R.mipmap.ic_back);
        binding.includeActionBar.toolbarMenuIcon.setVisibility(View.GONE);
        binding.includeActionBar.viewLine.setVisibility(View.VISIBLE);
        binding.includeActionBar.ivBack.setOnClickListener(this);
    }


    private void initView() {
        if (qrCodeBean == null) return;
        walletAddress = qrCodeBean.getEthereum();
        binding.tvData.setText("data   " + qrCodeBean.getData());
        binding.tvWalletAddress.setText(walletAddress);
        binding.tvAction.setOnClickListener(this);
        randomCode = qrCodeBean.getData();
        WalletInfo walletInfo = WalletInfoDaoUtils.sqlByAddress(MyApp.getContext(), walletAddress);
        //todo warm to backup
//        if(!walletInfo.isBackup()){
//            WarmDialog.showTip(this,"");
//        }
        if (walletInfo == null) {
            WarmDialog.showTip(this, "wallet is not exits");
            return;
        }


    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.toolbar_menu_icon:
                ContactsCreateActivity.startContactsCreateActivity(SignDetailActivity.this);
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_action:

                signData();
                break;

        }

    }

    private void signData() {

        PasswordConfirmDialog instance = PasswordConfirmDialog.getInstance(this);
        instance.setListener(new PasswordConfirmDialog.ConfirmListener() {
            @Override
            public void onConfirm(String pwd) {
                WalletVerifyHelp walletVerifyHelp = new WalletVerifyHelp() {
                    @Override
                    public void showLoading() {
                        SignDetailActivity.this.showLoading(false);
                    }

                    @Override
                    public void onError(StatusResponse statusResponse) {
                        SignDetailActivity.this.dismissLoading();
                        WarmDialog.showTip(SignDetailActivity.this, statusResponse.getResponseTip());
                    }

                    @Override
                    public void onSuccess(StatusResponse statusResponse) {
                        SignDetailActivity.this.dismissLoading();
                        QRCodeSignDialog qrCodeSignDialog = QRCodeSignDialog.getInstance(SignDetailActivity.this);
                        try {
                            qrCodeSignDialog.show();
                            String encrypt = EncodeUtils.encodeByMD5(randomCode);
                            qrCodeBean.setData(encrypt);
                            String respJson = CommonUtils.Object2Json(qrCodeBean);
                            qrCodeSignDialog.changeType(MODE_SIGN_SUCCESSFUL);
                            qrCodeSignDialog.setData(respJson);
                        } catch (Exception e) {
                            WarmDialog.showTip(SignDetailActivity.this, getString(R.string.sign_fail));
                            e.printStackTrace();
                        }


                    }


                };
                walletVerifyHelp.validWalletPassword(walletAddress, pwd);
            }
        });
        instance.show();
    }


}
