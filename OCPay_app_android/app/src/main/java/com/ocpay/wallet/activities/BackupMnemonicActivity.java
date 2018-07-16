package com.ocpay.wallet.activities;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.ocpay.wallet.AnalyticsEvent;
import com.ocpay.wallet.Constans;
import com.ocpay.wallet.MyApp;
import com.ocpay.wallet.OCPWallet;
import com.ocpay.wallet.R;
import com.ocpay.wallet.databinding.ActivityBackupMnemonicBinding;
import com.ocpay.wallet.greendao.manager.WalletInfoDaoUtils;
import com.ocpay.wallet.manager.AnalyticsManager;
import com.ocpay.wallet.utils.eth.bean.OCPWalletFile;
import com.ocpay.wallet.utils.wallet.WalletStorage;
import com.ocpay.wallet.widget.FlowLayout;
import com.ocpay.wallet.widget.LabelText;
import com.ocpay.wallet.widget.dialog.PasswordConfirmDialog;
import com.ocpay.wallet.widget.dialog.WarmDialog;
import com.snow.commonlibrary.utils.StringUtil;
import com.snow.commonlibrary.utils.ViewUtils;
import com.snow.commonlibrary.widget.webview.WebViewActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.ocpay.wallet.Constans.WALLET.ADDRESS_FROM;
import static com.ocpay.wallet.Constans.WALLET.WALLET_MNEMONIC;
import static com.ocpay.wallet.utils.eth.OCPWalletUtils.getDecryptMnemonic;
import static com.ocpay.wallet.widget.LabelText.MODE_ADDED;
import static com.ocpay.wallet.widget.LabelText.MODE_UN_SELECT;

public class BackupMnemonicActivity extends BaseActivity implements View.OnClickListener {


    private ActivityBackupMnemonicBinding binding;
    private static final int MODE_ONLY_SHOW = 99;
    private static final int MODE_STANDAR = 100;
    private FlowLayout flInput;
    protected List<LabelText> labelTexts;
    private FlowLayout flSelect;
    private int step;
    private int mode;
    private String address;


    public static void startBackupActivity(Activity activity, String address, String mnemonic) {
        Intent intent = new Intent(activity, BackupMnemonicActivity.class);
        intent.putExtra(WALLET_MNEMONIC, mnemonic);
        intent.putExtra(ADDRESS_FROM, address);
        activity.startActivity(intent);

    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        binding = DataBindingUtil.setContentView(BackupMnemonicActivity.this, R.layout.activity_backup_mnemonic);
        step = 1;
        address = getIntent().getStringExtra(ADDRESS_FROM);
        mnemonic = getIntent().getStringExtra(WALLET_MNEMONIC);
        initActionBar();
        initView();
        mode = MODE_STANDAR;
        if (!StringUtil.isEmpty(mnemonic)) {
            step = 2;
            setShow(2);
            initShow();
            mode = MODE_ONLY_SHOW;
        }
    }

    private void initActionBar() {
        binding.includeActionBar.actionBarTitle.setText(R.string.activity_backup_mnemonic_title);
        binding.includeActionBar.llToolbar.setBackgroundColor(getResources().getColor(R.color.white));
        binding.includeActionBar.actionBarTitle.setTextColor(getResources().getColor(R.color.color_text_main));
        binding.includeActionBar.ivBack.setImageResource(R.mipmap.ic_back);
        binding.includeActionBar.toolbarMenuIcon.setVisibility(View.GONE);
        binding.includeActionBar.ivBack.setOnClickListener(this);
        binding.includeActionBar.viewLine.setVisibility(View.VISIBLE);

    }

    private void initView() {
        flInput = binding.includeReview.flMnemonicInput;
        flSelect = binding.includeReview.flMnemonicSelect;

        binding.includeShow.llMnemonicShow.setVisibility(View.GONE);
        binding.includeReview.llMnemonicReview.setVisibility(View.GONE);

        binding.includeReview.tvActionConfirm.setOnClickListener(this);
        binding.includeSummary.tvGoBackup.setOnClickListener(this);

        binding.includeShow.tvActionNext.setOnClickListener(this);
        binding.includeSummary.tvViewToTutorial.setOnClickListener(this);
    }


    protected String mnemonic = "";


    private void initShow() {
        if (mnemonic == null || StringUtil.isEmpty(mnemonic.trim()) || mnemonic.length() < 5) {
            WarmDialog.showTip(BackupMnemonicActivity.this, "Missing mnemonics");
            return;
        }
        binding.includeShow.tvMnemonicShow.setText(mnemonic);
        initSelect();
        randomShow();
    }


