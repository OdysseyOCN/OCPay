package com.ocpay.wallet.activities;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.ocpay.wallet.AnalyticsEvent;
import com.ocpay.wallet.MyApp;
import com.ocpay.wallet.OCPWallet;
import com.ocpay.wallet.R;
import com.ocpay.wallet.bean.StatusResponse;
import com.ocpay.wallet.databinding.ActivityWalletDetailBinding;
import com.ocpay.wallet.greendao.WalletInfo;
import com.ocpay.wallet.greendao.manager.WalletInfoDaoUtils;
import com.ocpay.wallet.http.rx.RxBus;
import com.ocpay.wallet.manager.ActivityManager;
import com.ocpay.wallet.manager.AnalyticsManager;
import com.ocpay.wallet.utils.BalanceUtils;
import com.ocpay.wallet.utils.CommonUtils;
import com.ocpay.wallet.utils.WalletVerifyHelp;
import com.ocpay.wallet.utils.eth.OCPWalletUtils;
import com.ocpay.wallet.utils.eth.bean.OCPWalletFile;
import com.ocpay.wallet.utils.wallet.WalletStorage;
import com.ocpay.wallet.widget.dialog.ConfirmDialog;
import com.ocpay.wallet.widget.dialog.ExportPrivateKeyDialog;
import com.ocpay.wallet.widget.dialog.PasswordConfirmDialog;
import com.ocpay.wallet.widget.dialog.TipBackupDialog;
import com.ocpay.wallet.widget.dialog.WarmDialog;

import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.WalletFile;

import java.math.BigDecimal;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.ocpay.wallet.Constans.RXBUS.ACTION_SELECT_WALLET;
import static com.ocpay.wallet.Constans.RXBUS.ACTION_TOKEN_WALLET_MANAGE_UPDATE;
import static com.ocpay.wallet.Constans.WALLET.WALLET_ADDRESS;
import static com.ocpay.wallet.Constans.WALLET.WALLET_NAME;
import static com.ocpay.wallet.Constans.WALLET.WALLET_TYPE_WATCHING;
import static com.ocpay.wallet.utils.TokenUtils.ETH;
import static com.ocpay.wallet.utils.eth.OCPWalletUtils.foldWalletAddress;
import static com.ocpay.wallet.utils.eth.OCPWalletUtils.getDecryptMnemonic;

public class WalletDetailActivity extends BaseActivity implements View.OnClickListener {

    private static String Extra_Show_Backup_Tip = "extra_show_backup_tip";
    private ActivityWalletDetailBinding binding;
    private String walletAddress;
    private String walletName;
    private WalletInfo walletInfo;
    private boolean hintIsShow;
    private boolean showTip;
    protected WalletFile walletFile;

    public static void startWalletDetailActivity(Activity activity, String walletAddress, boolean showBackupTip) {
        Intent intent = new Intent(activity, WalletDetailActivity.class);
        intent.putExtra(Extra_Show_Backup_Tip, showBackupTip);
        intent.putExtra(WALLET_ADDRESS, walletAddress);
        activity.startActivity(intent);

    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(WalletDetailActivity.this, R.layout.activity_wallet_detail);
        initActionBar();
        initView();

        initWatchingWallet();
        initListener();
    }

    private void initWatchingWallet() {
        int walletType = walletInfo.getWalletType();
        if (walletType != WALLET_TYPE_WATCHING) return;
        binding.rlPwdHint.setVisibility(View.GONE);
        binding.viewMiddleLine.setVisibility(View.GONE);
        binding.llChangePwd.setVisibility(View.GONE);
        binding.tvActionBackupMnemonic.setVisibility(View.GONE);
        binding.llExportPrivateKey.setVisibility(View.GONE);
        binding.llExportKeystore.setVisibility(View.GONE);
        binding.viewTopLine.setVisibility(View.GONE);

    }

