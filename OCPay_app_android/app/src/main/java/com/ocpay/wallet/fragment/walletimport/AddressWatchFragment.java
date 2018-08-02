package com.ocpay.wallet.fragment.walletimport;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.ocpay.wallet.AnalyticsEvent;
import com.ocpay.wallet.Constans;
import com.ocpay.wallet.MyApp;
import com.ocpay.wallet.OCPWallet;
import com.ocpay.wallet.R;
import com.ocpay.wallet.bean.QRCodeBean;
import com.ocpay.wallet.databinding.FragmentAddressWatchBinding;
import com.ocpay.wallet.greendao.WalletInfo;
import com.ocpay.wallet.greendao.manager.WalletInfoDaoUtils;
import com.ocpay.wallet.http.rx.RxBus;
import com.ocpay.wallet.manager.AnalyticsManager;
import com.ocpay.wallet.utils.CommonUtils;
import com.ocpay.wallet.utils.OCPPrefUtils;
import com.ocpay.wallet.utils.eth.util.AES;
import com.ocpay.wallet.widget.dialog.QRCodeScanDialog;
import com.ocpay.wallet.widget.dialog.QRCodeSignDialog;
import com.ocpay.wallet.widget.dialog.WarmDialog;
import com.snow.commonlibrary.log.MyLog;
import com.snow.commonlibrary.utils.StringUtil;
import com.snow.commonlibrary.widget.webview.WebViewActivity;

import org.spongycastle.jcajce.provider.digest.MD5;

import java.util.Random;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static com.ocpay.wallet.Constans.ERROR.WALLET_EXIST;
import static com.ocpay.wallet.Constans.RXBUS.ACTION_IMPORT_WALLET_WATCH;
import static com.ocpay.wallet.Constans.WALLET.WALLET_TYPE_WATCHING;
import static com.ocpay.wallet.utils.eth.OCPWalletUtils.getImportWalletName;
import static com.ocpay.wallet.widget.dialog.QRCodeScanDialog.WATCH_MODE;
import static com.ocpay.wallet.widget.dialog.QRCodeSignDialog.MODE_SIGN;
import static com.snow.commonlibrary.utils.EncodeUtils.encodeByMD5;

public class AddressWatchFragment extends BaseImportWalletFragment<FragmentAddressWatchBinding> implements View.OnClickListener {

    private String key;
    private String randomCode;

    @Override
    public int setContentView() {
        return R.layout.fragment_address_watch;
    }

    @Override
    public void loadData() {

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initListener();
        initRXBus();
        initRandomCode();
    }

    private void initRandomCode() {
        Random random = new Random();
        randomCode = String.format("%04d", random.nextInt(10000));
    }


    private void initRXBus() {
        Disposable disposable = RxBus.getInstance()
                .toObservable(ACTION_IMPORT_WALLET_WATCH, String.class)
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        bindingView.etAddress.setText(s);
                    }
                });
        addDisposable(disposable);
    }

    private boolean checkInput() {

        //rep pwd
        String address = bindingView.etAddress.getText().toString().trim();
        if (StringUtil.isEmpty(address)) {
            WarmDialog.showTip(getActivity(), "Invalid Address");
            return false;
        }
        // sql repeat
        if (sqlRepeatAddress(address)) return false;
        return true;

    }


    private void initListener() {
        bindingView.tvActionImport.setOnClickListener(this);
        bindingView.tvAboutOfflineSignature.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_action_import:
                AnalyticsManager.getInstance().sendEvent(AnalyticsEvent.ImportWalletObserved_button_StartImporting);
                boolean b = checkInput();
                if (!b) return;
                verifyWallet_ShowData();
                break;
            case R.id.tv_about_offline_signature:

                WebViewActivity.loadUrl(getActivity(), Constans.H5.Howdoesofflinesigningwork, null);

                break;

        }
    }


    private void verifyWallet_ShowData() {
        QRCodeSignDialog codeSignDialog = QRCodeSignDialog.getInstance(getActivity());
        codeSignDialog.show();
        codeSignDialog.changeType(MODE_SIGN);
        codeSignDialog.setListener(new QRCodeSignDialog.OnNextListener() {
            @Override
            public void onNext() {
                QRCodeScanDialog scanDialog = QRCodeScanDialog.getInstance(getActivity());
                scanDialog.setListener(new QRCodeScanDialog.OnNextListener() {
                    @Override
                    public void onNext(String code) {
                        try {
                            String codeMD5 = encodeByMD5(randomCode);
                            if (codeMD5.equals(code)) {
                                importWatchWallet();
                            } else {
                                WarmDialog.showTip(getActivity(), getString(R.string.sign_fail));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            WarmDialog.showTip(getActivity(), getString(R.string.sign_fail));
                        }
                    }
                });
                scanDialog.show();
                scanDialog.changeMode(WATCH_MODE);
            }
        });
        QRCodeBean qrCodeBean = new QRCodeBean(bindingView.etAddress.getText().toString().trim(), Constans.QRCODE.MODE_DATA_SIGN, randomCode);
        codeSignDialog.setData(CommonUtils.Object2Json(qrCodeBean));

    }


    private void importWatchWallet() {
        int requestId = getRequestId();
        WalletInfo walletInfo = new WalletInfo();
        walletInfo.setBackup(true);
        String address = bindingView.etAddress.getText().toString().trim();
        WalletInfo sqlWalletInfo = WalletInfoDaoUtils.sqlByAddress(MyApp.getContext(), address);
        if (sqlWalletInfo != null) {
            WarmDialog.showTip(getActivity(), getErrorTip(WALLET_EXIST));
            return;
        }
        walletInfo.setWalletAddress(address);
        walletInfo.setWalletType(WALLET_TYPE_WATCHING);
        walletInfo.setBackup(true);
        walletInfo.setWalletName(getImportWalletName(bindingView.etAddress.getText().toString().trim()));
        walletInfo.setStartBlock(OCPPrefUtils.getLastBlockNo());
        WalletInfoDaoUtils.insertWalletInfo(walletInfo, MyApp.getContext());
        OCPWallet.setCurrentWallet(walletInfo);
        importWalletComplete(requestId);
    }

    @Override
    public void onDestroyView() {
        WarmDialog.getInstance(getActivity()).destroy();
        super.onDestroyView();
    }
}
