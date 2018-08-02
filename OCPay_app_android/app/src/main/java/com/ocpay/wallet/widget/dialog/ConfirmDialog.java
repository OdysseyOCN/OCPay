package com.ocpay.wallet.widget.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.ocpay.wallet.R;
import com.ocpay.wallet.databinding.DialogConfirmBinding;
import com.ocpay.wallet.databinding.DialogPasswordConfirmBinding;


public class ConfirmDialog extends AlertDialog implements View.OnClickListener {
    public static final String TAG = "PasswordConfirmDialog";
    private Activity activity;
    private DialogConfirmBinding binding;
    static ConfirmDialog dialog;
    public ConfirmListener listener;

    public interface ConfirmListener {
        void onConfirm();
    }


    public static ConfirmDialog getInstance(Activity activity) {
        if (dialog == null) {
            dialog = new ConfirmDialog(R.style.PasswordConfirmDialog, activity);
        }
        return dialog;
    }


    private ConfirmDialog(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    private ConfirmDialog(int ResTheme, Activity activity) {
        super(activity, ResTheme);
        this.activity = activity;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.dialog_confirm, null, false);
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
                    listener.onConfirm();
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
}
