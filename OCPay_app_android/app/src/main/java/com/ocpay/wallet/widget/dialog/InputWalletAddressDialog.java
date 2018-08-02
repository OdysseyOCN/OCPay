package com.ocpay.wallet.widget.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;

import com.ocpay.wallet.MyApp;
import com.ocpay.wallet.R;
import com.ocpay.wallet.databinding.DialogInputWalletAddressBinding;
import com.ocpay.wallet.databinding.DialogPasswordConfirmBinding;


public class InputWalletAddressDialog extends AlertDialog implements View.OnClickListener {
    public static final String TAG = "PasswordConfirmDialog";
    private Activity activity;
    private DialogInputWalletAddressBinding binding;
    static InputWalletAddressDialog dialog;
    public ConfirmListener listener;

    public interface ConfirmListener {
        void onConfirm(String pwd);
    }


    public static InputWalletAddressDialog getInstance(Activity activity) {
        if (dialog == null) {
            dialog = new InputWalletAddressDialog(R.style.PasswordConfirmDialog, activity);
        }
        return dialog;
    }


    private InputWalletAddressDialog(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    private InputWalletAddressDialog(int ResTheme, Activity activity) {
        super(activity, ResTheme);
        this.activity = activity;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.dialog_input_wallet_address, null, false);
        setContentView(binding.getRoot());
        initView();


    }

    private void initView() {
        binding.tvActionCancel.setOnClickListener(this);
        binding.tvActionConfirm.setOnClickListener(this);
    }

    @Override
    public void show() {
        if (dialog != null && !isShowing() && !activity.isFinishing()) {
            super.show();
            binding.tvPassword.setText("");
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_action_cancel:
                dismiss();
                break;
            case R.id.tv_action_confirm:
                if (listener != null) {
                    listener.onConfirm(binding.tvPassword.getText().toString().trim());
                }
                dismiss();
                break;
        }
    }


    public void destroy() {
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            dialog = null;
        }
        activity = null;
        binding = null;
        if (listener != null) listener = null;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        destroy();
    }

    public ConfirmListener getListener() {
        return listener;
    }

    public void setListener(ConfirmListener listener) {
        this.listener = listener;
    }


    public void showInputWalletAddress() {
        show();
        if (binding == null) return;
        String title = MyApp.getContext().getString(R.string.tip_enter_wallet);
//        binding.tvPassword.setInputType(InputType.TYPE_CLASS_TEXT);
        binding.tvDialogTitle.setText(title);
    }


}
