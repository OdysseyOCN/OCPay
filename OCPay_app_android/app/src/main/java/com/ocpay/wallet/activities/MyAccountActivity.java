package com.ocpay.wallet.activities;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import com.ocpay.wallet.R;
import com.ocpay.wallet.bean.CountryData;
import com.ocpay.wallet.bean.OCPayUserInfo;
import com.ocpay.wallet.bean.response.OCPResponse;
import com.ocpay.wallet.bean.response.UserWalletInfoResponse;
import com.ocpay.wallet.databinding.ActivityMyAccountBinding;
import com.ocpay.wallet.http.client.OCPayHttpClientIml;
import com.ocpay.wallet.http.rx.RxBus;
import com.ocpay.wallet.manager.OUserManager;
import com.ocpay.wallet.utils.CountryUtils;
import com.ocpay.wallet.utils.eth.OCPWalletUtils;
import com.ocpay.wallet.utils.web3j.utils.OWalletUtils;
import com.ocpay.wallet.widget.dialog.ExportPrivateKeyDialog;
import com.ocpay.wallet.widget.dialog.PasswordConfirmDialog;
import com.ocpay.wallet.widget.dialog.WarmDialog;
import com.snow.commonlibrary.utils.StringUtil;

import io.github.novacrypto.bip39.MnemonicValidator;
import io.github.novacrypto.bip39.wordlists.English;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static com.ocpay.wallet.Constans.RXBUS.ACTION_CONFIRM_PASSWORD;
import static com.ocpay.wallet.Constans.RXBUS.ACTION_MODIFY_WALLET_ADDRESS;
import static com.ocpay.wallet.Constans.RXBUS.ACTION_OCN_WALLET;
import static com.ocpay.wallet.activities.AccountSignUpActivity.MODE_RESET;
import static com.ocpay.wallet.utils.CountryUtils.COUNTRY_INDEX;
import static com.ocpay.wallet.utils.CountryUtils.initCountryIndex;
import static com.ocpay.wallet.utils.eth.OCPWalletUtils.isEthAddress;