    private void initListener() {

        binding.tvActionBackupMnemonic.setOnClickListener(this);
        binding.tvActionDeleteWallet.setOnClickListener(this);
        binding.llExportPrivateKey.setOnClickListener(this);
        binding.llExportKeystore.setOnClickListener(this);
        binding.llChangePwd.setOnClickListener(this);
        binding.ivComplete.setOnClickListener(this);
        binding.ivShowHint.setOnClickListener(this);
        binding.ivBack.setOnClickListener(this);
    }

    private void initView() {
        hintIsShow = false;
        walletAddress = getIntent().getStringExtra(WALLET_ADDRESS);
        walletName = getIntent().getStringExtra(WALLET_NAME);
        walletInfo = WalletInfoDaoUtils.sqlByAddress(this, walletAddress);
        binding.tvWalletAddress.setText(foldWalletAddress(walletInfo.getWalletAddress()));
        binding.etWalletName.setText(walletInfo.getWalletName());
        binding.tvEthAmount.setText(BalanceUtils.decimalFormat(new BigDecimal(walletInfo.getTokenBalance(MyApp.getContext(), ETH))));
        int btnBackupVisible = walletInfo.isBackup() ? View.GONE : View.VISIBLE;
        binding.tvActionBackupMnemonic.setVisibility(btnBackupVisible);
        binding.civWalletIcon.setBackgroundResource(OCPWalletUtils.getWalletProfilePicture(walletAddress));
        showTip = getIntent().getBooleanExtra(Extra_Show_Backup_Tip, false);
        if (showTip) TipBackupDialog.getInstance(WalletDetailActivity.this).show();
    }

    private void initActionBar() {

    }


