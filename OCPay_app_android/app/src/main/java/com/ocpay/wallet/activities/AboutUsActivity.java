package com.ocpay.wallet.activities;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.ocpay.wallet.Constans;
import com.ocpay.wallet.MyApp;
import com.ocpay.wallet.R;
import com.ocpay.wallet.databinding.ActivityAboutUsBinding;
import com.ocpay.wallet.http.client.OCPayHttpClientIml;
import com.ocpay.wallet.http.rx.RxBus;
import com.ocpay.wallet.utils.OCPPrefUtils;
import com.ocpay.wallet.utils.UpdateUtils;
import com.ocpay.wallet.utils.eth.OCPWalletUtils;
import com.ocpay.wallet.widget.dialog.WarmDialog;
import com.snow.commonlibrary.utils.ApplicationUtils;
import com.snow.commonlibrary.widget.webview.WebViewActivity;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static com.ocpay.wallet.Constans.RXBUS.ACTION_ABOUT_US_GET_LAST_VERSION;
import static com.ocpay.wallet.utils.UpdateUtils.responseUpdate;

public class AboutUsActivity extends BaseActivity implements View.OnClickListener {


    private ActivityAboutUsBinding binding;
    public static final int ITEM_USE_AGREE = 0;
    public static final int ITEM_PRIVACY_POLICY = 1;
    public static final int ITEM_VERSION_LOG = 2;
    public static final int ITEM_PRODUCT_GUIDE = 3;
    public static final int ITEM_CHECK_VERSION = 4;


    public static void startAboutActivity(Activity activity) {

        Intent intent = new Intent(activity, AboutUsActivity.class);
        activity.startActivity(intent);

    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(AboutUsActivity.this, R.layout.activity_about_us);
        initActionBar();
        initView();
        initListener();
        initRxBus();

    }

    private void initRxBus() {
        Disposable subscribe = RxBus.getInstance().toObservable(ACTION_ABOUT_US_GET_LAST_VERSION, Boolean.class)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) {
                        dismissLoading();
                        responseUpdate(AboutUsActivity.this, aBoolean);
                    }
                });

        addDisposable(subscribe);


    }


    private void initListener() {
        if (Constans.DEBUG) {
            binding.tvBackGate.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Constans.TEST = !OCPPrefUtils.getEnableTestMode();
                    Toast.makeText(AboutUsActivity.this, "卐Test Mode卐" + Constans.TEST, Toast.LENGTH_SHORT).show();
                    OCPPrefUtils.setEnableTestMode(Constans.TEST);
                    return false;
                }
            });
        }

    }

    private void initActionBar() {
        binding.includeActionBar.actionBarTitle.setText(R.string.activity_about_us);
        binding.includeActionBar.llToolbar.setBackgroundColor(getResources().getColor(R.color.white));
        binding.includeActionBar.actionBarTitle.setTextColor(getResources().getColor(R.color.color_text_main));
        binding.includeActionBar.ivBack.setImageResource(R.mipmap.ic_back);
        binding.includeActionBar.toolbarMenuIcon.setVisibility(View.GONE);
        binding.includeActionBar.ivBack.setOnClickListener(this);
        binding.includeActionBar.viewLine.setVisibility(View.VISIBLE);
    }


    private void initView() {
        binding.tvAppVersion.setText("V" + ApplicationUtils.getInstance(MyApp.getContext()).getVersionName());

        binding.includeUseAgreement.tvSettingsName.setText(R.string.activity_about_us_use_agreement);
        binding.includeUsePrivacyPolicy.tvSettingsName.setText(R.string.activity_about_us_privacy_policy);
        binding.includeVersion.tvSettingsName.setText(R.string.activity_about_us_version_log);
        binding.includeProductGuide.tvSettingsName.setText(R.string.activity_about_us_product_guide);
        binding.includeCheckVersion.tvSettingsName.setText(R.string.activity_about_us_check_version);

        binding.includeUsePrivacyPolicy.llSettingsItem.setOnClickListener(new AboutUsListener(ITEM_PRIVACY_POLICY));
        binding.includeVersion.llSettingsItem.setOnClickListener(new AboutUsListener(ITEM_VERSION_LOG));
        binding.includeUseAgreement.llSettingsItem.setOnClickListener(new AboutUsListener(ITEM_USE_AGREE));
        binding.includeProductGuide.llSettingsItem.setOnClickListener(new AboutUsListener(ITEM_PRODUCT_GUIDE));
        binding.includeCheckVersion.llSettingsItem.setOnClickListener(new AboutUsListener(ITEM_CHECK_VERSION));

        binding.includeVersion.llItemRoot.setVisibility(View.GONE);
        binding.includeProductGuide.llItemRoot.setVisibility(View.GONE);
    }


    public class AboutUsListener implements View.OnClickListener {

        private int item;

        public AboutUsListener(int item) {
            this.item = item;
        }

        @Override
        public void onClick(View v) {
            switch (item) {
                case ITEM_USE_AGREE:
                    WebViewActivity.loadUrl(AboutUsActivity.this, Constans.H5.OCPayTermsofService);
                    break;
                case ITEM_CHECK_VERSION:
                    OCPayHttpClientIml.getLastVersion(ACTION_ABOUT_US_GET_LAST_VERSION);
                    showLoading(false);
                    break;
                case ITEM_PRIVACY_POLICY:
                    WebViewActivity.loadUrl(AboutUsActivity.this, Constans.H5.OCPayPrivacyPolicy);
                    break;
                case ITEM_PRODUCT_GUIDE:
//                    WebViewActivity.loadUrl(MyApp.getContext(), Constans.H5.OCPayPrivacyPolicy);
                    break;
                case ITEM_VERSION_LOG:
//                    WebViewActivity.loadUrl(MyApp.getContext(), Constans.H5.);

                    break;

            }

        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.toolbar_menu_icon:
                ContactsCreateActivity.startContactsCreateActivity(AboutUsActivity.this);
                break;
            case R.id.iv_back:
                finish();
                break;

        }

    }


}
