package com.ocpay.wallet.fragment.walletimport;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;

import com.ocpay.wallet.AnalyticsEvent;
import com.ocpay.wallet.Constans;
import com.ocpay.wallet.R;
import com.ocpay.wallet.databinding.FragmentMnemonicBinding;
import com.ocpay.wallet.http.rx.RxBus;
import com.ocpay.wallet.manager.AnalyticsManager;
import com.ocpay.wallet.utils.CheckInputManager;
import com.ocpay.wallet.utils.OCPWalletPasswordHelp;
import com.ocpay.wallet.utils.eth.OCPWalletUtils;
import com.ocpay.wallet.widget.WalletPathSelectorDialog;
import com.ocpay.wallet.widget.dialog.WarmDialog;
import com.snow.commonlibrary.utils.RegularExpressionUtils;
import com.snow.commonlibrary.widget.webview.WebViewActivity;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.WalletFile;

import io.github.novacrypto.bip39.MnemonicValidator;
import io.github.novacrypto.bip39.Validation.InvalidChecksumException;
import io.github.novacrypto.bip39.Validation.InvalidWordCountException;
import io.github.novacrypto.bip39.Validation.UnexpectedWhiteSpaceException;
import io.github.novacrypto.bip39.Validation.WordNotFoundException;
import io.github.novacrypto.bip39.wordlists.English;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.ocpay.wallet.Constans.RXBUS.ACTION_IMPORT_WALLET_MNEMONIC;
import static com.snow.commonlibrary.utils.ViewUtils.dp2px;

public class MnemonicFragment extends BaseImportWalletFragment<FragmentMnemonicBinding> implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private WalletPathSelectorDialog mDialog;

    @Override
    public int setContentView() {
        return R.layout.fragment_mnemonic;
    }

    @Override
    public void loadData() {

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initListener();
        initView();
        initRXBUS();
    }

    private void initRXBUS() {
        Disposable disposable = RxBus.getInstance()
                .toObservable(ACTION_IMPORT_WALLET_MNEMONIC, String.class)
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        bindingView.etMnemonic.setText(s);
                    }
                });
        addDisposable(disposable);
    }

    private void initView() {
        mDialog = WalletPathSelectorDialog.getInstance(getActivity(), R.style.CustomDialog, getActivity());
        bindingView.etPath.setClickable(false);
        bindingView.tvActionImport.setClickable(false);
        mDialog.setListener(new WalletPathSelectorDialog.OnPathSelectorListener() {
            @Override
            public void onSelect(int pathType, String path) {
                bindingView.etPath.setClickable(pathType == WalletPathSelectorDialog.Custom);
                bindingView.etPath.setText(path);
            }
        });
        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                bindingView.ivBtnShowPath.setBackgroundResource(R.mipmap.ic_list_close);
            }
        });


    }


    private boolean checkInput() {


        //Mnemonic
        String mnemonic = bindingView.etMnemonic.getText().toString().trim();
        boolean validMnemonic = RegularExpressionUtils.valid(mnemonic, Constans.REGULAR.REGULAR_MNEMONIC);
        if (!validMnemonic) {
            WarmDialog.showTip(getActivity(), "Mnemonic is error");
            return false;
        }
        //path
        String path = bindingView.etPath.getText().toString().trim();
        if (path.split("/").length != 6) {
            WarmDialog.showTip(getActivity(), "path is error");
            return false;
        }


        boolean status = OCPWalletPasswordHelp.checkInput(getActivity(),
                null,
                bindingView.etPwd.getText().toString().trim(),
                bindingView.etCheckPwd.getText().toString().trim(),
                false);


        if (!status) return false;

//        //pwd
//        String pwd = bindingView.etPwd.getText().toString().trim();
//        if (pwd.length() <= 8) {
//            Toast.makeText(MyApp.getContext(), "password length  is short", Toast.LENGTH_LONG).show();
//            return false;
//        }
//        //rep pwd
//        String repPwd = bindingView.etCheckPwd.getText().toString().trim();
//        if (pwd.equals(repPwd)) {
//            Toast.makeText(MyApp.getContext(), "password is different", Toast.LENGTH_LONG).show();
//            return false;
//        }

        return true;

    }


    private void initListener() {
        bindingView.tvActionImport.setOnClickListener(this);
        bindingView.include.cbPrivacyPolicy.setOnCheckedChangeListener(this);
        bindingView.include.llCheckPolicy.setOnClickListener(this);
        bindingView.include.tvPrivacyPolicy.setOnClickListener(this);
        bindingView.tvAboutMnemonic.setOnClickListener(this);
        bindingView.ivBtnShowPath.setOnClickListener(this);
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
                AnalyticsManager.getInstance().sendEvent(AnalyticsEvent.ImportWalletMnemonic_button_StartImporting);
                boolean b = checkInput();
                if (!b) return;
                importByMnemonic();
                break;
            case R.id.ll_check_policy:
                bindingView.include.cbPrivacyPolicy.setChecked(!bindingView.include.cbPrivacyPolicy.isChecked());
                break;

            case R.id.tv_privacy_policy:
                WebViewActivity.loadUrl(getActivity(), Constans.H5.OCPayPrivacyPolicy, null);
                break;
            case R.id.tv_about_mnemonic:
                WebViewActivity.loadUrl(getActivity(), Constans.H5.Whatisamnemonic, null);
                break;

            case R.id.iv_btn_show_path:
                showDialog();
                break;


        }
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        setCheckBoxStatus(getContext(), isChecked, bindingView.tvActionImport);
    }


    public void importByMnemonic() {
        String mnemonic = bindingView.etMnemonic.getText().toString().trim();


        try {
            MnemonicValidator
                    .ofWordList(English.INSTANCE)
                    .validate(mnemonic);
        } catch (Exception e) {
            e.printStackTrace();
            WarmDialog.showTip(getActivity(), "Mnemonic is error");
            return;

        }
        final int requestId = getRequestId();
        Observable.just(mnemonic)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .map(new Function<String, Integer>() {
                    @Override
                    public Integer apply(String o) throws Exception {
                        WalletFile walletFile = null;
                        try {
                            walletFile = OCPWalletUtils.getWalletFileByMnemonic(o, bindingView.etPath.getText().toString().trim(), bindingView.etPwd.getText().toString().trim());
                        } catch (CipherException e) {
                            e.printStackTrace();
                            return Constans.ERROR.WALLET_IMPORT_FAIL;
                        }
                        if (walletFile == null) return Constans.ERROR.WALLET_IMPORT_FAIL;
                        return saveWallet(walletFile,bindingView.etPwdTip.getText().toString());

                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObserver(requestId));


    }


    private void showDialog() {
        Window win = mDialog.getWindow();
        win.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        WindowManager.LayoutParams lp = win.getAttributes();
        float getY = bindingView.viewLinePath.getY();
        lp.y = (int) getY + dp2px(getContext(), 100);
        bindingView.ivBtnShowPath.setBackgroundResource(R.mipmap.ic_list_open);
        mDialog.show();

    }

    @Override
    public void onDestroy() {
        if (mDialog != null) {
            mDialog.destroy();
        }
        WarmDialog.getInstance(getActivity()).destroy();
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
