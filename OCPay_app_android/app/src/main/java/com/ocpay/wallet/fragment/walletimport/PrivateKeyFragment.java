package com.ocpay.wallet.fragment.walletimport;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CompoundButton;

import com.ocpay.wallet.AnalyticsEvent;
import com.ocpay.wallet.Constans;
import com.ocpay.wallet.R;
import com.ocpay.wallet.databinding.FragmentPrivateKeyBinding;
import com.ocpay.wallet.http.rx.RxBus;
import com.ocpay.wallet.manager.AnalyticsManager;
import com.ocpay.wallet.utils.CheckInputManager;
import com.ocpay.wallet.utils.OCPWalletPasswordHelp;
import com.ocpay.wallet.utils.eth.OCPWalletUtils;
import com.snow.commonlibrary.widget.webview.WebViewActivity;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.WalletFile;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.ocpay.wallet.Constans.RXBUS.ACTION_IMPORT_WALLET_PRIVATE_KEY;

public class PrivateKeyFragment extends BaseImportWalletFragment<FragmentPrivateKeyBinding> implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    @Override
    public int setContentView() {
        return R.layout.fragment_private_key;
    }

    @Override
    public void loadData() {

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initListener();
        initRXBus();
    }


    private void initRXBus() {
        Disposable disposable = RxBus.getInstance()
                .toObservable(ACTION_IMPORT_WALLET_PRIVATE_KEY, String.class)
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        bindingView.etPrivateKey.setText(s);
                    }
                });
        addDisposable(disposable);
    }


    private boolean checkInput() {


        boolean status = OCPWalletPasswordHelp.checkInput(getActivity(),
                null,
                bindingView.etPwd.getText().toString().trim(),
                bindingView.etCheckPwd.getText().toString().trim(),
                false);

        if (!status) return false;

        return true;

    }


    private void initListener() {
        bindingView.tvActionImport.setClickable(false);
        bindingView.tvActionImport.setOnClickListener(this);
        bindingView.include.cbPrivacyPolicy.setOnCheckedChangeListener(this);
        bindingView.include.llCheckPolicy.setOnClickListener(this);
        bindingView.include.tvPrivacyPolicy.setOnClickListener(this);
        bindingView.tvAboutPrivateKey.setOnClickListener(this);
        bindingView.etPwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                bindingView.tvPwdTip.setVisibility(View.VISIBLE);
                bindingView.tvPwdLv.setVisibility(View.VISIBLE);
                bindingView.ivPwdLv.setVisibility(View.VISIBLE);


            }
        });
        setTextChangedListener(bindingView.etPwd, bindingView.tvPwdLv, bindingView.tvPwdTip, bindingView.ivPwdLv);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_action_import:
                AnalyticsManager.getInstance().sendEvent(AnalyticsEvent.ImportWalletPrivatekey_button_StartImporting);
                boolean b = checkInput();
                if (!b) return;
                importByPrivateKey();
                break;
            case R.id.ll_check_policy:
                bindingView.include.cbPrivacyPolicy.setChecked(!bindingView.include.cbPrivacyPolicy.isChecked());

                break;

            case R.id.tv_privacy_policy:
                WebViewActivity.loadUrl(getActivity(), Constans.H5.OCPayPrivacyPolicy, null);
                break;
            case R.id.tv_about_private_key:
                WebViewActivity.loadUrl(getActivity(), Constans.H5.Whatisaprivatekey, null);
                break;


        }
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        setCheckBoxStatus(getContext(), isChecked, bindingView.tvActionImport);

    }


    private void importByPrivateKey() {

        final int requestId = getRequestId();
        String privateKey = bindingView.etPrivateKey.getText().toString().trim();

        Observable.just(privateKey)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .map(new Function<String, Integer>() {
                    @Override
                    public Integer apply(String privateKey) throws Exception {

                        WalletFile walletFile = null;
                        try {
                            walletFile = OCPWalletUtils.getWalletFileByPrivateKey(privateKey, bindingView.etPwd.getText().toString().trim());
                        } catch (CipherException e) {
                            e.printStackTrace();
                            return Constans.ERROR.WALLET_IMPORT_FAIL;
                        }
                        if (walletFile == null) return Constans.ERROR.WALLET_IMPORT_FAIL;
                        return saveWallet(walletFile, bindingView.etPwdTip.getText()==null?"": bindingView.etPwdTip.getText().toString());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObserver(requestId));


    }
}