    @Override
    protected void onResume() {
        super.onResume();
        walletInfo = WalletInfoDaoUtils.sqlByAddress(this, walletAddress);
        int btnBackupVisible = walletInfo.isBackup() ? View.GONE : View.VISIBLE;
        binding.tvActionBackupMnemonic.setVisibility(btnBackupVisible);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_complete:
                AnalyticsManager.getInstance().sendEvent(AnalyticsEvent.Walletdetail_button_save);
                String newWalletName = binding.etWalletName.getText().toString().trim();
                if (walletInfo == null || walletInfo.getWalletName().equals(newWalletName)) {
                    finish();
                    return;
                }

                if (WalletInfoDaoUtils.sqlByWalletName(MyApp.getContext(), newWalletName) != null) {
                    //todo toast
                    WarmDialog.getInstance(WalletDetailActivity.this).show();
                    WarmDialog.getInstance(WalletDetailActivity.this).setTip(getString(R.string.dialog_tip_name_token));
                    return;
                }
                AnalyticsManager.getInstance().sendEvent(AnalyticsEvent.Walletdetail_button_WalletName);
                walletInfo.setWalletName(newWalletName);
                WalletInfoDaoUtils.update(MyApp.getContext(), walletInfo);
                RxBus.getInstance().post(ACTION_TOKEN_WALLET_MANAGE_UPDATE, "");
                RxBus.getInstance().post(ACTION_SELECT_WALLET, "");
                finish();

                break;
            case R.id.ll_export_keystore:
                AnalyticsManager.getInstance().sendEvent(AnalyticsEvent.Walletdetail_button_ExportKeystore);

                actionExportKeystore();
                break;

            case R.id.ll_export_private_key:
                AnalyticsManager.getInstance().sendEvent(AnalyticsEvent.Walletdetail_button_Exportprivatekey);

                actionExportPrivateKey();

                break;
            case R.id.tv_action_delete_wallet:
                AnalyticsManager.getInstance().sendEvent(AnalyticsEvent.Walletdetail_button_DeleteWallet);

                if (walletInfo.getWalletType() == WALLET_TYPE_WATCHING) {
                    ConfirmDialog confirmDialog = ConfirmDialog.getInstance(WalletDetailActivity.this);
                    confirmDialog.setListener(new ConfirmDialog.ConfirmListener() {
                        @Override
                        public void onConfirm() {
                            WalletInfoDaoUtils.dealWalletInfo(MyApp.getContext(), walletInfo);
                            deleteSuccessful();
                        }
                    });
                    confirmDialog.show();
                    return;
                }


                PasswordConfirmDialog dialog = PasswordConfirmDialog.getInstance(WalletDetailActivity.this);

                dialog.setListener(new PasswordConfirmDialog.ConfirmListener() {
                    @Override
                    public void onConfirm(String pwd) {
                        WalletVerifyHelp verifyHelp = new WalletVerifyHelp() {
                            @Override
                            public void showLoading() {
                                WalletDetailActivity.this.showLoading(false);

                            }

                            @Override
                            public void onError(StatusResponse statusResponse) {
                                WalletDetailActivity.this.dismissLoading();

                            }

                            @Override
                            public void onSuccess(StatusResponse statusResponse) {
                                deleteWalletAndUp();

                            }
                        };
                        verifyHelp.validWalletPassword(walletAddress, pwd);
                    }
                });

                dialog.show();

                break;

            case R.id.ll_change_pwd:
                AnalyticsManager.getInstance().sendEvent(AnalyticsEvent.Walletdetail_button_Changepassword);
                WalletModifyPwdActivity.startWalletModifyActivity(WalletDetailActivity.this, walletAddress, walletName);
                break;


            case R.id.iv_show_hint:
                AnalyticsManager.getInstance().sendEvent(AnalyticsEvent.Walletdetail_button_Passwordhint);
                hintIsShow = !hintIsShow;
                int icRes = hintIsShow ? R.mipmap.ic_show_pwd_hint : R.mipmap.ic_hide_pwd_hint;
                String tipContent = !hintIsShow ? "***********" : walletInfo.getPasswordTip() == null ? "" : walletInfo.getPasswordTip();
                binding.ivShowHint.setImageResource(icRes);
                binding.tvPwdHint.setText(tipContent);
                break;

            case R.id.tv_action_backup_mnemonic:
                AnalyticsManager.getInstance().sendEvent(AnalyticsEvent.Walletdetail_button_BackupMnemonic);
                PasswordConfirmDialog passwordConfirmDialog = PasswordConfirmDialog.getInstance(this);
                passwordConfirmDialog.setListener(new PasswordConfirmDialog.ConfirmListener() {
                    @Override
                    public void onConfirm(String pwd) {
                        showLoading(false);

                        Observable.just(pwd)
                                .observeOn(Schedulers.newThread())
                                .map(new Function<String, String>() {
                                    @Override
                                    public String apply(String pwd) {
                                        OCPWalletFile ocpWallet = null;
                                        String mnemonic = null;
                                        try {
                                            ocpWallet = WalletStorage.getInstance().getOCPWallet(walletAddress);
                                            mnemonic = getDecryptMnemonic(pwd, ocpWallet);

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        return mnemonic == null ? "" : mnemonic;
                                    }
                                })
                                .observeOn(AndroidSchedulers.mainThread())
                                .map(new Function<String, Object>() {
                                    @Override
                                    public Object apply(String mnemonic) throws Exception {
                                        dismissLoading();
                                        if (mnemonic == null || mnemonic.isEmpty()) {
                                            WarmDialog.showTip(WalletDetailActivity.this, "Password Incorrect. ");
                                        } else {
                                            BackupMnemonicActivity.startBackupActivity(WalletDetailActivity.this, walletAddress, mnemonic);
                                        }
                                        return "";
                                    }
                                }).subscribe();


                    }
                });
                passwordConfirmDialog.show();

                break;


        }

    }

    private void deleteWalletAndUp() {
        boolean b = WalletStorage.getInstance().deleteWallet(walletInfo.getWalletAddress());
        WalletInfoDaoUtils.dealWalletInfo(MyApp.getContext(), walletInfo);
        String tip = b ? "operate successfully" : "operation failure";
        dismissLoading();
        WarmDialog.showTip(WalletDetailActivity.this, tip);
        if (b) {
            deleteSuccessful();
        }
    }

