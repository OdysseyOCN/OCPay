package com.ocpay.wallet.widget.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.ocpay.wallet.MyApp;
import com.ocpay.wallet.R;
import com.ocpay.wallet.activities.QRReaderActivity;
import com.ocpay.wallet.bean.QRCodeBean;
import com.ocpay.wallet.databinding.DialogScanSignBinding;
import com.ocpay.wallet.http.rx.RxBus;
import com.ocpay.wallet.utils.CommonUtils;
import com.snow.commonlibrary.log.MyLog;
import com.snow.commonlibrary.utils.StringUtil;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static com.ocpay.wallet.Constans.RXBUS.ACTION_DIALOG_GET_QRCODE_INFO;
import static com.ocpay.wallet.activities.QRReaderActivity.QR_CODE_MODE_READER;


public class QRCodeScanDialog extends AlertDialog implements View.OnClickListener {
    public static final String TAG = "TxDetailDialog";


    private OnNextListener listener;
    private Activity activity;
    private DialogScanSignBinding binding;
    static QRCodeScanDialog dialog;
    private CompositeDisposable mDisposable;


    public interface OnNextListener {
        void onNext(String code);
    }


    public static synchronized QRCodeScanDialog getInstance(Activity activity) {
        if (dialog == null) {
            synchronized (QRCodeScanDialog.class) {
                MyLog.i("dialog:getInstance");

                if (dialog == null) {
                    dialog = new QRCodeScanDialog(R.style.PasswordConfirmDialog, activity);
                }
            }
        }
        return dialog;
    }


    private QRCodeScanDialog(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    private QRCodeScanDialog(int ResTheme, Activity activity) {
        super(activity, ResTheme);
        this.activity = activity;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyLog.i("dialog:onCreate");
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.dialog_scan_sign, null, false);
        setContentView(binding.getRoot());
        initView();
        initRxJava();
    }

    private void initRxJava() {
        mDisposable = new CompositeDisposable();
        Disposable disposable = RxBus.getInstance()
                .toObservable(ACTION_DIALOG_GET_QRCODE_INFO, String.class)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        if (binding == null) return;
                        QRCodeBean qrCodeBean = CommonUtils.json2Object(s, QRCodeBean.class);
                        String showData = qrCodeBean == null || qrCodeBean.getData() == null ? "" : qrCodeBean.getData();
                        binding.tvSignData.setText(showData);
                    }
                });
        mDisposable.add(disposable);


    }

    private void initView() {
        ViewGroup.LayoutParams layoutParams = binding.getRoot().getLayoutParams();
        layoutParams.width = MyApp.getContext().getResources().getDisplayMetrics().widthPixels;
        binding.ivClose.setOnClickListener(this);
        binding.tvAction.setOnClickListener(this);
        binding.ivScan.setOnClickListener(this);
    }


    @Override
    public void show() {
        MyLog.i("dialog:show");
        if (dialog != null && !isShowing() && !activity.isFinishing()) {
            Window win = this.getWindow();
            win.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
            super.show();
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        destroy();
    }


    public void destroy() {
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            dialog = null;
        }
        binding = null;
        activity = null;
        if (mDisposable != null) mDisposable.clear();
        mDisposable = null;

    }

    public void setListener(OnNextListener listener) {
        this.listener = listener;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                dismiss();
                break;

            case R.id.iv_scan:
                QRReaderActivity.startQRReaderActivity(activity, ACTION_DIALOG_GET_QRCODE_INFO, QR_CODE_MODE_READER);
                break;
            case R.id.tv_action:
                String signData = binding.tvSignData.getText().toString();
                if (StringUtil.isEmpty(signData)) {
                    Toast.makeText(getContext(), "input is null", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (listener != null) listener.onNext(signData.trim());
                dismiss();
                break;

        }
    }


    public static final int WATCH_MODE = 1;
    public static final int TRANSACTION_MODE = 2;

    public void changeMode(int mode) {
        if (binding == null) return;
        switch (mode) {
            case WATCH_MODE:
                binding.tvDialogTitle.setText(R.string.tip_scanlog_watch_title);
                binding.tvSignData.setHint(R.string.tip_scanlog_watch_hint);
                binding.tvAction.setText(R.string.tip_scanlog_watch_action);
                break;
            case TRANSACTION_MODE:
                binding.tvDialogTitle.setText(R.string.tip_scanlog_transaction_title);
                binding.tvSignData.setHint(R.string.tip_scanlog_transaction_hint);
                binding.tvAction.setText(R.string.tip_scanlog_transaction_action);
                break;
        }

    }


}
