package com.ocpay.wallet.widget.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.ocpay.wallet.MyApp;
import com.ocpay.wallet.R;
import com.ocpay.wallet.databinding.DialogPrivateKeyExportBinding;

import static com.snow.commonlibrary.utils.ShareUtils.toClipboardData;


public class ExportPrivateKeyDialog extends AlertDialog implements View.OnClickListener {
    public static final String TAG = "WarmDialog";
    private Activity activity;
    private DialogPrivateKeyExportBinding binding;
    static ExportPrivateKeyDialog dialog;


    public static ExportPrivateKeyDialog getInstance(Activity activity) {
        if (dialog == null) {
            dialog = new ExportPrivateKeyDialog(R.style.PasswordConfirmDialog, activity);
        }
        return dialog;
    }


    private ExportPrivateKeyDialog(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    private ExportPrivateKeyDialog(int ResTheme, Activity activity) {
        super(activity, ResTheme);
        this.activity = activity;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.dialog_private_key_export, null, false);

        setContentView(binding.getRoot());

        initView();


    }


    private void initView() {
        binding.tvCopyPrivateKey.setOnClickListener(this);
        binding.tvPrivateKey.setOnClickListener(this);
        binding.ivClose.setOnClickListener(this);
    }

    @Override
    public void show() {
        if (dialog != null && !isShowing() && !activity.isFinishing()) {
            super.show();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                dismiss();
                break;
            case R.id.tv_copy_private_key:
            case R.id.tv_private_key:
                copyPrivateKey();
                break;
        }
    }

    private void copyPrivateKey() {
        String tip = getContext().getResources().getString(R.string.address_copied_to_clipboard);
        toClipboardData(MyApp.getContext(), "", binding.tvPrivateKey.getText().toString().trim(), tip);

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
    }


    @Override
    public void dismiss() {
        super.dismiss();
        destroy();
    }

    public void setPrivateKey(String s) {
        if (binding == null) return;
        binding.tvPrivateKey.setText(s);
    }

    public void showOCNWallet(String walletAddress) {
        show();
        if (binding == null) return;
        binding.tvExportPrivateWarm.setVisibility(View.GONE);
        binding.tvExportPrivate.setText(MyApp.getContext().getResources().getString(R.string.title_ocn_wallet));
        binding.tvPrivateKey.setText(walletAddress);
    }

}
