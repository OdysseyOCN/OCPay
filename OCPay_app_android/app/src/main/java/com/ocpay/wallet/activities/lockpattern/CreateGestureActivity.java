package com.ocpay.wallet.activities.lockpattern;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ocpay.wallet.R;
import com.ocpay.wallet.activities.WalletLoginActivity;
import com.ocpay.wallet.databinding.ActivityCreateGestureBinding;
import com.ocpay.wallet.http.rx.RxBus;
import com.ocpay.wallet.utils.OCPPrefUtils;
import com.star.lockpattern.util.LockPatternUtil;
import com.star.lockpattern.widget.LockPatternIndicator;
import com.star.lockpattern.widget.LockPatternView;

import java.util.ArrayList;
import java.util.List;

import static com.ocpay.wallet.Constans.RXBUS.ACTION_ENABLE_GESTURE;


/**
 * create gesture activity
 * Created by Sym on 2015/12/23.
 */
public class CreateGestureActivity extends Activity implements LockPatternView.OnPatternListener {

    LockPatternIndicator lockPatternIndicator;
    LockPatternView lockPatternView;
    Button resetBtn;
    TextView messageTv;

    private List<LockPatternView.Cell> mChosenPattern = null;
    private static final long DELAYTIME = 600L;
    private static final String TAG = "CreateGestureActivity";
    private ActivityCreateGestureBinding binding;


    public static void startCreateGestureActivity(Context context) {
        context.startActivity(new Intent(context, CreateGestureActivity.class));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_gesture);
        this.init();
    }

    private void init() {
        lockPatternIndicator = binding.lockPatterIndicator;
        lockPatternView = binding.lockPatternView;
        messageTv = binding.messageTv;
        lockPatternView.setOnPatternListener(this);
        resetBtn = binding.resetBtn;
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // reset gesture password
                mChosenPattern = null;
                lockPatternIndicator.setDefaultIndicator();
                updateStatus(Status.DEFAULT, null);
                lockPatternView.setPattern(LockPatternView.DisplayMode.DEFAULT);
            }
        });
    }


    /**
     * 更新状态
     *
     * @param status
     * @param pattern
     */
    private void updateStatus(Status status, List<LockPatternView.Cell> pattern) {
        messageTv.setTextColor(getResources().getColor(status.colorId));
        messageTv.setText(status.strId);
        switch (status) {
            case DEFAULT:
                lockPatternView.setPattern(LockPatternView.DisplayMode.DEFAULT);
                break;
            case CORRECT:
                updateLockPatternIndicator();
                lockPatternView.setPattern(LockPatternView.DisplayMode.DEFAULT);
                break;
            case LESSERROR:
                lockPatternView.setPattern(LockPatternView.DisplayMode.DEFAULT);
                break;
            case CONFIRMERROR:
                lockPatternView.setPattern(LockPatternView.DisplayMode.ERROR);
                lockPatternView.postClearPatternRunnable(DELAYTIME);
                break;
            case CONFIRMCORRECT:
                saveChosenPattern(pattern);
                lockPatternView.setPattern(LockPatternView.DisplayMode.DEFAULT);
                setLockPatternSuccess();
                break;
        }
    }

    /**
     * 更新 Indicator
     */
    private void updateLockPatternIndicator() {
        if (mChosenPattern == null)
            return;
        lockPatternIndicator.setIndicator(mChosenPattern);
    }


    /**
     * 成功设置了手势密码(跳到首页)
     */
    private void setLockPatternSuccess() {
        RxBus.getInstance().post(ACTION_ENABLE_GESTURE, true);
        OCPPrefUtils.setLastTimeOfOnPause();
        WalletLoginActivity.setNotFirstStart(true);
        finish();
    }

    /**
     * 保存手势密码
     */
    private void saveChosenPattern(List<LockPatternView.Cell> cells) {
        //  save
        byte[] bytes = LockPatternUtil.patternToHash(cells);
        OCPPrefUtils.setGesturePassword(bytes);
    }

    @Override
    public void onPatternStart() {
        lockPatternView.removePostClearPatternRunnable();
        //updateStatus(Status.DEFAULT, null);
        lockPatternView.setPattern(LockPatternView.DisplayMode.DEFAULT);
    }

    @Override
    public void onPatternComplete(List<LockPatternView.Cell> pattern) {
        //Log.e(TAG, "--onPatternDetected--");
        if (mChosenPattern == null && pattern.size() >= 4) {
            mChosenPattern = new ArrayList<LockPatternView.Cell>(pattern);
            updateStatus(Status.CORRECT, pattern);
        } else if (mChosenPattern == null && pattern.size() < 4) {
            updateStatus(Status.LESSERROR, pattern);
        } else if (mChosenPattern != null) {
            if (mChosenPattern.equals(pattern)) {
                updateStatus(Status.CONFIRMCORRECT, pattern);
            } else {
                updateStatus(Status.CONFIRMERROR, pattern);
            }
        }
    }


    private enum Status {
        //默认的状态，刚开始的时候（初始化状态）
        DEFAULT(R.string.create_gesture_default, R.color.grey_a5a5a5),
        //第一次记录成功
        CORRECT(R.string.create_gesture_correct, R.color.grey_a5a5a5),
        //连接的点数小于4（二次确认的时候就不再提示连接的点数小于4，而是提示确认错误）
        LESSERROR(R.string.create_gesture_less_error, R.color.red_f4333c),
        //二次确认错误
        CONFIRMERROR(R.string.create_gesture_confirm_error, R.color.red_f4333c),
        //二次确认正确
        CONFIRMCORRECT(R.string.create_gesture_confirm_correct, R.color.grey_a5a5a5);

        private Status(int strId, int colorId) {
            this.strId = strId;
            this.colorId = colorId;
        }

        private int strId;
        private int colorId;
    }
}
