package com.ocpay.wallet.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.ocpay.wallet.manager.ActivityManager;
import com.ocpay.wallet.widget.dialog.LoadingDialog;

import java.util.Stack;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class BaseActivity extends AppCompatActivity {
    private CompositeSubscription mSubscription;
    private CompositeDisposable mDisposable;
    private LoadingDialog loadingDialog;


    public void addSubscription(Subscription s) {
        if (this.mSubscription == null) {
            this.mSubscription = new CompositeSubscription();
        }
        this.mSubscription.add(s);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityManager am = ActivityManager.getInstance();
        am.addActivity(this);
    }


    public void addDisposable(Disposable disposable) {
        if (this.mDisposable == null) {
            this.mDisposable = new CompositeDisposable();
        }
        this.mDisposable.add(disposable);

    }

    @Override
    protected void onDestroy() {
        if (mSubscription != null && mSubscription.hasSubscriptions()) {
            mSubscription.unsubscribe();
        }
        if (mDisposable != null) mDisposable.clear();

        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }


        ActivityManager am = ActivityManager.getInstance();
        am.removeActivity(this);

        super.onDestroy();

    }


    public void showLoading(boolean hasBackground) {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
        loadingDialog = new LoadingDialog(this);
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

    public void getPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return;
        //当前系统大于等于6.0
        if (!(ContextCompat.checkSelfPermission(BaseActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(BaseActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.INTERNET}, 10086);
        }
    }


    public void notificationReturnHome() {
        Stack<Activity> activityStack = ActivityManager.getInstance().getActivityStack();
        if (activityStack.size() == 1) {
            startActivity(new Intent(this, MainActivity.class));
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!filterProtectLock()) {
            WalletLoginActivity.protectWithLock(this, true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!filterProtectLock()) {
            WalletLoginActivity.protectWithLock(this, false);
        }

    }

    public boolean filterProtectLock() {
        return false;
    }


}
