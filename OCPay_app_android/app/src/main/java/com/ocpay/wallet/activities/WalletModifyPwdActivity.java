package com.ocpay.wallet.activities;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ocpay.wallet.MyApp;
import com.ocpay.wallet.R;
import com.ocpay.wallet.bean.StatusResponse;
import com.ocpay.wallet.databinding.ActivityWalletModifyPwdBinding;
import com.ocpay.wallet.greendao.WalletInfo;
import com.ocpay.wallet.greendao.manager.WalletInfoDaoUtils;
import com.ocpay.wallet.utils.OCPWalletPasswordHelp;
import com.ocpay.wallet.utils.WalletVerifyHelp;
import com.ocpay.wallet.utils.eth.OCPWalletUtils;
import com.ocpay.wallet.utils.eth.bean.OCPWalletFile;
import com.ocpay.wallet.utils.wallet.WalletStorage;
import com.ocpay.wallet.widget.dialog.WarmDialog;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.ocpay.wallet.Constans.WALLET.WALLET_ADDRESS;
import static com.ocpay.wallet.Constans.WALLET.WALLET_NAME;
import static com.ocpay.wallet.utils.OCPWalletPasswordHelp.setPwdLevel;
import static com.ocpay.wallet.utils.eth.OCPWalletUtils.modifyMnenomicPwd;

public class WalletModifyPwdActivity extends BaseActivity implements View.OnClickListener {


    private ActivityWalletModifyPwdBinding binding;
    private LinearLayout expertMode;
    private String walletAddress;

    public static void startWalletModifyActivity(Activity activity, String walletAddress, String walletName) {
        Intent intent = new Intent(activity, WalletModifyPwdActivity.class);
        intent.putExtra(WALLET_NAME, walletName);
        intent.putExtra(WALLET_ADDRESS, walletAddress);
        activity.startActivity(intent);

    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(WalletModifyPwdActivity.this, R.layout.activity_wallet_modify_pwd);
        initActionBar();
        initView();
        initListener();
    }

    private void initListener() {
        binding.includeActionBar.ivBack.setOnClickListener(this);
        binding.includeActionBar.toolbarMenuIcon.setOnClickListener(this);

    }

    private void initView() {
        walletAddress = getIntent().getStringExtra(WALLET_ADDRESS);
        WalletInfo walletInfo = WalletInfoDaoUtils.sqlByAddress(this, walletAddress);

        binding.includePasswordInput.etPwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                binding.includePasswordInput.tvPwdTip.setVisibility(View.VISIBLE);
                binding.includePasswordInput.tvPwdLv.setVisibility(View.VISIBLE);
                binding.includePasswordInput.ivPwdLv.setVisibility(View.VISIBLE);


            }
        });
        setTextChangedListener(binding.includePasswordInput.etPwd, binding.includePasswordInput.tvPwdLv, binding.includePasswordInput.tvPwdTip, binding.includePasswordInput.ivPwdLv);


    }

    private void initActionBar() {
        binding.includeActionBar.actionBarTitle.setText(R.string.activity_modify_title);
        binding.includeActionBar.ivBack.setImageResource(R.mipmap.ic_back);
        binding.includeActionBar.toolbarMenuIcon.setImageResource(R.mipmap.ic_complete);
        binding.includeActionBar.llToolbar.setBackgroundColor(getResources().getColor(R.color.white));
        binding.includeActionBar.actionBarTitle.setTextColor(getResources().getColor(R.color.color_text_main));
        binding.includeActionBar.ivBack.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.iv_back:
                finish();
                break;
            case R.id.toolbar_menu_icon:
                startModifyPassword();
                break;
            case R.id.ll_export_keystore:

                break;

            case R.id.ll_export_private_key:

                break;

            case R.id.ll_change_pwd:
                break;


        }

    }

    private void startModifyPassword() {
        boolean checkInput = OCPWalletPasswordHelp.checkInput(this,
                null,
                binding.includePasswordInput.etPwd.getText().toString().trim(),
                binding.etCheckPwd.getText().toString().trim(),
                false);
        if (!checkInput) return;
        actionModifyPassword();

    }

    private void actionModifyPassword() {

//        modifyWalletPwd();

        WalletVerifyHelp verifyHelp = new WalletVerifyHelp() {
            @Override
            public void showLoading() {
                WalletModifyPwdActivity.this.showLoading(false);
            }

            @Override
            public void onError(StatusResponse statusResponse) {
                WalletModifyPwdActivity.this.dismissLoading();
                WarmDialog.showTip(WalletModifyPwdActivity.this, statusResponse.getResponseTip());
            }

            @Override
            public void onSuccess(StatusResponse statusResponse) {

            }

            @Override
            public void getPrivateKey(ECKeyPair keyPair) {
                Observable.just(keyPair)
                        .observeOn(Schedulers.newThread())
                        .map(new Function<ECKeyPair, Boolean>() {
                            @Override
                            public Boolean apply(ECKeyPair ecKeyPair) throws Exception {

                                try {
                                    OCPWalletFile ocpWallet = WalletStorage.getInstance().getOCPWallet(walletAddress);

                                    WalletFile walletFile = Wallet.createStandard(binding.includePasswordInput.etPwd.getText().toString().trim(), ecKeyPair);

                                    ocpWallet.setWalletFile(walletFile);

                                    modifyMnenomicPwd(binding.etCurrentPwd.getText().toString().trim(), binding.includePasswordInput.etPwd.getText().toString().trim(), ocpWallet);

                                    WalletStorage.getInstance().saveWalletFile(WalletModifyPwdActivity.this, ocpWallet);


                                } catch (Exception e) {
                                    e.printStackTrace();

                                    return false;
                                }

                                return true;
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(new Function<Boolean, Object>() {
                            @Override
                            public Object apply(Boolean aBoolean) throws Exception {
                                WalletModifyPwdActivity.this.dismissLoading();
                                if (aBoolean) {
                                    Toast.makeText(MyApp.getContext(), "Password reset complete ! ", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(MyApp.getContext(), "Password reset failed ! ", Toast.LENGTH_SHORT).show();
                                }
                                return aBoolean;
                            }
                        })
                        .subscribe();


            }
        };
        verifyHelp.validWalletPassword(walletAddress, binding.etCurrentPwd.getText().toString().trim());
    }


    public void setTextChangedListener(EditText editText, final TextView tvLv, final TextView tvTip, final ImageView ivLv) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                final String input = s.toString();
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        setPwdLevel(input, tvLv, tvTip, ivLv);
                    }
                });
            }
        });
    }
}
