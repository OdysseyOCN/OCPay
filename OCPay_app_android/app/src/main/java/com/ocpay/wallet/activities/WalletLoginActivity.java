package com.ocpay.wallet.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;

import com.ocpay.wallet.MyApp;
import com.ocpay.wallet.R;
import com.ocpay.wallet.bean.PickerData;
import com.ocpay.wallet.databinding.ActivityWalletLoginBinding;
import com.ocpay.wallet.greendao.WalletInfo;
import com.ocpay.wallet.greendao.manager.WalletInfoDaoUtils;
import com.ocpay.wallet.utils.OCPPrefUtils;
import com.ocpay.wallet.utils.PickerUtil;
import com.ocpay.wallet.utils.eth.OCPWalletUtils;
import com.ocpay.wallet.widget.dialog.VerifyFingerprintDialog;
import com.ocpay.wallet.widget.dialog.WarmDialog;
import com.snow.commonlibrary.utils.StringUtil;
import com.star.lockpattern.util.LockPatternUtil;
import com.star.lockpattern.widget.LockPatternView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.ocpay.wallet.activities.SystemSettingsActivity.PROTECT_MODE_ACCOUNT;
import static com.ocpay.wallet.activities.SystemSettingsActivity.PROTECT_MODE_FINGERPRINT;
import static com.ocpay.wallet.activities.SystemSettingsActivity.PROTECT_MODE_GESTURE;

public class WalletLoginActivity extends BaseActivity implements View.OnClickListener, PickerUtil.OnWheelViewClick, LockPatternView.OnPatternListener {


    private ActivityWalletLoginBinding binding;
    private LinearLayout expertMode;
    private List<PickerData> pickerData;
    private int selected;
    private List<WalletInfo> walletInfos;
    private String currentAddress;
    private static final long DELAYTIME = 60l;


    private int currentMode;
    private LockPatternView lockPatternView;
    private int lastMode;
    protected static boolean notFirstStart;


    public static void startWalletLoginActivity(Activity activity) {
        Intent intent = new Intent(activity, WalletLoginActivity.class);
        activity.startActivity(intent);
    }

    public static void protectWithLock(Activity activity, boolean isOnResume) {
        if (!OCPPrefUtils.isEnableProtect()) return;

        if (!notFirstStart) {
            notFirstStart = true;
            startWalletLoginActivity(activity);
        }

        //on pause
        if (!isOnResume) {
            OCPPrefUtils.setLastTimeOfOnPause();
            return;
        }

        if (isOnResume) {
            if (SystemClock.elapsedRealtime() - OCPPrefUtils.getLastTimeOnPause() < (DELAYTIME * 1000))
                return;
            startWalletLoginActivity(activity);
        }


    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(WalletLoginActivity.this, R.layout.activity_wallet_login);
        initData();
        initView();

    }

    private void initView() {
        binding.llLoginWalletAddress.setOnClickListener(this);
        binding.tvLoginWalletAddress.setText(OCPWalletUtils.foldWalletAddress(currentAddress));
        binding.ivWalletHead.setBackgroundResource(OCPWalletUtils.getWalletProfilePicture(currentAddress));

        binding.includeLoginFingerprint.llContentLogin.setVisibility(View.GONE);
        binding.includeLoginGesture.llContentLogin.setVisibility(View.VISIBLE);
        binding.includeLoginPassword.llContentLogin.setVisibility(View.GONE);

        lockPatternView = binding.includeLoginGesture.lockPatternView;
        binding.includeLoginGesture.lockPatternView.setOnPatternListener(this);
        binding.tvChangeLoginMode.setOnClickListener(this);
        binding.includeLoginFingerprint.ivVerifyFingerprint.setOnClickListener(this);
        binding.includeLoginPassword.tvCreateWallet.setOnClickListener(this);
        binding.ivFingerprintTip.setOnClickListener(this);
        int protectMode = OCPPrefUtils.getEnableProtectMode();
        currentMode = PROTECT_MODE_ACCOUNT;
        showProtectMode(protectMode);

    }


