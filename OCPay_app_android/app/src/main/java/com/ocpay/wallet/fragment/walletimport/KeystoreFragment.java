package com.ocpay.wallet.fragment.walletimport;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CompoundButton;

import com.ocpay.wallet.AnalyticsEvent;
import com.ocpay.wallet.Constans;
import com.ocpay.wallet.R;
import com.ocpay.wallet.databinding.FragmentKeystoreBinding;
import com.ocpay.wallet.http.rx.RxBus;
import com.ocpay.wallet.manager.AnalyticsManager;
import com.ocpay.wallet.utils.eth.OCPWalletUtils;
import com.ocpay.wallet.widget.dialog.WarmDialog;
import com.snow.commonlibrary.utils.StringUtil;
import com.snow.commonlibrary.widget.webview.WebViewActivity;

import org.web3j.crypto.WalletFile;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.ocpay.wallet.Constans.RXBUS.ACTION_IMPORT_WALLET_KEYSTORE;

public class KeystoreFragment extends BaseImportWalletFragment<FragmentKeystoreBinding> implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    @Override
    public int setContentView() {
        return R.layout.fragment_keystore;
    }

    @Override
    public void loadData() {

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initListener();
        initRXBUS();
    }


    private void initRXBUS() {
        Disposable disposable = RxBus.getInstance()
                .toObservable(ACTION_IMPORT_WALLET_KEYSTORE, String.class)
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        bindingView.etKeystore.setText(s);
                    }
                });
        addDisposable(disposable);
    }


    private boolean checkInput() {
        //pwd
        String pwd = bindingView.etPwd.getText().toString().trim();
        if (StringUtil.isEmpty(pwd)) {
            WarmDialog.showTip(getActivity(), "password not null");
            return false;
        }

        return true;

    }


    private void initListener() {
        bindingView.tvActionImport.setClickable(false);
        bindingView.tvActionImport.setOnClickListener(this);
        bindingView.include.cbPrivacyPolicy.setOnCheckedChangeListener(this);
        bindingView.include.llCheckPolicy.setOnClickListener(this);
        bindingView.include.tvPrivacyPolicy.setOnClickListener(this);
        bindingView.tvAboutKeystore.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_action_import:
                AnalyticsManager.getInstance().sendEvent(AnalyticsEvent.ImportWalletOfficialwallet_button_StartImporting);

                boolean b = checkInput();
                if (!b) return;
                importByKeystore();
                break;
            case R.id.ll_check_policy:
                bindingView.include.cbPrivacyPolicy.setChecked(!bindingView.include.cbPrivacyPolicy.isChecked());
                break;
            case R.id.tv_privacy_policy:
                WebViewActivity.loadUrl(getActivity(), Constans.H5.OCPayPrivacyPolicy,null);
                break;
            case R.id.tv_about_keystore:
                WebViewActivity.loadUrl(getActivity(), Constans.H5.Whatisakeystore,null);

                break;


        }
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        setCheckBoxStatus(getContext(), bindingView.include.cbPrivacyPolicy.isChecked(), bindingView.tvActionImport);
    }


    private void importByKeystore() {

        final int requestId = getRequestId();
        String keystore = bindingView.etKeystore.getText().toString().trim();

        Observable.just(keystore)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .map(new Function<String, Integer>() {
                    @Override
                    public Integer apply(String keystore) throws Exception {

                        WalletFile walletFile = OCPWalletUtils.createWalletFileByKeystore(keystore);
                        boolean valid = OCPWalletUtils.valid(bindingView.etPwd.getText().toString().trim(), walletFile);
                        if (walletFile == null || !valid) {
                            return Constans.ERROR.WALLET_IMPORT_FAIL;
                        }

                        return saveWallet(walletFile,"");

                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( getObserver(requestId));


    }


    @Override
    public void onDestroyView() {
        WarmDialog.getInstance(getActivity()).destroy();
        super.onDestroyView();
    }


}
