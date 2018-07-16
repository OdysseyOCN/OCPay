package com.ocpay.wallet.activities;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CompoundButton;

import com.ocpay.wallet.Constans;
import com.ocpay.wallet.R;
import com.ocpay.wallet.activities.lockpattern.CreateGestureActivity;
import com.ocpay.wallet.databinding.ActivitySystemSettingsBinding;
import com.ocpay.wallet.http.rx.RxBus;
import com.ocpay.wallet.utils.FingerprintUtils;
import com.ocpay.wallet.utils.OCPPrefUtils;
import com.ocpay.wallet.widget.dialog.VerifyFingerprintDialog;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class SystemSettingsActivity extends BaseActivity implements View.OnClickListener {


    private ActivitySystemSettingsBinding binding;
    public static final int ITEM_LANGUAGE = 0;
    public static final int ITEM_CURRENCY = 1;
    public static final int PROTECT_MODE_GESTURE = 1;
    public static final int PROTECT_MODE_FINGERPRINT = 2;
    public static final int PROTECT_MODE_NOT_DEFINED = 0;
    public static final int PROTECT_MODE_ACCOUNT = 4;

    private int protectMode;


    public static void startSystemSettingsActivity(Activity activity) {
        Intent intent = new Intent(activity, SystemSettingsActivity.class);
        activity.startActivity(intent);

    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(SystemSettingsActivity.this, R.layout.activity_system_settings);
        initActionBar();
        initView();
        initListener();
        initRxBus();

    }

    private void initListener() {


    }

    private void initRxBus() {
        Disposable disposable = RxBus.getInstance()
                .toObservable(Constans.RXBUS.ACTION_ENABLE_GESTURE, Boolean.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) OCPPrefUtils.setEnableProtectMode(PROTECT_MODE_GESTURE);
                        OCPPrefUtils.setEnableProtect(aBoolean);
                        OCPPrefUtils.setLastTimeOfOnPause();

                    }
                });

        addDisposable(disposable);
    }


    private void initActionBar() {
        binding.includeActionBar.actionBarTitle.setText(R.string.activity_system_settings);
        binding.includeActionBar.llToolbar.setBackgroundColor(getResources().getColor(R.color.white));
        binding.includeActionBar.actionBarTitle.setTextColor(getResources().getColor(R.color.color_text_main));
        binding.includeActionBar.ivBack.setImageResource(R.mipmap.ic_back);
        binding.includeActionBar.toolbarMenuIcon.setVisibility(View.GONE);
        binding.includeActionBar.ivBack.setOnClickListener(this);

    }


    private void initView() {
        binding.includeLanguage.tvSettingsName.setText(R.string.activity_settings_language);
        binding.includeLanguage.llSettingsItem.setVisibility(View.GONE);
        binding.includeUnit.tvSettingsName.setText(R.string.activity_settings_unit);
        binding.includeUnit.llSettingsItem.setOnClickListener(new SettingsListener(ITEM_LANGUAGE, this));
        binding.includeUnit.llSettingsItem.setOnClickListener(new SettingsListener(ITEM_CURRENCY, this));


        protectMode = getProtectMode();

        binding.shTouch.setChecked(OCPPrefUtils.isEnableProtect());
        binding.shTouch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    if (protectMode == PROTECT_MODE_GESTURE) {
                        verifyGesture();
                    } else {
                        verifyFingerprint();
                    }
                } else {
                    OCPPrefUtils.setEnableProtectMode(PROTECT_MODE_NOT_DEFINED);
                    OCPPrefUtils.setEnableProtect(false);
                }

            }
        });
    }

    @Override
    protected void onResume() {
        if (OCPPrefUtils.getEnableProtectMode() == PROTECT_MODE_NOT_DEFINED) {
            binding.shTouch.setChecked(false);
        }
        super.onResume();
    }

    public void verifyFingerprint() {
        VerifyFingerprintDialog instance = VerifyFingerprintDialog.getInstance(SystemSettingsActivity.this);
        instance.setListener(new VerifyFingerprintDialog.OnVerifyFingerprintListener() {
            @Override
            public void verifySuccessful() {
                OCPPrefUtils.setEnableProtectMode(PROTECT_MODE_FINGERPRINT);
                OCPPrefUtils.setEnableProtect(true);
                OCPPrefUtils.setLastTimeOfOnPause();
                WalletLoginActivity.setNotFirstStart(true);
            }

            @Override
            public void cancel() {
                binding.shTouch.setChecked(false);

            }
        });
        instance.show();
    }


    public void verifyGesture() {
        CreateGestureActivity.startCreateGestureActivity(this);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.toolbar_menu_icon:
                ContactsCreateActivity.startContactsCreateActivity(SystemSettingsActivity.this);
                break;
            case R.id.iv_back:
                finish();
                break;

        }

    }


    public int getProtectMode() {
        int enableProtectMode = OCPPrefUtils.getEnableProtectMode();
        if (enableProtectMode != PROTECT_MODE_NOT_DEFINED) {
            return enableProtectMode;
        }
        boolean canUseFingerprint = FingerprintUtils.canUseFingerprint();

        return canUseFingerprint ? PROTECT_MODE_FINGERPRINT : PROTECT_MODE_GESTURE;

    }


    public static class SettingsListener implements View.OnClickListener {
        private Activity mActivity;
        private int item;

        public SettingsListener(int item, Activity mActivity) {
            this.mActivity = mActivity;
            this.item = item;
        }

        @Override
        public void onClick(View v) {
            switch (item) {
                case ITEM_LANGUAGE:
                    LanguageActivity.startLanguageActivity(mActivity);

                    break;
                case ITEM_CURRENCY:
                    CurrencyUnitActivity.startAboutActivity(mActivity);
                    break;
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VerifyFingerprintDialog.getInstance(this).destroy();
    }
}
