package com.ocpay.wallet.widget.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.support.annotation.RequiresApi;
import android.view.View;

import com.ocpay.wallet.MyApp;
import com.ocpay.wallet.R;
import com.ocpay.wallet.databinding.DialogVerifyFingerprintBinding;


public class VerifyFingerprintDialog extends AlertDialog implements View.OnClickListener {
    public static final String TAG = VerifyFingerprintDialog.class.getSimpleName();
    private Activity activity;
    private DialogVerifyFingerprintBinding binding;
    static VerifyFingerprintDialog dialog;

    private OnVerifyFingerprintListener listener;

    public interface OnVerifyFingerprintListener {

        void verifySuccessful();

        void cancel();

    }


    public static VerifyFingerprintDialog getInstance(Activity activity) {
        if (dialog == null) {
            dialog = new VerifyFingerprintDialog(R.style.PasswordConfirmDialog, activity);
        }
        return dialog;
    }


    private VerifyFingerprintDialog(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    private VerifyFingerprintDialog(int ResTheme, Activity activity) {
        super(activity, ResTheme);
        this.activity = activity;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.dialog_verify_fingerprint, null, false);

        setContentView(binding.getRoot());
        initView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setupFingerprintStuff();
        }


    }


    private void initView() {
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
                if (listener != null) listener.cancel();
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
        if (listener != null) {
            listener = null;
        }
        binding = null;
        activity = null;
    }


    public OnVerifyFingerprintListener getListener() {
        return listener;
    }

    public void setListener(OnVerifyFingerprintListener listener) {
        this.listener = listener;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setupFingerprintStuff() {
        FingerprintManager fingerprintManager = (FingerprintManager) MyApp.getContext().getSystemService(Context.FINGERPRINT_SERVICE);

        FingerprintManager.AuthenticationCallback mCallBack = new FingerprintManager.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                if (listener != null) listener.verifySuccessful();
                dismiss();

            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                if (listener != null) listener.cancel();
                dismiss();
            }
        };
        CancellationSignal mCancellationSignal = new CancellationSignal();


        fingerprintManager.authenticate(null, mCancellationSignal, 0, mCallBack, null);

    }


    @Override
    public void dismiss() {
        super.dismiss();
        destroy();
    }
}