public class MyAccountActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private ActivityMyAccountBinding binding;
    private CountryData countryData;
    private boolean showWallet;


    public static void startMyAccountActivity(Activity activity) {
        if (activity == null) return;
        Intent intent = new Intent(activity, MyAccountActivity.class);
        activity.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_account);
        initDataBinding();
        initActionBar();
        initListener();
        initView();
        showLoading(false);
        pullData();
        initRxBux();

    }

    private void initRxBux() {

        Disposable subscribe = RxBus.getInstance().toObservable(ACTION_OCN_WALLET, UserWalletInfoResponse.class)
                .subscribe(new Consumer<UserWalletInfoResponse>() {
                    @Override
                    public void accept(UserWalletInfoResponse userWalletInfoResponse) throws Exception {
                        dismissLoading();
                        if (userWalletInfoResponse.isSuccess()) {
                            OUserManager.getInstance().setUserInfo(userWalletInfoResponse.getData());
                            updateInfo();
                        } else {
                            WarmDialog.showTip(MyAccountActivity.this, "Fail !");
                        }
                    }


                });
        addDisposable(subscribe);

        Disposable confirm = RxBus.getInstance().toObservable(ACTION_CONFIRM_PASSWORD, OCPResponse.class)
                .subscribe(new Consumer<OCPResponse>() {
                    @Override
                    public void accept(OCPResponse userWalletInfoResponse) throws Exception {
                        dismissLoading();
                        if (userWalletInfoResponse.isSuccess()) {
                            if (showWallet) {
                                showWalletAddress();
                            } else {
                                enterWalletAddress();
                            }
                        } else {
                            WarmDialog.showTip(MyAccountActivity.this, "Fail !");
                        }
                    }
                });
        addDisposable(confirm);
        Disposable modifyAddress = RxBus.getInstance().toObservable(ACTION_MODIFY_WALLET_ADDRESS, OCPResponse.class)
                .subscribe(new Consumer<OCPResponse>() {
                    @Override
                    public void accept(OCPResponse userWalletInfoResponse) throws Exception {
                        dismissLoading();
                        if (userWalletInfoResponse.isSuccess()) {
                            WarmDialog.showTip(MyAccountActivity.this, getString(R.string.tip_modify_address_successful));

                        } else {
                            WarmDialog.showTip(MyAccountActivity.this, "Fail !");
                        }
                    }
                });
        addDisposable(modifyAddress);


    }

    private void enterWalletAddress() {
        PasswordConfirmDialog dialog = PasswordConfirmDialog.getInstance(this);
        dialog.setListener(new PasswordConfirmDialog.ConfirmListener() {
            @Override
            public void onConfirm(String pwd) {
                boolean isValid = isEthAddress(pwd);
                if (!isValid) {
                    WarmDialog.showTip(MyAccountActivity.this, getString(R.string.tip_invalid_address));
                    return;
                }
                OCPayHttpClientIml.modifyBindingAddress(ACTION_MODIFY_WALLET_ADDRESS, pwd);
            }
        });
        dialog.showInputWalletAddress();


    }

    private void showWalletAddress() {
        ExportPrivateKeyDialog exportPrivateKeyDialog = ExportPrivateKeyDialog.getInstance(this);
        String address = OUserManager.getInstance().getUserInfo().walletAddress;
        exportPrivateKeyDialog.showOCNWallet(address);
    }

    private void updateInfo() {
        OCPayUserInfo userInfo = OUserManager.getInstance().getUserInfo();
        binding.tvPhoneNumber.setText(userInfo.phone);
        binding.tvBingWalletAddress.setText(userInfo.walletAddress);

        //update btn tip
        int tip = StringUtil.isEmpty(userInfo.walletAddress) ? R.string.tip_change_wallet : R.string.tip_change_wallet;
        binding.tvChooseWallet.setText(getString(tip));


    }


    private void pullData() {
        OCPayHttpClientIml.getUserWalletInfo(ACTION_OCN_WALLET);
    }

    private void initView() {
        initCountryIndex();
        countryData = CountryUtils.getCountryBean(this).get(COUNTRY_INDEX);

    }


    private void initListener() {


    }

    private void initActionBar() {
        binding.includeActionBar.actionBarTitle.setText(getResources().getString(R.string.settings_user));
        binding.includeActionBar.ivBack.setImageResource(R.mipmap.ic_close_black);
        binding.includeActionBar.actionBarTitle.setTextColor(getResources().getColor(R.color.color_text_main));
        binding.includeActionBar.ivBack.setOnClickListener(this);
        binding.includeActionBar.viewLine.setVisibility(View.VISIBLE);
    }


    private void initDataBinding() {
        binding.llShowWallet.setOnClickListener(this);
        binding.tvChooseWallet.setOnClickListener(this);
        binding.tvLogOut.setOnClickListener(this);
        binding.tvResetPwd.setOnClickListener(this);

    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

//        setCheckBoxStatus(isChecked);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_show_wallet:
                //show bind  wallet address

                PasswordConfirmDialog instance = PasswordConfirmDialog.getInstance(this);
                instance.setListener(new PasswordConfirmDialog.ConfirmListener() {
                    @Override
                    public void onConfirm(String pwd) {
                        showLoading(false);
                        showWallet = true;
                        OCPayHttpClientIml.confirmPassword(ACTION_CONFIRM_PASSWORD, pwd);

                    }
                });
                instance.show();
                break;
            case R.id.tv_choose_wallet:
                // change wallet address

                PasswordConfirmDialog chooseWalelt = PasswordConfirmDialog.getInstance(this);
                chooseWalelt.setListener(new PasswordConfirmDialog.ConfirmListener() {
                    @Override
                    public void onConfirm(String pwd) {
                        showLoading(false);
                        showWallet = true;
                        OCPayHttpClientIml.confirmPassword(ACTION_CONFIRM_PASSWORD, pwd);

                    }
                });
                chooseWalelt.show();

                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_reset_pwd:
                AccountSignUpActivity.startAccountSignUpActivity(this, MODE_RESET);
                break;

        }
    }


    @Override
    protected void onDestroy() {
        WarmDialog.getInstance(MyAccountActivity.this).destroy();
        super.onDestroy();
    }


}
