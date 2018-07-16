package com.ocpay.wallet.activities;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import com.ocpay.wallet.R;
import com.ocpay.wallet.bean.CountryData;
import com.ocpay.wallet.bean.requestBean.RequestRegisterBean;
import com.ocpay.wallet.databinding.ActivitySignInBinding;
import com.ocpay.wallet.http.client.OCPayHttpClientIml;
import com.ocpay.wallet.bean.response.SignInResponse;
import com.ocpay.wallet.http.rx.RxBus;
import com.ocpay.wallet.utils.CountryUtils;
import com.ocpay.wallet.utils.OCPPrefUtils;
import com.ocpay.wallet.widget.dialog.WarmDialog;
import com.snow.commonlibrary.utils.PrefUtils;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static com.ocpay.wallet.Constans.RXBUS.ACTION_ACCOUNT_SIGN_IN;
import static com.ocpay.wallet.activities.AccountSignUpActivity.MODE_RESET;
import static com.ocpay.wallet.activities.AccountSignUpActivity.MODE_SIGN_UP;
import static com.ocpay.wallet.utils.CountryUtils.COUNTRY_INDEX;
import static com.ocpay.wallet.utils.CountryUtils.initCountryIndex;
import static com.ocpay.wallet.utils.OCPWalletPasswordHelp.checkInput;

public class AccountSignInActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private ActivitySignInBinding binding;
    private static final String Action_Request = "wallet_create_request";
    private static final int REQUEST_CODE = 0x12;
    private int requestId;
    private CountryData countryData;


    public static void startAccountSignInActivity(Activity activity) {
        if (activity == null) return;
        Intent intent = new Intent(activity, AccountSignInActivity.class);
//        intent.putExtra(Action_Request, requestId);
        activity.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in);
//        requestId = getIntent().getIntExtra(Action_Request, -1);
        initDataBinding();
        initActionBar();
        initListener();
        initRxBus();
        initView();

    }

    private void initRxBus() {
        Disposable signIn = RxBus.getInstance().toObservable(ACTION_ACCOUNT_SIGN_IN, SignInResponse.class)
                .subscribe(new Consumer<SignInResponse>() {
                    @Override
                    public void accept(SignInResponse signInResponse) throws Exception {
                        dismissLoading();
                        if (signInResponse.getSuccess()) {
                            finish();
                            WarmDialog.showTip(AccountSignInActivity.this, "Sign In Successful !");
                            OCPPrefUtils.setAccessToken(signInResponse.getData().getAccessToken());
                        } else {
                            String tip = signInResponse.getData().getMessage();
                            WarmDialog.showTip(AccountSignInActivity.this, tip);
                        }


                    }
                });
        addDisposable(signIn);


    }

    private void initView() {

        initCountryIndex();
        countryData = CountryUtils.getCountryBean(this).get(COUNTRY_INDEX);
        updateCountry();


    }

    private void updateCountry() {
        String countryDial = countryData != null ? countryData.getName() + countryData.getDial_code() : "--";

        binding.tvCountryCode.setText(countryDial);
    }


    private void initListener() {


    }

    private void initActionBar() {
        binding.includeActionBar.actionBarTitle.setText(getResources().getString(R.string.activity_title_sign_in));
        binding.includeActionBar.ivBack.setImageResource(R.mipmap.ic_close_black);
        binding.includeActionBar.actionBarTitle.setTextColor(getResources().getColor(R.color.color_text_main));
        binding.includeActionBar.ivBack.setOnClickListener(this);
        binding.includeActionBar.viewLine.setVisibility(View.VISIBLE);
    }


    private void initDataBinding() {
        binding.tvSignIn.setOnClickListener(this);
        binding.tvSignUp.setOnClickListener(this);
        binding.llCountrySelected.setOnClickListener(this);
        binding.tvForget.setOnClickListener(this);

    }


    private void signIn() {

        String password = binding.etPassword.getText().toString().trim();
        String phoneNumber = binding.etPhoneNumber.getText().toString().trim();
        //  check input
        boolean status = checkInput(AccountSignInActivity.this,
                password,
                password
        );
        if (!status) return;


        int dialCodeInteger = countryData.getDialCodeInteger();
        boolean isValid = CountryUtils.checkPhoneNumber(phoneNumber, dialCodeInteger);
        if (!isValid) {
            WarmDialog.showTip(this, getString(R.string.tip_invalid_phone_number));
            return;
        }
        showLoading(false);
        RequestRegisterBean requestRegisterBean = new RequestRegisterBean(phoneNumber, dialCodeInteger, "", password);
        OCPayHttpClientIml.getLogin(ACTION_ACCOUNT_SIGN_IN, requestRegisterBean);

    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//        setCheckBoxStatus(isChecked);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_sign_up:
                AccountSignUpActivity.startAccountSignUpActivity(this, MODE_SIGN_UP);
                break;
            case R.id.tv_sign_in:
                signIn();
                break;
            case R.id.ll_country_selected:
                CountryCodeSelectedActivity.startContryCodeSelectedActivity(this, REQUEST_CODE);

                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_forget:
                AccountSignUpActivity.startAccountSignUpActivity(this, MODE_RESET);
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
        WarmDialog.getInstance(AccountSignInActivity.this).destroy();
        super.onDestroy();
    }


}