    public void alertOption(ArrayList<?> list, int selectIndex, PickerUtil.OnWheelViewClick onWheelViewClick) {
        PickerUtil.alertBottomWheelOption(this, list, selectIndex, onWheelViewClick);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ll_login_wallet_address:
                alertOption((ArrayList<PickerData>) pickerData, selected, this);
                break;
            case R.id.tv_create_wallet:
                actionLogin();
                break;
            case R.id.tv_change_login_mode:
                showProtectMode(lastMode);
                break;

            case R.id.iv_fingerprint_tip:
                showProtectMode(lastMode);
                break;
            case R.id.iv_verify_fingerprint:
                verifyFingerprint();
                break;
        }
    }

    private void verifyFingerprint() {
        VerifyFingerprintDialog instance = VerifyFingerprintDialog.getInstance(WalletLoginActivity.this);
        instance.setListener(new VerifyFingerprintDialog.OnVerifyFingerprintListener() {
            @Override
            public void verifySuccessful() {
                loginSuccess();
            }

            @Override
            public void cancel() {

            }
        });
        instance.show();

    }

    private void actionLogin() {
        Observable.just("")
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String s) throws Exception {
                        showLoading(false);
                        return s;
                    }
                })
                .observeOn(Schedulers.newThread())
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String s) throws Exception {
                        boolean success = false;
                        try {
                            success = OCPWalletUtils.loginWallet(walletInfos.get(selected).getWalletAddress(), binding.includeLoginPassword.etWalletPassword.getText().toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                            return e.getMessage();
                        }
                        if (!success) {
                            return getResources().getString(R.string.gesture_error);
                        }

                        return s;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<String, Object>() {
                    @Override
                    public Object apply(String s) throws Exception {
                        dismissLoading();
                        if (!StringUtil.isEmpty(s)) {
                            WarmDialog.showTip(WalletLoginActivity.this, s);
                        } else {
                            loginSuccess();
                        }
                        return s;
                    }
                })
                .subscribe();
    }


    public void initData() {
        Object bean = OCPPrefUtils.getCurrentWallet();
        walletInfos = WalletInfoDaoUtils.sqlAll(MyApp.getContext());
        currentAddress = "";

        if (bean != null && (bean instanceof WalletInfo)) {
            currentAddress = ((WalletInfo) bean).getWalletAddress();
        } else {
            if (walletInfos != null && walletInfos.size() > 0) {
                currentAddress = walletInfos.get(0).getWalletAddress();
            }
        }

        if (StringUtil.isEmpty(currentAddress)) {
            //todo 创建wallet
            return;
        }

        pickerData = new ArrayList<>();
        selected = -1;
        for (int i = 0; i < walletInfos.size(); i++) {
            if (currentAddress.equals(walletInfos.get(i).getWalletAddress())) {
                selected = i;
            }
            pickerData.add(new PickerData(i + "", OCPWalletUtils.foldWalletAddress(walletInfos.get(i).getWalletAddress())));
        }

    }


    @Override
    public void onClick(View view, int position) {
        selected = position;
        binding.tvLoginWalletAddress.setText(OCPWalletUtils.foldWalletAddress(walletInfos.get(position).getWalletAddress()));
        binding.ivWalletHead.setBackgroundResource(OCPWalletUtils.getWalletProfilePicture(walletInfos.get(position).getWalletAddress()));

    }


    public void showProtectMode(int mode) {
        if (currentMode == mode) return;
        lastMode = currentMode;
        currentMode = mode;
        switch (mode) {
            case PROTECT_MODE_GESTURE:
                setVisibleMode(View.VISIBLE, View.GONE, View.GONE);
                binding.tvChangeLoginMode.setText("Account password login");
                binding.ivFingerprintTip.setVisibility(View.GONE);
                binding.tvChangeLoginMode.setVisibility(View.VISIBLE);
                break;
            case PROTECT_MODE_ACCOUNT:
                setVisibleMode(View.GONE, View.VISIBLE, View.GONE);
                String modeTip = lastMode == PROTECT_MODE_GESTURE ? "Gesture login" : "Fingerprint Login";
                binding.tvChangeLoginMode.setText(modeTip);
                if (lastMode == PROTECT_MODE_FINGERPRINT) {
                    binding.tvChangeLoginMode.setVisibility(View.GONE);
                    binding.ivFingerprintTip.setVisibility(View.VISIBLE);
                } else {
                    binding.tvChangeLoginMode.setVisibility(View.VISIBLE);
                }

                break;
            case SystemSettingsActivity.PROTECT_MODE_FINGERPRINT:
                setVisibleMode(View.GONE, View.GONE, View.VISIBLE);
//                setupFingerprintStuff();
                verifyFingerprint();
                binding.tvChangeLoginMode.setText("Account password login");
                binding.ivFingerprintTip.setVisibility(View.GONE);
                binding.tvChangeLoginMode.setVisibility(View.VISIBLE);
                break;
        }

    }

    public void setVisibleMode(int gesture, int account, int fingerprint) {
        binding.ivFingerprintTip.setVisibility(fingerprint);
        binding.includeLoginPassword.llContentLogin.setVisibility(account);
        binding.includeLoginFingerprint.llContentLogin.setVisibility(fingerprint);
        binding.includeLoginGesture.llContentLogin.setVisibility(gesture);
    }


    @Override
    public void onPatternStart() {
        lockPatternView.removePostClearPatternRunnable();

    }

    @Override
    public void onPatternComplete(List<LockPatternView.Cell> pattern) {
        if (pattern != null) {
            if (LockPatternUtil.checkPattern(pattern, OCPPrefUtils.getGesturePassword())) {
                updateStatus(Status.CORRECT);
            } else {
                updateStatus(Status.ERROR);
            }
        }
    }


    private void loginSuccess() {
        //todo login
        finish();
        OCPPrefUtils.setLastTimeOfOnPause();
    }

    /**
     * 更新状态
     *
     * @param status
     */
    private void updateStatus(Status status) {
        binding.includeLoginGesture.messageTv.setText(status.strId);
        binding.includeLoginGesture.messageTv.setTextColor(getResources().getColor(status.colorId));
        switch (status) {
            case DEFAULT:
                lockPatternView.setPattern(LockPatternView.DisplayMode.DEFAULT);
                break;
            case ERROR:
                lockPatternView.setPattern(LockPatternView.DisplayMode.ERROR);
                lockPatternView.postClearPatternRunnable(DELAYTIME);
                break;
            case CORRECT:
                lockPatternView.setPattern(LockPatternView.DisplayMode.DEFAULT);
                loginSuccess();
                break;
        }
    }


    private enum Status {
        //默认的状态
        DEFAULT(R.string.gesture_default, R.color.grey_a5a5a5),
        //密码输入错误
        ERROR(R.string.gesture_error, R.color.red_f4333c),
        //密码输入正确
        CORRECT(R.string.gesture_correct, R.color.grey_a5a5a5);

        private Status(int strId, int colorId) {
            this.strId = strId;
            this.colorId = colorId;
        }

        private int strId;
        private int colorId;
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean filterProtectLock() {
        return true;
    }


    public static void setNotFirstStart(boolean notFirstStart) {
        WalletLoginActivity.notFirstStart = notFirstStart;
    }


    public void setupFingerprintStuff() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return;

        FingerprintManager fingerprintManager = (FingerprintManager) MyApp.getContext().getSystemService(Context.FINGERPRINT_SERVICE);

        FingerprintManager.AuthenticationCallback mCallBack = new FingerprintManager.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                setupFingerprintStuff();

            }

            @Override
            public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                loginSuccess();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                setupFingerprintStuff();
            }
        };
        CancellationSignal mCancellationSignal = new CancellationSignal();


        fingerprintManager.authenticate(null, mCancellationSignal, 0, mCallBack, null);

    }
}
