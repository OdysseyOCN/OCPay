package com.ocpay.wallet.widget.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.ocpay.wallet.MyApp;
import com.ocpay.wallet.R;
import com.ocpay.wallet.databinding.DialogQrcodeSignBinding;
import com.ocpay.wallet.utils.qr.QRCodeUtils;
import com.snow.commonlibrary.log.MyLog;


public class QRCodeSignDialog extends AlertDialog implements View.OnClickListener {
    public static final String TAG = "TxDetailDialog";


    private OnNextListener listener;

    private Activity activity;
    private DialogQrcodeSignBinding binding;
    static QRCodeSignDialog dialog;
    private int current;


    public interface OnNextListener {
        void onNext();
    }


    public static synchronized QRCodeSignDialog getInstance(Activity activity) {
        if (dialog == null) {
            synchronized (QRCodeSignDialog.class) {
                MyLog.i("dialog:getInstance");

                if (dialog == null) {
                    dialog = new QRCodeSignDialog(R.style.PasswordConfirmDialog, activity);
                }
            }
        }
        return dialog;
    }


    private QRCodeSignDialog(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    private QRCodeSignDialog(int ResTheme, Activity activity) {
        super(activity, ResTheme);
        this.activity = activity;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.dialog_qrcode_sign, null, false);
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        ViewGroup.LayoutParams layoutParams = binding.getRoot().getLayoutParams();
        layoutParams.width = MyApp.getContext().getResources().getDisplayMetrics().widthPixels;
        binding.ivClose.setOnClickListener(this);
        binding.tvAction.setOnClickListener(this);
    }


    @Override
    public void show() {
        if (dialog != null && !isShowing() && !activity.isFinishing()) {
            Window win = this.getWindow();
            win.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
            super.show();
        }
    }


    public void destroy() {
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            dialog = null;
        }
        if (listener == null) {
            listener = null;
        }
        binding = null;
        activity = null;
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
            case R.id.tv_action:
                if (listener != null) listener.onNext();
                dismiss();
                break;

        }
    }


    public void setData(String qrCodeBean) {
        if (binding == null) return;
        QRCodeUtils.updateQRCode(binding.ivQrCode, qrCodeBean);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        destroy();
    }


    public static final int MODE_SIGN = 1;
    public static final int MODE_SIGN_SUCCESSFUL = 2;
    public static final int MODE_SHOW_TRANSACTION_INFO = 3;

    /**
     * 1
     */
    public void changeType(int mode) {
        if (binding == null) return;
        current = mode;
        String title = mode == MODE_SIGN ? activity.getString(R.string.dialog_sign) : mode == MODE_SHOW_TRANSACTION_INFO ? "Scanning by using cold wallet" : activity.getString(R.string.dialog_sign_successfully);
        binding.tvDialogTitle.setText(title);
        String tip = mode == MODE_SIGN ? "Scanning by cold wallet" : mode == MODE_SHOW_TRANSACTION_INFO ? "Go next when the signature is ready" : "Scan signature QR code by using hot wallet";
        binding.tvQrCodeTip.setText(tip);
        String actionName = mode == MODE_SIGN || mode == MODE_SHOW_TRANSACTION_INFO ? "Next Step" : "Finished";
        binding.tvAction.setText(actionName);

    }


}
