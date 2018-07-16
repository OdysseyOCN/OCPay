package com.ocpay.wallet.activities;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.ocpay.wallet.AnalyticsEvent;
import com.ocpay.wallet.Constans;
import com.ocpay.wallet.MyApp;
import com.ocpay.wallet.OCPWallet;
import com.ocpay.wallet.R;
import com.ocpay.wallet.databinding.ActivityAboutUsBinding;
import com.ocpay.wallet.databinding.ActivityRiskAssessmentBinding;
import com.ocpay.wallet.http.rx.RxBus;
import com.ocpay.wallet.manager.AnalyticsManager;
import com.snow.commonlibrary.utils.ApplicationUtils;
import com.snow.commonlibrary.widget.webview.WebViewActivity;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static com.ocpay.wallet.Constans.RXBUS.ACTION_WALLET_FIRST_CREATE;
import static com.ocpay.wallet.Constans.WALLET.ADDRESS_FROM;

public class RiskAssessmentActivity extends BaseActivity implements View.OnClickListener {


    private ActivityRiskAssessmentBinding binding;


    public static void startRiskAssessmentActivity(Activity activity) {

        Intent intent = new Intent(activity, RiskAssessmentActivity.class);
        activity.startActivity(intent);

    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(RiskAssessmentActivity.this, R.layout.activity_risk_assessment);
        initWebView();
        initView();
        initListener();

    }

    private void initWebView() {
        binding.webviewDetail.loadUrl(Constans.H5.Riskassessment);
    }

    private void initListener() {
        binding.tvCreateYourWallet.setOnClickListener(this);
        binding.tvLearnMore.setOnClickListener(this);

    }


    private void initView() {

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.toolbar_menu_icon:
                ContactsCreateActivity.startContactsCreateActivity(RiskAssessmentActivity.this);
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_create_your_wallet:
                AnalyticsManager.getInstance().sendEvent(AnalyticsEvent.RiskAssessment_button_CreateyourWallet);
                WalletCreateActivity.startActivity(RiskAssessmentActivity.this, ACTION_WALLET_FIRST_CREATE);
                finish();
                break;
            case R.id.tv_learn_more:
                WebViewActivity.loadUrl(this, Constans.H5.learnmore);
//                RiskAssessmentDetailActivity.startRiskAssessmentDetailActivity(this);
                break;

        }

    }


}
