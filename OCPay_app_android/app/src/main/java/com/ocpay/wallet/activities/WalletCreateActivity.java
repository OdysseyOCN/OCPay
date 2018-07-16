package com.ocpay.wallet.activities;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;

import com.ocpay.wallet.AnalyticsEvent;
import com.ocpay.wallet.Constans;
import com.ocpay.wallet.OCPWallet;
import com.ocpay.wallet.R;
import com.ocpay.wallet.databinding.ActivityWalletCreateBinding;
import com.ocpay.wallet.greendao.WalletInfo;
import com.ocpay.wallet.greendao.manager.WalletInfoDaoUtils;
import com.ocpay.wallet.http.rx.RxBus;
import com.ocpay.wallet.manager.AnalyticsManager;
import com.ocpay.wallet.utils.CheckInputManager;
import com.ocpay.wallet.utils.OCPPrefUtils;
import com.ocpay.wallet.utils.eth.OCPWalletUtils;
import com.ocpay.wallet.utils.eth.bean.OCPWalletFile;
import com.ocpay.wallet.utils.wallet.WalletStorage;
import com.ocpay.wallet.widget.dialog.WarmDialog;
import com.snow.commonlibrary.log.MyLog;
import com.snow.commonlibrary.utils.RegularExpressionUtils;
import com.snow.commonlibrary.widget.webview.WebViewActivity;

import org.web3j.crypto.CipherException;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.ocpay.wallet.Constans.RXBUS.ACTION_SELECT_WALLET;
import static com.ocpay.wallet.Constans.WALLET.PATH_imToken;
import static com.ocpay.wallet.Constans.WALLET.WALLET_TYPE_LOCAL_GEN;
import static com.ocpay.wallet.utils.OCPWalletPasswordHelp.checkInput;
import static com.ocpay.wallet.utils.OCPWalletPasswordHelp.setPwdLevel;

