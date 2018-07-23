package com.ocpay.wallet.widget.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;

import com.ocpay.wallet.AnalyticsEvent;
import com.ocpay.wallet.R;
import com.ocpay.wallet.databinding.DialogTipBackupBinding;
import com.ocpay.wallet.manager.AnalyticsManager;
import com.snow.commonlibrary.log.MyLog;


public class TipBackupDialog extends AlertDialog implements View.OnClickListener {

    public static final String TAG = TipBackupDialog.class.getSimpleName();

    private Activity activity;
    private DialogTipBackupBinding binding;
    static TipBackupDialog dialog;


    public static synchronized TipBackupDialog getInstance(Activity activity) {
        if (dialog == null) {
            synchronized (TipBackupDialog.class) {
                if (dialog == null) {
                    dialog = new TipBackupDialog(R.style.FullScreenDialog, activity);
                }
            }
        }
        return dialog;
    }


    private TipBackupDialog(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    private TipBackupDialog(int ResTheme, Activity activity) {
        super(activity, ResTheme);
        this.activity = activity;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.dialog_tip_backup, null, false);
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        ViewGroup.LayoutParams layoutParams = binding.getRoot().getLayoutParams();
        Display display = activity.getWindowManager().getDefaultDisplay();
//        layoutParams.width = MyApp.getContext().getResources().getDisplayMetrics().widthPixels;
        layoutParams.width = display.getWidth();
//        layoutParams.height = MyApp.getContext().getResources().getDisplayMetrics().heightPixels;
        layoutParams.height = display.getHeight();;
        binding.tvBackupNow.setOnClickListener(this);
    }


    @Override
    public void show() {
        MyLog.i("dialog:show");
        if (dialog != null && !isShowing() && !activity.isFinishing()) {
            super.show();
        }
    }


    public void destroy() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        binding = null;
        activity = null;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                dismiss();
                break;
            case R.id.tv_backup_now:
                AnalyticsManager.getInstance().sendEvent(AnalyticsEvent.Youneedtobackupfirst_button_BackupNow);
                dismiss();
                break;

        }
    }


}