    private void initSelect() {
        String[] split = mnemonic.split(" ");
        labelTexts = new ArrayList<>();
        for (String word : split) {
            LabelText labelText = new LabelText(this, word, MODE_UN_SELECT, false);
            labelText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

            labelText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((LabelText) v).isSelected()) return;
                    ((LabelText) v).setSelected(true);
                    addInput(((LabelText) v).getText().toString());
                    randomShow();
                }
            });
            int padding = ViewUtils.dp2px(MyApp.getContext(), 8);
            int margin = ViewUtils.dp2px(MyApp.getContext(), 6);
            labelText.setPadding(padding, padding, padding, padding);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(margin, 0, margin, 0);
            labelText.setLayoutParams(layoutParams);
            labelTexts.add(labelText);
        }
    }

    private void addInput(String s) {
        LabelText labelText = new LabelText(this, s, MODE_ADDED, false);
        labelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flInput.removeView(flInput.findViewWithTag(v.getTag()));
                updateSelect(((LabelText) v).getText().toString());

            }
        });
        flInput.addView(labelText);

    }

    private void randomShow() {
        flSelect.removeAllViews();
        for (Integer index : getIntegerList()) {
            int padding = ViewUtils.dp2px(MyApp.getContext(), 8);
            int margin = ViewUtils.dp2px(MyApp.getContext(), 6);
            labelTexts.get(index).setPadding(padding, padding, padding, padding);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(margin, 0, margin, 0);
            labelTexts.get(index).setLayoutParams(layoutParams);
            flSelect.addView(labelTexts.get(index));
        }
    }


    private List<Integer> getIntegerList() {
        List<Integer> integers = new ArrayList<>();
        List<Integer> integerList = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            integers.add(i);
        }
        Random random = new Random();
        for (int i = 0; i < 12; i++) {
            int i1 = random.nextInt(integers.size());
            integerList.add(integers.get(i1));
            integers.remove(i1);
        }
        return integerList;
    }


    public void updateSelect(String word) {
        for (LabelText text : labelTexts) {
            if (word.equals(text.getText().toString())) {
                text.setSelected(false);
            }
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_go_backup:
                AnalyticsManager.getInstance().sendEvent(AnalyticsEvent.Backuppaymentplatform_button_BackupMnemonic);
                PasswordConfirmDialog.getInstance(BackupMnemonicActivity.this).show();
                PasswordConfirmDialog.getInstance(BackupMnemonicActivity.this).setListener(new PasswordConfirmDialog.ConfirmListener() {
                    @Override
                    public void onConfirm(final String pwd) {
                        showLoading(false);

                        Observable.just(pwd)
                                .observeOn(Schedulers.newThread())
                                .map(new Function<String, String>() {
                                    @Override
                                    public String apply(String pwd) {
                                        OCPWalletFile ocpWallet = null;
                                        String mnemonic = null;
                                        try {
                                            ocpWallet = WalletStorage.getInstance().getOCPWallet(address);
                                            mnemonic = getDecryptMnemonic(pwd, ocpWallet);

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        return mnemonic == null ? "" : mnemonic;
                                    }
                                })
                                .observeOn(AndroidSchedulers.mainThread())
                                .map(new Function<String, Object>() {
                                    @Override
                                    public Object apply(String mnemonic) throws Exception {
                                        dismissLoading();
                                        if (mnemonic == null || mnemonic.isEmpty()) {
                                            WarmDialog.showTip(BackupMnemonicActivity.this, "Password Incorrect. ");
                                        } else {
                                            setShow(2);
                                            BackupMnemonicActivity.this.mnemonic = mnemonic;
                                            initShow();
                                        }
                                        return "";
                                    }
                                }).subscribe();
                    }
                });
                break;


            case R.id.tv_action_confirm:
                if (flInput.getChildCount() != 12) {
                    WarmDialog.showTip(BackupMnemonicActivity.this, "Confirm failed,please examine your mnemonic words. ");
                }
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < 12; i++) {
                    if (flInput.getChildAt(i) instanceof LabelText) {
                        builder.append(((LabelText) flInput.getChildAt(i)).getText().toString());
                        builder.append(" ");
                    }
                }
                if (builder.toString().trim().equals(mnemonic)) {
                    /** go to main activity
                     * delete mnemonic **/
                    OCPWallet.getCurrentWallet().setBackup(true);
                    OCPWallet.setCurrentWallet(OCPWallet.getCurrentWallet());
                    WalletInfoDaoUtils.update(MyApp.getContext(), OCPWallet.getCurrentWallet());
                    WalletStorage.getInstance().deleteMnemonic(OCPWallet.getCurrentWallet().getWalletAddress());
                    if (mode == MODE_ONLY_SHOW) {
                        finish();
                    } else {
                        MainActivity.startMainActivity(BackupMnemonicActivity.this);

                    }

                } else {
                    WarmDialog.showTip(BackupMnemonicActivity.this, "Confirm failed,please examine your mnemonic words. ");
                }
                break;
            case R.id.tv_action_next:
                setShow(3);

                break;
            case R.id.iv_back:
                if (backAction()) return;
                finish();
                break;


            case R.id.tv_view_to_tutorial:
                AnalyticsManager.getInstance().sendEvent(AnalyticsEvent.Backuppaymentplatform_button_Viewthetutorial);
                WebViewActivity.loadUrl(this, Constans.H5.Backuppamentplatform, null);
                break;

        }
    }


    private boolean backAction() {
        if (step == 2 && mode == MODE_ONLY_SHOW) {
            return false;
        }

        if (step == 3) {
            setShow(2);
            return true;
        }

        if (step == 2) {
            setShow(1);
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (backAction()) return;
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PasswordConfirmDialog.getInstance(BackupMnemonicActivity.this).destroy();
        WarmDialog.getInstance(BackupMnemonicActivity.this).destroy();
    }


    public void setShow(int i) {
        step = i;
        int summaryVisible = i == 1 ? View.VISIBLE : View.GONE;
        int showVisible = i == 2 ? View.VISIBLE : View.GONE;
        int reviewVisible = i == 3 ? View.VISIBLE : View.GONE;

        binding.includeReview.llMnemonicReview.setVisibility(reviewVisible);
        binding.includeSummary.llMnemonicReview.setVisibility(summaryVisible);
        binding.includeShow.llMnemonicShow.setVisibility(showVisible);


    }


}