public class WalletCreateActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private ActivityWalletCreateBinding binding;
    private static final String Action_Request = "wallet_create_request";
    private int requestId;


    public static void startActivity(Activity activity, int requestId) {
        if (activity == null) return;
        Intent intent = new Intent(activity, WalletCreateActivity.class);
        intent.putExtra(Action_Request, requestId);
        activity.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_wallet_create);
        requestId = getIntent().getIntExtra(Action_Request, -1);
        initDataBinding();
        initActionBar();
        initListener();

    }


    private void initListener() {
        binding.includePrivacy.tvPrivacyPolicy.setOnClickListener(this);

        binding.etWalletPwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                binding.tvPwdTip.setVisibility(View.VISIBLE);
                binding.tvPwdLv.setVisibility(View.VISIBLE);
                binding.ivPwdLv.setVisibility(View.VISIBLE);


            }
        });
        binding.etWalletPwd.addTextChangedListener(new TextWatcher() {
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
                        setPwdLevel(input, binding.tvPwdLv, binding.tvPwdTip, binding.ivPwdLv);
                    }
                });
            }
        });


    }

    private void initActionBar() {
        binding.includeActionBar.actionBarTitle.setText(getResources().getString(R.string.activity_title_create_wallet));
        binding.includeActionBar.ivBack.setImageResource(R.mipmap.ic_back);
        binding.includeActionBar.actionBarTitle.setTextColor(getResources().getColor(R.color.color_text_main));
        binding.includeActionBar.ivBack.setOnClickListener(this);
    }


    private void initDataBinding() {
        binding.tvGenerate.setOnClickListener(this);
        binding.tvImportWallet.setOnClickListener(this);
        binding.includePrivacy.cbPrivacyPolicy.setOnCheckedChangeListener(this);
        binding.includePrivacy.llCheckPolicy.setOnClickListener(this);
        binding.tvGenerate.setClickable(false);


    }


    private void createWallet() {

        //  check input

        boolean status = checkInput(WalletCreateActivity.this,
                binding.etWalletName.getText().toString().trim(),
                binding.etWalletPwd.getText().toString().trim(),
                binding.etWalletCheckPwd.getText().toString().trim(),
                true);
        if (!status) return;
        Observable
                .create(new ObservableOnSubscribe<Boolean>() {
                    @Override
                    public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                        boolean result = functionCreateWallet();
                        e.onNext(result);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        showLoading(false);

                    }

                    @Override
                    public void onNext(Boolean o) {
                        if (o) {
                            RxBus.getInstance().post(requestId, "");
                            if (ACTION_SELECT_WALLET == requestId) {
                                BackupMnemonicActivity.startBackupActivity(WalletCreateActivity.this, OCPWallet.getCurrentWallet().getWalletAddress(), "");
                            }
                            finish();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissLoading();
                        String tip = "error";
                        if (e.getMessage().contains("WALLET_NAME")) {
                            MyLog.i("onComplete" + e.getMessage());
                            tip = "The name is taken,please try another!";
                        }
                        WarmDialog.showTip(WalletCreateActivity.this, e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        MyLog.i("onComplete" + Thread.currentThread().getName());

                    }
                });

    }

    private boolean functionCreateWallet() {
        try {
            //   create wallet
            OCPWalletFile ocpWalletFile = OCPWalletUtils.createWithMnemonic(binding.etWalletPwd.getText().toString(), PATH_imToken.split(" ")[0], getApplication(), true);
            //  save file
            boolean b = WalletStorage.getInstance().saveWalletFile(getApplicationContext(), ocpWalletFile);
            MyLog.i("createWallet:" + b);
            //  insert dao
            WalletInfo walletInfo = new WalletInfo();
            walletInfo.setWalletName(binding.etWalletName.getText().toString());
            walletInfo.setWalletType(WALLET_TYPE_LOCAL_GEN);
            walletInfo.setWalletAddress(ocpWalletFile.getWalletFile().getAddress());
            walletInfo.setBackup(false);
            walletInfo.setPasswordTip(binding.etWalletPwdTp.getText().toString());
            walletInfo.setStartBlock(OCPPrefUtils.getLastBlockNo());

            boolean isSuccess = WalletInfoDaoUtils.insertWalletInfo(walletInfo, getApplication());

            OCPWallet.setCurrentWallet(walletInfo);

            //todo hide processing

            //todo send  msg  update  resource
            return true;

        } catch (CipherException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return false;

    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        setCheckBoxStatus(isChecked);
    }

    private void setCheckBoxStatus(boolean isChecked) {
        binding.tvGenerate.setClickable(isChecked);
        int resBgImport = isChecked ? R.drawable.shape_corner_btn_main_r6 : R.drawable.shape_btn_grave;
        int resTxColor = isChecked ? getResources().getColor(R.color.white) : getResources().getColor(R.color.color_btn_text_un_click);
        binding.tvGenerate.setBackgroundResource(resBgImport);
        binding.tvGenerate.setTextColor(resTxColor);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_generate:
                AnalyticsManager.getInstance().sendEvent(AnalyticsEvent.CreateYourWallet_button_CreateWallet);
                createWallet();
                break;

            case R.id.tv_privacy_policy:
                AnalyticsManager.getInstance().sendEvent(AnalyticsEvent.CreateYourWallet_button_Service);
                WebViewActivity.loadUrl(this, Constans.H5.OCPayPrivacyPolicy, null);
                break;
            case R.id.tv_import_wallet:
                AnalyticsManager.getInstance().sendEvent(AnalyticsEvent.CreateYourWallet_button_ImportWallet);
                WalletImportActivity.startActivity(this);
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.ll_check_policy:
                binding.includePrivacy.cbPrivacyPolicy.setChecked(!binding.includePrivacy.cbPrivacyPolicy.isChecked());
                break;


        }
    }

    @Override
    protected void onDestroy() {
        WarmDialog.getInstance(WalletCreateActivity.this).destroy();
        super.onDestroy();
    }
}
