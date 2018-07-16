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

import com.ocpay.wallet.R;
import com.ocpay.wallet.bean.CountryData;
import com.ocpay.wallet.bean.requestBean.RequestRegisterBean;
import com.ocpay.wallet.databinding.ActivitySignUpBinding;
import com.ocpay.wallet.http.client.OCPayHttpClientIml;
import com.ocpay.wallet.bean.response.OCPResponse;
import com.ocpay.wallet.http.rx.RxBus;
import com.ocpay.wallet.utils.CountryUtils;
import com.ocpay.wallet.widget.dialog.WarmDialog;
import com.snow.commonlibrary.utils.StringUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

import static com.ocpay.wallet.Constans.RXBUS.ACTION_ACCOUNT_SEND_SMS;
import static com.ocpay.wallet.Constans.RXBUS.ACTION_ACCOUNT_SIGN_UP;
import static com.ocpay.wallet.utils.CountryUtils.COUNTRY_INDEX;
import static com.ocpay.wallet.utils.CountryUtils.initCountryIndex;
import static com.ocpay.wallet.utils.OCPWalletPasswordHelp.checkInput;
import static com.ocpay.wallet.utils.OCPWalletPasswordHelp.setPwdLevel;

public class AccountSignUpActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private ActivitySignUpBinding binding;
    public static final int MODE_SIGN_UP = 0x1;
    public static final int MODE_RESET = 0x2;
    private static final String MODE_TYPE = "mode_type";
    private static final int REQUEST_CODE = 0x12;
    private CountryData countryData;
    private int modeType;
    private final long INTERVAL_GET_CODE = 120;
    private String modifyAccessToken;


    public static void startAccountSignUpActivity(Activity activity, int modeType) {
        if (activity == null) return;
        Intent intent = new Intent(activity, AccountSignUpActivity.class);
        intent.putExtra(MODE_TYPE, modeType);
        activity.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
        modeType = getIntent().getIntExtra(MODE_TYPE, MODE_SIGN_UP);
        initDataBinding();
        initActionBar();
        initListener();
        initView();
        initRxBus();

    }

    private void initRxBus() {
        Disposable getCode = RxBus.getInstance().toObservable(ACTION_ACCOUNT_SEND_SMS, OCPResponse.class)
                .subscribe(new Consumer<OCPResponse>() {

                    @Override
                    public void accept(OCPResponse ocpResponse) throws Exception {
                        dismissLoading();
                        if (ocpResponse.isSuccess()) {
                            WarmDialog.showTip(AccountSignUpActivity.this, getString(R.string.tip_send_successful));
                            modifyAccessToken = ocpResponse.getData();
                        } else {
                            WarmDialog.showTip(AccountSignUpActivity.this, ocpResponse.getData());
                        }
                    }
                });

        addDisposable(getCode);
        Disposable signIn = RxBus.getInstance().toObservable(ACTION_ACCOUNT_SIGN_UP, OCPResponse.class)
                .subscribe(new Consumer<OCPResponse>() {
                    @Override
                    public void accept(OCPResponse ocpResponse) throws Exception {
                        dismissLoading();
                        if (ocpResponse.isSuccess()) {
                            WarmDialog instance = WarmDialog.getInstance(AccountSignUpActivity.this);
                            instance.setListen(new WarmDialog.ConfirmListen() {
                                @Override
                                public void onConfirm() {
                                    AccountSignInActivity.startAccountSignInActivity(AccountSignUpActivity.this);
                                    finish();
                                }
                            });
                            instance.show();
                            int tip = modeType == MODE_SIGN_UP ? R.string.tip_sign_up_success : R.string.tip_reset_success;
                            instance.setTip(getString(tip));
                        } else {
                            WarmDialog.showTip(AccountSignUpActivity.this, ocpResponse.getData());
                        }
                    }
                });

        addDisposable(signIn);

    }

    private void initView() {
        int resBtnTxt = modeType != MODE_RESET ? R.string.activity_title_sign_up : R.string.action_confirm;
        binding.tvSignUp.setText(resBtnTxt);

        int visibleSignUp = modeType != MODE_RESET ? View.VISIBLE : View.GONE;

        binding.tvSignIn.setVisibility(visibleSignUp);

        initCountryIndex();

        countryData = CountryUtils.getCountryBean(this).get(COUNTRY_INDEX);

        updateCountry();


    }

    private void updateCountry() {
        String countryDial = countryData != null ? countryData.getName() + countryData.getDial_code() : "--";

        binding.tvCountryCode.setText(countryDial);
    }


    private void initListener() {

        binding.etSignUpPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                binding.tvPwdTip.setVisibility(View.VISIBLE);
                binding.tvPwdLv.setVisibility(View.VISIBLE);
                binding.ivPwdLv.setVisibility(View.VISIBLE);


            }
        });
        binding.etSignUpPassword.addTextChangedListener(new TextWatcher() {
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
        int resTitle = modeType != MODE_RESET ? R.string.activity_title_sign_up : R.string.activity_title_reset_pwd;
        binding.includeActionBar.actionBarTitle.setText(getResources().getString(resTitle));
        binding.includeActionBar.ivBack.setImageResource(R.mipmap.ic_close_black);
        binding.includeActionBar.actionBarTitle.setTextColor(getResources().getColor(R.color.color_text_main));
        binding.includeActionBar.ivBack.setOnClickListener(this);
        binding.includeActionBar.viewLine.setVisibility(View.VISIBLE);
    }


    private void initDataBinding() {
        binding.tvSignIn.setOnClickListener(this);
        binding.tvSignUp.setOnClickListener(this);
        binding.tvGetCode.setOnClickListener(this);
        binding.llCountrySelected.setOnClickListener(this);

    }


    private void signUp() {

        boolean status = checkInput(AccountSignUpActivity.this,
                binding.etSignUpPassword.getText().toString().trim(),
                binding.etRepeatPassword.getText().toString().trim()
        );
        if (!status) return;

        String verifyCode = binding.etVerifyCode.getText().toString().trim();
        String phoneNumber = binding.etPhoneNumber.getText().toString();
        String password = binding.etSignUpPassword.getText().toString().trim();
        String countryCode = countryData.getDial_code().replace("+", "");
        if (!StringUtil.isNumber(verifyCode) || verifyCode == null || verifyCode.length() <= 3) {
            WarmDialog.showTip(this, "Invalid Code !");
        }

        RequestRegisterBean registerBean = new RequestRegisterBean(phoneNumber, Integer.valueOf(countryCode), verifyCode, password);
        if (modeType == MODE_SIGN_UP) {
            OCPayHttpClientIml.getRegister(ACTION_ACCOUNT_SIGN_UP, registerBean);
        } else {
            registerBean.setAccesstoken(modifyAccessToken);
            registerBean.newPassword = password;
            OCPayHttpClientIml.getResetPwd(ACTION_ACCOUNT_SIGN_UP, registerBean);
        }
        showLoading(false);


    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//        setCheckBoxStatus(isChecked);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_sign_up:
                signUp();
                break;
            case R.id.tv_sign_in:
                break;
            case R.id.ll_country_selected:
                CountryCodeSelectedActivity.startContryCodeSelectedActivity(this, REQUEST_CODE);

                break;
            case R.id.iv_back:
                finish();
                break;

            case R.id.tv_get_code:

                String countryCode = countryData.getDial_code().replace("+", "");
                String phoneNumber = binding.etPhoneNumber.getText().toString();
                boolean valid = CountryUtils.checkPhoneNumber(phoneNumber, Integer.valueOf(countryCode));
                if (valid) {
                    intervalGetCode();
                    if (modeType == MODE_SIGN_UP) {
                        OCPayHttpClientIml.sendSMS(ACTION_ACCOUNT_SEND_SMS, new RequestRegisterBean(phoneNumber, Integer.valueOf(countryCode)));
                    } else {
                        RequestRegisterBean requestRegisterBean = new RequestRegisterBean(phoneNumber, Integer.valueOf(countryCode));
                        OCPayHttpClientIml.sendSMSForget(ACTION_ACCOUNT_SEND_SMS, requestRegisterBean);
                    }
                    showLoading(false);

                } else {
                    WarmDialog.showTip(this, getString(R.string.tip_invalid_phone_number));
                }


                break;


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    countryData = CountryUtils.getCountryBean(this).get(COUNTRY_INDEX);
                    updateCountry();
                }
                break;
        }


    }

    @Override
    protected void onDestroy() {
        WarmDialog.getInstance(AccountSignUpActivity.this).destroy();
        super.onDestroy();
    }


    public void intervalGetCode() {

        Observable.interval(0, 1, TimeUnit.SECONDS)
                .take(INTERVAL_GET_CODE - 1)
                .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) throws Exception {
                        return INTERVAL_GET_CODE - aLong;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        changStatus(false);

                    }
                })
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Long aLong) {
                        binding.tvGetCode.setText(aLong + "");

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        changStatus(true);


                    }
                });


    }

    private void changStatus(boolean enable) {
        binding.tvGetCode.setClickable(enable);
        binding.tvGetCode.setText(R.string.tip_get_code);
        int resDrawable = enable ? R.drawable.shape_corner_btn_main_r3 : R.drawable.shape_btn_grave;
        binding.tvGetCode.setBackgroundResource(resDrawable);
        int colorText = enable ? R.color.white : R.color.color_text_main;
        binding.tvGetCode.setTextColor(getResources().getColor(colorText));
    }


}
