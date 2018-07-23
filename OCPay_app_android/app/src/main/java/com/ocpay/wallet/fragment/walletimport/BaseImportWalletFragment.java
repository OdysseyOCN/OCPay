package com.ocpay.wallet.fragment.walletimport;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ocpay.wallet.Constans;
import com.ocpay.wallet.MyApp;
import com.ocpay.wallet.OCPWallet;
import com.ocpay.wallet.R;
import com.ocpay.wallet.fragment.BaseFragment;
import com.ocpay.wallet.greendao.WalletInfo;
import com.ocpay.wallet.greendao.manager.WalletInfoDaoUtils;
import com.ocpay.wallet.http.rx.RxBus;
import com.ocpay.wallet.utils.OCPPrefUtils;
import com.ocpay.wallet.utils.eth.OCPWalletUtils;
import com.ocpay.wallet.utils.eth.bean.OCPWalletFile;
import com.ocpay.wallet.utils.wallet.WalletStorage;
import com.ocpay.wallet.widget.dialog.LoadingDialog;
import com.ocpay.wallet.widget.dialog.WarmDialog;
import com.snow.commonlibrary.utils.RegularExpressionUtils;

import org.web3j.crypto.WalletFile;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static com.ocpay.wallet.Constans.ERROR.OPERATION_SUCCESS;
import static com.ocpay.wallet.Constans.ERROR.WALLET_EXIST;
import static com.ocpay.wallet.Constans.ERROR.WALLET_IMPORT_FAIL;
import static com.ocpay.wallet.Constans.RXBUS.ACTION_SELECT_WALLET;
import static com.ocpay.wallet.Constans.RXBUS.ACTION_WALLET_FIRST_CREATE;
import static com.ocpay.wallet.Constans.WALLET.WALLET_TYPE_IMPORT;
import static com.ocpay.wallet.utils.OCPWalletPasswordHelp.setPwdLevel;

/**
 * Created by y on 2018/5/25.
 */

public abstract class BaseImportWalletFragment<VD extends ViewDataBinding> extends BaseFragment<VD> {

    private LoadingDialog loadingDialog;


    public void setCheckBoxStatus(Context mContext, boolean isChecked, TextView textView) {
        textView.setClickable(isChecked);
        int resBgImport = isChecked ? R.drawable.shape_corner_btn_main_r6 : R.drawable.shape_btn_grave;
        int resTxColor = isChecked ? mContext.getResources().getColor(R.color.white) : getResources().getColor(R.color.color_btn_text_un_click);
        textView.setTextColor(resTxColor);
        textView.setBackgroundResource(resBgImport);
    }


    public int saveWallet(WalletFile walletFile,String passwordTip) {
        try {
            /** duplicate wallet **/
            WalletInfo sqlWalletInfo = WalletInfoDaoUtils.sqlByAddress(MyApp.getContext(), walletFile.getAddress());
            if (sqlWalletInfo != null) {
                return WALLET_EXIST;
            }
            OCPWalletFile ocpWalletFile = new OCPWalletFile();
            ocpWalletFile.setWalletFile(walletFile);
            WalletStorage.getInstance().saveWalletFile(MyApp.getContext(), ocpWalletFile);
            WalletInfo walletInfo = new WalletInfo();
            walletInfo.setBackup(true);
            walletInfo.setWalletAddress(walletFile.getAddress());
            walletInfo.setWalletType(WALLET_TYPE_IMPORT);
            walletInfo.setPasswordTip(passwordTip);
            walletInfo.setWalletName(OCPWalletUtils.getImportWalletName(walletFile.getAddress()));
            walletInfo.setStartBlock(OCPPrefUtils.getLastBlockNo());
            WalletInfoDaoUtils.insertWalletInfo(walletInfo, MyApp.getContext());

            OCPWallet.setCurrentWallet(walletInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return WALLET_IMPORT_FAIL;
        }
        return OPERATION_SUCCESS;
    }


    public void importWalletComplete(int requestId) {
        RxBus.getInstance().post(requestId, "");
        getActivity().finish();
    }

    public int getRequestId() {
        WalletInfo currentWallet = OCPWallet.getCurrentWallet();
        if (currentWallet == null) {
            return ACTION_WALLET_FIRST_CREATE;
        }
        return ACTION_SELECT_WALLET;
    }


    public void showLoading(boolean hasBackground) {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
        loadingDialog = new LoadingDialog(getContext());
        loadingDialog.show();
    }

    public void setCancel(boolean isCancel) {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.setCance(isCancel);
        }
    }

    public void setTip(String txt) {
        if (loadingDialog != null && !TextUtils.isEmpty(txt)) {
            loadingDialog.setText(txt);
        }
    }

    public void dismissLoading() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
        super.onDestroy();
    }


    public void setTextChangedListener(EditText editText, final TextView tvLv, final TextView tvTip, final ImageView ivLv) {
        editText.addTextChangedListener(new TextWatcher() {
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
                        setPwdLevel(input, tvLv, tvTip, ivLv);
                    }
                });
            }
        });
    }


    public String getErrorTip(int errorId) {

        switch (errorId) {
            case Constans.ERROR.WALLET_EXIST:
                return "Wallet exist";

            case Constans.ERROR.WALLET_IMPORT_FAIL:
                return "Wallet Import Fail";

            case Constans.ERROR.WALLET_INVALID_PASSWORD:
                return "Wallet Import Fail";


        }
        return "Wallet Import Fail";


    }


    /**
     * @param requestId
     * @return
     */
    public Observer<Integer> getObserver(final int requestId) {
        return new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                showLoading(false);

            }

            @Override
            public void onNext(Integer code) {
                dismissLoading();
                if (code == Constans.ERROR.OPERATION_SUCCESS) {
                    importWalletComplete(requestId);
                } else {
                    WarmDialog.showTip(getActivity(), getErrorTip(code));
                }
            }

            @Override
            public void onError(Throwable e) {
                dismissLoading();
                WarmDialog.showTip(getActivity(), getErrorTip(-1));
            }

            @Override
            public void onComplete() {

            }
        };
    }


    public boolean sqlRepeatAddress(String address) {
        WalletInfo walletInfo = WalletInfoDaoUtils.sqlByAddress(MyApp.getContext(), address);
        if (walletInfo != null) {
            WarmDialog.showTip(getActivity(), "Wallet exits");
            return true;
        }
        return false;
    }

}