    private void deleteSuccessful() {
        finish();
        List<WalletInfo> walletInfos = WalletInfoDaoUtils.sqlAll(MyApp.getContext());
        if (walletInfos != null && walletInfos.size() > 0 && walletInfos.get(0) != null) {
            OCPWallet.setCurrentWallet(walletInfos.get(0));
        } else {
            OCPWallet.setCurrentWallet(null);
            ActivityManager.getInstance().finishActivity(WalletManageActivity.class);
        }

        RxBus.getInstance().post(ACTION_SELECT_WALLET, "");
    }

    private void actionExportPrivateKey() {

        PasswordConfirmDialog instance = PasswordConfirmDialog.getInstance(this);
        instance.setListener(new PasswordConfirmDialog.ConfirmListener() {
            @Override
            public void onConfirm(String pwd) {
                WalletVerifyHelp walletVerifyHelp = new WalletVerifyHelp() {
                    @Override
                    public void showLoading() {
                        WalletDetailActivity.this.showLoading(false);
                    }

                    @Override
                    public void getPrivateKey(ECKeyPair keyPair) {
                        Observable.just(keyPair)
                                .observeOn(AndroidSchedulers.mainThread())
                                .map(new Function<ECKeyPair, Object>() {
                                    @Override
                                    public Object apply(ECKeyPair ecKeyPair) throws Exception {
                                        WalletDetailActivity.this.dismissLoading();
                                        String privateKey = OCPWalletUtils.privateKey(ecKeyPair.getPrivateKey());
                                        ExportPrivateKeyDialog dialog = ExportPrivateKeyDialog.getInstance(WalletDetailActivity.this);
                                        dialog.show();
                                        dialog.setPrivateKey(privateKey);
                                        return "";
                                    }
                                })
                                .subscribe();

                    }

                    @Override
                    public void onError(StatusResponse statusResponse) {
                        WalletDetailActivity.this.dismissLoading();
                        WarmDialog.showTip(WalletDetailActivity.this, statusResponse.getResponseTip());
                    }

                    @Override
                    public void onSuccess(StatusResponse statusResponse) {
                    }


                };
                walletVerifyHelp.validWalletPassword(walletAddress, pwd);
            }
        });
        instance.show();

    }

    private void actionExportKeystore() {
        PasswordConfirmDialog instance = PasswordConfirmDialog.getInstance(this);
        instance.setListener(new PasswordConfirmDialog.ConfirmListener() {
            @Override
            public void onConfirm(String pwd) {
                WalletVerifyHelp walletVerifyHelp = new WalletVerifyHelp() {
                    @Override
                    public void showLoading() {
                        WalletDetailActivity.this.showLoading(false);
                    }

                    @Override
                    public StatusResponse doSomething(OCPWalletFile ocpWalletFile) {
                        walletFile = ocpWalletFile.getWalletFile();
                        return super.doSomething(ocpWalletFile);
                    }

                    @Override
                    public void onError(StatusResponse statusResponse) {
                        WalletDetailActivity.this.dismissLoading();
                        WarmDialog.showTip(WalletDetailActivity.this, statusResponse.getResponseTip());
                    }

                    @Override
                    public void onSuccess(StatusResponse statusResponse) {
                        WalletDetailActivity.this.dismissLoading();
                        String jsonKeystore = CommonUtils.Object2Json(walletFile);
                        ExportKeystoreActivity.startExportKeystoreActivity(WalletDetailActivity.this, jsonKeystore);
                    }


                };
                walletVerifyHelp.validWalletPassword(walletAddress, pwd);
            }
        });
        instance.show();
    }

    @Override
    protected void onDestroy() {
        WarmDialog.getInstance(WalletDetailActivity.this).destroy();
        TipBackupDialog.getInstance(WalletDetailActivity.this).destroy();
        PasswordConfirmDialog.getInstance(WalletDetailActivity.this).destroy();
        super.onDestroy();
    }


}
