package com.ocpay.wallet.activities;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.ocpay.wallet.Constans;
import com.ocpay.wallet.OCPWallet;
import com.ocpay.wallet.R;
import com.ocpay.wallet.databinding.ActivityFirstLauncherBinding;
import com.ocpay.wallet.http.client.EthScanHttpClientIml;
import com.ocpay.wallet.http.rx.RxBus;
import com.ocpay.wallet.manager.AnalyticsManager;
import com.ocpay.wallet.utils.OCPPrefUtils;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static com.ocpay.wallet.AnalyticsEvent.Login_button_CreateyourOCPay;
import static com.ocpay.wallet.AnalyticsEvent.Login_button_ImportyourOCPay;
import static com.ocpay.wallet.Constans.RXBUS.ACTION_WALLET_FIRST_CREATE;
import static com.ocpay.wallet.Constans.WALLET.ADDRESS_FROM;

/**
 * Created by y on 2018/5/21.
 */

public class FirstLauncherActivity extends BaseActivity implements View.OnClickListener {


    private ActivityFirstLauncherBinding binding;

    public static void startFirstLauncherActivity(Activity activity) {
        activity.startActivity(new Intent(activity, FirstLauncherActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(FirstLauncherActivity.this, R.layout.activity_first_launcher);
        binding.tvCreateWallet.setOnClickListener(this);
        binding.tvImportWallet.setOnClickListener(this);
        getPermission();
        intRxBus();
        if (!OCPPrefUtils.isRecordFirstBlock()) {
            EthScanHttpClientIml.getBlockNumber(Constans.RXBUS.ACTION_RECORD_FIRST_BLOCK_NO);
        }

    }

    private void intRxBus() {

        Disposable disposable = RxBus.getInstance().toObservable(ACTION_WALLET_FIRST_CREATE, String.class).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Intent backActivity = new Intent(FirstLauncherActivity.this, BackupMnemonicActivity.class);
                backActivity.putExtra(ADDRESS_FROM, OCPWallet.getCurrentWallet().getWalletAddress());

                Intent mainActivity = new Intent(FirstLauncherActivity.this, MainActivity.class);

                Intent[] list = OCPWallet.getCurrentWallet().isBackup() ? new Intent[]{mainActivity} : new Intent[]{mainActivity, backActivity};

                startActivities(list);
                finish();
            }
        });

        addDisposable(disposable);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_create_wallet:
                RiskAssessmentActivity.startRiskAssessmentActivity(FirstLauncherActivity.this);
                AnalyticsManager.getInstance().sendEvent(Login_button_CreateyourOCPay);
                break;
            case R.id.tv_import_wallet:
                WalletImportActivity.startActivity(FirstLauncherActivity.this);
                AnalyticsManager.getInstance().sendEvent(Login_button_ImportyourOCPay);

                break;
        }

    }
}
